package baylor.cloudhubs.prophetutils.visualizer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.stream.Collectors;

public class LinkAlg {

    private ArrayList<Link> msLinks;
    private Set<Node> nodes;

    private double dissimilarityPercent;

    private final int ENDPOINT_CSV_SCHEMA_LENGTH = 8;
    private final int RESTCALL_CSV_SCHEMA_LENGTH = 7;

    public LinkAlg() {
        this.dissimilarityPercent = 0.3;
        this.msLinks = new ArrayList<>();
        this.nodes = new HashSet<>();
    }


    // takes similarity percentage as a whole number or integer
    public LinkAlg(int similarityPercentage) {
        this.dissimilarityPercent = 1.0 - (similarityPercentage / 100.0);
        this.msLinks = new ArrayList<>();
        this.nodes = new HashSet<>();
    }

    public void calculateLinks(String dir) throws IOException, InterruptedException {
        // read from output dir and create list of all files *endpoints.csv and *restcalls.csv
        
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
        String linksJsonString = gson.toJson(msLinks);
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

    public ArrayList<Link> getMsLinks() {
        return this.msLinks;
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

    private String addCurlyBraceToURI(String s) {
        String addCurlyStr = s.replaceFirst("\\/$", "/{}").replaceAll("//", "/{}/");
        ArrayList<String> targetList = new ArrayList<String>(Arrays.asList(addCurlyStr.split("/")));

        targetList.remove(0);

        return String.join("/", targetList);
    }

    private void parseRestCalls(File csv, ArrayList<Endpoint> endpoints) throws IOException {
        Map<Request, Endpoint> requestEndpointMap = new HashMap<>();

        // open file readers
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

        // close file
        br.close();
        fileReader.close();

        // loop through parsed requests
        for (Request r : requests) {

            URL uriObj;
            String uri; //only necessary because of final requirement for comparator

            // parse the endpoint path from the request URL
            try {
                uriObj = new URL(r.getUri());
                uri = uriObj.getPath();
            } catch (MalformedURLException ex) {
                uri = r.getUri();
            }

            int minDist = Integer.MAX_VALUE;
            int currDist = -1;
            Endpoint closestMatch = null;
            int lengthOfLongerStr = 0;


            // find the specific endpoint being called
            for (Endpoint e : endpoints) {

                String endpointURI = e.getPath();

//                System.out.println("ms name endpoint: " + e.getMsName());
//                System.out.println("ms name req: " + r.getMsName());
//                System.out.println("ms name req dest: " + r.getEndpointMsName() + "\n");

//                System.out.println("Pre processed: " + uri);
                String restCallURI = addCurlyBraceToURI(uri);
//                System.out.println("Post processed: " + restCallURI + "\n");

                currDist = findDistance(endpointURI, restCallURI);
                if (e.getHttpMethod().equals(r.getType()) && !e.getMsName().equals(r.getMsName()) && minDist > currDist) {
                    minDist = currDist;
                    closestMatch = e;
                    lengthOfLongerStr = Math.max(e.getPath().length(), uri.length());
                }
            }

            double percent = lengthOfLongerStr * dissimilarityPercent;

            // add request to endpoint map
            if (closestMatch != null && percent > minDist) {
                requestEndpointMap.put(r, closestMatch);
            }

        }

        // create the links
        for (Map.Entry<Request, Endpoint> reqs : requestEndpointMap.entrySet()) {
            Request r = reqs.getKey();
            Endpoint e = reqs.getValue();

            // create the link
            Link l = new Link(r.getMsName(), e.getMsName(), new ArrayList<>());

            // set missing fields in the request
            r.setEndpointMsName(e.getMsName());
            r.setTargetEndpoint(e.getPath());
            r.setEndpointFunction(e.getParentMethod());

            // if the link doesn't exist add it to the list
            if (!this.msLinks.contains(l)) {
                l.addRequest(r);
                this.msLinks.add(l);
            }
            // if the link does exist, find it then add the request to it
            else {
                this.msLinks
                        .stream()
                        .filter((link) -> link.equals(l))
                        .collect(Collectors.toList())
                        .get(0)
                        .addRequest(r);
            }

        }


    }

    // levenstein algorithm for two strings
    private int findDistance(String a, String b) {
        short d[][] = new short[a.length() + 1][b.length() + 1];

        // Initialising first column:
        for(short i = 0; i <= a.length(); i++)
            d[i][0] = i;

        // Initialising first row:
        for(short j = 0; j <= b.length(); j++)
            d[0][j] = j;

        // Applying the algorithm:
        short insertion, deletion, replacement;
        for(short i = 1; i <= a.length(); i++) {
            for(short j = 1; j <= b.length(); j++) {
                if(a.charAt(i - 1) == (b.charAt(j - 1)))
                    d[i][j] = d[i - 1][j - 1];
                else {
                    insertion = d[i][j - 1];
                    deletion = d[i - 1][j];
                    replacement = d[i - 1][j - 1];

                    // Using the sub-problems
                    d[i][j] = (short) (1 + findMin(insertion, deletion, replacement));
                }
            }
        }

        return d[a.length()][b.length()];
    }

    // Helper function used by findDistance()
    private short findMin(short x, short y, short z) {
        if(x <= y && x <= z)
            return x;
        if(y <= x && y <= z)
            return y;
        else
            return z;
    }

}
