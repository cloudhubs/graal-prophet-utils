package baylor.cloudhubs.prophetutils.nativeimage;

import java.util.List;
import java.util.Objects;

public class MicroserviceSystem {
    private final List<Microservice> microservices;
    private final String systemName;

    public MicroserviceSystem(List<Microservice> microservices, String systemName) {
        this.microservices = microservices;
        this.systemName = systemName;
    }

    public String getSystemName() {
        return this.systemName;
    }

    public List<Microservice> getMicroservices() {
        return microservices;
    }


    /**
     * If the config contains paths relative to the PROPHET_PLUGIN_HOME env var, resolve them into full paths.
     */
    public void resolveProphetHome() {
        var home = Objects.requireNonNull(System.getenv("PROPHET_PLUGIN_HOME"), "PROPHET_PLUGIN_HOME is not set");
        for (var microservice : microservices) {
            if (microservice.getBaseDir().contains("$PROPHET_PLUGIN_HOME")) {
                microservice.setBaseDir(microservice.getBaseDir().replace("$PROPHET_PLUGIN_HOME", home));
            }
        }
    }
}
