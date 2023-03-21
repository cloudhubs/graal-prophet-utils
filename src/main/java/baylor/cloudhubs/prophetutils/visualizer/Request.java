package baylor.cloudhubs.prophetutils.visualizer;

public class Request {
    private String httpType;
    private String uri;
    private String requestReturn;
    private String endpointFunction;
    private String targetEndpoint;
    
    public Request(String httpType, String uri, String requestReturn, String endpointFunction, String targetEndpoint) {
        this.httpType = httpType;
        this.uri = uri;
        this.requestReturn = requestReturn;
        this.endpointFunction = endpointFunction;
        this.targetEndpoint = targetEndpoint;
    }
    
    public String getHttpType() {
        return httpType;
    }

    public void setHttpType(String httpType) {
        this.httpType = httpType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRequestReturn() {
        return requestReturn;
    }

    public void setRequestReturn(String requestReturn) {
        this.requestReturn = requestReturn;
    }

    public String getEndpointFunction() {
        return endpointFunction;
    }

    public void setEndpointFunction(String endpointFunction) {
        this.endpointFunction = endpointFunction;
    }

    public String getTargetEndpoint() {
        return targetEndpoint;
    }

    public void setTargetEndpoint(String targetEndpoint) {
        this.targetEndpoint = targetEndpoint;
    }
}
