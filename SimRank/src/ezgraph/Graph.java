package ezgraph;

import es.yrbcn.graph.weighted.*;
import it.unimi.dsi.webgraph.*;
import it.unimi.dsi.webgraph.labelling.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.lang.reflect.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.logging.ProgressLogger;

public class Graph extends ArcLabelledImmutableGraph {

  protected ezgraph.NodeIterator iterator;

  protected Int2ObjectMap<String> nodes;

  protected Object2IntMap<String> nodesReverse;

  protected ArcLabelledImmutableGraph graph;
  
  protected ArcLabelledImmutableGraph reverse;

  protected int numArcs;

  public Graph ( String file ) throws IOException {
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	numArcs = 0;
	nodes = new Int2ObjectOpenHashMap<String>();
	nodesReverse = new Object2IntOpenHashMap<String>();
	String aux = null;
	Float weight = (float)1.0;
	Set<WeightedArc> list = new HashSet<WeightedArc>();
	BufferedReader br;
	try { 
		br = new BufferedReader(new InputStreamReader( new GZIPInputStream( new FileInputStream(file) ))); 
	} catch ( Exception ex ) { 
		br = new BufferedReader(new FileReader(file));
	}
	while ( ( aux = br.readLine() ) != null ) try {
		String parts[] = aux.split("\t");
		String l1 = new String(parts[0]);
		String l2 = new String(parts[1]);
		if ( !nodesReverse.containsKey(l1) ) { nodesReverse.put(l1, nodesReverse.size()); nodes.put(nodes.size(), l1); }
		if ( !nodesReverse.containsKey(l2) ) { nodesReverse.put(l2, nodesReverse.size()); nodes.put(nodes.size(), l2); }
		if ( parts.length == 3 ) weight = new Float(parts[2]);
		list.add((WeightedArc)cons[0].newInstance(nodesReverse.get(l1),nodesReverse.get(l2),weight));
	} catch ( Exception ex ) { throw new Error(ex); }
	this.graph = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	br.close();
	list = new HashSet<WeightedArc>();
	br = new BufferedReader(new FileReader(file));
	while ( ( aux = br.readLine() ) != null ) try {
		String parts[] = aux.split("\t");
		String l1 = new String(parts[0]);
		String l2 = new String(parts[1]);
		if ( parts.length == 3 ) weight = new Float(parts[2]);
		list.add((WeightedArc)cons[0].newInstance(nodesReverse.get(l2),nodesReverse.get(l1),weight));
		numArcs++;
	} catch ( Exception ex ) { throw new Error(ex); }
	br.close();
	this.reverse = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	numArcs = list.size();
	iterator = nodeIterator();
  }

  public Graph ( WeightedArc[] arcs ) { this( new WeightedBVGraph( arcs ) ); }

  public Graph ( WeightedArc[] arcs, String[] names ) { this( new WeightedBVGraph( arcs ) , names ); }

  public Graph ( ArcLabelledImmutableGraph graph ) { }

  public Graph ( ArcLabelledImmutableGraph graph , String names[] ) { }

