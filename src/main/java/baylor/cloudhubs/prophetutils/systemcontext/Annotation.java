package baylor.cloudhubs.prophetutils.systemcontext;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Annotation {
    private String name;
    private String stringValue;
    private Integer intValue;
}
