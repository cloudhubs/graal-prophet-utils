package baylor.cloudhubs.prophetutils.visualizer;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Link {

    @EqualsAndHashCode.Include
    private String source;
    @EqualsAndHashCode.Include
    private String target;
    private ArrayList<Request> requests;

    public void addRequest(Request r) { this.requests.add(r); }

    @Override
    public String toString() {
        return "Link{\n" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", requests=" + requests +
                "\n}\n";
    }
}
