package ezgraph;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;


public class BFS {
	
	Node sourceNode = null;
	
	Node destNode = null;
	
	private String endpoint = "http://dbpedia.org/sparql";	
		
	private String graphURI = null;
	
	private Node path[];
	
	private Map<String, String> dictionary = new HashMap<String, String>();
	
	private SynchronizedCounter counter = new SynchronizedCounter();;
	
	private String PREFIX = 
		" PREFIX dbpedia: <http://dbpedia.org/resource/> " +
		" PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
		" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+	
		" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
		" PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
		" PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + 
	    " PREFIX dcterms: <http://purl.org/dc/terms/>" +
		" PREFIX skos: <http://www.w3.org/2004/02/skos/core#>";
	
	ArrayList<String> properties = null;//Node.printNodes();
	
	public BFS(Node sourceNode, Node destNode) {		
		this.sourceNode = sourceNode;		
		this.destNode = destNode;	
		properties = Node.readProperties();
		shortestPath();				
		//GraphConstruction();		
	}	
	
	public Node[] getPath() { return path; }
	
	public void shortestPath() {
		
		Collection<Node>  h,
	     now1 = new HashSet<Node>(),
	     now2 = new HashSet<Node>(),
	     next = new HashSet<Node>();
		
	   int direction, label, n;
	    
	   Node.resetAllLabels();
		
       if (sourceNode == null || destNode == null)
           return;
       if (sourceNode == destNode) {
           sourceNode.setLabel(1);
           path = new Node[1];
           path[0] = sourceNode;
           return;
       }
       
       sourceNode.setLabel( 1); now1.add(sourceNode);
       destNode.setLabel(-1); now2.add(destNode);
       
       while (true) {
           if (now1.isEmpty() || now2.isEmpty())
               return;
           
           if (now2.size() < now1.size()) {
               h = now1; now1 = now2; now2 = h;
           }
           
           Iterator<Node> nowI = now1.iterator();
           
           while (nowI.hasNext()) {
               Node pnow = nowI.next();
               label = pnow.getLabel();
               direction = Integer.signum(label);
               
               
               System.out.println("Name: " + pnow.getName());
               
               Node neighbours[] = pnow.getNeighbourNodes(pnow.getName());
                              
               int i;
                              
               for (i=0; i<neighbours.length; i++) {
                   Node px = neighbours[i];
                   
                   //System.out.println(px.getName());
                   
                   if (px.hasLabel()) {
                       if (Integer.signum(px.getLabel())==direction) continue;
                       if (direction < 0) {
                           Node ph;
                           ph = px; px = pnow; pnow = ph;
                       }
                       // pnow has a positive label,
                       // px a negative                       
                       n = pnow.getLabel() - px.getLabel();
                       path = new Node[n];
                       path[pnow.getLabel()-1] = pnow;
                       path[n+px.getLabel()] = px;
                       
                       
                       tracing(pnow.getLabel()-1);                       
                       tracing(n+px.getLabel());
                       
                       
                       return;
                   }
                   px.setLabel(label+direction);
                   next.add(px);
               }
           }
           now1.clear(); h = now1; now1 = next; next = h;
       }       
   
	}
	
    private void tracing(int position) {
    	Node pNow, pNext;
        int direction, i, label;
        
        label = path[position].getLabel();
        direction = Integer.signum(label);
        label -= direction;
        while (label != 0) {
            pNow = path[position];
            
            Node neighbours[] = pNow.getNeighbourNodes(pNow.getName());
            
            for (i=0; i<neighbours.length; i++) {
                pNext = neighbours[i];
                if (!pNext.hasLabel())
                    continue;
                if (pNext.getLabel() == label) {
                    position -= direction;
                    label -= direction;
                    path[position] = pNext;
                    break;
                }
            }
        }
    }
    
       
    
    

