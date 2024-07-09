package baylor.cloudhubs.prophetutils.visualizer;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Endpoint {
    private String httpMethod;
    private String parentMethod;
    private List<String> arguments;
    private String returnType;
    private String path;
    private boolean isCollection;
    private String endpointInClassName;
    private String msName;

    private String toStringModified(List<String> args){
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < args.size(); i++){
            String str = args.get(i);
            sb.append(str.replaceAll(" ", "_"));
            if (i < args.size() - 1){
                sb.append("&");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Endpoint{\n" +
                "httpMethod='" + httpMethod + '\'' +
                ", parentMethod='" + parentMethod + '\'' +
                ", arguments=" + arguments +
                ", returnType='" + returnType + '\'' +
                ", path='" + path + '\'' +
                ", isCollection=" + isCollection +
                ", endpointInClassName='" + endpointInClassName + '\'' +
                ", msName='" + msName + '\'' +
                "\n}\n";
    }
}
