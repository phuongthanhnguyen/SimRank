package ezgraph;

import java.util.*;

public class Runner {

	 private static void path(Node n1, Node n2) {
		 
	        BFS bfs = new BFS(n1,n2);
	        
	        Node path[] = bfs.getPath();
	        
	        int i;
	        System.out.println("# of Person objects = " + Node.numberOfNodes());
	        if (path == null) {
	            System.out.println("<"+n1+","+n2+"> not connected");
	            return;
	        }
	        
	        for (i=0; i<path.length; i++) {
	            System.out.println(i + ": " + path[i] + " "  );
	        }
	        
	        System.out.println();
	        
	       
	        
	        
	    }
	
	
	public static void main ( String args[] ) throws Exception {
		
		/*
		String file = "/home/nguyen/Public/Evaluation/example-graph.txt";
		
		if ( args.length > 0 ) file = args[0];
					
		System.out.println("Loading graph...");
		
		Graph graph = new Graph(file);
		
		System.out.println(" done.");
		System.out.print("Computing a subgraph...");
		Graph graph2 = graph.neighbourhoodGraph(new int[]{1,2},2);
		System.out.println(" done.");
		
		System.out.print("Computing SimRank on a Subgraph...");
		SimRank simrank = new SimRank(graph2);
		System.out.println(" done.");
		
		
		System.out.println("SimRank similarity for 100 random nodes");
		for ( int i=0; i<100; i++) {
			int n1 = new Random().nextInt(graph2.numNodes());
			int n2 = new Random().nextInt(graph2.numNodes());
			System.out.println( graph2.node(n1) + "\t" + graph2.node(n2) + "\t" + simrank.getSimRankScore(n1,n2));
		}
		System.out.println("Top 10 Nodes on Subgraph Sorted By PageRank");
		*/
		
		
		
		Node node1, node2;
        node1 = Node.create("http://dbpedia.org/resource/Janet_Jackson");        
    	node2 = Node.create("http://dbpedia.org/resource/Jennifer_Lopez");
	  	    	
    	path(node1,node2);    	    	
		
	}

}
