package baylor.cloudhubs.prophetutils.contextmap;

public enum Type {
    BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, OBJECT;

    public static Type get(String name){
        switch(name.toLowerCase()){
            case "byte":
                return BYTE;
            case "char":
                return  CHAR;
            case "short":
                return SHORT;
            case "int":
            case "integer":
                return INT;
            case "long":
                return LONG;
            case "float":
                return FLOAT;
            case "double":
                return DOUBLE;
            case "string":
                return STRING;
            case "object":
            default:
                return OBJECT;

        }
    }
}