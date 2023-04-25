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

    private List<Data> dataList = new ArrayList<>();

    private Data d;

    private HashMap<Pair<String, String>, Pair<Integer, Integer>> links = new HashMap<>();

    private List<Link> listLinks = new ArrayList<>();

    private HashMap<String, String> msNames = new HashMap<>();

    private HashMap<Pair<String, String>, Integer> mults = new HashMap<>();

    private List<String> tsCommon = new ArrayList<>(Arrays.asList("Account", "AdminTrip", "Assurance", "AssuranceType", "Config", "Consign", "Contacts", "DocumentType", "Food", "FoodOrder",
                                                                "Gender", "LeftTicketInfo", "NotifyInfo", "Order", "OrderAlterInfo", "OrderSecurity", "OrderStatus", "OrderTicketsInfo",
                                                                "PaymentDifferenceInfo", "PriceConfig", "Route", "RouteInfo", "RoutePlanInfo", "RoutePlanResultUnit", "Seat", "SeatClass",
                                                                "SoldTicket", "Station", "StationFoodStore", "Ticket", "TrainFood", "TrainType", "Travel", "TravelInfo", "TravelResult",
                                                                "Trip", "TripAllDetail", "TripAllDetailInfo", "TripId", "TripInfo", "TripResponse", "Type", "User", "VerifyResult"));

    public void readIn(){
        String pathName = "/home/jack/Capstone/graal-prophet-utils/output_trainticket";
        File directory = new File(pathName);
        File[] files = directory.listFiles();
        for(File f : files){
            if(f.getName().substring(f.getName().length() - 5).equals(".json") && !f.getName().equals("entities.json") 
            && !f.getName().equals("communicationGraph.json") && !f.getName().equals("system-context.json")){
                try{
                    Gson gson = new Gson();
                    FileReader reader = new FileReader(pathName + "/" + f.getName());
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
            FileWriter fileWriter = new FileWriter("/home/jack/Capstone/graal-prophet-utils/output_trainticket/entities.json");
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
            //System.out.println("---" + d.getName().getName() + "---");
            for(Entity e : d.getEntities()){
                if(!msNames.containsKey(e.getEntityName().getName())){
                    //System.out.println(e.getEntityName().getName());
                    msNames.put(e.getEntityName().getName(), d.getName().getName());
                }
            }
        }
        for(String s : tsCommon){
            msNames.put(s, "ts-common");
        }
        //Adds all mults to hashmap
        Pattern pattern = Pattern.compile("<(.*?)>");
        for(Data d : dataList){
            for(Entity e : d.getEntities()){
                for(Field f : e.getFields()){
                    Matcher matcher = pattern.matcher(f.getType());
                    if(msNames.containsKey(f.getType()) && msNames.get(f.getType()) != d.getName().getName()){
                        //System.out.println(e.getEntityName().getName() + " | " + f.getType());
                        Pair<String, String> p = new Pair<>(e.getEntityName().getName(), f.getType());
                        if(mults.containsKey(p)){
                            if(mults.get(p) != -1){
                                mults.put(p, mults.get(p) + 1);
                            }
                        }else{
                            mults.put(p, 1);
                        }
                    }else{
                        while(matcher.find()){
                            String type = matcher.group(1);
                            for(String s : type.split(",")){
                                //System.out.println(s + " | " + d.getName().getName());
                                if(msNames.containsKey(type) && msNames.get(type) != d.getName().getName()){
                                    System.out.println(type);
                                    Pair<String, String> p = new Pair<>(e.getEntityName().getName(), type);
                                    mults.put(p, -1);
                                }
                            }
                        }
                    }
                }
            }
        }
        //Combines any mults
        for(Map.Entry<Pair<String, String>, Integer> entry : mults.entrySet()){
            if(!links.containsKey(entry.getKey()) && !links.containsKey(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey()))){
                links.put(entry.getKey(), new Pair<>(entry.getValue(), 0));
            }else if(links.containsKey(entry.getKey())){
                Pair<Integer, Integer> p = new Pair<>(links.get(entry.getKey()).getKey(), entry.getValue());
                links.put(entry.getKey(), p);
            }else if(links.containsKey(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey()))){
                Pair<Integer, Integer> p = new Pair<>(links.get(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey())).getKey(), entry.getValue());
                links.put(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey()), p);
            }
        }
        for(Map.Entry<Pair<String, String>, Pair<Integer, Integer>> entry : links.entrySet()){
            if(entry.getValue().getKey() == -1 && entry.getValue().getValue() == -1){
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), "0..*", "0..*"));
            }else if(entry.getValue().getKey() == -1){
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), "0..*", entry.getValue().getValue().toString()));
            }else if (entry.getValue().getValue() == -1){
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), entry.getValue().getKey().toString(), "0..*"));
            }else{
                listLinks.add(new Link(entry.getKey().getKey(), entry.getKey().getValue(), entry.getValue().getKey().toString(), entry.getValue().getValue().toString()));
            }
        }
    }

    @Override
    public String toString(){
        String ret = "{\n";
        ret += "\t\"node\": [\n";
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
