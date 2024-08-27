package baylor.cloudhubs.prophetutils.systemcontext;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class SystemContext {
    private String systemName;
    private Set<Module> modules;
}
