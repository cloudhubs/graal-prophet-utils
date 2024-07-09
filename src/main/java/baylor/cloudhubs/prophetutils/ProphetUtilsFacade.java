package baylor.cloudhubs.prophetutils;

import baylor.cloudhubs.prophetutils.systemcontext.Module;
import baylor.cloudhubs.prophetutils.systemcontext.SystemContext;
import baylor.cloudhubs.prophetutils.contextmap.ReadCreate;
import baylor.cloudhubs.prophetutils.visualizer.LinkAlg;
import baylor.cloudhubs.prophetutils.microservice.MicroserviceSystem;
import baylor.cloudhubs.prophetutils.microservice.Microservice;
import baylor.cloudhubs.prophetutils.nativeimage.NativeImageRunner;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ProphetUtilsFacade {

    public final static Map<String, Integer> MS_TO_ANALYZE = new HashMap<>(); 
    public final static int RETRY_MAX = 3;

     private static SystemContext createSystemContext(List<Microservice> microservices, String graalProphetHome, String outputDir) {
        Set<Module> modules = new HashSet<>();
        while (!MS_TO_ANALYZE.isEmpty()){
            for (Microservice info : microservices) {
                //if the microservice is not in the MS_TO_ANALYZE, it has already been analyzed
                if (MS_TO_ANALYZE.get(info.getMicroserviceName()) == null){
                    continue;
                }

                NativeImageRunner runner = new NativeImageRunner(info, graalProphetHome, outputDir);
                System.out.println("RUNNING: " + info.getMicroserviceName());
                Module module = runner.runProphetPlugin();
                
                //microservice analysis did NOT failed
                if (module != null){
                    modules.add(module);
                    MS_TO_ANALYZE.remove(info.getMicroserviceName()); //SUCCESSFUL analysis, remove from list
                    System.out.println("SUCCESS: removing " + info.getMicroserviceName() + " from list\n");
                }

            }
        }

        return new SystemContext(!microservices.isEmpty() ? microservices.get(0).getMicroserviceName() : "unknown", modules);
    }

    private static void initializeMap(MicroserviceSystem ar){
        for (Microservice ms : ar.getMicroservices()){
            MS_TO_ANALYZE.put(ms.getMicroserviceName(), 0);
        }
    }
    public static void runNativeImage(MicroserviceSystem microserviceSystem, String graalProphetHome, int percentMatch){
        String outputFolderName = null;
        List<Microservice> microservices = microserviceSystem.getMicroservices();
        String systemName = microserviceSystem.getSystemName();
        if (systemName == null){
            System.err.println("WARNING: No system name provided in microservices JSON");
            return;
        }
        if (!microservices.isEmpty()){
            
            initializeMap(microserviceSystem); //INIT MAP OF MICROSERVICES FOR ANALYSIS

            outputFolderName = "output_" + microserviceSystem.getSystemName();
            try {
				createOutputDir(outputFolderName);
                SystemContext ctx = createSystemContext(microservices, graalProphetHome, outputFolderName);
                Gson gson = new Gson();
                gson.toJson(ctx, new BufferedWriter(new FileWriter("./" + outputFolderName + "/system-context.json")));

                System.out.println("Beginning Linking and Communication Graph Creation\n");
                Boolean isTrainTicket = systemName.equals("trainticket");
                LinkAlg linkAlgorithm = new LinkAlg(microservices, percentMatch, isTrainTicket);
                linkAlgorithm.calculateLinks("./" + outputFolderName);
                ReadCreate r = new ReadCreate(outputFolderName);
                r.readIn();
			}
            catch(IOException | InterruptedException e){
                e.printStackTrace();
            }
        }else{
            System.err.println("WARNING: No microservices in system");
            return;
        }
             
    }

    private static void createOutputDir(String outputFolderName) throws IOException{
        // Create a File object for the root directory
        File rootDir = new File("./");

        // Check if the 'output' directory exists in the root directory
        File outputDir = new File(rootDir, "./" + outputFolderName);
        if (!outputDir.exists()) {
            // Create the 'output' directory if it does not exist
            if (!(outputDir.mkdir())){
					throw new IOException("Unable to create output directory");
            }else{
                System.out.println("Creating outputdir: " + outputDir.getCanonicalPath());
            }
        }
    }
}
