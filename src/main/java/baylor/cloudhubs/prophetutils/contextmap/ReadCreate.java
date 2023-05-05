package baylor.cloudhubs.prophetutils.contextmap;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.*;
import java.util.HashSet;
import java.util.Set;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

import baylor.cloudhubs.prophetutils.contextmap.Data.Entity;
import baylor.cloudhubs.prophetutils.contextmap.Data.Field;

public class ReadCreate {

    private static List<Data> dataList = new ArrayList<>();

    private static Data d;

    private static SpecialLinkCollection<Link> listLinks = new SpecialLinkCollection<>();

    private static Set<String> msNames = new HashSet<>();

    private static SpecialLinkCollection<Link> mults = new SpecialLinkCollection<>();

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
                    //System.out.println(d.getName().getName() + " | " + d.getName().getFullName());
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        ConvertToBounded c = new ConvertToBounded();
        if(tsCommonBool){
            dataList.add(convertCommonTXT.convertCommon(pathName.substring(0, pathName.lastIndexOf('/')) + "/config/ts-common.txt"));
        }
        d.setEntities(c.convert(dataList, systemName));
        findLinks(tsCommonBool);

        try{
            FileWriter fileWriter = new FileWriter(pathName + "/entities.json");
            fileWriter.write(write());
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void findLinks(boolean tsCommonBool){
        for(Entity e : d.getEntities()){
            msNames.add(e.getEntityName().getName());
        }
        if(tsCommonBool){
            for(String s : tsCommon){
                msNames.add(s);
            }
        }
        Pattern pattern = Pattern.compile("<(.*?)>");
        for(Entity e : d.getEntities()){
            for(Field f : e.getFields()){
                Matcher matcher = pattern.matcher(f.getType());
                if(msNames.contains(f.getType())){
                    if(mults.contains(new Link(e.getEntityName().getName(), f.getType(), "", ""))){
                        //mults.add(new Link(e.getEntityName().getName(), f.getType(), 
                            //mults.get(new Link(e.getEntityName().getName(), f.getType(), "", "")).getSrcMult().contains("..*") ? String.valueOf(Integer.parseInt(mults.get(new Link(e.getEntityName().getName(), f.getType(), "", "")).getSrcMult().replace("..*", "")) + 1) + "..*" : String.valueOf(Integer.parseInt(mults.get(new Link(e.getEntityName().getName(), f.getType(), "", "")).getSrcMult()) + 1),
                            //mults.get(new Link(e.getEntityName().getName(), f.getType(), "", "")).getTargetMult().contains("..*") ? String.valueOf(Integer.parseInt(mults.get(new Link(e.getEntityName().getName(), f.getType(), "", "")).getTargetMult().replace("..*", "")) + 1) + "..*" : String.valueOf(Integer.parseInt(mults.get(new Link(e.getEntityName().getName(), f.getType(), "", "")).getTargetMult()) + 1)));
                        mults.add(new Link(e.getEntityName().getName(), f.getType(), mults.get(new Link(e.getEntityName().getName(), f.getType())).getSrcMult(), addMults(mults.get(new Link(e.getEntityName().getName(), f.getType())).getTargetMult(), "1")));
                    }else{
                        mults.add(new Link(e.getEntityName().getName(), f.getType(), "0", "1"));
                    }
                }else{
                    while(matcher.find()){
                        String type = matcher.group(1);
                        for(String s : type.split(",")){
                            if(msNames.contains(s) && mults.contains(new Link(e.getEntityName().getName(), s, "", ""))){
                                mults.add(new Link(e.getEntityName().getName(), s, mults.get(new Link(e.getEntityName().getName(), s, "0", "0..*")).getSrcMult(), String.valueOf(Integer.parseInt(mults.get(new Link(e.getEntityName().getName(), s, "", "")).getTargetMult().replace("..*", "")) + 1) + "..*"));
                            }else if(msNames.contains(s)){
                                mults.add(new Link(e.getEntityName().getName(), s, "0", "0..*"));
                            }
                        }
                    }
                }
            }
        }
        for(Link l : mults){
            if(!listLinks.contains(new Link(l.getTarget(), l.getSrc(), "", ""))){
                listLinks.add(l);
            }else{
                Link temp = listLinks.get(new Link(l.getTarget(), l.getSrc(), "", ""));
                temp.setSrcMult(addMults(temp.getSrcMult(), l.getTargetMult()));
                temp.setTargetMult(addMults(temp.getTargetMult(), l.getSrcMult()));
                listLinks.add(temp);
            }
        }
    }

    public static String addMults(String one, String two){
        if(one.contains("..*") || two.contains("..*")){
            return String.valueOf(Integer.parseInt(one.replace("..*", "")) + Integer.parseInt(two.replace("..*", ""))) + "..*";
        }else{
            return String.valueOf(Integer.parseInt(one) + Integer.parseInt(two));
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
        if(listLinks.size() != 0){
            ret = ret.substring(0, ret.length() - 2);
            ret += "\n";
        }
        ret += "\t]\n";
        ret += "}";
        return ret;
    }
}
