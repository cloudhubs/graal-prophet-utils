package baylor.cloudhubs.prophetutils;

import baylor.cloudhubs.prophetutils.systemcontext.Module;
import baylor.cloudhubs.prophetutils.systemcontext.SystemContext;
import baylor.cloudhubs.prophetutils.filemanager.FileManager;
import baylor.cloudhubs.prophetutils.visualizer.LinkAlg;
import baylor.cloudhubs.prophetutils.nativeimage.AnalysisRequest;
import baylor.cloudhubs.prophetutils.nativeimage.MicroserviceInfo;
import baylor.cloudhubs.prophetutils.nativeimage.NativeImageRunner;

import java.io.File;
import java.io.IOException;

import java.util.*;

public class ProphetUtilsFacade {

     private static SystemContext createSystemContext(List<MicroserviceInfo> microservices, String graalProphetHome, String outputDir) {
        Set<Module> modules = new HashSet<>();
        for (MicroserviceInfo info : microservices) {
            NativeImageRunner runner = new NativeImageRunner(info, graalProphetHome, outputDir);
            Module module = runner.runProphetPlugin();
            modules.add(module);
        }
        return new SystemContext(!microservices.isEmpty() ? microservices.get(0).getMicroserviceName() : "unknown", modules);
    }

    public static void runNativeImage(AnalysisRequest analysisRequest, String graalProphetHome){
        String outputFolderName = null;
        List<MicroserviceInfo> microservices = analysisRequest.getMicroservices();
        String systemName = analysisRequest.getSystemName();
        if (systemName == null){
            System.err.println("WARNING: No system name provided in microservices JSON");
            return;
        }
        if (!microservices.isEmpty()){
            
            outputFolderName = "output_" + analysisRequest.getSystemName();
            try {
				createOutputDir(outputFolderName);
                SystemContext ctx = createSystemContext(microservices, graalProphetHome, outputFolderName);
                FileManager.writeToFile(ctx, "./" + outputFolderName + "/system-context.json");
                LinkAlg linkAlgorithm = new LinkAlg();
                linkAlgorithm.calculateLinks("./" + outputFolderName);
			}
            // catch(IOException e){
            //     e.printStackTrace();
            // }
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
