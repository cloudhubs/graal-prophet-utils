import java.text.ParseException;
import com.google.gson.Gson;

import baylor.cloudhubs.prophetutils.ProphetUtilsFacade;
import baylor.cloudhubs.prophetutils.filemanager.FileManager;
import baylor.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.prophet.bounded.context.api.impl.BoundedContextApiImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        
        // List<String> commands = new ArrayList<>();
        // commands.add("/bin/bash");
        // commands.add("-c");
        // commands.add("/home/jack/Capstone/graal-prophet-utils/src/main/java/script.sh");

        // try{
        //     ProcessBuilder pb = new ProcessBuilder(commands);
        //     Process process = pb.start();
        //     process.waitFor();
        // }catch (IOException | InterruptedException e){
        //     e.printStackTrace();
        // }

        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"), "GRAAL_PROPHET_HOME not set");
        if (args.length != 1) {
            throw new IllegalArgumentException("Expecting one argument - the configuration request to parse.");
        }

        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);
        SystemContext ctx = ProphetUtilsFacade.getSystemContextViaNativeImage(analysisRequest.getMicroservices(), graalProphetHome);
        FileManager.writeToFile(ctx, "./output/ni-system-context.json");
        // System.out.println("GSON TO JSON: " + gson.toJson(ctx));
        BoundedContext boundedContext = new BoundedContextApiImpl().getBoundedContext(ctx, false);
        FileManager.writeToFile(boundedContext, "./output/ni-bounded-context.json");
    }
}