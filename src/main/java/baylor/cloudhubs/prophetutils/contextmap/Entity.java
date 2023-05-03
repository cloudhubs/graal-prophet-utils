package baylor.cloudhubs.prophetutils.contextmap;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Entity {

    private Name entityName;

    private Set<Field> fields = new HashSet<>();

    public Entity(Name entityName) {
        this.entityName = new Name(entityName);
    }

    public Entity(String entityName){
        this.entityName = new Name(entityName);
    }

    public Entity(Name entityName, Set<Field> fields) {
        this.entityName = new Name(entityName);
        this.fields = fields;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(entityName, entity.entityName) &&
                Objects.equals(fields, entity.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityName, fields);
    }

    public Name getEntityName() {
        return entityName;
    }

    public void setEntityName(Name entityName) {
        this.entityName = entityName;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "entityName='" + entityName + '\'' +
                '}';
    }
}
