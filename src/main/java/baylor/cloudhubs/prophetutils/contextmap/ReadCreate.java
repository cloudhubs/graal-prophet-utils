package baylor.cloudhubs.prophetutils.contextmap;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.*;
import javafx.util.Pair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

import baylor.cloudhubs.prophetutils.contextmap.Data.Entity;
import baylor.cloudhubs.prophetutils.contextmap.Data.Field;

public class ReadCreate {

    private static List<Data> dataList = new ArrayList<>();

    private static Data d;

    private static HashMap<Pair<String, String>, Pair<Pair<Integer, Integer>, Pair<String, String>>> links = new HashMap<>();

    private static List<Link> listLinks = new ArrayList<>();

    private static List<Link> listLinks2 = new ArrayList<>();

    private static HashMap<String, String> msNames = new HashMap<>();

    private static HashMap<Pair<String, String>, Pair<Integer, Pair<String, String>>> mults = new HashMap<>();

    private static List<Link> mults2 = new ArrayList<>();

    private static List<Link> combinedMults = new ArrayList<>();

    //This is a list of Entities within ts-common, which cannot be parsed because of the kind of JAR file which
    //is created
    private static List<String> tsCommon = new ArrayList<>(Arrays.asList("Account", "AdminTrip", "Assurance", "AssuranceType", "Config", "Consign", "Contacts", "DocumentType", "Food", "FoodOrder",
                                                                "Gender", "LeftTicketInfo", "NotifyInfo", "Order", "OrderAlterInfo", "OrderSecurity", "OrderStatus", "OrderTicketsInfo",
                                                                "PaymentDifferenceInfo", "PriceConfig", "Route", "RouteInfo", "RoutePlanInfo", "RoutePlanResultUnit", "Seat", "SeatClass",
                                                                "SoldTicket", "Station", "StationFoodStore", "Ticket", "TrainFood", "TrainType", "Travel", "TravelInfo", "TravelResult",
                                                                "Trip", "TripAllDetail", "TripAllDetailInfo", "TripId", "TripInfo", "TripResponse", "Type", "User", "VerifyResult"));

