package baylor.cloudhubs.prophetutils.nativeimage;

import com.google.gson.Gson;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Module;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NativeImageRunner {
    private final String classpath;
    private final String entityOutput;
    private final String restcallOutput;
    private final String endpointOutput;

    private final MicroserviceInfo info;
    private final String niCommand;

    public NativeImageRunner(MicroserviceInfo info, String graalProphetHome) {
        this.niCommand = graalProphetHome + "/bin/native-image";
        this.info = info;
        //NEW
        String microservicePath = info.getBaseDir();
        //PREVIOUS
        // String microservicePath = info.getBaseDir() + File.separator + info.getMicroserviceName();
        this.classpath = microservicePath + "/target/BOOT-INF/classes" + ":" + microservicePath + "/target/BOOT-INF/lib/*";
        this.entityOutput = "./output/" + info.getMicroserviceName() + ".json";
        this.restcallOutput = "./output/" + info.getMicroserviceName() + "_restcalls.csv";
        this.endpointOutput = "./output/" + info.getMicroserviceName() + "_endpoints.csv";

    }

    public Module runProphetPlugin() {
        executeNativeImage();
        return parseOutputFile();
    }

    private Module parseOutputFile() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(entityOutput)) {
            return gson.fromJson(reader, Module.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeNativeImage() {
        List<String> cmd = prepareCommand();
        // System.out.println(String.join(" ", cmd));
        try {
            Process process = new ProcessBuilder()
                    .command(cmd)
                    .inheritIO()
                    .start();
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
        cmd.add("-H:+ProphetPlugin");
        cmd.add("-H:-InlineBeforeAnalysis");
        cmd.add("-H:ProphetMicroserviceName=" + this.info.getMicroserviceName());
        cmd.add("-H:ProphetBasePackage=" + this.info.getBasePackage());
        cmd.add("-H:ProphetEntityOutputFile=" + this.entityOutput);   
        cmd.add("-H:ProphetRestCallOutputFile=" + this.restcallOutput);        
        cmd.add("-H:ProphetEndpointOutputFile=" + this.endpointOutput);        
        cmd.add("-cp");
        cmd.add(classpath);
        cmd.add(this.info.getMicroserviceName());
        return cmd;
    }
}
