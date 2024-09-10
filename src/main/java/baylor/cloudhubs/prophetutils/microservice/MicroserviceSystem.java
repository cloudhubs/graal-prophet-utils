package baylor.cloudhubs.prophetutils.microservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MicroserviceSystem {
    private final List<Microservice> microservices;
    private final String systemName;


    /**
     * If the config contains paths relative to the MS_ROOT env var, resolve them into full paths.
     */
    public void resolveMSroot() {
        String home = System.getenv("MS_ROOT");
        microservices.stream().filter(microservice -> microservice.getBaseDir().contains("$MS_ROOT")).forEach(microservice -> {
            if (home == null) {
                throw new NullPointerException("MS_ROOT not set, but services in config file define relative paths");
            }
            microservice.setBaseDir(microservice.getBaseDir().replace("$MS_ROOT", home));
        });
    }
}
