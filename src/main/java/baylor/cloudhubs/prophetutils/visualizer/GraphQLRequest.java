package baylor.cloudhubs.prophetutils.visualizer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class GraphQLRequest {
    private String parentMethod;
    private String returnType;
    private String uri;
    private boolean isCollection;
    private String graphqlCallInClassName;
    private String msName;
    private String param;
    private String document;
    private String arguments;

    public GraphQLRequest(String parentMethod,
                       String returnType, String uri, Boolean isCollection,
                       String graphqlCallInClassName, String msName, String param, String document, String arguments) {

        this.parentMethod = parentMethod;
        this.returnType = returnType;
        this.uri = uri;
        this.isCollection = isCollection;
        this.graphqlCallInClassName = graphqlCallInClassName;
        this.msName = msName;
        this.param = param;
        this.document = document;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "Request{\n" +
                "parentMethod='" + parentMethod + '\'' +
                ", returnType='" + returnType + '\'' +
                ", uri='" + uri + '\'' +
                ", isCollection=" + isCollection +
                ", graphqlCallInClassName='" + graphqlCallInClassName + '\'' +
                ", msName='" + msName + '\'' +
                ", param='" + param + '\'' +
                ", document='" + document + '\'' +
                ", arguments='" + arguments + '\'' +
                "\n}\n";
    }
}
