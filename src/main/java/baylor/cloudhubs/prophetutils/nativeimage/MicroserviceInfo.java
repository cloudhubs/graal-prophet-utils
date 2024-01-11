package baylor.cloudhubs.prophetutils.nativeimage;

public class MicroserviceInfo {

    private String baseDir;
    private final String basePackage;
    private final String microserviceName;

    public MicroserviceInfo(String baseDir, String basePackage, String microserviceName) {
        this.baseDir = baseDir;
        this.basePackage = basePackage;
        this.microserviceName = microserviceName;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public String getMicroserviceName() {
        return microserviceName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
