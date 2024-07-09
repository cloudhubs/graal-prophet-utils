package baylor.cloudhubs.prophetutils.visualizer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
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
