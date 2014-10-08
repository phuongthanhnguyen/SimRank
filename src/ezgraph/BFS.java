package ezgraph;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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


public class BFS {
	
	Node sourceNode = null;
	
	Node destNode = null;
	
	private String endpoint = "http://dbpedia.org/sparql";	
		
	private String graphURI = null;
	
	private Node path[];
	
	private Map<String, String> dictionary = new HashMap<String, String>();
	
	private SynchronizedCounter counter = new SynchronizedCounter();

	BufferedWriter writer2 = null;
	
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
		properties = readProperties();
		shortestPath();				
		GraphConstruction();		
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
                       
               
               Node neighbours[] = pnow.getNeighbourNodes(pnow.getName(),properties);
                              
               int i;
                              
               for (i=0; i<neighbours.length; i++) {
                   Node px = neighbours[i];
                   
                   System.out.println(px.getName());
                   
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
            
            Node neighbours[] = pNow.getNeighbourNodes(pNow.getName(),properties);
            
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
    
       
    

    public static ArrayList<String> readProperties(){		
		ArrayList<String> props = new ArrayList<String>();		
		String line="";		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/home/nguyen/Public/Evaluation/propList4.txt"));
						
			while ((line = reader.readLine()) != null) {				
				props.add(line);							
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}									
		return props;		
	}
	    
    
    

    
    
    
    
    
    
    

    public void GraphConstruction(){
    	    	
    	Node path[] = getPath();

    	//Iterator iterator = Node.nodeMap.entrySet().iterator();    	
    	    	
    	Set<String> nodes = new HashSet<String>();
    	
    	List<Set<String>> neighbours = new ArrayList<Set<String>>();
    	    	
    	for (int i=0; i<path.length; i++) {
    		nodes = new HashSet<String>();
    		String s = path[i].getName();
    		nodes.add(s);
    		nodes.addAll(getNeighbourNodes(s));
    		neighbours.add(nodes);
        }    	
    	
    	//System.out.println("The size of the list: " + nodes.size());    	
    	
    	List<String> 	l0 = new ArrayList<String>(),
    					l1 = new ArrayList<String>(),
    					l2 = new ArrayList<String>();    	
    	
		for(String s:neighbours.get(0)){
			l0.add(s);    		
		}    	
		for(String s:neighbours.get(1)){
			l1.add(s);    		
		}
		for(String s:neighbours.get(2)){
			l2.add(s);    		
		}
		
    	
    	/*
    	for(String s:nodes){
    		nodes.addAll(getNeighbourNodes(s));
    	}
    	*/
    	   	
    	
    	
    	int n0 = l0.size(),edge=0,count=0;
    	int n1 = l1.size();    	
    	boolean jump = false;
    	String r1="",r2="";    	    	
    	System.out.println("the number of resources of the meeting point is:" + n0);
    	System.out.println("the number of resources of the first point is:" + n1);    	
    	String resource1="",resource2="";
    	BufferedWriter writer = null;    	
    	
    	
    	try {			
			writer = new BufferedWriter(new FileWriter("/home/nguyen/Public/Evaluation/SimRank/SimRank_Graph.txt"));
			writer2 = new BufferedWriter(new FileWriter("/home/nguyen/Public/Evaluation/SimRank_Dictionary.txt"));				    	    	
	    	for(int i=0;i<n0;i++){			
				resource1 = l0.get(i);
				if(jump){
					jump=false;
					count=0;
					continue;
				}
				for(int j=0;j<n1;j++){	
					resource2 = l1.get(j);			
					edge = EdgeDetection(resource1,resource2);
					r1 = extractKey(resource1);r2 = extractKey(resource2);
					switch(edge){					
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
					
					System.out.println("Edges between: " + resource1 + " and " + resource2 + " is: " + edge );
					
					if(edge==0)count+=1;else count=0;
					if(count>20){
						jump=true;
						break;						
					}
				}		
				
	    	}
	    	
	    	jump = false;edge=0;count=0;
	    	
	    	int n2 = l2.size();
	    	for(int i=0;i<n0;i++){			
				resource1 = l0.get(i);
				if(jump){
					jump=false;
					count=0;
					continue;
				}
				for(int j=0;j<n2;j++){	
					resource2 = l2.get(j);			
					edge = EdgeDetection(resource1,resource2);
					r1 = extractKey(resource1);r2 = extractKey(resource2);
					switch(edge){					
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
					System.out.println("Edges between: " + resource1 + " and " + resource2 + " is: " + edge );
					if(edge==0)count+=1;else count=0;
					if(count>20){
						jump=true;
						break;						
					}
				}			
	    	}    	
    	} catch (IOException e) {
			e.printStackTrace();
		}						
		try {
			writer.flush();
			writer.close();
			writer2.flush();
			writer2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
    	System.out.println("Finish detecting edges!");    	
    	return;    	
    }
      
    

    
    
    
    
    public Set<String> getNeighbourNodes(String currentUri){
    	
    	Set<String> list = new HashSet<String>();    	
		Query query;
		String queryString;

		currentUri = "<" + currentUri + ">";
		    	
    	for(String p : properties){
    		
    		queryString = this.PREFIX +
 				   " SELECT * WHERE {{ " +			   		
 				   " ?subject " + p + " " + currentUri + " . " +
 				   " FILTER isIRI(?subject)} UNION {" +				   
 				   	 currentUri + " " + p + " ?object . " + 
 				   " FILTER isIRI(?object)}" +
 				   " } ";
   		    		
    		query = QueryFactory.create(queryString);
    		//System.out.println(queryString);			
			list.addAll(executeQuery(query));   		
    	}
    	    	
    	return list; 	    	
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
    		
    		//System.out.println(queryString);
    		
    		//System.out.println("======================================================");
    		
    		queryString = this.PREFIX +
  				   " SELECT (COUNT(*) AS ?frequency ) WHERE { " +			   		
  				   r2 + " ?predicate " + r1 + " } "; 				       		
     		val = getCount(queryString);
     		if(val>0)direction2=true;
    		
     		if(direction1 && direction2)val=3;
     		else if(direction1 && !direction2)val=1;
     		else if(!direction1 && direction2) val=2;	

    		//System.out.println(queryString);
    		
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
		String content = "";			
		synchronized (dictionary) {
			if (dictionary.containsKey(s))
				return dictionary.get(s);
			else {
				int c = counter.value();				
				String key = "node"+ Integer.toString(c);				
				dictionary.put(s, key);								
				try {
					synchronized (writer2) {						
						content = key + "\t" + s ;						
						writer2.append(content);						
						writer2.newLine();					
					}
				} catch (IOException e) {
					e.printStackTrace();
				}						
				try {
					writer2.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}								
				return key;
			}
		}
	}

	
}
