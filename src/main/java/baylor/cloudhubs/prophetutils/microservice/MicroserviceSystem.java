package baylor.cloudhubs.prophetutils.microservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class MicroserviceSystem {
    private final List<Microservice> microservices;
    private final String systemName;


    /**
     * If the config contains paths relative to the MS_ROOT env var, resolve them into full paths.
     */
    public void resolveMSroot() {
        var home = Objects.requireNonNull(System.getenv("MS_ROOT"), "MS_ROOT is not set");
        for (var microservice : microservices) {
            if (microservice.getBaseDir().contains("$MS_ROOT")) {
                microservice.setBaseDir(microservice.getBaseDir().replace("$MS_ROOT", home));
            }
        }
    }
}
