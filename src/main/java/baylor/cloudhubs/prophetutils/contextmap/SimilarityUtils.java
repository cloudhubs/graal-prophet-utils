package baylor.cloudhubs.prophetutils.contextmap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import java.util.Map;

/**
 * similaity util methods
 */
public interface SimilarityUtils {

    /**
     * finds the similarity between two fields
     *
     * @param fieldOne the first field to compare
     * @param fieldTwo the second field to compare
     * @return the similarity of the fields
     */
    double localFieldSimilarity(Field fieldOne, Field fieldTwo, boolean useWuPalmer);

    /**
     * find the similarity of two entities
     *
     * @param entityOne first entity to find similarity of
     * @param entityTwo second entity to find similarity of
     * @return tuple of similarity of the entities as well as the mapping between their fields
     */
    ImmutablePair<Double, Map<Field, Field>> globalFieldSimilarity(Entity entityOne, Entity entityTwo, boolean useWuPalmer);

    /**
     * finds the similalrity of two names (i.e. nouns)
     *
     * @param one the first name to comapare
     * @param two the second name to compare
     * @return the Wu Palmer similarity of these names
     */
    double nameSimilarity(String one, String two, boolean useWuPalmer);
}