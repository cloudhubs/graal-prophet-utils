package baylor.cloudhubs.prophetutils;

import java.util.Objects;

public class Env {

    public static String PROPHET_PLUGIN_HOME_ENV = "PROPHET_PLUGIN_HOME";

    public static String load(String name) {
        return Objects.requireNonNull(System.getenv(name), () -> name + " is not set");
    }
}
