package ezgraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

public class Node {
	
   	public static Map<String, Node> nodeMap = new HashMap<String, Node>();

    private String name;
    //private String urlpt;

    private String endpoint = "http://dbpedia.org/sparql";	
	
	private String graphURI = null;
		
	private String dbpediaCat; //dbpedia-owl:Film, dbpedia-owl:MusicalArtist, dbpedia-owl:Band.
	
	private List<Node> plist = new ArrayList<Node>();
		
	private String PREFIX = 
			" PREFIX dbpedia: <http://dbpedia.org/resource/> " +
			" PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
			" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+	
			" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
			" PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
			" PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + 
		    " PREFIX dcterms: <http://purl.org/dc/terms/>" +
			" PREFIX skos: <http://www.w3.org/2004/02/skos/core#>";
    
    
    private int label;
    private boolean labelvalid;
     
   //ArrayList<String> properties = readProperties();;
    
    /*
     * Create a new Node object.
     */
        
    private Node(String name) {
        this.name = name;
        nodeMap.put(name, this);  
        labelvalid = false;
        
    }
    
    public String getName(){    	
    	return this.name;
    }
    
    /*
     * Create a new Node object if necessary.
     */
    
    static public Node create(String name) {
    	Node node;
    	node = searchNode(name);
        if (node == null)
        	node = new Node(name);
        return node;
    }

    /*
     * Returns the number of Node objects which
     * have been created until now.
     */
    static public int numberOfNodes() {
        return nodeMap.size();
    }
    
    /*
     * Check if a Node object already has been created. 
     */
        
    static public Node searchNode(String name) {
        return nodeMap.get(name);
    }
    
    
    public static void printNodes(){
    	
    	Iterator iterator = nodeMap.entrySet().iterator();
    	
    	Map<String, Node> node = new HashMap<String, Node>();
    	
    	while (iterator.hasNext()) {
    	
    		Map.Entry entry = (Map.Entry) iterator.next();
    		    		
    		
    		System.out.println(entry.getKey());
    		
    	}
    	
    	return;
    }
       
    
    public int getLabel() {
        if (!labelvalid)
            return 0;
        return label;
    }
    
    public void setLabel(int label) {
        this.label = label;
        labelvalid = true;
    }
    
    public void resetLabel() {
        labelvalid = false;
    }
    
    public boolean hasLabel() {
        return labelvalid;
    }
    
    static public void resetAllLabels() {
        Iterator<Node> i = nodeMap.values().iterator();
        while (i.hasNext()) {
            Node p = i.next();
            p.labelvalid = false;
            p.label = 0;
        }
    }

    public String toString() {
        return name;
    }

    private Node neighbours[];
    
     
    
    public Node[] getNeighbourNodes(String currentUri, ArrayList<String> properties){
    	
    	Set<String> list = new HashSet<String>();    	
		Query query;
		String queryString;

		currentUri = "<" + currentUri + ">";
		    	
    	for(String p : properties){
    		
    		//queryString = this.PREFIX +
    		//	   " SELECT (COUNT(*) AS ?frequency ) WHERE { " +			   		
    		//	   r1 + " ?predicate " + r2 + " } "; 
    		
    		
    		
    		queryString = this.PREFIX +
 				   " SELECT * WHERE {{ " +			   		
 				   " ?subject " + p + " " + currentUri + " . " +
 				   " FILTER isIRI(?subject)} UNION {" +				   
 				   	 currentUri + " " + p + " ?object . " + 
 				   " FILTER isIRI(?object)}" +
 				   " } ";
   		    		
    		query = QueryFactory.create(queryString);
			
    		    		
			list.addAll(executeQuery(query));
		
			/*construct the graph here*/
			
			
    	}
    	
    	
    	
    	
    	for(String n: list){
    		plist.add(create(n));
    		    		
    	} 	   
    	
    	
    	    	
    	neighbours = new Node[plist.size()];
        
    	neighbours = plist.toArray(neighbours);
            	
    	return neighbours; 	    	
    }
  
  	private Set<String> executeQuery(Query query) {

		Set<String> ret = new HashSet<String>();
	
		QueryExecution qexec = null;
		try {

			if (graphURI == null)
				qexec = QueryExecutionFactory.sparqlService(endpoint, query);

			else

				qexec = QueryExecutionFactory.sparqlService(endpoint, query,graphURI);

			ResultSet results = qexec.execSelect();

			QuerySolution qs;
			RDFNode node;
			String n;

			while (results.hasNext()) {

				qs = results.next();

				if (qs.get("object") == null) {
					node = qs.get("subject");

					n = node.toString();
					n = n.replace("<", "");
					n = n.replace(">", "");


				} else {

					node = qs.get("object");
					n = node.toString();
					n = n.replace("<", "");
					n = n.replace(">", "");
				}

				ret.add(n);
				
			}

		} catch (QueryExceptionHTTP e) {
			
            System.out.println(endpoint + " is temporarily down");
            
		} finally {
			qexec.close();
		}

		return ret;
	}
	
	

}
