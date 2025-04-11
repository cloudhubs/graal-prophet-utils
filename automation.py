import argparse
import os
import xml.etree.ElementTree as ET
import re
import json
import zipfile
import shutil
import subprocess


def find_microservices(base_dir, build):
    microservices = []

    for root, dirs, files in os.walk(base_dir):
        if "pom.xml" in files or "build.gradle" in files or "build.gradle.kts" in files:
            # Check for pom.properties in the same folder as pom.xml to exclude build metadata folders
            if "pom.xml" in files and "pom.properties" in files:
                continue

            # Exclude the root directory to avoid catching the main package
            if root == base_dir:
                continue

            microservice = {}
            microservice["baseDir"] = os.path.abspath(root)

            if "pom.xml" in files:
                pom_path = os.path.join(root, "pom.xml")
                try:
                    tree = ET.parse(pom_path)
                    root_elem = tree.getroot()
                    namespace = {'ns': 'http://maven.apache.org/POM/4.0.0'}
                    microservice["basePackage"] = find_base_package(root)
                    microservice["microserviceName"] = root_elem.find("ns:artifactId", namespace).text
                    microservice["version"] = root_elem.find("ns:version", namespace).text
                except Exception as e:
                    print(f"Error parsing {pom_path}: {e}")

            elif "build.gradle" in files or "build.gradle.kts" in files:
                gradle_path = os.path.join(root, "build.gradle") if "build.gradle" in files else os.path.join(root,
                                                                                                              "build.gradle.kts")
                try:
                    microservice["basePackage"] = find_base_package(root)
                    gradle_data = parse_gradle_file(gradle_path)
                    # Attempt to retrieve the microservice name from Gradle data
                    microservice["microserviceName"] = (
                            gradle_data.get("name")  # Try to extract from `rootProject.name`
                            or os.path.basename(root)  # Fallback to the directory name
                    )
                    microservice["version"] = gradle_data.get("version", "unknown")
                except Exception as e:
                    print(f"Error parsing {gradle_path}: {e}")
                    continue

            # Locate target directory dynamically
            target_dir = find_target_dir(root)

            if not target_dir and build:
                try:
                    build_project(root)
                    target_dir = find_target_dir(root)
                except Exception as e:
                    print(f"Warning building project failed {root}: {e}")
                    continue

            if target_dir:
                microservice["targetDir"] = os.path.abspath(target_dir)

                classes_dir = find_classes_dir(target_dir)
                if classes_dir:
                    microservice["classesDir"] = classes_dir

                # Locate JAR files in the target directory
                jar_files = [
                    os.path.abspath(os.path.join(target_dir, f)) for f in os.listdir(target_dir)
                    if re.match(r".*\.(jar|war|ear)$", f)
                ]

                # Locate the lib directory
                lib_dir = find_lib_dir(target_dir)
                if lib_dir:
                    microservice["libDir"] = os.path.abspath(lib_dir)
                    # Add JAR files from the lib directory
                    jar_files.extend(
                        os.path.abspath(os.path.join(lib_dir, f)) for f in os.listdir(lib_dir)
                        if f.endswith(".jar")
                    )

                microservice["jars"] = jar_files

            # Combine version and ending
            microservice["jarEnding"] = f"{microservice.get('version', 'unknown')}.jar"

            # Add microservice to the list only if it matches the conditions
            if microservice.get("jars"):
                microservices.append(microservice)

    return microservices


def build_project(base_dir):
    if os.path.exists(os.path.join(base_dir, "pom.xml")):
        command = ["mvn", "clean", "package"]
    elif os.path.exists(os.path.join(base_dir, "build.gradle")) or os.path.exists(
            os.path.join(base_dir, "build.gradle.kts")):
        gradlew_path = os.path.join(base_dir, "gradlew")
        if not os.access(gradlew_path, os.X_OK):
            os.chmod(gradlew_path, 0o755)
        command = ["./gradlew", "clean", "build"]
    else:
        return

    result = subprocess.run(command, cwd=base_dir, capture_output=True, text=True)
    if result.returncode != 0:
        raise Exception(result.stderr)
    else:
        print(f"Successfully built project in {base_dir}")


def find_base_package(base_dir):
    src_main_dir = os.path.join(base_dir, "src", "main")
    package_names = []

    for root, dirs, files in os.walk(src_main_dir):
        for file in files:
            if file.endswith(".java") or file.endswith(".kt"):
                with open(os.path.join(root, file), 'r') as f:
                    content = f.read()
                    match = re.search(r"package\s+([a-zA-Z0-9_.]+);", content)
                    if match:
                        package_name = match.group(1)
                        if not package_names or package_name != package_names[0]:
                            package_names.append(package_name)
                        if len(package_names) == 2:
                            break
        if len(package_names) == 2:
            break

    if not package_names:
        return None

    # If only one package name is found, return it
    if len(package_names) == 1:
        return package_names[0]

    # Find the common prefix if more than one package name is found
    common_prefix = os.path.commonprefix(package_names)

    return common_prefix


def parse_gradle_file(gradle_path):
    """
    Parse a build.gradle or build.gradle.kts file to extract project details.
    """
    with open(gradle_path, "r") as f:
        content = f.read()

    gradle_data = {}
    name_match = re.search(r'rootProject\.name\s*=\s*[\'"]([^\'"]+)[\'"]', content)
    version_match = re.search(r'version\s*=\s*[\'"]([^\'"]+)[\'"]', content)
    group_match = re.search(r'group\s*=\s*[\'"]([^\'"]+)[\'"]', content)

    gradle_data["name"] = name_match.group(1) if name_match else None
    gradle_data["version"] = version_match.group(1) if version_match else None
    gradle_data["group"] = group_match.group(1) if group_match else None

    return gradle_data


