package baylor.cloudhubs.prophetutils.contextmap;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.*;
import javafx.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

import baylor.cloudhubs.prophetutils.contextmap.Data.Entity;
import baylor.cloudhubs.prophetutils.contextmap.Data.Field;

public class ReadCreate {

    private String outputDirName;

    private List<Data> dataList = new ArrayList<>();

    private Data d;

    private HashMap<Pair<String, String>, Pair<Pair<Integer, Integer>, Pair<String, String>>> links = new HashMap<>();

    private List<Link> listLinks = new ArrayList<>();

    private HashMap<String, String> msNames = new HashMap<>();

    private HashMap<Pair<String, String>, Pair<Integer, Pair<String, String>>> mults = new HashMap<>();

    private final boolean isTrainTicket;
    private final List<String> tsCommon = new ArrayList<>(Arrays.asList("Account", "AdminTrip", "Assurance", "AssuranceType", "Config", "Consign", "Contacts", "DocumentType", "Food", "FoodOrder",
                                                                "Gender", "LeftTicketInfo", "NotifyInfo", "Order", "OrderAlterInfo", "OrderSecurity", "OrderStatus", "OrderTicketsInfo",
                                                                "PaymentDifferenceInfo", "PriceConfig", "Route", "RouteInfo", "RoutePlanInfo", "RoutePlanResultUnit", "Seat", "SeatClass",
                                                                "SoldTicket", "Station", "StationFoodStore", "Ticket", "TrainFood", "TrainType", "Travel", "TravelInfo", "TravelResult",
                                                                "Trip", "TripAllDetail", "TripAllDetailInfo", "TripId", "TripInfo", "TripResponse", "Type", "User", "VerifyResult"));


    public ReadCreate(String outputDirName, boolean isTrainTicket){
        this.outputDirName = outputDirName;
        this.isTrainTicket = isTrainTicket;
    }

