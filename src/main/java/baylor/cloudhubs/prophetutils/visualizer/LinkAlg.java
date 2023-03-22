package baylor.cloudhubs.prophetutils.visualizer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.swing.SpinnerDateModel;

import com.google.gson.Gson;
import java.io.IOException;

public class LinkAlg {

    private Map<String, Link> msMaptoLinks = new HashMap<>();
    private Set<Node> nodes = new HashSet<>();

    private int ENDPOINT_CSV_SCHEMA_LENGTH = 8;
    private int RESTCALL_CSV_SCHEMA_LENGTH = 7;

    public void calculateLinks(String dir) throws IOException, InterruptedException {
        // read from output dir and create list of all files *endpoints.csv and *restcalls.csv
        // File outputDir = new File("../../../../../../../output");
        File outputDir = new File(dir);
        File[] files = outputDir.listFiles();
        ArrayList<Endpoint> endpoints = new ArrayList<>();

        // filter and parse the correct files
        for (File f : files) {
            if (f.getName().endsWith("_endpoints.csv")) {
                endpoints.addAll(parseEndpoints(f));
            }
        }

        for (File f : files) {
            if (f.getName().endsWith("_restcalls.csv")) {
                parseRestCalls(f, endpoints);
            }
        }
        // Set<String> tempNodeSet = msMaptoLinks.keySet();
        // for (String s : tempNodeSet){
        //     nodes.add(new Node(s));
        // }
        Gson gson = new Gson();

        String nodesJsonString = gson.toJson(nodes);
        nodesJsonString.replaceFirst("\\[\\{", "");
        nodesJsonString.substring(0, nodesJsonString.length() - 2); //remove "}]"
        String linksJsonString = gson.toJson(msMaptoLinks.values());
        linksJsonString.replaceFirst("\\[\\{", "");
        linksJsonString.substring(0, linksJsonString.length() - 2); //remove "}]"
        String combinedJson = "{\"nodes\": " + nodesJsonString + ", \"links\": " + linksJsonString + "}";
        // System.out.println("COMBINED JSON =\n\n" + combinedJson);
        
        try (FileWriter fileWriter = new FileWriter(dir + "/communicationGraph.json")) {
            fileWriter.write(combinedJson);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }

    }

    public Map<String, Link> getMsMapToLinks() {
        return this.msMaptoLinks;
    }

    private ArrayList<Endpoint> parseEndpoints(File csv) throws IOException {
        FileReader fileReader = new FileReader(csv);
        BufferedReader br = new BufferedReader(fileReader);
        
        ArrayList<Endpoint> endpoints = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");
            // if (items.length != ENDPOINT_CSV_SCHEMA_LENGTH){
            //     br.close();
            //     throw new RuntimeException("Endpoint line parsed does not have " + ENDPOINT_CSV_SCHEMA_LENGTH + " items");
            // }
            
            // CSV SCHEMA
            //   0   ,        1          ,       2     ,    3  ,      4    ,     5  ,    6      ,      7
            //msName, endpointInClassName, parentMethod, arguments, path, httpMethod, returnType, isCollection
            // System.out.println("items = ");
            // for (String s : items){
            //     System.out.println("\t" + s);
            // }
            Endpoint end = new Endpoint(
                items[5],
                items[2],
                Arrays.asList(items[3].split("&")),
                items[6],
                items[4],
                Boolean.parseBoolean(items[7]),
                items[1],
                items[0]
            );
            endpoints.add(end);
            //ADD ENDPOINT MS 
            this.nodes.add(new Node(end.getMsName()));
        }
        br.close();

