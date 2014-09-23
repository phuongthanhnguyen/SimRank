package ezgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
	
	String sourceURI = null;
	
	String destURI = null;
	
	private Node path[];
	
	private String endpoint = "http://193.204.59.20:8890/sparql";	
	
	private String graphURI = null;
		
	private String dbpediaCat; //dbpedia-owl:Film, dbpedia-owl:MusicalArtist, dbpedia-owl:Band.
		
	private String PREFIX = 
			" PREFIX dbpedia: <http://dbpedia.org/resource/> " +
			" PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
			" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+	
			" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
			" PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
			" PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + 
		        " PREFIX dcterms: <http://purl.org/dc/terms/>" +
			" PREFIX skos: <http://www.w3.org/2004/02/skos/core#>";

	public BFS(String s, String d) {
		
		this.sourceURI = s;
		
		this.destURI = d;	
		
	}	
	
	public Node[] getPath() { return path; }
	
	
	private void shortestPath(Node n1, Node n2) {
		
		Collection<Node>  h,
	    now1 = new HashSet<Node>(),
	    now2 = new HashSet<Node>(),
	    next = new HashSet<Node>();
		int direction, label, n;
	       
       Node.resetAllLabels();
       if (n1 == null || n2 == null)
           return;
       if (n1 == n2) {
           n1.setLabel(1);
           path = new Node[1];
           path[0] = n1;
           return;
       }
       
       n1.setLabel( 1); now1.add(n1);
       n2.setLabel(-1); now2.add(n2);
       
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
               
               
               
               
               
               Node neighbours[] = pnow.getCoauthors();
               
               
               
               
               
               //resultList_lev1.addAll(runQuery(this.seedURI, p));     
               
               
               
               int i;
                              
               for (i=0; i<neighbours.length; i++) {
                   Node px = neighbours[i];
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
       
       return;
	}
			
	
	
	
	
    
	
	
    private void tracing(int position) {
    	Node pNow, pNext;
        int direction, i, label;
        
        label = path[position].getLabel();
        direction = Integer.signum(label);
        label -= direction;
        while (label != 0) {
            pNow = path[position];
            
            //Node ca[] = pNow.getCoauthors();
            
            for (i=0; i<ca.length; i++) {
                pNext = ca[i];
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
	
    
    
    
    
    
    
	
	
	public Set<Node> runQuery(String currentUri, String predicate) {

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

		return ret;
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
					// else
					// System.out.println("owl#Thing not added: "+n);

				}

				// if (!this.mapMetadata.containsKey(p))
				// this.mapMetadata.put(p, new ArrayList());
				// this.mapMetadata.get(p).add(n);

			}

		} catch (QueryExceptionHTTP e) {
			
            System.out.println(endpoint + " is temporarily down");
            
		} finally {
			qexec.close();
		}

		return ret;
	}
	

}
