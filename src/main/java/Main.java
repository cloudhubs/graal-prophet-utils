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
import java.nio.file.Path;
import java.nio.file.Paths;

import baylor.cloudhubs.prophetutils.contextmap.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        String graalProphetHome = Objects.requireNonNull(System.getenv("GRAAL_PROPHET_HOME"),
                "GRAAL_PROPHET_HOME not set");
        if (args.length == 0 || args.length > 3) {
            throw new IllegalArgumentException("Expecting one, two, or three args <microservice_JSON> <check_ts-common> <percentMatch>");
        }

        int percentMatch = 70;
        boolean tsCommon = false;
        if (args.length == 2 && (args[1].equals("true") || args[1].equals("false"))) {
            tsCommon = Boolean.parseBoolean(args[1]);
        }else if(args.length == 2){
            percentMatch = Integer.parseInt(args[1]);
        }
        if(args.length == 3){
            tsCommon = Boolean.parseBoolean(args[1]);
            percentMatch = Integer.parseInt(args[2]);
        }

        Gson gson = new Gson();
        AnalysisRequest analysisRequest = gson.fromJson(new FileReader(args[0]), AnalysisRequest.class);

        System.out.println("Path: " + args[0] + " | tsCommon: " + tsCommon + " | percentMatch: " + percentMatch);
        ProphetUtilsFacade.runNativeImage(analysisRequest, graalProphetHome, percentMatch);

        Path absolute = Paths.get(args[0]);
        Path parent = absolute.getParent().getParent();
        ReadCreate.readIn(parent.toString() + "/output_" + analysisRequest.getSystemName(), tsCommon);
    }


}