package baylor.cloudhubs.prophetutils.systemcontext;

import java.util.*;

public class Module {

    private Name name;

    private Set<Entity> entities;

    public Module clone(){
        Set<Entity> entitySet = new HashSet<>();
        this.getEntities().forEach(x ->
        {
            entitySet.add(x.clone());
        });

        return new Module(this.getName(), entitySet);
    }
    public Module(){}

    public Module(String name) {
        this.name = new Name(name);
        this.entities = new HashSet<>();
    }

    public Module( Name name, Set<Entity> entities) {
        this.name = new Name(name);
        this.entities = entities;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public void setEntities(Set<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(name, module.name) &&
                Objects.equals(entities, module.entities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, entities);
    }
}
