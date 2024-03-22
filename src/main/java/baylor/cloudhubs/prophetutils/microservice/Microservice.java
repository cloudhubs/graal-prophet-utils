package baylor.cloudhubs.prophetutils.microservice;

public class Microservice {

    private String baseDir;
    private final String basePackage;
    private final String microserviceName;

    public Microservice(String baseDir, String basePackage, String microserviceName) {
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
