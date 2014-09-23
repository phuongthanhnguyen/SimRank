package ezgraph;

import es.yrbcn.graph.weighted.*;
import it.unimi.dsi.webgraph.*;
import it.unimi.dsi.webgraph.labelling.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class StronglyConnectedComponents {

	it.unimi.dsi.webgraph.algo.StronglyConnectedComponents components;

 	public StronglyConnectedComponents ( Graph graph, boolean computeBuckets ) {
		//components = it.unimi.dsi.webgraph.algo.StronglyConnectedComponents.compute(graph, computeBuckets, null);
	}

	public int numberOfComponents() { return components.numberOfComponents; }

	public int component(int node) { return components.component[node]; }

	public int[] components() { return components.component; }

}
