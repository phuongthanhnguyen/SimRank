package ezgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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
               
               Node neighbours[] = pnow.getNeighbourNodes(pnow.getName());
                              
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
	
}
