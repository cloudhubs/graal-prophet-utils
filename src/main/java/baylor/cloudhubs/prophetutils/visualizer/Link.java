package baylor.cloudhubs.prophetutils.visualizer;

import java.util.ArrayList;

public class Link {
    
    private String source;
    private String target;
    private ArrayList<Request> requests;

    public Link(String source, String target, ArrayList<Request> requests){
        this.source = source;
        this.target = target;
        this.requests = requests;
    }
    // Getter and setter for source variable
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // Getter and setter for target variable
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    // Getter and setter for requests variable
    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

}