    //Node path[] = bfs.getPath();

    
    public void GraphConstruction(){

    	Iterator iterator = Node.nodeMap.entrySet().iterator();    	
    	
    	List<String> nodes = new ArrayList<String>();    	
    	Map<String, Node> node = new HashMap<String, Node>();    	
    	while (iterator.hasNext()) {    	
    		Map.Entry entry = (Map.Entry) iterator.next();    		
    		nodes.add((String)entry.getKey());  		
    	}    	
    	int n = nodes.size(),count=0;
    	String r1="",r2="";
    	System.out.println("the number is:" + n);
    	String resource1="",resource2="";
    	BufferedWriter writer = null;
    	try {
			
			writer = new BufferedWriter(new FileWriter("/home/nguyen/Public/Evaluation/SimRank_Graph.txt"));
				    	    	
	    	for(int i=0;i<n-1;i++){			
				resource1 = nodes.get(i);			
				for(int j=i+1;j<n;j++){				
					resource2 = nodes.get(j);
					count = EdgeDetection(resource1,resource2);
					r1 = extractKey(resource1);r2 = extractKey(resource2);
					switch(count){					
					case 0:
						break;
					case 1:
						writeEdge(writer,r1,r2);
						break;
					case 2:
						writeEdge(writer,r2,r1);
						break;
					case 3:
						writeEdge(writer,r1,r2);
						writeEdge(writer,r2,r1);
						break;					
					}					
				}
				
	    	}
    	} catch (IOException e) {
			e.printStackTrace();
		}						
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return;    	
    }
    
    
    
    
    
    
    
    
    
    
    
    public void writeEdge(BufferedWriter writer, String resource1, String resource2) {
		
		String content = null;

		try {						
								
			content = resource1 + "\t" + resource2 + "\t" + "1.0";			
			
			writer.append(content);
									
			writer.newLine();				
			
		} catch (IOException e) {
			e.printStackTrace();
		}						
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return;
	}
        
    
    public int EdgeDetection(String r1, String r2){    	
    	String queryString = "";    	
    	int val = 0;
    	r1 = "<" + r1 + ">";
    	r2 = "<" + r2 + ">";
    	boolean direction1 = false, direction2 = false;
    	
    	//for(String p : properties){
    	
    		val=0;
    		direction1 = false;
    		direction2 = false;    		
    		
    		queryString = this.PREFIX +
 				   " SELECT (COUNT(*) AS ?frequency ) WHERE { " +			   		
 				   r1 + " ?predicate " + r2 + " } "; 				       		
    		val = getCount(queryString);
    		if(val>0)direction1=true;
    		
    		System.out.println(queryString);
    		
    		System.out.println("======================================================");
    		
    		queryString = this.PREFIX +
  				   " SELECT (COUNT(*) AS ?frequency ) WHERE { " +			   		
  				   r2 + " ?predicate " + r1 + " } "; 				       		
     		val = getCount(queryString);
     		if(val>0)direction2=true;
    		
     		if(direction1 && direction2)val=3;
     		else if(direction1 && !direction2)val=1;
     		else if(!direction1 && direction2) val=2;	

    		System.out.println(queryString);
    		
    	//}   
    	
    	return val;
    }
	    


	private int getCount(String queryString) {			
		Query query = QueryFactory.create(queryString);		
		int ret=0;		
		QueryExecution qexec = null;		
		try {		
			if (graphURI == null)				
				qexec = QueryExecutionFactory.sparqlService(endpoint, query);
			else
				qexec = QueryExecutionFactory.sparqlService(endpoint, query, graphURI);						
			ResultSet results = qexec.execSelect();			  			
  			for ( ; results.hasNext() ; ){
      			QuerySolution soln = results.nextSolution() ;
      			ret = soln.getLiteral("frequency").getInt() ;
  			}
		} catch (QueryExceptionHTTP e) {			
            System.out.println(endpoint + " is temporarily down");            
		} finally {
			qexec.close();
		} 
		return ret;		
	}

	private String extractKey(String s) {
			
		synchronized (dictionary) {

			if (dictionary.containsKey(s))
				return dictionary.get(s);
			else {
				int c = counter.value();
				
				String key = "node"+ Integer.toString(c);
				
				dictionary.put(s, key);
				
				return key;
			}
		}
	}

	

	
}
