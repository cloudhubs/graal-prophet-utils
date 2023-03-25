package baylor.cloudhubs.prophetutils.nativeimage;

import java.util.List;

public class AnalysisRequest {
    private final List<MicroserviceInfo> microservices;
    private final String systemName;

    public AnalysisRequest(List<MicroserviceInfo> microservices, String systemName) {
        this.microservices = microservices;
        this.systemName = systemName;
    }
    public String getSystemName(){
        return this.systemName;
    }
    public List<MicroserviceInfo> getMicroservices() {
        return microservices;
    }
}
