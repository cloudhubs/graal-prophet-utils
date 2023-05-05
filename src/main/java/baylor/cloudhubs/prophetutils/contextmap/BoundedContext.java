package baylor.cloudhubs.prophetutils.contextmap;

import java.util.Objects;
import java.util.Set;
import java.util.Stack;

public class BoundedContext {

    private String systemName;

    private Set<Entity> boundedContextEntities;

    private Stack<Module> boundedContextModules;

    public BoundedContext(){}

    public BoundedContext(String systemName, Set<Entity> boundedContextEntities) {
        this.systemName = systemName;
        this.boundedContextEntities = boundedContextEntities;
    }

    public BoundedContext(String systemName, Set<Entity> boundedContextEntities, Stack<Module> boundedContextModules){
        this.systemName = systemName;
        this.boundedContextModules = boundedContextModules;
        this.boundedContextEntities = boundedContextEntities;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Set<Entity> getBoundedContextEntities() {
        return boundedContextEntities;
    }

    public void setBoundedContextEntities(Set<Entity> boundedContextEntities) {
        this.boundedContextEntities = boundedContextEntities;
    }

    public Stack<Module> getBoundedContextModules(){
        return boundedContextModules;
    }

    public void setBoundedContextModules(Stack<Module> boundedContextModules){
        this.boundedContextModules = boundedContextModules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundedContext that = (BoundedContext) o;
        return Objects.equals(systemName, that.systemName) &&
                Objects.equals(boundedContextEntities, that.boundedContextEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemName, boundedContextEntities);
    }
}