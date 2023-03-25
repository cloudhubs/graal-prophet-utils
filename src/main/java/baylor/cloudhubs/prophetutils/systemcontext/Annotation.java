package baylor.cloudhubs.prophetutils.systemcontext;

import java.util.Objects;

public class Annotation {

    private String name;

    private String stringValue;
    private Integer intValue;

    public Annotation(){}

    public Annotation(String name, String stringValue, Integer intValue) {
        this.name = name;
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Annotation that = (Annotation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(stringValue, that.stringValue) &&
                Objects.equals(intValue, that.intValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stringValue, intValue);
    }
}