    public void readIn(){

        File directory = new File(this.outputDirName);
        File[] files = directory.listFiles();
        for(File f : files){
            if(f.getName().substring(f.getName().length() - 5).equals(".json") && !f.getName().equals("entities.json") 
            && !f.getName().equals("communicationGraph.json") && !f.getName().equals("system-context.json")){
                try{
                    Gson gson = new Gson();
                    FileReader reader = new FileReader(this.outputDirName + "/" + f.getName());
                    JsonElement json = gson.fromJson(reader, JsonElement.class);
                    reader.close();
                    d = gson.fromJson(json, Data.class);
                    dataList.add(d);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        findLinks();

        try{
            FileWriter fileWriter = new FileWriter(outputDirName + "/entities.json");
            fileWriter.write(this.toString());
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void findLinks(){
        //Successfully pulls out all entity names for ts-travel-service and ts-train-service
        for(Data d : dataList){
            //For all entities in the data, add 
            for(Entity e : d.getEntities()){
                if(!msNames.containsKey(e.getEntityName().getName())){
                    msNames.put(e.getEntityName().getName(), d.getName().getName());
                }
            }
        }
        if (this.isTrainTicket) {
            for (String s : tsCommon) {
                msNames.put(s, "ts-common");
            }
        }
        //Adds all mults to hashmap
        Pattern pattern = Pattern.compile("<(.*?)>");
        for(Data d : dataList){
            for(Entity e : d.getEntities()){
                for(Field f : e.getFields()){
                    Matcher matcher = pattern.matcher(f.getType());

                    if(msNames.containsKey(f.getType())){
                        Pair<String, String> p = new Pair<>(e.getEntityName().getName(), f.getType());
                        if(mults.containsKey(p)){
                            if(mults.get(p).getKey() != -1){
                                mults.put(p, new Pair<>(mults.get(p).getKey() + 1, new Pair<>(d.getName().getName(), msNames.get(f.getType()))));
                            }
                        }else{
                            mults.put(p, new Pair<>(1, new Pair<>(d.getName().getName(), msNames.get(f.getType()))));
                        }
                    }else{
                        while(matcher.find()){
                            String type = matcher.group(1);
                            for(String s : type.split(",")){
                                if(msNames.containsKey(type) && msNames.get(type) != d.getName().getName()){
                                    Pair<String, String> p = new Pair<>(e.getEntityName().getName(), type);
                                    mults.put(p, new Pair<>(-1, new Pair<>(d.getName().getName(), msNames.get(type))));
                                }
                            }
                        }
                    }
                }
            }
        }
        //Combines any mults
        //links: Pair<src, target>, Pair<Pair<srcMult, targetMult>, Pair<msSrc, msTarget>>
        //entry: Pair<src, dest>, Pair<mult, Pair<msSrc, msDest>>
        for(Map.Entry<Pair<String, String>, Pair<Integer, Pair<String, String>>> entry : mults.entrySet()){
            //if there is not a link with the specified src/target and there is not a link with the same target/src
            //basically, a link doesn't exist
            if(!links.containsKey(entry.getKey()) && !links.containsKey(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey()))){
                links.put(entry.getKey(), new Pair<>(new Pair<>(entry.getValue().getKey(), 0), new Pair<>(entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue())));
            //if there is a link with the specified src/target
            }else if(links.containsKey(entry.getKey())){
                links.put(entry.getKey(), new Pair<>(new Pair<>(links.get(entry.getKey()).getKey().getKey() + 1, 0), new Pair<>(entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue())));
            //if there is a link with the specified target/src
            }else if(links.containsKey(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey()))){
                links.put(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey()), new Pair<>(
                    new Pair<>(links.get(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey())).getKey().getKey(), entry.getValue().getKey()), 
                    new Pair<>(entry.getValue().getValue().getValue(), entry.getValue().getValue().getKey())));
            }
        }
        //add the links to listLinks
        //entry: src, target, msSrc, msTarget, srcMult, targetMult
        //link: src, target, srcMult, targetMult, msSource, msTarget
        for(Map.Entry<Pair<String, String>, Pair<Pair<Integer, Integer>, Pair<String, String>>> entry : links.entrySet()){
            if(entry.getValue().getKey().getKey() == -1 && entry.getValue().getKey().getValue() == -1){
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), "0..*", "0..*", entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue()));
            }else if(entry.getValue().getKey().getKey() == -1){
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), "0..*", entry.getValue().getKey().getValue().toString(), entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue()));
            }else if (entry.getValue().getKey().getValue() == -1){
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), entry.getValue().getKey().getKey().toString(), "0..*", entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue()));
            }else{
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), entry.getValue().getKey().getKey().toString(), entry.getValue().getKey().getValue().toString(), entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue()));
            }
        }
    }

    @Override
    public String toString(){
        String ret = "{\n";
        ret += "\t\"nodes\": [\n";
        if (this.isTrainTicket) {
            for (String s : tsCommon) {
                ret += "\t{\n";
                ret += "\t\t\"msName\": \"ts-common\",\n";
                ret += "\t\t\"nodeName\": \"" + s + "\",\n";
                ret += "\t\t\"nodeFullName\": \"" + s + "\",\n";
                ret += "\t\t\"fields\": [\n\t\t]\n\t},\n";
            }
        }
        for(Data data : dataList){
            ret += data.toString();
            ret = ret.substring(0, ret.length() - 5);
            if(!ret.endsWith(",")){
                ret += ",\n";
            }
        }
        ret = ret.substring(0, ret.length() - 2);
        ret += "\t],\n";
        ret += "\"links\": [\n";
        for(Link l : listLinks){
            ret += l.toString();
        }
        if(links.size() != 0){
            ret = ret.substring(0, ret.length() - 2);
            ret += "\n";
        }
        ret += "\t]\n";
        ret += "}";
        return ret;
    }
}
