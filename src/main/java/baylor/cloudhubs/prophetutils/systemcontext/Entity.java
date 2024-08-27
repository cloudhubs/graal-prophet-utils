package baylor.cloudhubs.prophetutils.systemcontext;


import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Entity {

    private Name entityName;

    private Set<Field> fields = new HashSet<>();

    public Entity(Name entityName) {
        this.entityName = new Name(entityName);
    }

    public Entity(String entityName){
        this.entityName = new Name(entityName);
    }

    public Entity clone() {
        Set<Field> newFields = new HashSet<>(this.fields.size());
        this.getFields().forEach(x ->
        {
            newFields.add(x.clone());

        });
        return new Entity(new Name(this.getEntityName()), newFields);
    }

    public Entity copyWithNamePreface(String preface) {
        Entity toReturn = this.clone();
        toReturn.getEntityName().setFullName(preface + entityName.getFullName());
        return toReturn;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "entityName='" + entityName + '\'' +
                '}';
    }
}
