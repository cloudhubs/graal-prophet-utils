package baylor.cloudhubs.prophetutils.visualizer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WebSocketConnection {

    private String msName;
    private String connectionInClassName;
    private String parentMethod;
    private String uri;
    private String httpMethod;
    private String returnType;
    private boolean isCollection;

    public WebSocketConnection(String msName, String connectionInClassName, String parentMethod, String uri, String returnType, String httpMethod, boolean isCollection) {
        this.msName = msName;
        this.connectionInClassName = connectionInClassName;
        this.parentMethod = parentMethod;
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.returnType = returnType;
        this.isCollection = isCollection;
    }

    public boolean isCollection() {
        return isCollection;
    }
}