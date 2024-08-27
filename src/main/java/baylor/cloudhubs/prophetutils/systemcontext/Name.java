package baylor.cloudhubs.prophetutils.systemcontext;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class Name {

    private String name;
    private String fullName;

    public Name(Name n) {
        this.name = n.name;
        this.fullName = n.fullName;
    }

    public Name(String str) {
        this.name = str;
        this.fullName = str;
    }
}
