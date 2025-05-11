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
    public String toString() {
        if (entities == null || entities.length == 0) {
            return "";  // return empty if no entities
        }

        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < entities.length; i++) {
            Entity e = entities[i];
            ret.append("\t{\n");
            ret.append("\t\t\"msName\": \"").append(name.getName()).append("\",\n");
            ret.append("\t\t\"nodeName\": \"").append(e.entityName.getName()).append("\",\n");
            ret.append("\t\t\"nodeFullName\": \"").append(e.entityName.getFullName()).append("\",\n");
            ret.append("\t\t\"fields\": [\n");
            for (int j = 0; j < e.fields.length; j++) {
                ret.append(e.fields[j].toString());
                if (j < e.fields.length - 1) ret.append(",\n");
                else ret.append("\n");
            }
            ret.append("\t\t]\n");
            ret.append("\t}");
            if (i < entities.length - 1) ret.append(",\n");
            else ret.append("\n");
        }

        return ret.toString();
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
            StringBuilder ret = new StringBuilder();
            ret.append("\t\t{\n");
            ret.append("\t\t\t\"nodeName\": \"").append(entityName.getName()).append("\",\n");
            ret.append("\t\t\t\"nodeFullName\": \"").append(entityName.getFullName()).append("\",\n");
            ret.append("\t\t\t\"fields\": [\n");

            for(int i = 0; i < fields.length; i++){
                ret.append(fields[i].toString());
                if(i < fields.length - 1) ret.append(",\n");
                else ret.append("\n");
            }

            ret.append("\t\t\t]\n");
            ret.append("\t\t}");
            return ret.toString();
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
            StringBuilder ret = new StringBuilder();
            ret.append("\t\t\t{\n");
            ret.append("\t\t\t\t\"fieldName\": \"").append(fieldName.getName()).append("\",\n");
            ret.append("\t\t\t\t\"fieldFullName\": \"").append(fieldName.getFullName()).append("\",\n");
            ret.append("\t\t\t\t\"fieldType\": \"").append(type).append("\",\n");
            ret.append("\t\t\t\t\"fieldAnnotations\": [\n");

            for(int i = 0; i < annotations.length; i++){
                ret.append(annotations[i].toString());
                if(i < annotations.length - 1){
                    ret.append(",\n");
                } else {
                    ret.append("\n");
                }
            }

            ret.append("\t\t\t\t],\n");
            ret.append("\t\t\t\t\"fieldIsReference\": ").append(isReference).append(",\n");
            ret.append("\t\t\t\t\"fieldEntityRefName\": \"").append(entityRefName).append("\",\n");
            ret.append("\t\t\t\t\"isCollection\": ").append(isCollection).append("\n");
            ret.append("\t\t\t}");

            return ret.toString();
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
