import java.text.ParseException;

import com.google.gson.Gson;

import baylor.cloudhubs.prophetutils.ProphetUtilsFacade;
import baylor.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import baylor.cloudhubs.prophetutils.nativeimage.MicroserviceInfo;

import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.IOException;
import java.io.FileNotFoundException;

import baylor.cloudhubs.prophetutils.contextmap.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"),
                "GRAAL_PROPHET_HOME not set");
        if (args.length == 0 || args.length > 2) {
            throw new IllegalArgumentException("Expecting one or two args <microservice_JSON> <percentMatch>");
        }

        int percentMatch = 70;
        if (args.length == 2) {
            percentMatch = Integer.parseInt(args[1]);
        }

        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);

        ProphetUtilsFacade.runNativeImage(analysisRequest, graalProphetHome, percentMatch);
        ReadCreate r = new ReadCreate();
        r.readIn();
    }


}