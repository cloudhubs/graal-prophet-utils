package baylor.cloudhubs.prophetutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


/**
 * AppConfigLoader
 * handles functions relating to the config properties file for the application
 * REMINDER: must call loadConfigFile() to load the config file before use of CONFIG_PROPS
 */
public class AppConfigUtils{

    final public static Properties CONFIG_PROPS = new Properties();

    //attempt to load path config file, bubble up exceptions
    public static void loadConfigFile() throws FileNotFoundException, IOException{

        final String CONFIG_FILE_PATH = "/home/jack/Capstone/graal-prophet-utils/config/config.properties";
        try(FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)){
            CONFIG_PROPS.load(fis);
        }catch(FileNotFoundException err){
            throw err;
        }catch(IOException err){
            throw err;
        }
    }
    
}