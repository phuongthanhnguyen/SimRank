package ezgraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
*/

public class BFS {
	
	String sourceURI = null;
	
	String destURI = null;
	
	private Node path[];
	
	

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
               
                              
               
               
               Node neighbours[] = pnow.getNeighbourNodes();
               
                      
               
               
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
            
            Node ca[] = pNow.getCoauthors();
            
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
	

	
	
	
	
	
	

}
