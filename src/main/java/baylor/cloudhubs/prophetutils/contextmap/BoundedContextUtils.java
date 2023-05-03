package baylor.cloudhubs.prophetutils.contextmap;

import java.util.Map;

/**
 * Module's utility class to create bounded context from the system context
 */
public interface BoundedContextUtils {

    /**
     * creates bounded context from a system context
     * @param systemContext the system context
     * @return the bounded context
     */
    BoundedContext createBoundedContext(SystemContext systemContext, boolean useWuPalmer);

    /**
     * merges two entities together using the field mapping
     * @param one the first entity to merge
     * @param two the second entity to merge
     * @param fieldMapping the mapping between the fields of the entities
     * @return the newly creataed merged entity
     */
    Entity mergeEntities(Entity one, Entity two, Map<Field, Field> fieldMapping);

    /**
     * merges two modules into one module
     * @param one one of the modules
     * @param two the other module
     * @return a new module comprised of the other two
     */
    Module mergeModules(Module one, Module two, boolean useWuPalmer);

    /**
     * merges two fields into one field
     * @param one the first field to merge
     * @param two the second field to merge
     * @return the new field
     */
    Field mergeFields(Field one, Field two);

}