        return endpoints;
    }

    private void parseRestCalls(File csv, ArrayList<Endpoint> endpoints) throws IOException {
        Map<String, ArrayList<Request>> reqMaptoServices = new HashMap<>();

        FileReader fileReader = new FileReader(csv);
        BufferedReader br = new BufferedReader(fileReader);

        // CSV SCHEMA
        // 0   ,         1             ,     2   ,   3    ,     4   ,    5     ,     6,    
        //msName, restCallInClassName, parentMethod, uri, httpMethod, returnType, isCollection

        ArrayList<Request> requests = new ArrayList<>();

        // read in csv and make requests
        String line;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");

            if (items.length != RESTCALL_CSV_SCHEMA_LENGTH){
                br.close();
                throw new RuntimeException("Restcall line parsed does not have " + RESTCALL_CSV_SCHEMA_LENGTH + " items");
            }
            Request req = new Request(items[0], items[1], items[2], items[3], items[4], items[5], Boolean.parseBoolean(items[6]));
            //ADD REQUEST MS 
            this.nodes.add(new Node(req.getMsName()));
            requests.add(req);
        }
        
        br.close();
        fileReader.close();

        for (Request r : requests) {
            // parse the endpoint path from the request objects URI

            URL uri = null;
            String temp = null; //only necessary because of final requirement for comparator
            try{
                uri = new URL(r.getUri());
                temp = uri.getPath();

            }catch(MalformedURLException ex){
                temp = r.getUri();
            }
            String reqEndpointString = temp.replaceAll("//", "/");
            List<Endpoint> endpointContains = new ArrayList<>();

            Endpoint exactMatch = null;
            String regex = ".*\\{[^}]*\\}.*"; //removes the curly braces and its contents from endpoints

            for (Endpoint e : endpoints) {
                // String premoddedPath = e.getPath();
                // e.setPath(e.getPath().replaceAll(regex, ""));
                // System.out.println("modified path = " + e.getPath());

                if (r.getType().equals(e.getHttpMethod()) && !(r.getMsName().equals(e.getMsName()))) {

                    if (reqEndpointString.equals(r.getUri())) {
                        exactMatch = e;
                        break;
                    }

                    if (reqEndpointString.contains(e.getPath()) || e.getPath().contains(reqEndpointString)) {
                        endpointContains.add(e);
                    }

                }
            }

            if (exactMatch == null && !endpointContains.isEmpty()) {

                endpointContains.sort(new Comparator<Endpoint>() {
                    @Override
                    public int compare(Endpoint o1, Endpoint o2) {
                
                        boolean o1Has = o1.getPath().endsWith(reqEndpointString);
                        boolean rHaso1 = reqEndpointString.endsWith(o1.getPath());

                        boolean o2Has = o2.getPath().endsWith(reqEndpointString);
                        boolean rHaso2 = reqEndpointString.endsWith(o2.getPath());

                        if( (o1Has && !o2Has) || (rHaso1 && !rHaso2)) return -1;
                        else if( (o2Has && !o1Has) || (rHaso2 && !rHaso1)) return 1;
                        else if( (o1Has && o2Has) || (rHaso2 && rHaso1)) return 0;
                        else
                            return o1.getPath().length() - o2.getPath().length();
                    }
                });
                System.out.println("rest call = " + reqEndpointString);
                for (Endpoint test : endpointContains){
                    System.out.println(test.getPath() +", " + test.getParentMethod());
                }
                exactMatch = endpointContains.get(0);  
            }
            if (exactMatch != null){
                r.setEndpointFunction(exactMatch.getParentMethod());
                r.setTargetEndpoint(exactMatch.getPath());
                r.setEndpointMsName(exactMatch.getMsName());
                
                reqMaptoServices.putIfAbsent(r.getMsName(), new ArrayList<Request>());
                reqMaptoServices.get(r.getMsName()).add(r);

                // System.out.println("REQ = " + reqEndpointString + ", " + r.getParentMethod() + ", EXACT MATCH = " + exactMatch.getPath() + ", method = " + exactMatch.getParentMethod());
            }
        }
        for (Map.Entry<String,ArrayList<Request>> entry : reqMaptoServices.entrySet()) {
            //entry.getValue() the array it returns should have at least one item in it
            //cms
            msMaptoLinks.put(entry.getKey(), new Link( entry.getKey(), entry.getValue().get(0).getEndpointMsName(), entry.getValue()));
        }
    }
}
