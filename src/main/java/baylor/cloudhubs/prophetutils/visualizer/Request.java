package baylor.cloudhubs.prophetutils.visualizer;

import java.util.Objects;

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

    private boolean isPath;
    private boolean isBody;
    private String paramType;
    private int paramCount;


    public Request(String msName, String restCallInClassName, String parentMethod,
                String uri, String httpType, String requestReturn, Boolean isPath, Boolean isBody,
                   String paramType, int paramCount, Boolean isCollection) {
        this.type = httpType;
        this.uri = uri;
        this.requestReturn = requestReturn;
        this.isCollection = isCollection;
        this.msName = msName;
        this.parentMethod = parentMethod;
        this.restCallInClassName = restCallInClassName;
        this.isPath = isPath;
        this.isBody = isBody;
        this.paramType = paramType;
        this.paramCount = paramCount;
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

    public boolean isPath() {
        return isPath;
    }

    public boolean isBody() {
        return isBody;
    }

    public String getParamType() {
        return paramType;
    }

    public int getParamCount() {
        return paramCount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(type, request.type) && Objects.equals(uri, request.uri) && Objects.equals(requestReturn, request.requestReturn) && Objects.equals(endpointFunction, request.endpointFunction) && Objects.equals(endpointMsName, request.endpointMsName) && Objects.equals(targetEndpointUri, request.targetEndpointUri) && Objects.equals(isCollection, request.isCollection) && Objects.equals(parentMethod, request.parentMethod) && Objects.equals(msName, request.msName) && Objects.equals(restCallInClassName, request.restCallInClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, uri, requestReturn, endpointFunction, endpointMsName, targetEndpointUri, isCollection, parentMethod, msName, restCallInClassName);
    }

    @Override
    public String toString() {
        return "Request{\n" +
                "type='" + type + '\'' +
                ", uri='" + uri + '\'' +
                ", requestReturn='" + requestReturn + '\'' +
                ", endpointFunction='" + endpointFunction + '\'' +
                ", endpointMsName='" + endpointMsName + '\'' +
                ", targetEndpointUri='" + targetEndpointUri + '\'' +
                ", isCollection=" + isCollection +
                ", parentMethod='" + parentMethod + '\'' +
                ", msName='" + msName + '\'' +
                ", restCallInClassName='" + restCallInClassName + '\'' +
                "\n}\n";
    }
}
