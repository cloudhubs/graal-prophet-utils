package baylor.cloudhubs.prophetutils.contextmap;

public class Link {
    
    private String src;
    
    private String target;
    
    private String srcMult;
    
    private String targetMult;

    public Link(String src, String target, String srcMult, String targetMult){
        this.src = src;
        this.target = target;
        this.srcMult = srcMult;
        this.targetMult = targetMult;
    }

    @Override
    public String toString(){
        String ret = "\t\t{\n" + "\t\t\t\"source\": \"" + src + "\",\n"
                        + "\t\t\t\"target\": \"" + target + "\",\n"
                        + "\t\t\t\"sourceMultiplicity\": \"" + srcMult + "\",\n"
                        + "\t\t\t\"targetMultiplicity\": \"" + targetMult + "\"\n"
                        + "\t\t},\n";
        return ret;
    }
}
