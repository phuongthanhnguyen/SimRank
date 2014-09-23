package ezgraph;

import es.yrbcn.graph.weighted.*;
import it.unimi.dsi.webgraph.*;
import it.unimi.dsi.webgraph.labelling.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class NodeIterator extends ArcLabelledNodeIterator {

	protected ArcLabelledNodeIterator it1;

	protected ArcLabelledNodeIterator it2;

	public NodeIterator(ArcLabelledNodeIterator it1 , ArcLabelledNodeIterator it2, int n) { this.it1=it1; this.it2=it2; this.it1.skip(n); this.it2.skip(n); }

	public NodeIterator(ArcLabelledNodeIterator it1 , ArcLabelledNodeIterator it2) { this.it1=it1; this.it2=it2; }


        public int degree() { return outdegree() + indegree(); }

	public int outdegree() { return it1.outdegree(); }

	public int indegree() { return it2.outdegree(); }


        public double strength() { return outstrength() + instrength(); }

	public double outstrength() { 
		double strength = 0.0;
		for ( Label l : it1.labelArray() ) strength += l.getFloat();
		return strength;
	}

	public double instrength() { 
		double strength = 0.0;
		for ( Label l : it2.labelArray() ) strength += l.getFloat();
		return strength;
	}


	public int[] successorArray() { return it1.successorArray(); }

	public int[] ancestorArray() { return it2.successorArray(); }


	public Label[] labelArray() { return successorLabelArray(); }

	public Label[] successorLabelArray() { return it1.labelArray(); }

	public Label[] ancestorLabelArray() { return it2.labelArray(); }


	public ArcLabelledNodeIterator.LabelledArcIterator successors() { return it1.successors(); }

	public ArcLabelledNodeIterator.LabelledArcIterator ancestors() { return it2.successors(); }


	public boolean hasNext() { return it1.hasNext() && it2.hasNext(); }

	public void remove() { it1.remove(); it2.remove(); }	

	public int nextInt() {
		int i1 = it1.nextInt();
		int i2 = it2.nextInt();
		if ( i1 != i2 ) throw new Error("Problem with node iterator.");
		return i1;
	}

	public Integer next() {
		Integer i1 = it1.next();
		Integer i2 = it2.next();
		if ( i1 != i2 ) throw new Error("Problem with node iterator.");
		return i1;
	}

	public int skip(int n) {
		Integer i1 = it1.skip(n);
		Integer i2 = it2.skip(n);
		if ( i1 != i2 ) throw new Error("Problem with node iterator.");
		return i1;
	}

}
