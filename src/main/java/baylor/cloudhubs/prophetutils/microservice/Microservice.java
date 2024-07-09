package baylor.cloudhubs.prophetutils.microservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Microservice {

    @Setter private String baseDir;
    private final String basePackage;
    private final String microserviceName;
}
