package baylor.cloudhubs.prophetutils.contextmap;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.lti.jawjaw.db.SenseDAO;
import edu.cmu.lti.jawjaw.db.SynlinkDAO;
import edu.cmu.lti.jawjaw.db.WordDAO;
import edu.cmu.lti.jawjaw.pobj.Lang;
import edu.cmu.lti.jawjaw.pobj.Link;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.pobj.Sense;
import edu.cmu.lti.jawjaw.pobj.Synlink;
import edu.cmu.lti.jawjaw.pobj.Synset;
import edu.cmu.lti.jawjaw.pobj.SynsetDef;
import edu.cmu.lti.jawjaw.pobj.Word;

public class WordNetUtil {

	public static List<Synset> wordToSynsets( String word, POS pos ) {
		List<Word> words = WordDAO.findWordsByLemmaAndPos(word, pos);
		List<Synset> results = new ArrayList<Synset>();
		for ( Word wordObj : words ) {
			int wordid = wordObj.getWordid();
			List<Sense> senses = SenseDAO.findSensesByWordid( wordid );
			for ( Sense sense : senses ) {
				Synset synset = new Synset( sense.getSynset(), null, null, null );
				results.add( synset );
			}
		}
		return results;
	}

	public static List<Word> synsetToWords( String synset ) {	
		List<Word> words = new ArrayList<Word>();
		List<Sense> senses = SenseDAO.findSensesBySynset( synset );
		for ( Sense sense : senses ) {
			Word word = WordDAO.findWordByWordid( sense.getWordid() );
			words.add( word );
		}
		return words;
	}

	public static String getGloss( SynsetDef synsetDef ) {
		/*
		 * def field looks like this:
		 * powerful and effective language; "his eloquence attracted a large congregation"; "fluency in spoken and written English is essential"; "his oily smoothness concealed his guilt from the police"
		 */	
		String gloss = synsetDef.getDef().replaceFirst("; \".+", "");
		
		return gloss;
	}

	/**
	 * Find words that have a specific relationship with the given word
	 * @param word
	 * @param pos
	 * @param link
	 * @return words
	 */
	public static Set<String> findLinks( String word, POS pos, Link link ) {
		Set<String> results = new LinkedHashSet<String>();
		List<Synset> synsets = wordToSynsets( word, pos );
		Lang lang = findLang(word);
		for ( Synset synset : synsets ) {
			List<Synlink> synlinks = SynlinkDAO.findSynlinksBySynsetAndLink(synset.getSynset(), link);
			for ( Synlink synlink : synlinks ) {
				List<Sense> senses = SenseDAO.findSensesBySynsetAndLang(synlink.getSynset2(), lang);
				for ( Sense sense : senses ) {
					Word wordObj = WordDAO.findWordByWordid( sense.getWordid() );
					results.add( wordObj.getLemma() );
				}
			}
		}
		return results;
	}
	
	public static Lang findLang( String word ) {
		List<Word> words = WordDAO.findWordsByLemma(word);
		if ( words.size() > 0 ) {
			return words.get(0).getLang();
		} else {
			// default = jpn
			return Lang.jpn;
		}
	}
	
	public static Set<String> findSynonyms( String word, POS pos, boolean translate ) {
		Set<String> results = new LinkedHashSet<String>();
		List<Synset> synsets = WordNetUtil.wordToSynsets( word, pos );
		Lang srcLang = findLang( word );
		Lang anotherLang = srcLang.equals(Lang.jpn)?Lang.eng:Lang.jpn;
		Lang targetLang = translate?anotherLang:srcLang;
		for ( Synset synset : synsets ) {
			List<Sense> moreSenses = SenseDAO.findSensesBySynsetAndLang(synset.getSynset(), targetLang);
			for ( Sense moreSense : moreSenses ) {
				Word synonym = WordDAO.findWordByWordid( moreSense.getWordid() );
				results.add( synonym.getLemma() );
			}
		}
		// remove the original if any
		results.remove( word );
		return results;
	}
	
}