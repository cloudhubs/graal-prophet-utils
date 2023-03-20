import java.text.ParseException;
import com.google.gson.Gson;

import baylor.cloudhubs.prophetutils.AppConfigUtils;
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
        
        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"), "GRAAL_PROPHET_HOME not set");
        if (args.length != 1) {
            throw new IllegalArgumentException("Expecting one argument - the configuration request to parse.");
        }
        //Load path config properties properties, throws error if file is empty, can't be found, or can't be loaded
        AppConfigUtils.loadConfigFile();
        if (AppConfigUtils.CONFIG_PROPS.isEmpty()){
            throw new ParseException("application configuration file is empty", 0);
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