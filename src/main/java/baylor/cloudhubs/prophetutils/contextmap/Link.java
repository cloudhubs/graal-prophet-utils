package baylor.cloudhubs.prophetutils.contextmap;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Link {
    
    private String src;
    
    private String target;
    
    private String srcMult;
    
    private String targetMult;

    private String msSource;

    private String msTarget;

    @Override
    public String toString(){
        return "\t{\n" +
                "\t\t\"source\": \"" + src + "\",\n" +
                "\t\t\"target\": \"" + target + "\",\n" +
                "\t\t\"msSource\": \"" + msSource + "\",\n" +
                "\t\t\"msTarget\": \"" + msTarget + "\",\n" +
                "\t\t\"sourceMultiplicity\": \"" + srcMult + "\",\n" +
                "\t\t\"targetMultiplicity\": \"" + targetMult + "\"\n" +
                "\t}";
    }
}
