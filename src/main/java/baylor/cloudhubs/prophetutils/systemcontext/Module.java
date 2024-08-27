package baylor.cloudhubs.prophetutils.systemcontext;

import lombok.*;

import java.util.*;

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
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
    public Module(String name) {
        this.name = new Name(name);
        this.entities = new HashSet<>();
    }
}
