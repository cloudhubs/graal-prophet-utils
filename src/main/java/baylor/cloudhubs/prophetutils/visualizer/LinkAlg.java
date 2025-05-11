/**
 * Authors:
 * - Original Authors
 * - Vsevolod Pokhvalenko
 */

package baylor.cloudhubs.prophetutils.visualizer;

import baylor.cloudhubs.prophetutils.microservice.Microservice;
import com.google.gson.Gson;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class LinkAlg {

    private ArrayList<Link> msLinks;
    private Set<Node> nodes;

    private double dissimilarityPercent;

    private final int ENDPOINT_CSV_SCHEMA_LENGTH = 8;
    private final int RESTCALL_CSV_SCHEMA_LENGTH = 7;

    public LinkAlg(List<Microservice> microservices) {
        this.dissimilarityPercent = 0.3;
        this.msLinks = new ArrayList<>();
        this.nodes = new HashSet<>();
        for (Microservice mi : microservices) {
            nodes.add(new Node(mi.getMicroserviceName()));
        }
    }

    /**
     * Parses a CSV file containing WebSocket connection data and returns a list of WebSocketConnection objects.
     *
     * @param csv The CSV file to parse.
     * @return An ArrayList of WebSocketConnection objects parsed from the file.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private ArrayList<WebSocketConnection> parseWebSocketConnections(File csv) throws IOException {
        FileReader fileReader = new FileReader(csv);
        BufferedReader br = new BufferedReader(fileReader);

        ArrayList<WebSocketConnection> webSocketConnections = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");
            WebSocketConnection connection = new WebSocketConnection(
                    items[0],
                    items[1],
                    items[2],
                    items[3],
                    items[4],
                    items[5],
                    Boolean.parseBoolean(items[6])
            );
            webSocketConnections.add(connection);
        }
        br.close();

        return webSocketConnections;
    }


    // takes similarity percentage as a whole number or integer
    public LinkAlg(int similarityPercentage, List<Microservice> microservices) {
        this.dissimilarityPercent = 1.0 - (similarityPercentage / 100.0);
        this.msLinks = new ArrayList<>();
        this.nodes = new HashSet<>();

        for (Microservice mi : microservices) {
            nodes.add(new Node(mi.getMicroserviceName()));
        }
    }

    public void calculateLinks(String dir) throws IOException, InterruptedException {
        File outputDir = new File(dir);
        File[] files = outputDir.listFiles();
        ArrayList<Endpoint> endpoints = new ArrayList<>();
        ArrayList<Endpoint> graphqlendpoints = new ArrayList<>();
        ArrayList<WebSocketConnection> webSocketConnections = new ArrayList<>();
        ArrayList<WebSocketEndpoint> webSocketEndpoints = new ArrayList<>();

        for (File f : files) {
            if (f.getName().endsWith("_endpoints.csv")) {
                endpoints.addAll(parseEndpoints(f));
            } else if (f.getName().endsWith("_websocketconnections.csv")) {
                webSocketConnections.addAll(parseWebSocketConnections(f));
            } else if (f.getName().endsWith("_websocketendpoints.csv")) {
                webSocketEndpoints.addAll(parseWebSocketEndpoints(f));
            } else if (f.getName().endsWith("_graphqlendpoints.csv")) {
                graphqlendpoints.addAll(parseGraphQLEndpoints(f));
            }
        }

        for (File f : files) {
            if (f.getName().endsWith("_restcalls.csv")) {
                parseRestCalls(f, endpoints);
            }
        }

        for (File f : files) {
            if (f.getName().endsWith("_graphqlcalls.csv")) {
                parseGraphQLCalls(f, graphqlendpoints);
            }
        }

        for (WebSocketConnection connection : webSocketConnections) {
            System.out.println(connection.getUri());
        }


        // Link WebSocket connections based on the fourth value (uri)
        for (WebSocketConnection webSocketConnection : webSocketConnections) {
            for (WebSocketEndpoint webSocketEndpoint : webSocketEndpoints) {

                if (webSocketConnection.getUri().contains(webSocketEndpoint.getUri()) || webSocketEndpoint.getUri().contains(webSocketConnection.getUri())) {
                    Link link = new Link(webSocketConnection.getMsName(), webSocketEndpoint.getMsName(), new ArrayList<>());
                    if (!this.msLinks.contains(link)) {
                        this.msLinks.add(link);
                    }

                    // Create a Request object for the link
                    Request request = new Request(webSocketConnection.getMsName(), webSocketConnection.getClass().getName(), null, webSocketConnection.getUri(), "WS", webSocketConnection.getReturnType(), false, "", "");
                    link.addRequest(request);
                }
            }
        }

        Gson gson = new Gson();
        String nodesJsonString = gson.toJson(nodes);
        nodesJsonString.replaceFirst("\\[\\{", "");
        nodesJsonString.substring(0, nodesJsonString.length() - 2);
        String linksJsonString = gson.toJson(msLinks);
        linksJsonString.replaceFirst("\\[\\{", "");
        linksJsonString.substring(0, linksJsonString.length() - 2);
        String combinedJson = "{\"nodes\": " + nodesJsonString + ", \"links\": " + linksJsonString + "}";

        try (FileWriter fileWriter = new FileWriter(dir + "/communicationGraph.json")) {
            fileWriter.write(combinedJson);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    /**
     * Parses a CSV file containing WebSocket endpoint data and returns a list of WebSocketEndpoint objects.
     *
     * @param csv The CSV file to parse.
     * @return An ArrayList of WebSocketEndpoint objects parsed from the file.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private ArrayList<WebSocketEndpoint> parseWebSocketEndpoints(File csv) throws IOException {
        FileReader fileReader = new FileReader(csv);

//        String header = "msName,connectionInClassName,parentMethod,uri,httpMethod,returnType,isCollection";
//        ensureCsvHeader(csv, header);

        BufferedReader br = new BufferedReader(fileReader);

        ArrayList<WebSocketEndpoint> webSocketEndpoints = new ArrayList<>();

        String line;

        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");

            WebSocketEndpoint endpoint = new WebSocketEndpoint(
                    items[0],
                    items[1],
                    items[2],
                    items[3],
                    items[4],
                    items[5],
                    Boolean.parseBoolean(items[6])
            );
            webSocketEndpoints.add(endpoint);
        }
        br.close();

        return webSocketEndpoints;
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
//            System.out.println("items = " + Arrays.toString(items));
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
            // this.nodes.add(new Node(end.getMsName()));
        }
        br.close();

//        System.out.println("Parsed Endpoints: " + endpoints);
        return endpoints;
    }

    /**
     * Parses a CSV file containing GraphQL endpoint data and returns a list of Endpoint objects.
     *
     * @param csv The CSV file to parse.
     * @return An ArrayList of Endpoint objects parsed from the file.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private ArrayList<Endpoint> parseGraphQLEndpoints(File csv) throws IOException {
        FileReader fileReader = new FileReader(csv);
        BufferedReader br = new BufferedReader(fileReader);

        ArrayList<Endpoint> graphQLEndpoints = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");
            Endpoint endpoint = new Endpoint(
                    items[5],
                    items[2],
                    Arrays.asList(items[3].split("&")),
                    items[6],
                    items[4],
                    Boolean.parseBoolean(items[7]),
                    items[1],
                    items[0]
            );
            graphQLEndpoints.add(endpoint);
        }
        br.close();

        return graphQLEndpoints;
    }

    /**
     * Modifies a URI string by adding curly braces to specific segments and removing the first segment.
     *
     * @param s The input URI string to modify.
     * @return The modified URI string with curly braces added and the first segment removed.
     */
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
            if (items.length < RESTCALL_CSV_SCHEMA_LENGTH) {
                br.close();
                throw new RuntimeException("Restcall line parsed does not have " + RESTCALL_CSV_SCHEMA_LENGTH + " items, its length is " + items.length);
            }

            Request req = new Request(items[0], items[1], items[2], items[3], items[4], items[5], Boolean.parseBoolean(items[6]), "", "");
            //ADD REQUEST MS 
            // this.nodes.add(new Node(req.getMsName()));
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

            String restCallURI = addCurlyBraceToURI(uri);
//            boolean restHasCurlyBraces = restCallURI.contains("{") && restCallURI.contains("}");

            // find the specific endpoint being called
            for (Endpoint e : endpoints) {

                String endpointURI = e.getPath();
//                boolean endpointHasCurlyBraces = endpointURI.contains("{") && endpointURI.contains("}");
//
//                if (restHasCurlyBraces && !endpointHasCurlyBraces)
//                    continue;

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
            r.setTargetEndpointUri(e.getPath());
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

    /**
     * Parses a CSV file containing GraphQL call data and maps requests to their closest matching endpoints.
     *
     * @param csv The CSV file to parse.
     * @param endpoints A list of endpoints to match the requests against.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private void parseGraphQLCalls(File csv, ArrayList<Endpoint> endpoints) throws IOException {
        // Map to store the relationship between requests and their closest matching endpoints
        Map<Request, Endpoint> requestEndpointMap = new HashMap<>();

        // Open file readers
        FileReader fileReader = new FileReader(csv);
        BufferedReader br = new BufferedReader(fileReader);

        // CSV SCHEMA
        // 0   ,         1             ,     2   ,   3    ,     4   ,    5     ,     6,
        // msName, restCallInClassName, parentMethod, uri, graphqlMethod, returnType, isCollection

        ArrayList<Request> requests = new ArrayList<>();

        // Read the CSV file line by line and create Request objects
        String line;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");
            if (items.length < RESTCALL_CSV_SCHEMA_LENGTH) {
                br.close();
                throw new RuntimeException("Restcall line parsed does not have " + RESTCALL_CSV_SCHEMA_LENGTH + " items, its length is " + items.length);
            }

            // Create a Request object from the parsed CSV line
            Request req = new Request(items[0], items[1], items[2], items[3], "", items[4], Boolean.parseBoolean(items[6]), items[7], items[5]);
            requests.add(req);
        }

        // Close file readers
        br.close();
        fileReader.close();

        // Match each request to the closest endpoint
        for (Request r : requests) {
            int minDist = Integer.MAX_VALUE;
            int currDist;
            Endpoint closestMatch = null;
            int lengthOfLongerStr = 0;

            for (Endpoint e : endpoints) {
                String endpointURI = e.getPath();

                // Calculate the distance between the request URI and the endpoint URI
                currDist = findDistance(endpointURI, r.getUri());

                // Update the closest match if a better match is found
                if (!e.getMsName().equals(r.getMsName()) && minDist > currDist) {
                    minDist = currDist;
                    closestMatch = e;
                    lengthOfLongerStr = Math.max(e.getPath().length(), r.getUri().length());
                }
            }

            // Calculate the similarity threshold
            double percent = lengthOfLongerStr * dissimilarityPercent;

            // Add the request and its closest endpoint to the map if the match is valid
            if (closestMatch != null && percent > minDist) {
                requestEndpointMap.put(r, closestMatch);
            }
        }

        // Create links between microservices based on the matched requests and endpoints
        for (Map.Entry<Request, Endpoint> reqs : requestEndpointMap.entrySet()) {
            Request r = reqs.getKey();
            Endpoint e = reqs.getValue();

            // Create a new link between the microservices
            Link l = new Link(r.getMsName(), e.getMsName(), new ArrayList<>());

            // Set additional details for the request
            r.setEndpointMsName(e.getMsName());
            r.setType(e.getHttpMethod());
            r.setArguments(e.getArguments().toString());
            r.setTargetEndpointUri(e.getPath());
            r.setEndpointFunction(e.getParentMethod());

            // Add the link to the list if it doesn't already exist
            if (!this.msLinks.contains(l)) {
                l.addRequest(r);
                this.msLinks.add(l);
            } else {
                // If the link exists, add the request to the existing link
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
        for (short i = 0; i <= a.length(); i++)
            d[i][0] = i;

        // Initialising first row:
        for (short j = 0; j <= b.length(); j++)
            d[0][j] = j;

        // Applying the algorithm:
        short insertion, deletion, replacement;
        for (short i = 1; i <= a.length(); i++) {
            for (short j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == (b.charAt(j - 1)))
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
        if (x <= y && x <= z)
            return x;
        if (y <= x && y <= z)
            return y;
        else
            return z;
    }

}
