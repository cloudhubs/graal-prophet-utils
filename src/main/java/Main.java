import baylor.cloudhubs.prophetutils.ProphetUtilsFacade;
import baylor.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;


public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"), " GRAAL_PROPHET_HOME is not set");
        if (args.length == 0 || args.length > 3) {
            throw new IllegalArgumentException("Expecting one or two args <microservice_JSON> <isTrainTicket> <percentMatch>");
        }

        boolean isTrainTicket = false;
        if (args.length >= 2) {
            isTrainTicket = Boolean.parseBoolean(args[1]);
        }

        int percentMatch = 70;
        if (args.length == 3) {
            percentMatch = Integer.parseInt(args[2]);
        }

        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);
        analysisRequest.resolveProphetHome();

        var before = System.currentTimeMillis();
        ProphetUtilsFacade.runNativeImage(analysisRequest, graalProphetHome, percentMatch, isTrainTicket);
        var duration = System.currentTimeMillis() - before;
        var perService = duration / analysisRequest.getMicroservices().size();
        System.out.println("The whole analysis took " + duration + " ms for " + analysisRequest.getMicroservices().size() + " microservices, so on average " + perService + "ms per microservice.");
    }
}