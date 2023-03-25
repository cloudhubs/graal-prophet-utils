package baylor.cloudhubs.prophetutils.visualizer;

import java.util.List;
import java.util.Objects;

public class Endpoint {
    private String httpMethod;
    private String parentMethod;
    private List<String> arguments;
    private String returnType;
    private String path;
    private boolean isCollection;
    private String endpointInClassName;
    private String msName;

    public Endpoint(String httpMethod, String parentMethod, List<String> args, 
                    String returnType, String path, Boolean isCollection, 
                    String endpointInClassName, String msName) {

        this.httpMethod = httpMethod;
        this.parentMethod = parentMethod;
        this.arguments = args;
        this.returnType = returnType;
        this.path = path;
        this.isCollection = isCollection;
        this.endpointInClassName = endpointInClassName;
        this.msName = msName;
    }

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
    // Getter methods
    public String getHttpMethod() {
        return httpMethod;
    }
    public String getMsName() {
        return this.msName;
    }
    public String getEndpointInClassName() {
        return this.endpointInClassName;
    }
    public String getParentMethod() {
        return parentMethod;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getPath() {
        return path;
    }

    public boolean isCollection() {
        return isCollection;
    }

    // Setter methods
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    public void setMsName(String msName) {
        this.msName = msName;
    }
    public void setEndpointInClassName(String className) {
        this.endpointInClassName = className;
    }
    public void setParentMethod(String parentMethod) {
        this.parentMethod = parentMethod;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return isCollection == endpoint.isCollection && Objects.equals(httpMethod, endpoint.httpMethod) && Objects.equals(parentMethod, endpoint.parentMethod) && Objects.equals(arguments, endpoint.arguments) && Objects.equals(returnType, endpoint.returnType) && Objects.equals(path, endpoint.path) && Objects.equals(endpointInClassName, endpoint.endpointInClassName) && Objects.equals(msName, endpoint.msName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, parentMethod, arguments, returnType, path, isCollection, endpointInClassName, msName);
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