def find_classes_dir(target_dir):
    """
    Heuristically determine the classes directory inside the target directory.
    Checks common locations in both Maven and Gradle builds.
    """
    possible_paths = [
        os.path.join(target_dir, "classes", "java", "main"),   # Gradle standard
        os.path.join(target_dir, "BOOT-INF", "classes"),       # Spring Boot fat JAR
        os.path.join(target_dir, "target", "classes"),         # Maven
        os.path.join(target_dir, "classes"),                   # Fallback
    ]

    for path in possible_paths:
        if os.path.isdir(path):
            # Check if it contains .class files
            for root, dirs, files in os.walk(path):
                if any(f.endswith(".class") for f in files):
                    return os.path.abspath(path)

    # As fallback, search for any folder named 'classes' that contains .class files
    for root, dirs, files in os.walk(target_dir):
        if os.path.basename(root) == "classes":
            if any(f.endswith(".class") for f in files):
                return os.path.abspath(root)

    return None


def find_target_dir(base_dir):
    """
    Dynamically locate the target directory:
    - For Maven: look for target directory with .jar/.war/.ear files.
    - For Gradle: prefer the full 'build' directory if it contains meaningful content.
    """
    gradle_present = (
            os.path.exists(os.path.join(base_dir, "build.gradle")) or
            os.path.exists(os.path.join(base_dir, "build.gradle.kts"))
    )

    for root, dirs, files in os.walk(base_dir):
        # Skip irrelevant paths
        if root.endswith(os.path.join("gradle", "wrapper")):
            continue

        # Match against jar/war/ear
        archive_found = any(re.match(r".*\.(jar|war|ear)$", f) for f in files)
        if archive_found:
            if gradle_present and "build/libs" in root.replace("\\", "/"):
                # Check if we have a higher-level 'build' directory with classes
                build_dir = os.path.join(base_dir, "build")
                classes_dir = os.path.join(build_dir, "classes")
                if os.path.isdir(classes_dir):
                    return build_dir
            return root

    return None


def find_lib_dir(target_dir):
    """
    Search recursively inside the target directory for a folder containing multiple JAR files.
    """
    for root, dirs, files in os.walk(target_dir):
        jar_files = [f for f in files if f.endswith(".jar")]
        if len(jar_files) > 3:  # Assume a "lib" directory has at least 3 JAR files
            return root
    return None


def unzip_microservices(microservices, base_directory):
    for microservice in microservices:
        microservice_name = microservice["microserviceName"]

        if "jars" not in microservice or not microservice["jars"]:
            print(f"Warning: No JAR files found for microservice '{microservice.get('microserviceName', 'unknown')}'. Skipping...")
            continue

        fatjar = microservice["jars"][0]

        output_path = os.path.join(base_directory, microservice_name, microservice["targetDir"])

        with zipfile.ZipFile(fatjar, 'r') as zip_ref:
            zip_ref.extractall(output_path)


def copy_to_frontend(system_name):
    source_dir = f"./output_{system_name}"
    target_dir = "../graal_mvp/frontend/src/data"

    shutil.copy(os.path.join(source_dir, "entities.json"), os.path.join(target_dir, "contextMap.json"))
    shutil.copy(os.path.join(source_dir, "communicationGraph.json"),
                os.path.join(target_dir, "communicationGraph.json"))


def run_java_command(output_file):
    java_home = os.environ.get("JAVA_HOME")
    if not java_home:
        raise EnvironmentError("JAVA_HOME environment variable is not set.")

    java_bin = os.path.join(java_home, "bin", "java")
    jar_path = "target/graal-prophet-utils-0.0.8.jar"
    command = [java_bin, "-jar", jar_path, output_file]

    result = subprocess.run(command, capture_output=True, text=True)
    if result.returncode != 0:
        print(f"Error running command: {result.stderr}")
    else:
        print(f"Command output: {result.stdout}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Find microservices in a base directory.")
    parser.add_argument("base_directory", type=str, help="The base directory to search for microservices.")
    parser.add_argument("-b", "--build", action="store_true", help="Build the project before processing.")
    args = parser.parse_args()

    base_directory = args.base_directory
    system_name = os.path.basename(base_directory.rstrip('/'))

    # Find microservices
    # Build the project if the --build flag is provided
    microservices = find_microservices(base_directory, args.build)

    # Prepare and save the first file (microservices with baseDir, basePackage, microserviceName)
    result_main = {
        "systemName": system_name,
        "microservices": [
            {
                "baseDir": ms["baseDir"],
                "basePackage": ms.get("basePackage"),
                "targetDir": ms.get("targetDir"),
                "microserviceName": ms.get("microserviceName"),
                "jarFiles": ms.get("jars"),
                "classesDir": ms.get("classesDir"),
            } for ms in microservices
        ]
    }
    output_main_file = "./output_main.json"
    with open(output_main_file, "w") as f:
        json.dump(result_main, f, indent=4)

    unzip_microservices(microservices, base_directory)
    run_java_command(output_main_file)
    copy_to_frontend(system_name)
