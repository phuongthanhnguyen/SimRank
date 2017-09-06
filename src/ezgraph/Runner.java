package org.crossminer.similaritycalculator.SimRank;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class Runner {
		
	private String root;
	private String graphDir;
	private String sourceList;
	private String destList;
	private int index;
	private String groundTruth;
	//Map<String, Integer> dictionary = new HashMap<String, Integer>();

	public Runner(){
		
	}
	
	public void loadConfigurations(){		
		Properties prop = new Properties();				
		try {
			prop.load(new FileInputStream("evaluation.properties"));
			this.root=prop.getProperty("SimRankRoot");	
			this.graphDir=prop.getProperty("GraphDirectory");
			this.groundTruth=prop.getProperty("GroundTruthRoot");
			this.sourceList=prop.getProperty("SourceList");
			this.destList=prop.getProperty("DestList");
			this.index=Integer.parseInt(prop.getProperty("index"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return;
	}
		
	
	public void run(){		
		System.out.println("Similarity Calculation using SimRank!");
		loadConfigurations();				
		simRankSimilarity6();
		//float f=1.5486573101952672E-5f;		
		//System.out.println("the value is: " + f*10000);				
	}
	
	
	/*
	public void Test(){
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("/home/nguyen/Public/test.txt",true));
			writer.write("Another line");
			writer.newLine();		
			writer.close();
			
		}  catch (IOException e) {
			e.printStackTrace();					
		}
		
	}
	*/
	
	public void simRankSimilarity(){
		
		DataReader reader = new DataReader();	
		Map<String, List<String>> list = new HashMap<String, List<String>>();		
		Map<String, Integer> dictionary = new HashMap<String, Integer>();		
		String graphFilename = "", dictFilename = "";		
		String str="",content="";
		String filename = "ListOfPairs"+Integer.toString(index);
		BufferedWriter writer = null, writer2=null;
		
		List<String> nodes = new ArrayList<String>();
				
		Graph graph = null;		
		SimRank simrank = null;
				
		int id1=0,id2=0;
		double val=0;
				
		list = reader.readArtistList(this.graphDir+filename);
		
		Set<String> keySet = list.keySet();
				
		try {
		
			writer = new BufferedWriter(new FileWriter(this.root+"newArtistListForSimRank"+Integer.toString(index)));			
						
			for(String resource1:keySet) {			
				nodes = list.get(resource1);
			
				writer2 = new BufferedWriter(new FileWriter(this.root+resource1));
				
				for(String resource2:nodes ){		
					
					str = resource1+"+"+resource2;		
					
					graphFilename = this.graphDir+"Graph"+ Integer.toString(index) +"/" + str;							
					dictFilename = this.graphDir+"Dictionary" +Integer.toString(index) +"/" + str;				
					dictionary = reader.readDictionary(dictFilename);
					
					id1 = dictionary.get("http://dbpedia.org/resource/"+resource1);
					id2 = dictionary.get("http://dbpedia.org/resource/"+resource2);			

					//graph = new Graph(new File(graphFilename));
					graph = new Graph(graphFilename);
					//System.out.println(graph.numNodes());
					
					simrank = new SimRank(graph);
					simrank.computeSimRank();		
					
					val = simrank.getSimRank(new Integer(id1), new Integer(id2));
					
					//val = simrank.getSimRank(new Integer(137), new Integer(139));					
					//simrank.printScores();
									
					System.out.println("The similarity between " + resource1 + " and " + resource2 + " is " + val);
					content = resource2 + "\t" + Double.toString(val);
					
					writer2.append(content);							
					writer2.newLine();
					writer2.flush();					
				}
				
				writer2.close();
				
				writer.append(resource1);							
				writer.newLine();
				writer.flush();			
			}			
			writer.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}				
		return;
	}
	
	
	
	
	
	public void simRankSimilarity2(){
		
		DataReader reader = new DataReader();	
		List<String> artistList = new ArrayList<String>();		
		Map<String, Integer> dictionary = new HashMap<String, Integer>();		
		String graphFilename = "", dictFilename = "";		
		String str="",content="";
		String filename = "ListOfPairs"+Integer.toString(index);
		BufferedWriter writer = null, writer2=null;
		
		List<String> nodes = new ArrayList<String>();
				
		Graph graph = null;		
		SimRank simrank = null;
				
		int id1=0,id2=0;
		double val=0;
				
		artistList = reader.readArtistList2(this.graphDir+"ArtistList10");
		int n = artistList.size();
		
		graphFilename = this.graphDir+"Graph"+ Integer.toString(index) +"/Graph";
		dictFilename = this.graphDir+"Dictionary" +Integer.toString(index) +"/Dictionary";
		
		graph = new Graph(graphFilename);
		
		System.out.println(dictFilename);
		dictionary = reader.readDictionary(dictFilename);
		System.out.println("Dictionary size: "+dictionary.size());
	
		
		simrank = new SimRank(graph);
		simrank.computeSimRank();
		String resource1="",resource2="";
				
		//try {		
		//	writer = new BufferedWriter(new FileWriter(this.root+"newArtistListForSimRank"+Integer.toString(index)));			
			
		
		for(int i=0;i<n-1;i++){				
			resource1 = artistList.get(i);	
			try {
				writer2 = new BufferedWriter(new FileWriter(this.root+resource1));
				
				for(int j=i+1;j<n;j++){					
					resource2 = artistList.get(j);		
					id1 = dictionary.get("http://dbpedia.org/resource/"+resource1);
					id2 = dictionary.get("http://dbpedia.org/resource/"+resource2);	
					val = simrank.getSimRank(new Integer(id1), new Integer(id2));
					System.out.println("The similarity between " + resource1 + " and " + resource2 + " is: " + val);
					content = resource2 + "\t" + Double.toString(val);
					writer2.append(content);							
					writer2.newLine();
					writer2.flush();	
				}
				
				writer2.close();
		
			} catch (IOException e) {
					e.printStackTrace();
			}
		}		
		
			
			
		return;
	}
	
	
	/*Similarity for the list of 3194 artists/bands from www.similar-artist.com - One-hop neighbourhood graph */
	
	public void simRankSimilarity3(){		
		DataReader reader = new DataReader();	
		List<String> artistList = new ArrayList<String>();		
		Map<String, Integer> dictionary = new HashMap<String, Integer>();		
		String graphFilename = "", dictFilename = "",content="";
		
		String filename = "",resource1="",resource2="";
		
		BufferedWriter writer = null;
					
		Graph graph = null;		
		SimRank simrank = null;
				
		int id1=0,id2=0;
		double val=0;
				
		
		ArrayList<String> list = null;
		artistList = reader.readArtistList2(this.groundTruth+"newMusicalArtistList1200_12");
		int n = artistList.size();
		int m = 0;
		String key1="",key2="";
		
		
		for(int i=0;i<n;i++){		
			resource1 = artistList.get(i);
			list = new ArrayList<String>();
			filename = this.groundTruth + resource1;	
			
			list.addAll(reader.readSimilarityArtist(filename));
						
			m = list.size();
			graphFilename = this.graphDir+"Graph"+ Integer.toString(index) +"/"+resource1;
			dictFilename = this.graphDir+"Dictionary" + Integer.toString(index) +"/"+resource1;
			graph = new Graph(graphFilename);
			dictionary = reader.readDictionary(dictFilename);
			
			simrank = new SimRank(graph);
			simrank.computeSimRank();
			
			key1 = "http://dbpedia.org/resource/"+resource1;
			id1 = dictionary.get("http://dbpedia.org/resource/"+resource1);
			
			try {
				writer = new BufferedWriter(new FileWriter(this.root+resource1));
				
				for(int j=0;j<m;j++){
					resource2 = list.get(j);		
					key2 = "http://dbpedia.org/resource/"+resource2;
					if(dictionary.containsKey(key2)){
						id2 = dictionary.get("http://dbpedia.org/resource/"+resource2);	
						val = simrank.getSimRank(new Integer(id1), new Integer(id2));
						System.out.println("The similarity between " + resource1 + " and " + resource2 + " is: " + val);
						content = resource2 + "\t" + Double.toString(val);
						writer.append(content);							
						writer.newLine();
						writer.flush();							
					}										
				}			
				writer.close();				
			} catch (IOException e) {
					e.printStackTrace();
			}			
		}
		
						
		return;
	}
	
	
	
	
	
	
	
	public void simRankSimilarity4(){
		
		DataReader reader = new DataReader();					
		Map<String, Integer> dictionary = new HashMap<String, Integer>();		
		String graphFilename = "", dictFilename = "";		
		String str="",content="";
		String filename = "ListOfPairs"+Integer.toString(index);
		
		BufferedWriter writer = null, writer2=null;	
				
		Map<String, List<String>> list = new HashMap<String, List<String>>();
		List<String> nodes = new ArrayList<String>();
		list = reader.readArtistList(this.graphDir+filename);		
		Set<String> keySet = list.keySet();
		
		int id1=0,id2=0;
		double val=0;
								
		Graph graph = null;		
		SimRank simrank = null;
			
		
		for(String resource1:keySet) {			
			nodes = list.get(resource1);
			try {
						
				writer2 = new BufferedWriter(new FileWriter(this.root+resource1));		
				
				for(String resource2:nodes ){			
					str = resource1+"+"+resource2;
										
					graphFilename = this.graphDir+"Graph"+ Integer.toString(index) +"/" + str;							
					dictFilename = this.graphDir+"Dictionary" +Integer.toString(index) +"/" + str;	

					dictionary = reader.readDictionary(dictFilename);
					
					id1 = dictionary.get("http://dbpedia.org/resource/"+resource1);
					id2 = dictionary.get("http://dbpedia.org/resource/"+resource2);			
	
					//graph = new Graph(new File(graphFilename));
					graph = new Graph(graphFilename);
	
					simrank = new SimRank(graph);
					simrank.computeSimRank();		
					
					val = simrank.getSimRank(new Integer(id1), new Integer(id2));
									
					System.out.println("The similarity between " + resource1 + " and " + resource2 + " is " + val);
					content = resource2 + "\t" + Double.toString(val);
					
					writer2.append(content);							
					writer2.newLine();
					writer2.flush();	
					
				}				
				writer2.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
				
		return;
	}
	

	
	
	
	
		
	
	
	
	/*	Calculating similarity between two resources: 					*/
	/*	Reading graphs from two independent files   					*/
	/*	Combining two dictionaries to produce a common dictionary   	*/
			
	public void simRankSimilarity5(){
		
		DataReader reader = new DataReader();				
		Map<String, Integer> dictionary = new HashMap<String, Integer>();				
		Map<String, Integer> dictionary1 = new HashMap<String, Integer>();
		Map<String, Integer> tempDict = new HashMap<String, Integer>();
		Map<Integer, String> dictionary2 = new HashMap<Integer, String>();
					
		String graphFilename1 = "", dictFilename1 = "";
		String graphFilename2 = "", dictFilename2 = "";
		String content="";
		
		BufferedWriter writer = null;	
				
		List<String> sourceList = new ArrayList<String>();		
		List<String> destList = new ArrayList<String>();				
		
		//sourceList = reader.readMovieList(this.groundTruth + "themoviedblist_test_2");
		//destList = reader.readMovieList(this.groundTruth + "MovieList57794");
		
		sourceList = reader.readMovieList(this.groundTruth + "newMusicalArtistList_LastFM_2");
		destList = reader.readMovieList(this.groundTruth + "newMusicalArtistList10");	
				
		int id1=0,id2=0;
		double val=0;
								
		Graph graph = null, graph1 = null,graph2 = null;		
		SimRank simrank = null;
		String filename="",uri1="",uri2="";
		boolean append = true;
								
		for(String resource1:sourceList) {			
			filename = resource1;
			if(filename.contains("/"))filename=filename.replace("/", "#");
			
			graphFilename1 = this.graphDir+"Graph"+ Integer.toString(index) +"/" + filename;							
			dictFilename1 = this.graphDir+"Dictionary" +Integer.toString(index) +"/" + filename;
			dictionary1 = reader.readDictionary(dictFilename1);
			graph1 = new Graph(graphFilename1);
												
			try {					
				writer = new BufferedWriter(new FileWriter(this.root+resource1, append));				
				for(String resource2:destList ){							
					filename = resource2;
					if(filename.contains("/"))filename=filename.replace("/", "#");					
					graphFilename2 = this.graphDir+"Graph"+ Integer.toString(index) +"/" + filename;							
					dictFilename2 = this.graphDir+"Dictionary" +Integer.toString(index) +"/" + filename;
					dictionary2 = reader.readDictionary2(dictFilename2);				
					//System.out.println("The number of nodes is: " + graph1.numNodes());
					graph2 = new Graph(graphFilename2);
					graph = new Graph();
					graph = copyGraph(graph1);
					tempDict = copyDictionary(dictionary1);					
					dictionary = new HashMap<String, Integer>();
					dictionary = graph.combine(graph2, tempDict, dictionary2);				
					simrank = new SimRank(graph);					
					simrank.computeSimRank();
					uri1="http://dbpedia.org/resource/"+resource1;
					uri2="http://dbpedia.org/resource/"+resource2;
					id1 = dictionary.get(uri1);
					if(dictionary.containsKey(uri2)){
						id2 = dictionary.get(uri2);
						val = simrank.getSimRank(new Integer(id1), new Integer(id2));									
						System.out.println("The similarity between " + resource1 + " and " + resource2 + " is " + val);
						content = resource2 + "\t" + Double.toString(val);					
						writer.append(content);							
						writer.newLine();
						writer.flush();						
					}											
				}				
				writer.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}			
		return;
	}
	
	
	
	
	public void simRankSimilarity6(){
		
		DataReader reader = new DataReader();				
		Map<String, Integer> dictionary = new HashMap<String, Integer>();				
		Map<String, Integer> dictionary1 = new HashMap<String, Integer>();
		Map<String, Integer> tempDict = new HashMap<String, Integer>();
		Map<Integer, String> dictionary2 = new HashMap<Integer, String>();
					
		String graphFilename1 = "", dictFilename1 = "";
		String graphFilename2 = "", dictFilename2 = "";
		String content="";
		
		BufferedWriter writer = null;	


		Map<Integer,String> sourceList = new HashMap<Integer,String>();		
		Map<Integer,String> destList = new HashMap<Integer,String>();				
		
		sourceList = reader.readMovieLenList(this.groundTruth + this.sourceList);		
		destList = reader.readMovieLenList(this.groundTruth + this.destList);
		
		//sourceList = reader.readLastFMMostPopularArtistList(this.groundTruth + this.sourceList);
		//destList = reader.readLastFMMostPopularArtistList(this.groundTruth + this.destList);
				
		int id1=0,id2=0;
		double val=0;
								
		Graph graph = null, graph1 = null,graph2 = null;		
		SimRank simrank = null;
		String filename="",uri1="",uri2="",savename="";
		boolean append = true;
						
		Set<Integer> sourceKeySet = sourceList.keySet();
		Set<Integer> destKeySet = destList.keySet();
			
			
		for(Integer k1:sourceKeySet){
		
			String resource1 = sourceList.get(k1).replace("http://dbpedia.org/resource/", "");			
			filename = resource1;
			savename = Integer.toString(k1);
			if(filename.contains("/"))filename=filename.replace("/", "#");
			
			graphFilename1 = this.graphDir+"Graph"+ Integer.toString(index) +"/" + filename;							
			dictFilename1 = this.graphDir+"Dictionary" +Integer.toString(index) +"/" + filename;
			dictionary1 = reader.readDictionary(dictFilename1);
			graph1 = new Graph(graphFilename1);
			
			
						
			try {					
				writer = new BufferedWriter(new FileWriter(this.root+savename));		
				
				for(Integer k2:destKeySet){
					
					String resource2 = destList.get(k2).replace("http://dbpedia.org/resource/", "");
					
					filename = resource2;
					if(filename.contains("/"))filename=filename.replace("/", "#");					
					graphFilename2 = this.graphDir+"Graph"+ Integer.toString(index) +"/" + filename;							
					dictFilename2 = this.graphDir+"Dictionary" +Integer.toString(index) +"/" + filename;
					dictionary2 = reader.readDictionary2(dictFilename2);			
					
					//System.out.println("The number of nodes is: " + graph1.numNodes());
					graph2 = new Graph(graphFilename2);
					graph = new Graph();
					graph = copyGraph(graph1);
					tempDict = copyDictionary(dictionary1);					
					dictionary = new HashMap<String, Integer>();
					dictionary = graph.combine(graph2, tempDict, dictionary2);		
					
					simrank = new SimRank(graph);					
					simrank.computeSimRank();
					uri1="http://dbpedia.org/resource/"+resource1;
					uri2="http://dbpedia.org/resource/"+resource2;					
					id1 = dictionary.get(uri1);
					
					if(dictionary.containsKey(uri2)){
						id2 = dictionary.get(uri2);
						val = simrank.getSimRank(new Integer(id1), new Integer(id2));
						
						System.out.println("The similarity between " + resource1 + " and " + resource2 + " is " + val);
						content = Integer.toString(k2) + "\t" + Double.toString(val);					
						writer.append(content);							
						writer.newLine();
						writer.flush();						
					}											
				}				
				writer.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}			
		return;
	}
	
	
	
	
	
	
	
	public Graph copyGraph(Graph graph){
		
		Graph temp = new Graph();
		Map<Integer,Set<Integer>> InLinks = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> InLinks2 = new HashMap<Integer,Set<Integer>>();
		InLinks = graph.getInLinks();		
		Set<Integer> nodes = new HashSet<Integer>();
		Set<Integer> nodes2 = new HashSet<Integer>();
		Set<Integer> keySet = InLinks.keySet();
		
		for(Integer key:keySet){		
			nodes = InLinks.get(key);
			nodes2 = new HashSet<Integer>();
			for(Integer key2:nodes)nodes2.add(key2);
			InLinks2.put(key, nodes2);		
		}
		
		temp.setInLinks(InLinks2);
		temp.setNumNodes(graph.numNodes());
			
		return temp;
	}
	
	
	

	public Map<String, Integer> copyDictionary(Map<String, Integer> dict){		
		Map<String, Integer> tempDict = new HashMap<String, Integer>();		
		Set<String> keySet = dict.keySet();		
		int val=0;		
		for(String key:keySet){
			val = dict.get(key);
			tempDict.put(key, val);			
		}				
		return tempDict;
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {	
		Runner runner = new Runner();			
		runner.run();				    		    
		return;
	}	
	
}
