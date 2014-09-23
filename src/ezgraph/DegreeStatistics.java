package ezgraph;

import es.yrbcn.graph.weighted.*;
import it.unimi.dsi.webgraph.*;
import it.unimi.dsi.webgraph.labelling.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class DegreeStatistics {

  protected double minIndegree;

  protected double maxIndegree;

  protected double avgIndegree;

  protected double minOutdegree;

  protected double maxOutdegree;

  protected double avgOutdegree;
  
  protected double minDegree;

  protected double maxDegree;

  protected double avgDegree;

  protected double minInStrength;

  protected double maxInStrength;

  protected double avgInStrength;

  protected double minOutStrength;

  protected double maxOutStrength;

  protected double avgOutStrength;
  
  protected double minStrength;

  protected double maxStrength;

  protected double avgStrength;

  public DegreeStatistics ( Graph graph ) { this(graph,false); }

  public DegreeStatistics ( Graph graph, boolean print ) {
	ezgraph.NodeIterator it = graph.nodeIterator();
        minIndegree = Double.MAX_VALUE;
        maxIndegree = Double.MIN_VALUE;
        avgIndegree = 0.0;
        minOutdegree = Double.MAX_VALUE;
        maxOutdegree = Double.MIN_VALUE;
        avgOutdegree = 0.0;
        minDegree = Double.MAX_VALUE;
        maxDegree = Double.MIN_VALUE;
        avgDegree = 0.0;
        minInStrength = Double.MAX_VALUE;
        maxInStrength = Double.MIN_VALUE;
        avgInStrength = 0.0;
        minOutStrength = Double.MAX_VALUE;
        maxOutStrength = Double.MIN_VALUE;
        avgOutStrength = 0.0;
        minStrength = Double.MAX_VALUE;
        maxStrength = Double.MIN_VALUE;
        avgStrength = 0.0;
	while ( it.hasNext() ) {
		Integer node = it.nextInt();
		double indegree = it.indegree();
		double outdegree = it.outdegree();
		double degree = it.degree();
		double instrength = it.instrength();
		double outstrength = it.outstrength();
		double strength = it.strength();
		if ( print ) System.out.println(indegree + "\t" + outdegree + "\t" + degree + "\t" + instrength + "\t" + outstrength + "\t" + strength);
		avgInStrength += instrength;
		avgOutStrength += outstrength;
		avgStrength += strength;
		avgIndegree += indegree;
		avgOutdegree += outdegree;
		avgDegree += degree;
		if ( indegree > maxIndegree ) maxIndegree = indegree;
		if ( indegree < minIndegree ) minIndegree = indegree;
		if ( outdegree > maxOutdegree ) maxOutdegree = outdegree;
		if ( outdegree < minOutdegree ) minOutdegree = outdegree;
		if ( degree > maxDegree ) maxDegree = degree;
		if ( degree < minDegree ) minDegree = degree;
		if ( instrength > maxInStrength ) maxInStrength = instrength;
		if ( instrength < minInStrength ) minInStrength = instrength;
		if ( outstrength > maxOutStrength ) maxOutStrength = outstrength;
		if ( outstrength < minOutStrength ) minOutStrength = outstrength;
		if ( strength > maxStrength ) maxStrength = strength;
		if ( strength < minStrength ) minStrength = strength;
	}
	avgIndegree = avgIndegree / (double)(graph.numNodes());
	avgOutdegree = avgOutdegree / (double)(graph.numNodes());
	avgDegree = avgDegree / (graph.numNodes() * 2.0);
	avgInStrength = avgInStrength / (double)(graph.numNodes());
	avgOutStrength = avgOutStrength / (double)(graph.numNodes());
	avgStrength = avgStrength / (graph.numNodes() * 2.0);
  }

  public double minIndegree ( ) { return minIndegree; }

  public double maxIndegree ( ) { return maxIndegree; }

  public double avgIndegree ( ) { return avgIndegree; }

  public double minOutdegree ( ) { return minOutdegree; }

  public double maxOutdegree ( ) { return maxOutdegree; }

  public double avgOutdegree ( ) { return avgOutdegree; }
  
  public double minDegree ( ) { return minDegree; }

  public double maxDegree ( ) { return maxDegree; }

  public double avgDegree ( ) { return avgDegree; }

  public double minInstrength ( ) { return minInStrength; }

  public double maxInstrength ( ) { return maxInStrength; }

  public double avgInstrength ( ) { return avgInStrength; }

  public double minOutstrength ( ) { return minOutStrength; }

  public double maxOutstrength ( ) { return maxOutStrength; }

  public double avgOutstrength ( ) { return avgOutStrength; }
  
  public double minStrength ( ) { return minStrength; }

  public double maxStrength ( ) { return maxStrength; }

  public double avgStrength ( ) { return avgStrength; }

}
