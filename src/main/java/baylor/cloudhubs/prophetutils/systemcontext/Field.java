package baylor.cloudhubs.prophetutils.systemcontext;

import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Field {

    private Name name;

    private String type;

    private Set<Annotation> annotations = new HashSet<>();

    private boolean isReference;

    private String entityRefName;
    private boolean isCollection;

    public Field clone(){
        // need to change the entity reference later to the new entity
        return new Field(this.getName(), this.getType(), this.getAnnotations(),
                this.isReference(), this.getEntityRefName(), this.isCollection());
    }

    public Field(String type, Name name) {
        this.type = type;
        this.name = name;
    }

    public Field(String type, String name){
        this.type = type;
        this.name = new Name(name);
    }

    public Field(Name name, String type, Set<Annotation> annotations) {
        this.name = new Name(name);
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name.getName() + '\'' +
                ", type='" + type + '\'' +
                ", isReference=" + isReference +
                '}';
    }
}
