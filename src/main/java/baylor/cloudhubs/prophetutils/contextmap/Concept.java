package baylor.cloudhubs.prophetutils.contextmap;

/**
 * Concept can be synset, or any semantic node.  
 * @author Hideki Shima
 *
 */
public class Concept implements Cloneable {

	private String synset;
	private POS pos;
	private String name;
	private String src;
	
	public Concept( String synset ) {
		this.synset = synset; 
	}

	public Concept( String synset, POS pos ) {
		this.synset = synset;
		this.pos = pos; 
	}
	
	public Concept( String synset, POS pos, String name, String src ) {
		this.synset = synset;
		this.pos = pos;
		this.name = name;
		this.src = src;
	}

	/**
	 * Dump the content into String in JSON format
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "{ " );
		sb.append( "\"synset\":\""+synset+"\", " );
		sb.append( "\"pos\":\""+pos+"\", " );
		sb.append( "\"name\":\""+name+"\", " );
		sb.append( "\"src\":\""+src+"\"" );
		sb.append( " }" );
		return sb.toString();
	}
	
	/**
	 * @return the synset
	 */
	public String getSynset() {
		return synset;
	}

	/**
	 * @param synset the synset to set
	 */
	public void setSynset(String synset) {
		this.synset = synset;
	}

	/**
	 * @return the pos
	 */
	public POS getPos() {
		if ( pos==null ) {
//			Synset realSynset = SynsetDAO.findSynsetBySynset( getSynset() );
//			setName( realSynset.getName() );
//			setPos( realSynset.getPos() );
//			setSrc( realSynset.getSrc() );
		}
		return pos;
	}

	/**
	 * @param pos the pos to set
	 */
	public void setPos(POS pos) {
		this.pos = pos;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		if ( name==null ) {
//			Synset realSynset = SynsetDAO.findSynsetBySynset( getSynset() );
//			setName( realSynset.getName() );
//			setPos( realSynset.getPos() );
//			setSrc( realSynset.getSrc() );
//			return name;
		}
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	@Override
	public Concept clone() {
		return new Concept( synset, POS.valueOf(pos.toString()), name, src );
	}
}