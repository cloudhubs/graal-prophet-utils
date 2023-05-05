package baylor.cloudhubs.prophetutils.contextmap;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.HashSet;
import javafx.util.Pair;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Data {

    @SerializedName("name")
    private Name name;

    @SerializedName("entities")
    private Entity[] entities;

    protected HashMap<Pair<String, String>, Integer> mults = new HashMap<>();

    private List<String> tsCommon = new ArrayList<>(Arrays.asList("Account", "AdminTrip", "Assurance", "AssuranceType", "Config", "Consign", "Contacts", "DocumentType", "Food", "FoodOrder",
                                                                "Gender", "LeftTicketInfo", "NotifyInfo", "Order", "OrderAlterInfo", "OrderSecurity", "OrderStatus", "OrderTicketsInfo",
                                                                "PaymentDifferenceInfo", "PriceConfig", "Route", "RouteInfo", "RoutePlanInfo", "RoutePlanResultUnit", "Seat", "SeatClass",
                                                                "SoldTicket", "Station", "StationFoodStore", "Ticket", "TrainFood", "TrainType", "Travel", "TravelInfo", "TravelResult",
                                                                "Trip", "TripAllDetail", "TripAllDetailInfo", "TripId", "TripInfo", "TripResponse", "Type", "User", "VerifyResult"));

    public Name getName(){
        return name;
    }

    public void setName(Name name){
        this.name = name;
    }

    public Entity[] getEntities(){
        return entities;
    }

    public void setEntities(Entity[] entities){
        this.entities = entities;
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

        public void setName(Name name){
            this.entityName = name;
        }

        public Field[] getFields(){
            return fields;
        }

        public void setFields(Field[] fields){
            this.fields = fields;
        }

        @Override
        public String toString(){
            String ret = "\t\t\"nodeName\": \"" + entityName.getName() + "\",\n";
            ret += "\t\t\"nodeFullName\": \"" + entityName.getName() + "\",\n";
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
            return fieldName;
        }

        public void setFieldName(Name fieldName){
            this.fieldName = fieldName;
        }

        public String getType(){
            return type;
        }

        public void setType(String type){
            this.type = type;
        }

        public Annotation[] getAnnotations(){
            return annotations;
        }

        public void setAnnotations(Annotation[] annotations){
            this.annotations = annotations;
        }

        public boolean getIsReference(){
            return isReference;
        }

        public void setIsReference(boolean isReference){
            this.isReference = isReference;
        }

        public String getEntityRefName(){
            return entityRefName;
        }

        public void setEntityRefName(String entityRefName){
            this.entityRefName = entityRefName;
        }

        public boolean getIsCollection(){
            return isCollection;
        }

        public void setIsCollection(boolean isCollection){
            this.isCollection = isCollection;
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

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getStringValue(){
            return stringValue;
        }

        public void setStringValue(String stringValue){
            this.stringValue = stringValue;
        }

        public String getIntValue(){
            return intValue;
        }

        public void setIntValue(String intValue){
            this.intValue = intValue;
        }

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

        public void setName(String name){
            this.name = name;
        }

        public String getFullName(){
            return fullName;
        }

        public void setFullName(String fullName){
            this.fullName = fullName;
        }

        @Override
        public String toString(){
            String ret = "Name: " + name + ", Fullname: " + fullName;
            return ret;
        }
    }
}
