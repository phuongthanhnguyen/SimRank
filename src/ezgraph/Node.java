package ezgraph;

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
	
   	private static Map<String, Node> nodeMap = new HashMap<String, Node>();

    private String name;
    //private String urlpt;

    private String endpoint = "http://193.204.59.20:8890/sparql";	
	
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
    private boolean coauthorsLoaded;
    
    /*
     * Create a new Node object.
     */
    private Node(String name) {
        this.name = name;
        //this.urlpt = urlpt;
        nodeMap.put(name, this);
        coauthorsLoaded = false;
        labelvalid = false;
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
    static public int numberOfPersons() {
        return nodeMap.size();
    }
    
    /*
     * Check if a Node object already has been created. 
     */
        
    static public Node searchNode(String name) {
        return nodeMap.get(name);
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
       

	public Node[] getNeighborhoodNodes(String currentUri, String predicate) {

		Set<Node> ret = new HashSet<Node>();
		
		if (!currentUri.contains("http://dbpedia.org/resource/"))
			currentUri = "http://dbpedia.org/resource/" + currentUri;
		
		Query query;
		String queryString;

		currentUri = "<" + currentUri + ">";
				
		queryString = this.PREFIX +
				   " SELECT * WHERE {{ " +			   		
				   " ?subject " + predicate + " " + currentUri + " . " +
				   " FILTER isIRI(?subject)} UNION {" +				   
				   	 currentUri + " " + predicate + " ?object . " + 
				   " FILTER isIRI(?object)}" +
				   " } ";
		
		//System.out.println(queryString);
		
		try {
			
			query = QueryFactory.create(queryString);
						
			
			ret.addAll(executeQuery(query, queryString));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
				
		Node node[] = new Node[ret.size()];
		
		node = ret.toArray(node);
		
		return node;
	}
    
	

	
	private Set<Node> executeQuery(Query query, String p) {

		Set<Node> ret = new HashSet<Node>();

		p = p.replace("<", "");
		p = p.replace(">", "");
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

					// n e' il soggetto n,uri,p sogg,ogg,prop

					// update(n, uri, p);

				} else {

					node = qs.get("object");
					n = node.toString();
					n = n.replace("<", "");
					n = n.replace(">", "");

					// n e' l'oggetto uri,n,p sogg,ogg,prop
					// update(uri, n, p);

				}

				n = n.replace("http://dbpedia.org/resource/", "");
				if (!p.contains("type"))
					ret.add(n);
				else { // consideriamo solo i type di yago che sono come delle
						// categorie
					if (n.contains("yago"))
						ret.add(n);

				}

			}

		} catch (QueryExceptionHTTP e) {
			
            System.out.println(endpoint + " is temporarily down");
            
		} finally {
			qexec.close();
		}

		return ret;
	}



}
