package baylor.cloudhubs.prophetutils.visualizer;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class LinkAlg {

    Map<String, Link> msMaptoLinks;

    public LinkAlg(String dir) throws IOException, InterruptedException {
        calculateLinks(dir);
    }

    private void parseRestCalls(File csv, ArrayList<Endpoint> endpoints) throws IOException {
        FileReader fileReader = new FileReader(csv);
        BufferedReader br = new BufferedReader(fileReader);


        // CSV SCHEMA
        // 0     ,         1          ,       2     ,  3 ,     4     ,     5     ,     6
        // msName, restCallInClassName, parentMethod, uri, httpMethod, returnType, isCollection

        ArrayList<Request> requests = new ArrayList<>();

        // read in csv and make requests
        String line;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");
            Request req = new Request(items[4], items[3], items[5], Boolean.parseBoolean(items[6]), items[1], "");
            requests.add(req);
        }

        for (Request r : requests) {
            // parse the endpoint path from the request objects URI
            URL uri = new URL(r.getUri());
            String reqEndpointString = uri.getPath().replace("//", "/");

            List<Endpoint> endpointContains = new ArrayList<>();

            Endpoint exactMatch = null;

            for (Endpoint e : endpoints) {

                if (r.getHttpType().equals(e.getHttpMethod())) {

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
                endpointContains = endpointContains
                        .stream()
                        .filter((e) -> e.getPath().endsWith(reqEndpointString) || reqEndpointString.endsWith(e.getPath()))
                        .collect(Collectors.toList());
                exactMatch = endpointContains.get(0);
            }



        }

    }

    private ArrayList<Endpoint> parseEndpoints(File csv) throws IOException {
        FileReader fileReader = new FileReader(csv);
        BufferedReader br = new BufferedReader(fileReader);

        ArrayList<Endpoint> endpoints = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] items = line.split(",");

            // CSV SCHEMA
            // 0    ,        1           ,       2     ,  3  ,      4    ,     5     ,       6     ,    7
            //msName, endpointInClassName, parentMethod, path, httpMethod, returnType, isCollection, arguments

            endpoints.add(
                    new Endpoint(
                    items[4],
                    items[2],
                    Arrays.asList(items[7].split("&")),
                    items[5],
                    items[3],
                    Boolean.parseBoolean(items[6]),
                    items[1],
                    items[0]
                )
            );

        }

        return endpoints;
    }

    private void calculateLinks(String dir) throws IOException, InterruptedException {
        // read from output dir and create list of all files *endpoints.csv and *restcalls.csv
        File outputDir = new File("../../../../../../../output");
        File[] files = outputDir.listFiles();

        ArrayList<Endpoint> endpoints = new ArrayList<>();


        // filter and parse the correct files
        for (File f : files) {
            if (f.getName().endsWith("endpoints.csv")) {
                endpoints.addAll(parseEndpoints(f));
            }
        }

        for (File f : files) {
            if (f.getName().endsWith("restcalls.csv")) {
                parseRestCalls(f, endpoints);
            }
        }

    }

    public Map<String, Link> getMsMapToLinks() {
        return this.msMaptoLinks;
    }

}
