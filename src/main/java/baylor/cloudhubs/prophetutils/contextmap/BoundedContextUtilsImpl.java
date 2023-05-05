package baylor.cloudhubs.prophetutils.contextmap;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * methods for creating a {@link BoundedContext} from a {@link SystemContext}
 * @author Ian laird
 */
public class BoundedContextUtilsImpl implements BoundedContextUtils {

    // tools used for finding similarities
    private SimilarityUtils similarityUtils = new SimilarityUtilsImpl();

    public static double ENTITY_SIMILARITY_CUTOFF = 0.9;

    /**
     * creates a bounded context for the system context
     * @param systemContext the system
     * @return the bounded context
     */
    @Override
    public BoundedContext createBoundedContext(SystemContext systemContext, boolean useWuPalmer) {

        // sanitize all of the name in the systemContext
        NameStripper.sanitizeSystemContext(systemContext);

        Set<Module> modules = systemContext.getModules();
        Stack<Module> moduleStack = new Stack<>();
        for (Module m: modules) {
            moduleStack.add(m.clone());
        }
//        moduleStack.addAll(modules);
        while(moduleStack.size() > 1) {
            Module m1 = moduleStack.pop();
            Module m2 = moduleStack.pop();
            Module result = mergeModules(m1, m2, useWuPalmer);
            if (result.getEntities().size() > 0) {
                moduleStack.push(result);
            }
        }

        return new BoundedContext(systemContext.getSystemName(), moduleStack.size() > 0 ? moduleStack.get(0).getEntities() : null);
        //return new BoundedContext(systemContext.getSystemName(), moduleStack.size() > 0 ? moduleStack.get(0).getEntities() : null, moduleStack);
    }

    /**
     * merges two modules into one module
     * @param moduleOne one of the modules
     * @param moduleTwo the other module
     * @return a new module comprised of the other two
     */
    @Override
    public Module mergeModules(Module moduleOne, Module moduleTwo, boolean useWuPalmer){

        // for each entity find the similarity it has to other entities
        Map<Entity, TreeMap<Double, ImmutablePair<Entity, Map<Field, Field>>>>
                entitySimilarity = new HashMap<>();

        // shows which entities in module two are encountered
        Set<Entity> mappedInTwo = new HashSet<>();

        // get all entities in module one
        moduleOne.getEntities()

            // for each entity in entity one add to entity similarity
            .forEach(x -> entitySimilarity.put(

                // the current entity of module one
                x,

                // create stream of entity two entities
                moduleTwo.getEntities().stream()

                    // create map
                    .collect(Collectors.toMap(

                        // similarity of entity from module one and entity from module two
                        y -> similarityUtils.globalFieldSimilarity(x, y, useWuPalmer).getLeft(),

                        // tuple of entity from module two
                        y -> new ImmutablePair<>(y,
                                //and the field mapping
                                similarityUtils.globalFieldSimilarity(x, y, useWuPalmer).getRight()),
                        (oldValue,newValue) -> newValue,
                        TreeMap::new
                    ))
                ));

        Module newModule = new Module(moduleOne.getName().getName());

        newModule.setEntities(new HashSet<>());

        // sets the entities of the new module
        newModule.getEntities().addAll(

                // stream of all entries in entity similarity
                entitySimilarity.entrySet().stream()

                        // if the similarity is strong enough
                        .filter(x -> {
                            // if it is not mapped to anything, no merging needs to be performed
                            if(x.getValue().isEmpty()){
                                newModule.getEntities().add(x.getKey().clone());
                                return false;
                            }

                            Map.Entry<Double, ImmutablePair<Entity, Map<Field, Field>>> val = x.getValue().lastEntry();
                            double similarity = val.getKey();

                            // if the two entities should be merged
                            if (similarity > ENTITY_SIMILARITY_CUTOFF) {
                                // add the one mapped to
                                mappedInTwo.add(val.getValue().getLeft());

                                return true;
                            } else {
                                newModule.getEntities().add(x.getKey().copyWithNamePreface(moduleOne.getName() + "::"));
                                //val.getValue().getLeft().getEntityName().setFullName(moduleTwo.getName() + val.getValue().getLeft().getEntityName().getFullName() + "::");
                                //newModule.getEntities().add(val.getValue().getLeft().copyWithNamePreface(moduleTwo.getName() + "::"));

                                return false;
                            }

                        })

                        // map each mapping between entities to a new merged entity
                        .map(x -> mergeEntities(x.getKey(), x.getValue().lastEntry().getValue().getLeft(), x.getValue().lastEntry().getValue().getRight()))

                        // collect as a list
                        .collect(Collectors.toList())
        );

        // now add all of the entities in module two that were not mapped to
        for(Entity e : moduleTwo.getEntities()){
            if(!mappedInTwo.contains(e)){
                newModule.getEntities().add(e);
            }
        }

        return newModule;
    }

