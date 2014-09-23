package ezgraph;

/*
import es.yrbcn.graph.weighted.*;
import it.unimi.dsi.webgraph.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
*/

import it.unimi.dsi.webgraph.labelling.*;

public class SimRank {

	private SparseMatrix simrank;

	private Graph graph;

	private double DEFAULT_C = 0.6;

 	public SimRank ( Graph graph ) { this(graph, 0.0000000001, 5); }

 	public SimRank ( Graph graph, double threshold, int maxIter ) {
		this.graph = graph;
		simrank = new SparseMatrix(graph.numNodes());
		SparseMatrix simrank2 = new SparseMatrix(graph.numNodes());
		for ( int step=0; step < maxIter && maxIter > 0; step++ ) {
			double maxDelta = Double.MIN_VALUE;
			for ( int i = 0 ; i < graph.numNodes() ; i++ ) { simrank.set(i,i,1.0); simrank2.set(i,i,1.0); }
			ezgraph.NodeIterator it1 = graph.nodeIterator();
			while ( it1.hasNext() ) {
				int currentVertex1 = it1.nextInt();
				ezgraph.NodeIterator it2 = graph.nodeIterator();
				while ( it2.hasNext() ) {
					int currentVertex2 = it2.nextInt();
					if ( currentVertex1 == currentVertex2 ) continue;
					double quantity = 0.0;
					Integer aux1 = null , aux2 = null;
					ArcLabelledNodeIterator.LabelledArcIterator anc1 = it1.ancestors();
					double sum1 = 0.0;
					while ( (aux1 = anc1.nextInt()) != null && aux1 >= 0 && aux1 < ( graph.numNodes() ) ) sum1 += anc1.label().getFloat();
					anc1 = it1.ancestors();
					while ( (aux1 = anc1.nextInt()) != null && aux1 >= 0 && aux1 < ( graph.numNodes() ) ) {
						double weight1 = anc1.label().getFloat() / sum1;
						ArcLabelledNodeIterator.LabelledArcIterator anc2 = it2.ancestors();
						double sum2 = 0.0;
						while ( (aux2 = anc2.nextInt()) != null && aux2 >= 0 && aux2 < ( graph.numNodes() ) ) sum2 += anc2.label().getFloat();
						anc2 = it2.ancestors();
						while ( (aux2 = anc2.nextInt()) != null && aux2 >= 0 && aux2 < ( graph.numNodes() ) ) {
							double weight2 = anc2.label().getFloat() / sum2;
							quantity += weight1 * weight2 * simrank.get(aux1,aux2);
						}
					}
					if ( quantity != 0.0 ) {
						simrank2.set(currentVertex1,currentVertex2, quantity * ( DEFAULT_C / ( 1.0 * it1.indegree() * it2.indegree() )));
						maxDelta = Math.max(maxDelta, Math.abs( simrank2.get(currentVertex1,currentVertex2) - simrank.get(currentVertex1,currentVertex2) ) );
					}
				}
			}
			simrank = simrank2.clone();
			simrank2 = new SparseMatrix(graph.numNodes());
			if ( maxDelta < threshold && threshold > 0 ) break;
		} 
 	}

	public double getSimRankScore ( int node1, int node2 ) { return simrank.get(node1,node2); }

	public double getSimRankScore ( String node1, String node2 ) { 
		int id1 = graph.node(node1), id2 = graph.node(node2); 
		return simrank.get(id1,id2); 
	}

}
