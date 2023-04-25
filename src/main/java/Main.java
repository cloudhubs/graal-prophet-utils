import java.text.ParseException;

import baylor.cloudhubs.prophetutils.visualizer.Link;
import com.google.gson.Gson;

import baylor.cloudhubs.prophetutils.ProphetUtilsFacade;
import baylor.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import java.io.IOException;
import java.io.FileReader;
import java.util.Map;
import java.util.Objects;
import java.io.IOException;
import java.io.FileNotFoundException;

import baylor.cloudhubs.prophetutils.contextmap.*;

public class Main {

    public static String outputDir = null;

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"),
                "GRAAL_PROPHET_HOME not set");
        if (args.length != 1) {
            throw new IllegalArgumentException("Expecting one argument - the microservices JSON");
        }

        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);

        ProphetUtilsFacade.runNativeImage(analysisRequest, graalProphetHome);
        //ReadCreate r = new ReadCreate();
        //r.readIn();
    }
}