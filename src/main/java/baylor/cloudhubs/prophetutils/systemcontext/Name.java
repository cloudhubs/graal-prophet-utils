package baylor.cloudhubs.prophetutils.systemcontext;

import java.util.Objects;

public class Name {

    private String name;
    private String fullName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Name(Name n){
        this.name = n.name;
        this.fullName = n.fullName;
    }

    public Name(String str){
        this.name = str;
        this.fullName = str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Name name1 = (Name) o;
        return Objects.equals(name, name1.name);
    }

    //TODO fix this and equals
    @Override
    public int hashCode() {
        return Objects.hash(fullName);
    }
}