  public Graph ( WeightedBVGraph graph ) {
	this.nodes = new Int2ObjectOpenHashMap<String>(graph.numNodes());
	this.nodesReverse = new Object2IntOpenHashMap<String>(graph.numNodes());
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	this.graph = graph;
	Set<WeightedArc> list = new HashSet<WeightedArc>();
	ArcLabelledNodeIterator it = graph.nodeIterator();
	while ( it.hasNext() ) {
		Integer aux1 = it.nextInt();
		Integer aux2 = null;
		ArcLabelledNodeIterator.LabelledArcIterator suc = it.successors();
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  WeightedArc arc = (WeightedArc)cons[0].newInstance(aux2, aux1, suc.label().getFloat());
		  list.add(arc);
		  this.nodes.put(aux1, "" + aux1);
		  this.nodes.put(aux2, "" + aux2);
		  this.nodesReverse.put("" + aux1, aux1);
		  this.nodesReverse.put("" + aux2, aux2);
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	reverse = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	numArcs = list.size();
	iterator = nodeIterator();
  }

  public Graph ( WeightedBVGraph graph, String[] names ) {
	if ( names.length != graph.numNodes() ) throw new Error("Problem with the list of names for the nodes in the graph.");
	this.nodes = new Int2ObjectOpenHashMap<String>(graph.numNodes());
	this.nodesReverse = new Object2IntOpenHashMap<String>(graph.numNodes());
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	this.graph = graph;
	Set<WeightedArc> list = new HashSet<WeightedArc>();
	ArcLabelledNodeIterator it = graph.nodeIterator();
	while ( it.hasNext() ) {
		Integer aux1 = it.nextInt();
		Integer aux2 = null;
		ArcLabelledNodeIterator.LabelledArcIterator suc = it.successors();
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  WeightedArc arc = (WeightedArc)cons[0].newInstance(aux2, aux1, suc.label().getFloat());
		  list.add(arc);
		  this.nodes.put(aux1, names[aux1]);
		  this.nodes.put(aux2, names[aux2]);
		  this.nodesReverse.put(names[aux1], aux1);
		  this.nodesReverse.put(names[aux2], aux2);
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	reverse = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	numArcs = list.size();
	iterator = nodeIterator();
  }

  public Graph ( BVGraph graph ) {
	this.nodes = new Int2ObjectOpenHashMap<String>(graph.numNodes());
	this.nodesReverse = new Object2IntOpenHashMap<String>(graph.numNodes());
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	Integer aux1 = null;
	Set<WeightedArc> list = new HashSet<WeightedArc>();
	it.unimi.dsi.webgraph.NodeIterator it = graph.nodeIterator();
	while ( (aux1 = it.nextInt()) != null) {
		LazyIntIterator suc = it.successors();
		Integer aux2 = null;
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  list.add((WeightedArc)cons[0].newInstance(aux1,aux2,(float)1.0));
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	this.graph = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	list = new HashSet<WeightedArc>();
	it = graph.nodeIterator();
	while ( (aux1 = it.nextInt()) != null) {
		LazyIntIterator suc = it.successors();
		Integer aux2 = null;
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  list.add((WeightedArc)cons[0].newInstance(aux2,aux1,(float)1.0));
		  this.nodes.put(aux1, "" + aux1);
		  this.nodes.put(aux2, "" + aux2);
		  this.nodesReverse.put("" + aux1, aux1);
		  this.nodesReverse.put("" + aux2, aux2);
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	this.reverse = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	numArcs = list.size();
	iterator = nodeIterator();
  }

  public Graph ( BVGraph graph, String[] names ) {
	if ( names.length != graph.numNodes() ) throw new Error("Problem with the list of names for the nodes in the graph.");
	this.nodes = new Int2ObjectOpenHashMap<String>(graph.numNodes());
	this.nodesReverse = new Object2IntOpenHashMap<String>(graph.numNodes());
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	Integer aux1 = null;
	Set<WeightedArc> list = new HashSet<WeightedArc>();
	it.unimi.dsi.webgraph.NodeIterator it = graph.nodeIterator();
	while ( (aux1 = it.nextInt()) != null) {
		LazyIntIterator suc = it.successors();
		Integer aux2 = null;
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  list.add((WeightedArc)cons[0].newInstance(aux1,aux2,(float)1.0));
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	this.graph = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	list = new HashSet<WeightedArc>();
	it = graph.nodeIterator();
	while ( (aux1 = it.nextInt()) != null) {
		LazyIntIterator suc = it.successors();
		Integer aux2 = null;
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  list.add((WeightedArc)cons[0].newInstance(aux2,aux1,(float)1.0));
		  this.nodes.put(aux1, names[aux1]);
		  this.nodes.put(aux2, names[aux2]);
		  this.nodesReverse.put(names[aux1], aux1);
		  this.nodesReverse.put(names[aux2], aux2);
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	this.reverse = new WeightedBVGraph( list.toArray( new WeightedArc[0] ) );
	numArcs = list.size();
	iterator = nodeIterator();
  }

  public static Graph merge ( Graph g1 , Graph g2 ) {
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	Set<WeightedArc> list = new HashSet<WeightedArc>();
	ArcLabelledNodeIterator it1 = g1.graph.nodeIterator();
	while ( it1.hasNext() ) {
		Integer aux1 = it1.nextInt();
		ArcLabelledNodeIterator.LabelledArcIterator suc = it1.successors();
		Integer aux2 = null;
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < g1.graph.numNodes() ) ) try {
		  WeightedArc arc = (WeightedArc)cons[0].newInstance(aux1,aux2, suc.label().getFloat());
		  list.add(arc);
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	ArcLabelledNodeIterator it2 = g2.graph.nodeIterator();
	while ( it2.hasNext() ) {
		Integer aux1 = it2.nextInt();
		ArcLabelledNodeIterator.LabelledArcIterator suc = it2.successors();
		Integer aux2 = null;
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < g2.graph.numNodes() ) ) try {
		  int aaux1 = aux1 + g1.numNodes();
		  int aaux2 = aux2 + g1.numNodes();
		  if ( g1.nodes.get(aux1) != null && g1.nodes.get(aux1).equals(g2.nodes.get(aux1)) ) aaux1 = g1.nodesReverse.get(g2.nodes.get(aux1));
		  if ( g1.nodes.get(aux2) != null && g1.nodes.get(aux2).equals(g2.nodes.get(aux2)) ) aaux2 = g1.nodesReverse.get(g2.nodes.get(aux2));
		  WeightedArc arc = (WeightedArc)cons[0].newInstance(aux1, aux2, suc.label().getFloat());
		  list.add(arc);
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	Graph result = new Graph(list.toArray(new WeightedArc[0]));
	result.nodes = new Int2ObjectOpenHashMap<String>(result.numNodes());
	result.nodesReverse = new Object2IntOpenHashMap<String>(result.numNodes());
	for ( Integer n : g1.nodes.keySet() ) {
		result.nodesReverse.put(g1.nodes.get(n) , n);
		result.nodes.put(n , g1.nodes.get(n));
	}
	for ( Integer n : g2.nodes.keySet() ) {
		int nn = n + g1.numNodes();
		if ( g1.nodes.get(n) != null && g1.nodes.get(n).equals(g2.nodes.get(n)) ) nn = g1.nodesReverse.get(g2.nodes.get(n));
		result.nodesReverse.put(g2.nodes.get(n) , nn);
		result.nodes.put(nn , g2.nodes.get(n));
	}
	result.iterator = result.nodeIterator();
	return result;
  }

  public Graph copy() {
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	Set<WeightedArc> list = new HashSet<WeightedArc>();
	ArcLabelledNodeIterator it = graph.nodeIterator();
	while ( it.hasNext() ) {
		Integer aux1 = it.nextInt();
		Integer aux2 = null;
		ArcLabelledNodeIterator.LabelledArcIterator suc = it.successors();
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  WeightedArc arc = (WeightedArc)cons[0].newInstance(aux2, aux1, suc.label().getFloat());
		  list.add(arc);
                } catch ( Exception ex ) { throw new Error(ex); }
	}
	Graph result = new Graph( list.toArray( new WeightedArc[0] ) );
	result.nodes = new Int2ObjectOpenHashMap<String>(result.numNodes());
	result.nodesReverse = new Object2IntOpenHashMap<String>(result.numNodes());
	for ( Integer n : this.nodes.keySet() ) {
		result.nodesReverse.put(this.nodes.get(n) , n);
		result.nodes.put(n , this.nodes.get(n));
	}
	return result;
  }

  public void store ( CharSequence basename ) throws java.io.IOException {
	BVGraph.store( graph, basename + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX );
	BitStreamArcLabelledImmutableGraph.store( graph, basename, basename + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX );
	graph = WeightedBVGraph.load(basename + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX);

	BVGraph.store( reverse, basename + ".reverse" + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX );
	BitStreamArcLabelledImmutableGraph.store( reverse, basename + ".reverse", basename  + ".reverse" + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX );
	reverse = WeightedBVGraph.load(basename + ".reverse" + ArcLabelledImmutableGraph.UNDERLYINGGRAPH_SUFFIX);
  }

  public static Graph load(CharSequence basename) throws java.io.IOException { return new Graph(WeightedBVGraph.load(basename)); }
           
  public static Graph load(CharSequence basename, ProgressLogger pl) throws java.io.IOException { return new Graph(WeightedBVGraph.load(basename,pl)); }
           
  public static Graph loadOffline(CharSequence basename) throws java.io.IOException { return new Graph(WeightedBVGraph.loadOffline(basename)); }
           
  public static Graph loadOffline(CharSequence basename, ProgressLogger pl) throws java.io.IOException { return new Graph(WeightedBVGraph.loadOffline(basename,pl)); }
           
  public static Graph loadOnce(InputStream is) throws java.io.IOException { return new Graph(WeightedBVGraph.loadOnce(is));}
           
  public static Graph loadSequential(CharSequence basename) throws java.io.IOException { return new Graph(WeightedBVGraph.loadSequential(basename)); }
           
  public static Graph loadSequential(CharSequence basename, ProgressLogger pl) throws java.io.IOException { return new Graph(WeightedBVGraph.loadSequential(basename,pl)); }

  private NodeIterator advanceIterator ( int x ) {
	if ( x >= graph.numNodes() ) throw new Error("Problem with the id for the node.");
	if ( !iterator.hasNext() || iterator.nextInt() >= x ) iterator = nodeIterator();
	Integer aux = null;
	while ( (aux = iterator.nextInt()) != x ) {  }
	return iterator;
  }

  public int degree( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.degree(); }

  public int outdegree( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.outdegree(); }

  public int indegree( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.indegree(); }

  public double strength( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.strength(); }

  public double outstrength( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.outstrength(); }

  public double instrength( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.instrength(); }

  public int[] successorArray( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.successorArray(); }

  public int[] ancestorArray( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.successorArray(); }

  public Label[] labelArray( int x ) { return successorLabelArray(x); }

  public Label[] successorLabelArray( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.labelArray(); }

  public Label[] ancestorLabelArray( int x ) { NodeIterator iterator = advanceIterator(x); return iterator.labelArray(); }

  public ArcLabelledNodeIterator.LabelledArcIterator successors(int x) { NodeIterator iterator = advanceIterator(x); return iterator.successors(); }

  public ArcLabelledNodeIterator.LabelledArcIterator ancestors(int x) { NodeIterator iterator = advanceIterator(x); return iterator.ancestors(); }

  public String node ( int nodeNum ) { return nodes.get(nodeNum); }

  public int node ( String node ) { return nodesReverse.get(node); }

  public void setNode ( int nodeNum , String node ) { nodes.put(nodeNum,node); }

  public NodeIterator nodeIterator() { return new ezgraph.NodeIterator(graph.nodeIterator(),reverse.nodeIterator());  }

  public NodeIterator nodeIterator(int from) { return new ezgraph.NodeIterator(graph.nodeIterator(), reverse.nodeIterator(), from); }

  public long numArcs() { return numArcs; }

  public int numNodes() { return graph.numNodes(); }

  public Graph neighbourhoodGraph ( int node , int hops ) { return neighbourhoodGraph ( new int[]{ node } , hops ); }

  public Graph neighbourhoodGraph ( String node , int hops ) { return neighbourhoodGraph ( new String[]{ node } , hops ); }

  public Graph neighbourhoodGraph ( String nodes[] , int hops ) {
	int nnodes[] = new int[nodes.length];
	for ( int i = 0; i < nodes.length; i++ ) nnodes[i] = nodesReverse.get(nodes[i]);
	return neighbourhoodGraph(nnodes, hops);
  }

  public Graph neighbourhoodGraph ( int nnodes[] , int hops ) {
        Constructor[] cons = WeightedArc.class.getDeclaredConstructors();
        for ( int i = 0; i< cons.length; i++) cons[i].setAccessible(true);
	Set<WeightedArc> list1 = new HashSet<WeightedArc>();
	Int2IntAVLTreeMap map = new Int2IntAVLTreeMap();
	IntSet set = new IntLinkedOpenHashSet();
	for ( int n : nnodes ) map.put(n,0);
	NodeIterator it = nodeIterator();
        Int2ObjectMap<String> nodes = new Int2ObjectOpenHashMap<String>();
        Object2IntMap<String> nodesReverse = new Object2IntOpenHashMap<String>();
	while ( map.size() != 0 ) {
		Integer node = map.firstKey();
		Integer aux1 = null;
		while ( ( aux1 = it.nextInt() ) != null && aux1 >= 0 && aux1 < node ) { }
		if ( aux1 == null || aux1 > node ) { it = nodeIterator(); continue; }
		Integer aux2 = null;
		ArcLabelledNodeIterator.LabelledArcIterator suc = it.successors();
		while ( (aux2 = suc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  if ( !nodesReverse.containsKey(this.nodes.get(aux1)) ) {
			  nodes.put(nodes.size(), this.nodes.get(aux1));
		  	  nodesReverse.put(this.nodes.get(aux1), nodesReverse.size());
		  }
		  if ( !nodesReverse.containsKey(this.nodes.get(aux2)) ) {
			  nodes.put(nodes.size(), this.nodes.get(aux2));
			  nodesReverse.put(this.nodes.get(aux2), nodesReverse.size());
		  }
		  int aaux1 = nodesReverse.get(this.nodes.get(aux1));
		  int aaux2 = nodesReverse.get(this.nodes.get(aux2));
		  WeightedArc arc1 = (WeightedArc)cons[0].newInstance(aaux1,aaux2, suc.label().getFloat());
		  list1.add(arc1);
		  if ( map.get(node) < hops ) {
			if(!set.contains(aux1) && (map.get(aux1)==null || map.get(aux1) > map.get(node) + 1)) map.put(aux1.intValue(), map.get(node) + 1);
			if(!set.contains(aux2) && (map.get(aux2)==null || map.get(aux2) > map.get(node) + 1)) map.put(aux2.intValue(), map.get(node) + 1);
		  }
                } catch ( Exception ex ) { ex.printStackTrace(); throw new Error(ex); }
		ArcLabelledNodeIterator.LabelledArcIterator anc = it.ancestors();
		while ( (aux2 = anc.nextInt()) != null && aux2 >= 0 && ( aux2 < graph.numNodes() ) ) try {
		  if ( !nodesReverse.containsKey(this.nodes.get(aux1)) ) {
			  nodes.put(nodes.size(), this.nodes.get(aux1));
		  	  nodesReverse.put(this.nodes.get(aux1), nodesReverse.size());
		  }
		  if ( !nodesReverse.containsKey(this.nodes.get(aux2)) ) {
			  nodes.put(nodes.size(), this.nodes.get(aux2));
			  nodesReverse.put(this.nodes.get(aux2), nodesReverse.size());
		  }
		  int aaux1 = nodesReverse.get(this.nodes.get(aux1));
		  int aaux2 = nodesReverse.get(this.nodes.get(aux2));
		  WeightedArc arc1 = (WeightedArc)cons[0].newInstance(aaux2,aaux1,anc.label().getFloat());
		  list1.add(arc1);
		  if ( map.get(node) < hops ) {
			if(!set.contains(aux1) && (map.get(aux1)==null || map.get(aux1) > map.get(node) + 1)) map.put(aux1.intValue(), map.get(node) + 1);
			if(!set.contains(aux2) && (map.get(aux2)==null || map.get(aux2) > map.get(node) + 1)) map.put(aux2.intValue(), map.get(node) + 1);
		  }
                } catch ( Exception ex ) { ex.printStackTrace(); throw new Error(ex); }
		map.remove(node);
		set.add(node);
	}
	Graph newGraph = new Graph(list1.toArray(new WeightedArc[0]));
	newGraph.nodes = nodes;
	newGraph.nodesReverse = nodesReverse;
	return newGraph;
  } 

  public Label prototype ( ) { return graph.prototype(); }

  public boolean randomAccess() { return true; }

}
