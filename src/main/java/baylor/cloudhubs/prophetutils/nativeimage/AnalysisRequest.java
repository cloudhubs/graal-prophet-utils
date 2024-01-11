package baylor.cloudhubs.prophetutils.nativeimage;

import baylor.cloudhubs.prophetutils.Env;

import java.util.List;

import static baylor.cloudhubs.prophetutils.Env.PROPHET_PLUGIN_HOME_ENV;

public class AnalysisRequest {
    private final List<MicroserviceInfo> microservices;
    private final String systemName;

    public AnalysisRequest(List<MicroserviceInfo> microservices, String systemName) {
        this.microservices = microservices;
        this.systemName = systemName;
    }

    public String getSystemName() {
        return this.systemName;
    }

    public List<MicroserviceInfo> getMicroservices() {
        return microservices;
    }


    /**
     * If the config contains paths relative to the PROPHET_PLUGIN_HOME env var, resolve them into full paths.
     */
    public void resolveProphetHome() {
        var home = Env.load(PROPHET_PLUGIN_HOME_ENV);
        for (var microservice : microservices) {
            if (microservice.getBaseDir().contains("$" + PROPHET_PLUGIN_HOME_ENV)) {
                microservice.setBaseDir(microservice.getBaseDir().replace("$" + PROPHET_PLUGIN_HOME_ENV, home));
            }
        }
    }
}
