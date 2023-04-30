import java.text.ParseException;

import com.google.gson.Gson;

import baylor.cloudhubs.prophetutils.ProphetUtilsFacade;
import baylor.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import java.io.IOException;
import java.io.FileReader;
import java.util.Objects;
import java.io.FileNotFoundException;


public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"),
                "GRAAL_PROPHET_HOME not set");
        if (args.length == 0 || args.length > 3) {
            throw new IllegalArgumentException("Expecting one or two args <microservice_JSON> <isTrainTicket> <percentMatch>");
        }

        boolean isTrainTicket = false;
        if (args.length == 2) {
            isTrainTicket = Boolean.parseBoolean(args[1]);
        }

        int percentMatch = 70;
        if (args.length == 3) {
            percentMatch = Integer.parseInt(args[2]);
        }

        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);

        ProphetUtilsFacade.runNativeImage(analysisRequest, graalProphetHome, percentMatch, isTrainTicket);

    }


}