    public static void readIn(String pathName, boolean tsCommonBool, String systemName){
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
        /*int entityNumber = 0;
        for(Data d : dataList){
            entityNumber += d.getEntities().length;
        }
        entityNumber += tsCommon.size();
        System.out.println("Total entities: " + entityNumber);
        findLinks(tsCommonBool);*/
        ConvertToBounded.convert(dataList, systemName);

        try{
            FileWriter fileWriter = new FileWriter(pathName + "/entities.json");
            fileWriter.write(write());
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void findLinks(boolean tsCommonBool){
        //Successfully pulls out all entity names for ts-travel-service and ts-train-service
        for(Data d : dataList){
            //For all entities in the data, add 
            for(Entity e : d.getEntities()){
                if(!msNames.containsKey(e.getEntityName().getName())){
                    msNames.put(e.getEntityName().getName(), d.getName().getName());
                }
            }
        }
        //We only insert the ts-common entities manually if the user specifies to
        if(tsCommonBool){
            for(String s : tsCommon){
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
                                if(msNames.containsKey(s) && msNames.get(s) != d.getName().getName()){
                                    Pair<String, String> p = new Pair<>(e.getEntityName().getName(), s);
                                    mults.put(p, new Pair<>(-1, new Pair<>(d.getName().getName(), msNames.get(s))));
                                }
                            }
                        }
                    }
                }
            }
        }
        /*Pattern pattern = Pattern.compile("<(.*?)>");
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
                        Link temp = new Link(e.getEntityName().getName(), f.getType());
                        if(mults2.contains(temp)){
                            if(mults2.get(mults2.indexOf(temp)).getSrcMult() != "-1"){
                                mults2.add(new Link(e.getEntityName().getName(), f.getType(), String.valueOf(Integer.parseInt(mults2.get(mults2.indexOf(temp)).getSrcMult()) + 1), d.getName().getName(), msNames.get(f.getType()), null));
                                //System.out.println("adding");
                            }else{
                                //TODO
                            }
                        }else{
                            mults2.add(new Link(e.getEntityName().getName(), f.getType(), "1", d.getName().getName(), msNames.get(f.getType()), null));
                            //System.out.println("adding");
                        }
                    }else{
                        while(matcher.find()){
                            String type = matcher.group(1);
                            for(String s : type.split(",")){
                                if(msNames.containsKey(s) && msNames.get(s) != d.getName().getName()){
                                    Pair<String, String> p = new Pair<>(e.getEntityName().getName(), s);
                                    //mults.put(p, new Pair<>(-1, new Pair<>(d.getName().getName(), msNames.get(s))));
                                    mults2.add(new Link(e.getEntityName().getName(), s, "-1", d.getName().getName(), msNames.get(s), null));
                                }
                            }
                        }
                    }
                }
            }
        }*/
        //for(Link l : mults2){
            //System.out.println(l.getMsSrc() + " " + l.getMsTarget());
        //}
        //Combines any mults
        //links: Pair<src, target>, Pair<Pair<srcMult, targetMult>, Pair<msSrc, msTarget>>
        //entry: Pair<src, dest>, Pair<mult, Pair<msSrc, msDest>>
        /*for(int i = 0; i < mults2.size(); i++){
            for(int j = i; j < mults2.size(); j++){
                if(mults2.get(i).equals(mults2.get(j))){
                    Link temp = mults2.get(i);
                    temp.addSrcMult(mults2.get(i).getSrcMult(), mults2.get(j).getSrcMult());
                    temp.addTargetMult(mults2.get(i).getTargetMult(), mults2.get(j).getTargetMult());
                    listLinks2.add(temp);
                }else if(mults2.get(i).isInverse(mults2.get(j))){
                    Link temp = mults2.get(i);
                    temp.addSrcMult(mults2.get(j).getSrcMult(), mults2.get(i).getSrcMult());
                    temp.addTargetMult(mults2.get(j).getTargetMult(), mults2.get(i).getTargetMult());
                    listLinks2.add(temp);
                }
            }
            if(!listLinks2.contains(mults2.get(i))){
                listLinks2.add(mults2.get(i));
            }
        }
        //int counter = 1;
        for(Link l : listLinks2){
            //System.out.println(counter);
            //counter++;
            //System.out.println(l.getMsSrc() + " " + l.getMsTarget());
        }*/
        for(Map.Entry<Pair<String, String>, Pair<Integer, Pair<String, String>>> entry : mults.entrySet()){
            //System.out.println("Entry: Src: " + entry.getKey().getKey() + ", Dest: " + entry.getKey().getValue() + ", Mult: " + entry.getValue().getKey() + ", MSSrc: " + entry.getValue().getValue().getKey() + ", MSDest: " + entry.getValue().getValue().getKey());
            //if there is a link with the specified src/target && specified msSrc/msTarget
            if(links.containsKey(entry.getKey()) && links.get(entry.getKey()).getValue() != entry.getValue().getValue()){  //I think
                links.put(entry.getKey(), new Pair<>(new Pair<>(links.get(entry.getKey()).getKey().getKey() + 1, 0), new Pair<>(entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue())));
            //if there is a link with the specified target/src && specified msTarget/msSrc
            }else if(links.containsKey(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey())) && links.get(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey())).getValue() != new Pair<>(entry.getValue().getValue().getValue(), entry.getValue().getValue().getKey())){
                links.put(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey()), new Pair<>(
                    new Pair<>(links.get(new Pair<>(entry.getKey().getValue(), entry.getKey().getKey())).getKey().getKey(), entry.getValue().getKey()), 
                    new Pair<>(entry.getValue().getValue().getValue(), entry.getValue().getValue().getKey())));
            //if there is not a link with the specified src/target && msSrc/msTarget and there is not a link with the same target/src && msTarget/msSrc
            //basically, a link doesn't exist
            }else{
                links.put(entry.getKey(), new Pair<>(new Pair<>(entry.getValue().getKey(), 0), new Pair<>(entry.getValue().getValue().getKey(), entry.getValue().getValue().getValue())));
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

    public static String write(){
        String ret = "{\n";
        ret += "\t\"nodes\": [\n";
        for(String s : tsCommon){
            ret += "\t{\n";
            ret += "\t\t\"msName\": \"ts-common\",\n";
            ret += "\t\t\"nodeName\": \"" + s + "\",\n";
            ret += "\t\t\"nodeFullName\": \"" + s + "\",\n";
            ret += "\t\t\"fields\": [\n\t\t]\n\t},\n";
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
