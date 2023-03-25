package baylor.cloudhubs.prophetutils.visualizer;

import java.util.ArrayList;
import java.util.Objects;

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

    public void addRequest(Request r) { this.requests.add(r); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(source, link.source) && Objects.equals(target, link.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    @Override
    public String toString() {
        return "Link{\n" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", requests=" + requests +
                "\n}\n";
    }
}
