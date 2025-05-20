/**
 * Authors:
 * - Original Authors
 * - Vsevolod Pokhvalenko
 */

package baylor.cloudhubs.prophetutils.microservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Microservice {

    @Setter private String baseDir;
    @Setter private String targetDir;
    @Setter private String classesDir;
    private final String basePackage;
    private final String microserviceName;
    private final List<String> jarFiles;
}