    /**
     * merges two entities together using the field mapping
     * @param one the first entity to merge
     * @param two the second entity to merge
     * @param fieldMapping the mapping between the fields of the entities
     * @return the newly created merged entity
     */
    @Override
    public Entity mergeEntities(Entity one, Entity two, Map<Field, Field> fieldMapping) {

        // the entity that is to be returned
        Entity newEntity = new Entity(one.getEntityName());

        // get the fields of the second entity
        Set<Field> entityTwoFields = new HashSet<>(two.getFields());
        Field toAdd = null;

        if(Objects.isNull(fieldMapping)){
            fieldMapping = new HashMap<>();
        }

        Set<Field> alreadyEncountered = new HashSet<>();

        // make sure that all fields in the field mapping are also in f1 and that no two map to the same value
        for(final Map.Entry<Field, Field> f : fieldMapping.entrySet()){
            if(Objects.isNull(f.getValue())){
                continue;
            }
            // make sure that the key exists
            if(!one.getFields().contains(f.getKey())){
                throw new FieldMappingException();
            }
            //make sure that the value exists
            if(!entityTwoFields.contains(f.getValue())){
                throw new FieldMappingException();
            }
            // if the second has already been mapped too
            if(!alreadyEncountered.add(f.getValue())){
                throw new FieldMappingException();
            }
        }

        // for each field in entity one
        for (Field f1 : one.getFields()){

            // get the field that this field in entity one maps to
            Field f2 = fieldMapping.get(f1);
            toAdd = f1;

            String preface = one.getEntityName() + "::";

            if (f2 != null) {

                //make sure that mapped to field is present in entity 2
                entityTwoFields.remove(f2);

                if (f1.isReference() && f2.isReference() && !f1.equals(f2)) {
                    Field twoCopy = f2.clone();
                    String newName = two.getEntityName().getName() + "::" + twoCopy.getName().getName();
                    twoCopy.getName().setFullName(newName);
                    newEntity.getFields().add(twoCopy);
                    toAdd = f1;
                }

                // merge the fields into one field
                else {
                    preface = "";
                    toAdd = mergeFields(f1, f2);
                }
            }

            // add the field
            // TODO what if a field of this name already exists?
            Field newField = toAdd.clone();
            newField.getName().setFullName(preface + newField.getName().getName());
            newEntity.getFields().add(newField);
        }

        // add all of the remaining fields in entity 2
        // make a copy of all of the field in entity 2
        Set<Field> entityTwoFieldsMapped = entityTwoFields.stream().map(x -> {
            Field fieldCopy = x.clone();
            fieldCopy.getName().setFullName(two.getEntityName().getName() + "::" + fieldCopy.getName().getName());
            return fieldCopy;
        }).collect(Collectors.toSet());
        newEntity.getFields().addAll(entityTwoFieldsMapped);

        return newEntity;
    }

    /**
     * merges two fields into one field
     * @param one the first field to merge
     * @param two the second field to merge
     * @return the new field
     */
    @Override
    public Field mergeFields(Field one, Field two) {
        String name = one.getName().getName();
        String type = Type.get(one.getType()).ordinal() < Type.get(two.getType()).ordinal() ? two.getType() : one.getType();

        Field toReturn = new Field(type, name);

        // set isCollection
        toReturn.setCollection(one.isCollection() || two.isCollection());

        // set the annotations
        toReturn.setAnnotations(one.getAnnotations());
        toReturn.getAnnotations().addAll(two.getAnnotations());

        toReturn.setReference(one.isReference() | two.isReference());

        return toReturn;
    }
}