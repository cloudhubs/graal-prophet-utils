package baylor.cloudhubs.prophetutils.contextmap;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class ConvertToBounded {
    public static void convert(List<Data> dataList, String systemName){
        Set<Module> moduleSet = convertDataToModule(dataList);
        SystemContext systemContext = new SystemContext(systemName, moduleSet);
        BoundedContextUtilsImpl boundedContextUtilsImpl = new BoundedContextUtilsImpl();
        BoundedContext boundedContext = boundedContextUtilsImpl.createBoundedContext(systemContext, true);
        Set<Entity> entities = boundedContext.getBoundedContextEntities();
        for(Entity e : entities){
            System.out.println(e.toString());
        }
    }

    public static Set<Module> convertDataToModule(List<Data> dataList){
        Set<Module> moduleSet = new HashSet<>();
        for(Data d : dataList){
            Module temp = new Module(convertName(d.getName()), convertEntities(d.getEntities()));
            moduleSet.add(temp);
        }
        return moduleSet;
    }

    public static Set<Entity> convertEntities(baylor.cloudhubs.prophetutils.contextmap.Data.Entity[] entities){
        Set<Entity> entitySet = new HashSet<>();
        for(baylor.cloudhubs.prophetutils.contextmap.Data.Entity e : entities){
            System.out.println("entity name: " + e.getEntityName().getName());
            Entity temp = new Entity(convertName(e.getEntityName()), convertFields(e.getFields()));
            entitySet.add(temp);
        }
        return entitySet;
    }

    public static Set<Field> convertFields(baylor.cloudhubs.prophetutils.contextmap.Data.Field[] fields){
        Set<Field> fieldSet = new HashSet<>();
        //Name name, String type, Set<Annotation> annotations, boolean isReference, String entityRefName, boolean isCollection
        for(baylor.cloudhubs.prophetutils.contextmap.Data.Field f : fields){
            System.out.println("field type: " + f.getType());
            Field temp = new Field(convertName(f.getFieldName()), f.getType(), convertAnnotations(f.getAnnotations()), f.getIsReference(), f.getEntityRefName(), f.getIsCollection());
            fieldSet.add(temp);
        }
        return fieldSet;
    }

    public static Set<Annotation> convertAnnotations(baylor.cloudhubs.prophetutils.contextmap.Data.Annotation[] annotations){
        Set<Annotation> annotationSet = new HashSet<>();
        for(baylor.cloudhubs.prophetutils.contextmap.Data.Annotation a : annotations){
            Annotation temp = new Annotation(a.getName(), a.getStringValue(), Integer.parseInt(a.getIntValue()));
            annotationSet.add(temp);
        }
        return annotationSet;
    }

    public static Name convertName(baylor.cloudhubs.prophetutils.contextmap.Data.Name name){
        Name n = new Name(name.getFullName());
        return n;
    }
}
