package baylor.cloudhubs.prophetutils.visualizer;

public class Request {
    private String type;
    private String uri;
    private String requestReturn;
    private String endpointFunction;
    private String endpointMsName;
    private String targetEndpointUri;
    private Boolean isCollection;
    private String parentMethod;
    private String msName;
    private String restCallInClassName;

    public Request(String msName, String restCallInClassName, String parentMethod,
                String uri, String httpType, String requestReturn, Boolean isCollection) {
        this.type = httpType;
        this.uri = uri;
        this.requestReturn = requestReturn;
        this.isCollection = isCollection;
        this.msName = msName;
        this.parentMethod = parentMethod;
        this.restCallInClassName = restCallInClassName;
    }
    
    public String getType() {
        return type;
    }
    public Boolean getIsCollection() {
        return isCollection;
    }
    public String getEndpointMsName() {
        return this.endpointMsName;
    }
    public String getRestCallInClassName() {
        return  this.restCallInClassName;
    }
    public void setType(String httpType) {
        this.type = httpType;
    }
    public String getMsName() {
        return this.msName;
    }
    public String getParentMethod() {
        return parentMethod;
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
    public void setMsName(String msName) {
        this.msName = msName;
    }
    public void setEndpointMsName(String msName) {
        this.endpointMsName = msName;
    }
    
    public void setRestCallInClassName (String name) {
        this.restCallInClassName = name;
    }
    public void setParentMethod(String parentMethod) {
        this.parentMethod = parentMethod;
    }
    public void setRequestReturn(String requestReturn) {
        this.requestReturn = requestReturn;
    }
    public void setIsCollection(Boolean c) {
        this.isCollection = c;
    }
    public String getEndpointFunction() {
        return endpointFunction;
    }

    public void setEndpointFunction(String endpointFunction) {
        this.endpointFunction = endpointFunction;
    }

    public String getTargetEndpoint() {
        return targetEndpointUri;
    }

    public void setTargetEndpoint(String targetEndpoint) {
        this.targetEndpointUri = targetEndpoint;
    }
}
