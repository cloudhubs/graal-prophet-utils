package baylor.cloudhubs.prophetutils;

import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.Module;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;

import java.util.*;

import baylor.cloudhubs.prophetutils.nativeimage.MicroserviceInfo;
import baylor.cloudhubs.prophetutils.nativeimage.NativeImageRunner;

public class ProphetUtilsFacade {


    public static SystemContext getSystemContextViaNativeImage(List<MicroserviceInfo> msFullPaths, String graalProphetHome) {
        Set<Module> modules = new HashSet<>();
        for (MicroserviceInfo info : msFullPaths) {
            NativeImageRunner runner = new NativeImageRunner(info, graalProphetHome);
            Module module = runner.runProphetPlugin();
            modules.add(module);
        }
        return new SystemContext("unknown", modules);
    }
    
}
