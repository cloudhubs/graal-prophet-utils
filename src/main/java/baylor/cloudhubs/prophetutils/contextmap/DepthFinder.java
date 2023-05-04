//package baylor.cloudhubs.prophetutils.contextmap;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import baylor.cloudhubs.prophetutils.contextmap.PathFinder.Subsumer;
//
//public class DepthFinder {
//
//	private PathFinder pathFinder;
//	
//	public DepthFinder( ILexicalDatabase db ) {
//		this.pathFinder = new PathFinder( db );
//	}
//	
//	public List<Depth> getRelatedness( Concept synset1, Concept synset2, StringBuilder tracer ) {
//		
//		List<Subsumer> paths = pathFinder.getAllPaths( synset1, synset2, tracer );
//		if ( paths==null || paths.size() == 0 ) return null;
//		
//		//Depth has subsumer, depth and root
//		List<Depth> depthList = new ArrayList<Depth>( paths.size() );
//		for ( Subsumer s : paths ) {
//			List<Depth> depths = getSynsetDepths( s.subsumer.getSynset() );
//			if ( depths==null || depths.size() == 0 ) return null;
//			Depth depth = depths.get(0);
//			depthList.add( depth );
//		}
//		
//		//sort depthMap by depth (desc) --> this seems to be redundant. so let's skip.
//		
//		//Remove non-best elements
//		List<Depth> toBeDeleted = new ArrayList<Depth>( depthList.size() );
//		for ( Depth d : depthList ) {
//			if ( depthList.get(0).depth != d.depth ) toBeDeleted.add( d );
//		}
//		depthList.removeAll( toBeDeleted );
//
//		//Remove duplication
//		Map<Integer,Depth> map = new LinkedHashMap<Integer,Depth>( depthList.size() );
//		for ( Depth d : depthList ) {
//			int key = d.toString().hashCode();
//			map.put(key, d);
//		}
//		depthList = new ArrayList<Depth>( map.values() );
//		
//		return depthList;
//	}
//	
//	//TODO: read depth file for faster access?
//	
//	public List<Depth> getSynsetDepths( String synset ) {
//		Set<String> history = new HashSet<String>();
//		List<List<String>> hyperTrees = pathFinder.getHypernymTrees( synset,history );
//		if ( hyperTrees == null ) return null;
//		
//		List<Depth> depths = new ArrayList<Depth>( hyperTrees.size() );
//		for ( List<String> tree : hyperTrees ) {
//			Depth d = new Depth();
//			d.depth = tree.size();
//			d.root = tree.get(0);
//			d.leaf = synset;
//			depths.add( d );
//		}
//		
//		//asc
//		Collections.sort( depths, new Comparator<Depth>() {
//			public int compare( Depth d1, Depth d2 ) {
//				if ( d1.depth > d2.depth ) {
//					return 1; 
//				} else if ( d1.depth < d2.depth ) {
//					return -1;
//				} else {
//					return 0;
//				}
//			}
//		});
//		
//		return depths;
//	}
//	
//	public static class Depth {
//		public String leaf;
//		public int depth;
//		public String root;
//		@Override
//		public String toString() {
//			StringBuffer sb = new StringBuffer();
//			sb.append("{ ");
//			sb.append("\"depth\":\""+depth+"\", ");
//			sb.append("\"leaf\":\""+leaf+"\", ");
//			sb.append("\"root\":\""+root+"\"");
//			sb.append(" }");
//			return sb.toString();
//		}
//	}
//		
//	public int getShortestDepth( Concept synset ) {
//		try {
//			return getSynsetDepths( synset.getSynset() ).get(0).depth;
//		} catch ( Exception e ) {
//			return 0;
//		}
//	}
//	
//}