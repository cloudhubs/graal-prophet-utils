import java.text.ParseException;
import com.google.gson.Gson;

import baylor.cloudhubs.prophetutils.ProphetUtilsFacade;
import baylor.cloudhubs.prophetutils.filemanager.FileManager;
import baylor.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import baylor.cloudhubs.prophetutils.visualizer.LinkAlg;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.BoundedContext;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;
import edu.baylor.ecs.prophet.bounded.context.api.impl.BoundedContextApiImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.io.File;
import baylor.cloudhubs.prophetutils.visualizer.LinkAlg;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"),
                "GRAAL_PROPHET_HOME not set");
        if (args.length != 1) {
            throw new IllegalArgumentException("Expecting one argument - the microservices JSON");
        }

        // Create a File object for the root directory
        File rootDir = new File("./");
        // Check if the 'output' directory exists in the root directory
        File outputDir = new File(rootDir, "output");
        if (!outputDir.exists()) {
            // Create the 'output' directory if it does not exist
            if (!(outputDir.mkdir())){
                throw new IOException("Unable to create output directory");
            }
        }

        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);
        SystemContext ctx = ProphetUtilsFacade.getSystemContextViaNativeImage(analysisRequest.getMicroservices(),
                graalProphetHome);
        FileManager.writeToFile(ctx, "./output/ni-system-context.json");
        // System.out.println("GSON TO JSON: " + gson.toJson(ctx));
        BoundedContext boundedContext = new BoundedContextApiImpl().getBoundedContext(ctx, false);
        FileManager.writeToFile(boundedContext, "./output/ni-bounded-context.json");
        
        try{
            LinkAlg linkAlgorithm = new LinkAlg();
            linkAlgorithm.calculateLinks("./output");

        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}