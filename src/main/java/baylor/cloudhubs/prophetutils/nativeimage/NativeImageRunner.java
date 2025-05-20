/**
 * Authors:
 * - Original Authors
 * - Vsevolod Pokhvalenko
 */

package baylor.cloudhubs.prophetutils.nativeimage;

import baylor.cloudhubs.prophetutils.ProphetUtilsFacade;
import baylor.cloudhubs.prophetutils.microservice.Microservice;
import baylor.cloudhubs.prophetutils.systemcontext.Module;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import java.util.StringJoiner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NativeImageRunner {
    private final String classpath;
    private final String entityOutput;
    private final String restcallOutput;
    private final String endpointOutput;
    private final String websocketconnectionOutput;
    private final String websocketendpointOutput;
    private final String graphQLCallOutput;
    private final String graphQLEndpointOutput;

    private final Microservice ms;
    private final String niCommand;
//    private final String callGraphOutputDir;


    public NativeImageRunner(Microservice ms, String graalProphetHome, String outputDir) {
        this.niCommand = graalProphetHome + "/bin/native-image";
        this.ms = ms;

        List<String> jarFiles = ms.getJarFiles();
        String classesDir = ms.getClassesDir();

        StringJoiner classpathJoiner = new StringJoiner(":");
        classpathJoiner.add(classesDir);
        for (String jarFile : jarFiles) {
            classpathJoiner.add(jarFile);
        }
        this.classpath = classpathJoiner.toString();

        this.entityOutput = "./" + outputDir + "/" + ms.getMicroserviceName() + ".json";
        this.restcallOutput = "./" + outputDir + "/" + ms.getMicroserviceName() + "_restcalls.csv";
        this.endpointOutput = "./" + outputDir + "/" + ms.getMicroserviceName() + "_endpoints.csv";
        this.websocketconnectionOutput = "./" + outputDir + "/" + ms.getMicroserviceName() + "_websocketconnections.csv";
        this.websocketendpointOutput = "./" + outputDir + "/" + ms.getMicroserviceName() + "_websocketendpoints.csv";
        this.graphQLCallOutput = "./" + outputDir + "/" + ms.getMicroserviceName() + "_graphqlcalls.csv";
        this.graphQLEndpointOutput = "./" + outputDir + "/" + ms.getMicroserviceName() + "_graphqlendpoints.csv";
    }

    public Module runProphetPlugin() {
        executeNativeImage();
        return parseOutputFile();
    }

    private Module parseOutputFile() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(entityOutput)) {
            return gson.fromJson(reader, Module.class);
        } catch (FileNotFoundException fne) {
            System.out.println("WARNING: FILE '" + entityOutput + "' NOT FOUND, LIKELY ANALYSIS FAILED");

            //increment attempt for running microservice
            ProphetUtilsFacade.MS_TO_ANALYZE.put(this.ms.getMicroserviceName(), ProphetUtilsFacade.MS_TO_ANALYZE.get(this.ms.getMicroserviceName()) + 1);
            //if microservice failed third attempt, throw error
            if (ProphetUtilsFacade.MS_TO_ANALYZE.get(this.ms.getMicroserviceName()) >= ProphetUtilsFacade.RETRY_MAX) {
                throw new RuntimeException("ERROR: " + this.ms.getMicroserviceName() + " FAILED TO BE ANALYZED");
            }
            return null;
        } catch (IOException e) {
            System.out.println("ERROR: IOException RUNNING ON " + this.ms.getMicroserviceName());
            throw new RuntimeException(e);
        }
    }

    private void executeNativeImage() {
        List<String> cmd = prepareCommand();

        System.out.println(String.join(" ", cmd));
        try {
            Process process = new ProcessBuilder()
                    .command(cmd)
                    .inheritIO()
                    .start();
            System.out.println("Native image drive pid is " + process.pid());
            int res = process.waitFor();
            if (res != 0) {
                System.err.println("Failed to execute command.");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private List<String> prepareCommand() {
        List<String> cmd = new ArrayList<>();
        cmd.add(niCommand);
        cmd.add("-H:+UnlockExperimentalVMOptions");
        cmd.add("-H:+ProphetPlugin");
        cmd.add("-H:-InlineBeforeAnalysis");
        cmd.add("-H:-BuildOutputSilent");
//        cmd.add("-H:+PrintAnalysisCallTree");
//        cmd.add("-H:PrintAnalysisCallTreeType=CSV");
//        cmd.add("-H:Path=" + this.callGraphOutputDir);
        cmd.add("-H:+AllowDeprecatedBuilderClassesOnImageClasspath");
        cmd.add("-H:ProphetMicroserviceName=" + this.ms.getMicroserviceName());
        cmd.add("-H:ProphetBasePackage=" + this.ms.getBasePackage());
        cmd.add("-H:ProphetEntityOutputFile=" + this.entityOutput);
        cmd.add("-H:ProphetRestCallOutputFile=" + this.restcallOutput);
        cmd.add("-H:ProphetEndpointOutputFile=" + this.endpointOutput);
        cmd.add("-H:ProphetWebsocketConnectionsOutputFile=" + this.websocketconnectionOutput);
        cmd.add("-H:ProphetWebsocketEndpointsOutputFile=" + this.websocketendpointOutput);
        cmd.add("-H:ProphetGraphQLEndpointOutputFile=" + this.graphQLEndpointOutput);
        cmd.add("-H:ProphetGraphQLCallOutputFile=" + this.graphQLCallOutput);
//        cmd.add("--debug-attach");
        // cmd.add("-R:MinHeapSize=4m"); 
        // cmd.add("-R:MaxHeapSize=15m");
        // cmd.add("-R:MaxNewSize=2m");   
        cmd.add("-cp");
        cmd.add(classpath);
        cmd.add(this.ms.getMicroserviceName());
        return cmd;
    }
}
