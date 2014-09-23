package ezgraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Node {
	
   	private static Map<String, Node> nodeMap = new HashMap<String, Node>();

    private String name;
    //private String urlpt;

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

    



}
