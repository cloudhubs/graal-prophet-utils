package baylor.cloudhubs.prophetutils.filemanager;

import com.google.gson.Gson;
import edu.baylor.ecs.cloudhubs.prophetdto.systemcontext.SystemContext;

import java.io.*;
import java.util.List;

public class FileManager {

    public static void writeBoundedContextToFile(List<String> list) {
        try {

            PrintWriter pw = new PrintWriter("bounded-context.html");
            for (String s : list
            ) {
                pw.println(s);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(SystemContext systemContext) {
        writeToFile(systemContext, "systemContext.json");
    }

    public static void writeToFile(Object systemContext, String fileName) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(systemContext);
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
