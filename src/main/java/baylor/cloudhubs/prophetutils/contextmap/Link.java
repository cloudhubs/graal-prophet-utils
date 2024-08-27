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
        String ret = "\t\t{\n" + "\t\t\t\"source\": \"" + src + "\",\n"
                        + "\t\t\t\"target\": \"" + target + "\",\n"
                        + "\t\t\t\"msSource\": \"" + msSource + "\",\n"
                        + "\t\t\t\"msTarget\": \"" + msTarget + "\",\n"
                        + "\t\t\t\"sourceMultiplicity\": \"" + srcMult + "\",\n"
                        + "\t\t\t\"targetMultiplicity\": \"" + targetMult + "\"\n"
                        + "\t\t},\n";
        return ret;
    }
}
