package ezgraph;

import java.util.*;

public class Runner {

	public static void main ( String args[] ) throws Exception {
		
		String file = "/home/nguyen/Public/Evaluation/example-graph.txt";
		if ( args.length > 0 ) file = args[0];
					
		System.out.println("Loading graph...");
		
		Graph graph = new Graph(file);
		
		System.out.println(" done.");
		System.out.print("Computing a subgraph...");
		Graph graph2 = graph.neighbourhoodGraph(new int[]{1,2},2);
		System.out.println(" done.");
		
		
		//System.out.print("Computing PageRank...");
		//PageRank pagerank = new PageRank(graph);
		//System.out.println(" done.");
		//System.out.print("Computing PageRank on a Subgraph...");
		//PageRank pagerank2 = new PageRank(graph2);
		//System.out.println(" done.");		
		//System.out.print("Computing HITS...");		
		//HITS hits = new HITS(graph);		
		//System.out.println(" done.");
		
		
		
		System.out.print("Computing SimRank on a Subgraph...");
		SimRank simrank = new SimRank(graph2);
		System.out.println(" done.");
		
		
		
		//System.out.print("Computing a Graph Clustering...");
		//GraphClustering clustering = new GraphClustering(graph);
		//System.out.println(" done.");
		
		/*
		System.out.print("Computing Graph Statistics Through Sampling...");		
		SamplingStatistics sstats = new SamplingStatistics(graph,5000);		
		System.out.println(" done.");
		System.out.println("Computing Degree Statistics...");
		DegreeStatistics stats = new DegreeStatistics(graph,true);

		System.out.println("Min Indegree = " + stats.minIndegree());
		System.out.println("Max Indegree = " + stats.maxIndegree());
		System.out.println("Avg Indegree = " + stats.avgIndegree());

		System.out.println("Min Outdegree = " + stats.minOutdegree());
		System.out.println("Max Outdegree = " + stats.maxOutdegree());
		System.out.println("Avg Outdegree = " + stats.avgOutdegree());

		System.out.println("Min Degree = " + stats.minDegree());
		System.out.println("Max Degree = " + stats.maxDegree());
		System.out.println("Avg Degree = " + stats.avgDegree());
		*/

		/*
		System.out.println("Clustering Coefficient = " + sstats.clusteringCoefficient());
		System.out.println("Avg Neighbours = " + sstats.avgNumNeighbors());
		System.out.println("Avg Triangles = " + sstats.avgNumTriangles());
		System.out.println("Avg Distance = " + sstats.avgDistance());
		*/
		
		//System.out.println("Top 10 Nodes Sorted By PageRank");
		//for ( String node : pagerank.getSortedNodes().subList(0,Math.min(10,graph.numNodes()))) System.out.println(node + "\t" + pagerank.getPageRankScore(node));

		//System.out.println("Top 10 Nodes Sorted By HITS Hub Score");
		//for ( String node : hits.getSortedHubNodes().subList(0,Math.min(10,graph.numNodes()))) System.out.println(node + "\t" + hits.getHubScore(node));

		//System.out.println("Top 10 Nodes Sorted By HITS Authority Score");
		//for ( String node : hits.getSortedAuthorityNodes().subList(0,Math.min(10,graph.numNodes()))) System.out.println(node + "\t" + hits.getAuthorityScore(node));

		
		
		System.out.println("SimRank similarity for 100 random nodes");
		for ( int i=0; i<100; i++) {
			int n1 = new Random().nextInt(graph2.numNodes());
			int n2 = new Random().nextInt(graph2.numNodes());
			System.out.println( graph2.node(n1) + "\t" + graph2.node(n2) + "\t" + simrank.getSimRankScore(n1,n2));
		}

		
		
		//System.out.println("Graph Clusters");
		//for ( int i=0; i<graph.numNodes(); i++) System.out.println(graph.node(i) + "\t" + clustering.getCluster(i));
		

		System.out.println("Top 10 Nodes on Subgraph Sorted By PageRank");
		//for ( String node : pagerank2.getSortedNodes().subList(0,Math.min(10,graph2.numNodes()))) System.out.println(node + "\t" + pagerank2.getPageRankScore(node));
	}

}
