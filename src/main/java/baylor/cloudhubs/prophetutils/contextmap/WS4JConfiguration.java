package baylor.cloudhubs.prophetutils.contextmap;

import java.io.InputStream;
import java.util.Properties;

final public class WS4JConfiguration {

	private final static String CONF = "/similarity.conf";
	
	private static final WS4JConfiguration instance = new WS4JConfiguration();
	private Properties p;
	private boolean trace;
	private boolean cache;
	private int maxCacheSize;
	private String infoContent;
	private boolean stem;
	private String stopList;
	//private String leskRelation;
	private boolean leskNormalize;
	private boolean mfs;

	/**
	 * Private constructor 
	 */
	private WS4JConfiguration(){
		InputStream stream = null;
		try {
			stream = WS4JConfiguration.class.getResourceAsStream( CONF );
			p = new Properties();
			p.load( stream );
			cache = readInt("cache",1)==1;
			trace = readInt("trace",0)==1;
			maxCacheSize = readInt("maxCacheSize",1000);
			infoContent = readString("infocontent","ic-semcor.dat");
			stem = readInt("stem",0)==1;
			stopList = readString("stoplist","stoplist.txt");
			//leskRelation = readString("lesk-relation","lesk-relation.dat");
			leskNormalize = readInt("lesk-normalize", 0)==1;
			mfs = readInt("mfs", 0)==1;
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int readInt( String key, int defaultValue ) {
		try {
			return Integer.parseInt(readString(key, defaultValue+""));
		} catch ( Exception e ) {
			return defaultValue;
		}
	}
	
	private String readString( String key, String defaultValue ) {
		String value = p.getProperty(key);
		if ( value == null ) {
			System.err.println( "Configuration \""+key+"\" not found in "+CONF );
			return defaultValue;
		}
		value = value.replaceAll("#.+", "");
		value = value.trim();
		return value;
	}
	
	/**
	 * Singleton pattern
	 * @return singleton object
	 */
	public static WS4JConfiguration getInstance(){
		return WS4JConfiguration.instance;
	}

	/**
	 * @return cache
	 */
	public boolean useCache() {
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}
	
	/**
	 * @param trace the trace to set
	 */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	/**
	 * @return trace
	 */
	public boolean useTrace() {
		return trace;
	}

	/**
	 * @return the infoContent
	 */
	public String getInfoContent() {
		return infoContent;
	}

	/**
	 * @return the stem
	 */
	public boolean useStem() {
		return stem;
	}
	
	/**
	 * @return the stopList
	 */
	public String getStopList() {
		return stopList;
	}

	/**
	 * @param stopList the stopList to set
	 */
	public void setStopList(String stopList) {
		this.stopList = stopList;
	}
	
	/* (not yet supported)
	 * @return the leskRelation
	 
	public String getLeskRelation() {
		return leskRelation;
	}
	*/
	
	/**
	 * @return the stem
	 */
	public boolean useLeskNomalizer() {
		return leskNormalize;
	}
	
	/**
	 * @param leskNormalize the leskNormalize to set
	 */
	public void setLeskNormalize(boolean leskNormalize) {
		this.leskNormalize = leskNormalize;
	}

	/**
	 * @return the maxCacheSize
	 */
	public int getMaxCacheSize() {
		return maxCacheSize;
	}
	

	/**
	 * @return the stem
	 */
	public boolean useMFS() {
		return mfs;
	}

	/**
	 * @param mfs the mfs to set
	 */
	public void setMFS(boolean mfs) {
		this.mfs = mfs;
	}

	/**
	 * @param stem the stem to set
	 */
	public void setStem(boolean stem) {
		this.stem = stem;
	}
	
}