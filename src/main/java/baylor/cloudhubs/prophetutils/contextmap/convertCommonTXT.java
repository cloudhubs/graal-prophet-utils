package baylor.cloudhubs.prophetutils.contextmap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class convertCommonTXT {
    public static Data convertCommon(String filePath){
        Data d = new Data();
        baylor.cloudhubs.prophetutils.contextmap.Data.Name n = d.new Name();
        n.setName("ts-common");
        n.setFullName("ts-common");
        d.setName(n);
        List<baylor.cloudhubs.prophetutils.contextmap.Data.Entity> entities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                entities.add(readInEntity(line, d));
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        d.setEntities(entities.toArray(new baylor.cloudhubs.prophetutils.contextmap.Data.Entity[entities.size()]));
        return d;
    }

    public static baylor.cloudhubs.prophetutils.contextmap.Data.Entity readInEntity(String line, Data d){
        baylor.cloudhubs.prophetutils.contextmap.Data.Entity e = d.new Entity();
        String name = line.substring(0, line.indexOf('['));
        baylor.cloudhubs.prophetutils.contextmap.Data.Name n = d.new Name();
        n.setName(name);
        n.setFullName(name);
        e.setName(n);
        e.setFields(readInField(line.substring(line.indexOf('[') + 1, line.length() - 1), d));
        return e;
    }

    public static baylor.cloudhubs.prophetutils.contextmap.Data.Field[] readInField(String fieldsString, Data d){
        List<baylor.cloudhubs.prophetutils.contextmap.Data.Field> fields = new ArrayList<>();
        if(fieldsString.length() != 0){
            for(String field : fieldsString.split(", ")){
                String[] spl = field.split(" ");
                baylor.cloudhubs.prophetutils.contextmap.Data.Field temp = d.new Field();
                baylor.cloudhubs.prophetutils.contextmap.Data.Name n = d.new Name();
                n.setName(spl[2]);
                n.setFullName(spl[2]);
                temp.setFieldName(n);
                temp.setType(spl[1]);
                temp.setIsReference(true);
                temp.setEntityRefName(spl[0]);
                temp.setIsCollection(temp.getType().contains("<"));
                temp.setAnnotations(readInAnnotations(spl[3], d));
                fields.add(temp);
            }
        }
        return fields.toArray(new baylor.cloudhubs.prophetutils.contextmap.Data.Field[fields.size()]);
    }

    public static baylor.cloudhubs.prophetutils.contextmap.Data.Annotation[] readInAnnotations(String annotationsString, Data d){
        List<baylor.cloudhubs.prophetutils.contextmap.Data.Annotation> annotations = new ArrayList<>();
        annotationsString = annotationsString.substring(1, annotationsString.length() - 1);
        for(String annotation : annotationsString.split(",")){
            baylor.cloudhubs.prophetutils.contextmap.Data.Annotation temp = d.new Annotation();
            temp.setName(annotation);
            annotations.add(temp);
        }
        return annotations.toArray(new baylor.cloudhubs.prophetutils.contextmap.Data.Annotation[annotations.size()]);
    }
}
