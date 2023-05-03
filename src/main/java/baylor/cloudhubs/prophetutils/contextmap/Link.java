package baylor.cloudhubs.prophetutils.contextmap;

public class Link {
    
    private String src;
    
    private String target;
    
    private String srcMult;
    
    private String targetMult;

    private String msSrc;

    private String msTarget;

    private String className;

    public Link(String src, String target, String srcMult, String targetMult, String msSrc, String msTarget, String className){
        this.src = src;
        this.target = target;
        this.srcMult = srcMult;
        this.targetMult = targetMult;
        this.msSrc = msSrc;
        this.msTarget = msTarget;
        this.className = className;
    }

    public Link(String src, String target, String srcMult, String targetMult, String msSrc, String msTarget){
        this.src = src;
        this.target = target;
        this.srcMult = srcMult;
        this.targetMult = targetMult;
        this.msSrc = msSrc;
        this.msTarget = msTarget;
    }

    public Link(String src, String target){
        this.src = src;
        this.target = target;
    }

    public String getSrc(){
        return src;
    }

    public void setSrc(String src){
        this.src = src;
    }

    public String getTarget(){
        return target;
    }

    public void setTarget(String target){
        this.target = target;
    }

    public String getSrcMult(){
        return srcMult;
    }

    public void setSrcMult(String srcMult){
        this.srcMult = srcMult;
    }

    public String getTargetMult(){
        return targetMult;
    }

    public void setTargetMult(String targetMult){
        this.targetMult = targetMult;
    }

    public String getMsSrc(){
        return msSrc;
    }

    public void setMsSrc(String msSrc){
        this.msSrc = msSrc;
    }

    public String getMsTarget(){
        return msTarget;
    }

    public void setMsTarget(String msTarget){
        this.msTarget = msTarget;
    }

    public String getClassName(){
        return className;
    }

    public void setClassName(String className){
        this.className = className;
    }

    public boolean isInverse(Link other){
        return this.src.equals(other.target) && this.target.equals(other.src) && this.msSrc.equals(other.msTarget) && this.msTarget.equals(other.msSrc);
    }

    public void addSrcMult(String one, String two){

    }

    public void addTargetMult(String one, String two){

    }

    @Override
    public String toString(){
        String ret = "\t\t{\n" + "\t\t\t\"source\": \"" + src + "\",\n"
                        + "\t\t\t\"target\": \"" + target + "\",\n"
                        + "\t\t\t\"msSource\": \"" + msSrc + "\",\n"
                        + "\t\t\t\"msTarget\": \"" + msTarget + "\",\n"
                        + "\t\t\t\"sourceMultiplicity\": \"" + srcMult + "\",\n"
                        + "\t\t\t\"targetMultiplicity\": \"" + targetMult + "\"\n"
                        + "\t\t},\n";
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Link)) {
            return false;
        }
        Link other = (Link) obj;
        return src.equals(other.src) && target.equals(other.target);
    }
}
