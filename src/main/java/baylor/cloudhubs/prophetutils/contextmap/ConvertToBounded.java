package baylor.cloudhubs.prophetutils.contextmap;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class ConvertToBounded {
    public baylor.cloudhubs.prophetutils.contextmap.Data.Entity[] convert(List<Data> dataList, String systemName){
        Set<Module> moduleSet = convertDataToModule(dataList);
        int entityCnt = 0;
        for(Module m : moduleSet){
            entityCnt += m.getEntities().size();
        }
        System.out.println("starting number of entities: " + entityCnt);
        SystemContext systemContext = new SystemContext(systemName, moduleSet);
        BoundedContextUtilsImpl boundedContextUtilsImpl = new BoundedContextUtilsImpl();
        BoundedContext boundedContext = boundedContextUtilsImpl.createBoundedContext(systemContext, true);
        Set<Entity> entities = boundedContext.getBoundedContextEntities();
        System.out.println("Number of entities: " + entities.size());
        Set<String> counter = new HashSet<>();
        for(Entity e : entities){
            if(counter.contains(e.getEntityName().getName())){
                System.out.println("Already have: " + e.getEntityName().getName());
            }
            counter.add(e.getEntityName().getName());
        }
        System.out.println("Number without duplicates: " + counter.size());
        Data d = new Data();
        baylor.cloudhubs.prophetutils.contextmap.Data.Entity[] ret = convertEntitiesBack(entities, d);
        return ret;
    }

    public Set<Module> convertDataToModule(List<Data> dataList){
        Set<Module> moduleSet = new HashSet<>();
        for(Data d : dataList){
            Module temp = new Module(convertName(d.getName()), convertEntities(d.getEntities()));
            moduleSet.add(temp);
        }
        return moduleSet;
    }

    public List<Data> convertModuleToData(Set<Module> modules){
        List<Data> dataList = new ArrayList<>();
        for(Module m : modules){
            Data d = new Data();
            d.setName(convertNameBack(m.getName(), d));
            d.setEntities(convertEntitiesBack(m.getEntities(), d));
            dataList.add(d);
        }
        return dataList;
    }

    public Set<Entity> convertEntities(baylor.cloudhubs.prophetutils.contextmap.Data.Entity[] entities){
        Set<Entity> entitySet = new HashSet<>();
        for(baylor.cloudhubs.prophetutils.contextmap.Data.Entity e : entities){
            Entity temp = new Entity(convertName(e.getEntityName()), convertFields(e.getFields()));
            entitySet.add(temp);
        }
        return entitySet;
    }

    public baylor.cloudhubs.prophetutils.contextmap.Data.Entity[] convertEntitiesBack(Set<Entity> entitiesSet, Data data){
        Entity[] entities = entitiesSet.toArray(new Entity[entitiesSet.size()]);
        baylor.cloudhubs.prophetutils.contextmap.Data.Entity[] ret = new baylor.cloudhubs.prophetutils.contextmap.Data.Entity[entities.length];
        for(int i = 0; i < entities.length; i++){
            baylor.cloudhubs.prophetutils.contextmap.Data.Entity temp = data.new Entity();
            temp.setName(convertNameBack(entities[i].getEntityName(), data));
            temp.setFields(convertFieldsBack(entities[i].getFields(), data));
            ret[i] = temp;
        }
        return ret;
    }

    public Set<Field> convertFields(baylor.cloudhubs.prophetutils.contextmap.Data.Field[] fields){
        Set<Field> fieldSet = new HashSet<>();
        for(baylor.cloudhubs.prophetutils.contextmap.Data.Field f : fields){
            Field temp = new Field(convertName(f.getFieldName()), f.getType(), convertAnnotations(f.getAnnotations()), f.getIsReference(), f.getEntityRefName(), f.getIsCollection());
            fieldSet.add(temp);
        }
        return fieldSet;
    }

    public baylor.cloudhubs.prophetutils.contextmap.Data.Field[] convertFieldsBack(Set<Field> fieldsSet, Data data){
        Field[] fields = fieldsSet.toArray(new Field[fieldsSet.size()]);
        baylor.cloudhubs.prophetutils.contextmap.Data.Field[] ret = new baylor.cloudhubs.prophetutils.contextmap.Data.Field[fields.length];
        for(int i = 0; i < fields.length; i++){
            baylor.cloudhubs.prophetutils.contextmap.Data.Field temp = data.new Field();
            temp.setFieldName(convertNameBack(fields[i].getName(), data));
            temp.setType(fields[i].getType());
            temp.setAnnotations(convertAnnotationsBack(fields[i].getAnnotations(), data));
            temp.setIsCollection(fields[i].isCollection());
            temp.setEntityRefName(fields[i].getEntityRefName());
            temp.setIsReference(fields[i].isReference());
            ret[i] = temp;
        }
        return ret;
    }

    public Set<Annotation> convertAnnotations(baylor.cloudhubs.prophetutils.contextmap.Data.Annotation[] annotations){
        Set<Annotation> annotationSet = new HashSet<>();
        for(baylor.cloudhubs.prophetutils.contextmap.Data.Annotation a : annotations){
            Annotation temp = new Annotation(a.getName(), a.getStringValue(), null);
            if(a.getIntValue() != null){
                temp.setIntValue(Integer.parseInt(a.getIntValue()));
            }
            annotationSet.add(temp);
        }
        return annotationSet;
    }

    public baylor.cloudhubs.prophetutils.contextmap.Data.Annotation[] convertAnnotationsBack(Set<Annotation> annotationsSet, Data data){
        Annotation[] annotations = annotationsSet.toArray(new Annotation[annotationsSet.size()]);
        baylor.cloudhubs.prophetutils.contextmap.Data.Annotation[] ret = new baylor.cloudhubs.prophetutils.contextmap.Data.Annotation[annotations.length];
        for(int i = 0; i < annotations.length; i++){
            baylor.cloudhubs.prophetutils.contextmap.Data.Annotation temp = data.new Annotation();
            temp.setName(annotations[i].getName());
            temp.setStringValue(annotations[i].getStringValue());
            temp.setIntValue(String.valueOf(annotations[i].getIntValue()));
            ret[i] = temp;
        }
        return ret;
    }

    public Name convertName(baylor.cloudhubs.prophetutils.contextmap.Data.Name name){
        Name n = new Name(name.getName());
        return n;
    }

    public baylor.cloudhubs.prophetutils.contextmap.Data.Name convertNameBack(Name name, Data d){
        baylor.cloudhubs.prophetutils.contextmap.Data.Name n = d.new Name();
        n.setName(name.getName());
        n.setFullName(name.getFullName());
        return n;
    }
}
