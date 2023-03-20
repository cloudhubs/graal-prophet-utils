package baylor.cloudhubs.prophetutils.nativeimage;

import java.util.List;

public class AnalysisRequest {
    private final List<MicroserviceInfo> microservices;

    public AnalysisRequest(List<MicroserviceInfo> microservices) {
        this.microservices = microservices;
    }

    public List<MicroserviceInfo> getMicroservices() {
        return microservices;
    }
}
