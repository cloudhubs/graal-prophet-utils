package baylor.cloudhubs.prophetutils.contextmap;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import javafx.util.Pair;

public class Data {

    @SerializedName("name")
    private Name name;

    @SerializedName("entities")
    private Entity[] entities;

    protected HashMap<Pair<String, String>, Integer> mults = new HashMap<>();

    public Name getName(){
        return name;
    }

    public Entity[] getEntities(){
        return entities;
    }

    public Data addData(Data d){
        Data data = new Data();

        return data;
    }

    @Override
    public String toString(){
        String ret = "";
        for(Entity e : entities){
            ret += "\t{\n";
            ret += "\t\t\"msName\": \"" + name.getName() + "\",\n";
            ret += e.toString();
            ret += "\t},\n";
        }
        if(entities.length != 0){
            ret = ret.substring(0, ret.length() - 2);
            ret += "\n";
        }
        ret += "\t},\n";
        return ret;
    }

    public class Entity{
        @SerializedName("entityName")
        private Name entityName;

        @SerializedName("fields")
        private Field[] fields;

        public Name getEntityName(){
            return entityName;
        }

        public Field[] getFields(){
            return fields;
        }

        @Override
        public String toString(){
            String ret = "\t\t\"nodeName\": \"" + entityName.getName() + "\",\n";
            ret += "\t\t\"nodeFullName\": \"" + entityName.getFullName() + "\",\n";
            ret += "\t\t\"fields\": [\n";
            for(Field f : fields){
                ret += f.toString();
            }
            if(fields.length != 0){
                ret = ret.substring(0, ret.length() - 2);
                ret += "\n";
            }
            ret += "\t\t]\n";
            return ret;
        }
    }

    public class Field {

        @SerializedName("name")
        private Name fieldName;
    
        @SerializedName("type")
        private String type;

        @SerializedName("annotations")
        private Annotation[] annotations;

        @SerializedName("isReference")
        private boolean isReference;

        @SerializedName("entityRefName")
        private String entityRefName;

        @SerializedName("isCollection")
        private boolean isCollection;

        public Name getFieldName(){
            return name;
        }

        public String getType(){
            return type;
        }
    
        @Override
        public String toString() {
            String ret = "\t\t\t{\n";
            ret += "\t\t\t\t\"fieldName\": \"" + fieldName.getName() + "\",\n";
            ret += "\t\t\t\t\"fieldFullName\": \"" + fieldName.getFullName() + "\",\n";
            ret += "\t\t\t\t\"fieldType\": \"" + type + "\",\n";
            ret += "\t\t\t\t\"fieldAnnotations\": [\n";
            for(Annotation a : annotations){
                ret += a.toString();
                ret += ",\n";
            }
            if(annotations.length != 0){
                ret = ret.substring(0, ret.length() - 2);
            }
            ret += "\n\t\t\t\t],\n";
            ret += "\t\t\t\t\"fieldIsReference\": " + isReference + ",\n";
            ret += "\t\t\t\t\"fieldEntityRefName\": \"" + entityRefName + "\",\n";
            ret += "\t\t\t\t\"isCollection\": " + isCollection + "\n";
            ret += "\t\t\t},\n";
            return ret;
        }
    }

    public class Annotation{
        @SerializedName("name")
        private String name;

        @SerializedName("stringValue")
        private String stringValue;

        @SerializedName("intValue")
        private String intValue;

        @Override
        public String toString(){
            String ret = "\t\t\t\t\t{\n";
            ret += "\t\t\t\t\t\t\"annotation\": \"" + name + "\"\n";
            ret += "\t\t\t\t\t}";
            return ret;
        }
    }

    public class Name {
        @SerializedName("name")
        private String name;
    
        @SerializedName("fullName")
        private String fullName;

        public String getName(){
            return name;
        }

        public String getFullName(){
            return fullName;
        }

        @Override
        public String toString(){
            String ret = "Name: " + name + ", Fullname: " + fullName;
            return ret;
        }
    }
}
