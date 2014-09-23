package ezgraph;

import java.util.*;

public class DelaunayGraph {

/*    public static void main (String[] args) {
        Simplex tri = new Simplex(new Pnt[] {new Pnt(-10,10), new Pnt(10,10), new Pnt(0,-10)});
        System.out.println("Triangle created: " + tri);
        DelaunayTriangulation dt = new DelaunayTriangulation(tri);
        System.out.println("DelaunayTriangulation created: " + dt);
        dt.delaunayPlace(new Pnt(0,0));
        dt.delaunayPlace(new Pnt(1,0));
        dt.delaunayPlace(new Pnt(0,1));
        System.out.println("After adding 3 points, the DelaunayTriangulation is a " + dt);
        System.out.println("Neighbor data for " + this);
        for (Iterator it = neighbors.keySet().iterator(); it.hasNext();) {
            Simplex simplex = (Simplex) it.next();
            Simplex.moreInfo = true;
            System.out.print("    " + simplex + ":");
            Simplex.moreInfo = false;
            for (Iterator otherIt = ((Set) neighbors.get(simplex)).iterator(); 
                 otherIt.hasNext();)
                System.out.print(" " + otherIt.next());
            System.out.println();
        }
        Simplex.moreInfo = remember;
    } */

    private Simplex mostRecent = null;

    public boolean debug = false;

    private HashMap neighbors;
    
    public DelaunayGraph (Simplex triangle) {
        neighbors = new HashMap();
        neighbors.put(triangle, new HashSet());
        mostRecent = triangle;
    }
        
    private int size () {
        return neighbors.size();
    }
    
    private boolean contains (Simplex simplex) {
        return this.neighbors.containsKey(simplex);
    }
    
    private Iterator iterator () {
        return Collections.unmodifiableSet(this.neighbors.keySet()).iterator();
    }
    
    private Simplex neighborOpposite (Object vertex, Simplex simplex) {
        if (!simplex.contains(vertex)) throw new IllegalArgumentException("Bad vertex; not in simplex");
        SimplexLoop: for (Iterator it = ((Set) neighbors.get(simplex)).iterator(); 
            it.hasNext();) {
            Simplex s = (Simplex) it.next();
            for (Iterator otherIt = simplex.iterator(); otherIt.hasNext(); ) {
                Object v = otherIt.next();
                if (v.equals(vertex)) continue;
                if (!s.contains(v)) continue SimplexLoop;
            }
            return s;
        }
        return null;
    }
    
    private Set neighbors (Simplex simplex) {
        return new HashSet((Set) this.neighbors.get(simplex));
    }
    
    private void update (Set oldSet, Set newSet) {
        Set allNeighbors = new HashSet();
        for (Iterator it = oldSet.iterator(); it.hasNext();)
            allNeighbors.addAll((Set) neighbors.get((Simplex) it.next()));
        for (Iterator it = oldSet.iterator(); it.hasNext();) {
            Simplex simplex = (Simplex) it.next();
            for (Iterator otherIt = ((Set) neighbors.get(simplex)).iterator(); 
                 otherIt.hasNext();)
                ((Set) neighbors.get(otherIt.next())).remove(simplex);
            neighbors.remove(simplex);
            allNeighbors.remove(simplex);
        }
        allNeighbors.addAll(newSet);
        for (Iterator it = newSet.iterator(); it.hasNext();) neighbors.put((Simplex) it.next(), new HashSet());
        for (Iterator it = newSet.iterator(); it.hasNext();) {
            Simplex s1 = (Simplex) it.next();
            for (Iterator otherIt = allNeighbors.iterator(); otherIt.hasNext();) {
                Simplex s2 = (Simplex) otherIt.next();
                if (!s1.isNeighbor(s2)) continue;
                ((Set) neighbors.get(s1)).add(s2);
                ((Set) neighbors.get(s2)).add(s1);
            }
        }
    }

    private Simplex locate (Pnt point) {
        Simplex triangle = mostRecent;
        if (!this.contains(triangle)) triangle = null;
        Set visited = new HashSet();
        while (triangle != null) {
            if (visited.contains(triangle)) {
                System.out.println("Warning: Caught in a locate loop");
                break;
            }
            visited.add(triangle);
            Pnt corner = point.isOutside((Pnt[]) triangle.toArray(new Pnt[0]));
            if (corner == null) return triangle;
            triangle = this.neighborOpposite(corner, triangle);
        }
        System.out.println("Warning: Checking all triangles for " + point);
        for (Iterator it = this.iterator(); it.hasNext();) {
            Simplex tri = (Simplex) it.next();
            if (point.isOutside((Pnt[]) tri.toArray(new Pnt[0])) == null) return tri;
        }
        System.out.println("Warning: No triangle holds " + point);
        return null;
    }
    
    private Set delaunayPlace (Pnt site) {
        Set newTriangles = new HashSet();
        Set oldTriangles = new HashSet();
        Set doneSet = new HashSet();
        LinkedList waitingQ = new LinkedList();
        if (debug) System.out.println("Locate");
        Simplex triangle = locate(site);
        if (triangle == null || triangle.contains(site)) return newTriangles;
        if (debug) System.out.println("Cavity");
        waitingQ.add(triangle);
        while (!waitingQ.isEmpty()) {
            triangle = (Simplex) waitingQ.removeFirst();      
            if (site.vsCircumcircle((Pnt[]) triangle.toArray(new Pnt[0])) == 1) continue;
            oldTriangles.add(triangle);
            Iterator it = this.neighbors(triangle).iterator();
            for (; it.hasNext();) {
                Simplex tri = (Simplex) it.next();
                if (doneSet.contains(tri)) continue;
                doneSet.add(tri);
                waitingQ.add(tri);
            }
        }
        if (debug) System.out.println("Create");
        for (Iterator it = Simplex.boundary(oldTriangles).iterator(); it.hasNext();) {
            Set facet = (Set) it.next();
            facet.add(site);
            newTriangles.add(new Simplex(facet));
        }
        if (debug) System.out.println("Update");
        this.update(oldTriangles, newTriangles);
        if (!newTriangles.isEmpty()) mostRecent = (Simplex) newTriangles.iterator().next();
        return newTriangles;
    }
    
}

class Pnt {
    
    private double[] coordinates;
    
    public Pnt (double[] coords) {
        coordinates = new double[coords.length];
        System.arraycopy(coords, 0, coordinates, 0, coords.length);
    }
    
    public Pnt (double coordA, double coordB) {
        this(new double[] {coordA, coordB});
    }
    
    public Pnt (double coordA, double coordB, double coordC) {
        this(new double[] {coordA, coordB, coordC});
    }
        
    public boolean equals (Object other) {
        if (!(other instanceof Pnt)) return false;
        Pnt p = (Pnt) other;
        if (this.coordinates.length != p.coordinates.length) return false;
        for (int i = 0; i < this.coordinates.length; i++)
            if (this.coordinates[i] != p.coordinates[i]) return false;
        return true;
    }
    
    public int hashCode () {
        int hash = 0;
        for (int i = 0; i < this.coordinates.length; i++) {
            long bits = Double.doubleToLongBits(this.coordinates[i]);
            hash = (31*hash) ^ (int)(bits ^ (bits >> 32));
        }
        return hash;
    }
    
    public double coord (int i) {
        return this.coordinates[i];
    }
    
    public int dimension () {
        return coordinates.length;
    }
    
    public int dimCheck (Pnt p) {
        int len = this.coordinates.length;
        if (len != p.coordinates.length) throw new IllegalArgumentException("Dimension mismatch");
        return len;
    }
    
    public Pnt extend (double[] coords) {
        double[] result = new double[coordinates.length + coords.length];
        System.arraycopy(coordinates, 0, result, 0, coordinates.length);
        System.arraycopy(coords, 0, result, coordinates.length, coords.length);
        return new Pnt(result);
    }
    
    public double dot (Pnt p) {
        int len = dimCheck(p);
        double sum = 0;
        for (int i = 0; i < len; i++) sum += this.coordinates[i] * p.coordinates[i];
        return sum;
    }
    
    public double magnitude () {
        return Math.sqrt(this.dot(this));
    }
    
    public Pnt subtract (Pnt p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++) coords[i] = this.coordinates[i] - p.coordinates[i];
        return new Pnt(coords);
    }
    
    public Pnt add (Pnt p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] + p.coordinates[i];
        return new Pnt(coords);
    }
    
    public double angle (Pnt p) {
        return Math.acos(this.dot(p) / (this.magnitude() * p.magnitude()));
    }
    
    public Pnt bisector (Pnt point) {
        int dim = dimCheck(point);
        Pnt diff = this.subtract(point);
        Pnt sum = this.add(point);
        double dot = diff.dot(sum);
        return diff.extend(new double[] {-dot / 2});
    }
        
    public static double determinant (Pnt[] matrix) {
        if (matrix.length != matrix[0].dimension())
            throw new IllegalArgumentException("Matrix is not square");
        boolean[] columns = new boolean[matrix.length];
        for (int i = 0; i < matrix.length; i++) columns[i] = true;
        try {return determinant(matrix, 0, columns);}
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
    }
    
    private static double determinant(Pnt[] matrix, int row, boolean[] columns) {
        if (row == matrix.length) return 1;
        double sum = 0;
        int sign = 1;
        for (int col = 0; col < columns.length; col++) {
            if (!columns[col]) continue;
            columns[col] = false;
            sum += sign * matrix[row].coordinates[col] *
                   determinant(matrix, row+1, columns);
            columns[col] = true;
            sign = -sign;
        }
        return sum;
    }
    
    public static Pnt cross (Pnt[] matrix) {
        int len = matrix.length + 1;
        if (len != matrix[0].dimension())
            throw new IllegalArgumentException("Dimension mismatch");
        boolean[] columns = new boolean[len];
        for (int i = 0; i < len; i++) columns[i] = true;
        double[] result = new double[len];
        int sign = 1;
        try {
            for (int i = 0; i < len; i++) {
                columns[i] = false;
                result[i] = sign * determinant(matrix, 0, columns);
                columns[i] = true;
                sign = -sign;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
        return new Pnt(result);
    }
    
    public static double content (Pnt[] simplex) {
        Pnt[] matrix = new Pnt[simplex.length];
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = simplex[i].extend(new double[] {1});
        int fact = 1;
        for (int i = 1; i < matrix.length; i++) fact = fact*i;
        return determinant(matrix) / fact;
    }
    
    public int[] relation (Pnt[] simplex) {
        int dim = simplex.length - 1;
        if (this.dimension() != dim)
            throw new IllegalArgumentException("Dimension mismatch");
        Pnt[] matrix = new Pnt[dim+1];
        double[] coords = new double[dim+2];
        for (int j = 0; j < coords.length; j++) coords[j] = 1;
        matrix[0] = new Pnt(coords);
        for (int i = 0; i < dim; i++) {
            coords[0] = this.coordinates[i];
            for (int j = 0; j < simplex.length; j++) 
                coords[j+1] = simplex[j].coordinates[i];
            matrix[i+1] = new Pnt(coords);
        }
        Pnt vector = cross(matrix);
        double content = vector.coordinates[0];
        int[] result = new int[dim+1];
        for (int i = 0; i < result.length; i++) {
            double value = vector.coordinates[i+1];
            if (Math.abs(value) <= 1.0e-6 * Math.abs(content)) result[i] = 0;
            else if (value < 0) result[i] = -1;
            else result[i] = 1;
        }
        if (content < 0) {
            for (int i = 0; i < result.length; i++) result[i] = -result[i];
        }
        if (content == 0) {
            for (int i = 0; i < result.length; i++) result[i] = Math.abs(result[i]);
        }
        return result;
    }
    
    public Pnt isOutside (Pnt[] simplex) {
        int[] result = this.relation(simplex);
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) return simplex[i];
        }
        return null;
    }
    
    public Pnt isOn (Pnt[] simplex) {
        int[] result = this.relation(simplex);
        Pnt witness = null;
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 0) witness = simplex[i];
            else if (result[i] > 0) return null;
        }
        return witness;
    }
    
    public boolean isInside (Pnt[] simplex) {
        int[] result = this.relation(simplex);
        for (int i = 0; i < result.length; i++) if (result[i] >= 0) return false;
        return true;
    }
    
    public int vsCircumcircle (Pnt[] simplex) {
        Pnt[] matrix = new Pnt[simplex.length + 1];
        for (int i = 0; i < simplex.length; i++) matrix[i] = simplex[i].extend(new double[] {1, simplex[i].dot(simplex[i])});
        matrix[simplex.length] = this.extend(new double[] {1, this.dot(this)});
        double d = determinant(matrix);
        int result = (d < 0)? -1 : ((d > 0)? +1 : 0);
        if (content(simplex) < 0) result = - result;
        return result;
    }
    
    public static Pnt circumcenter (Pnt[] simplex) {
        int dim = simplex[0].dimension();
        if (simplex.length - 1 != dim) throw new IllegalArgumentException("Dimension mismatch");
        Pnt[] matrix = new Pnt[dim];
        for (int i = 0; i < dim; i++) matrix[i] = simplex[i].bisector(simplex[i+1]);
        Pnt hCenter = cross(matrix); 
        double last = hCenter.coordinates[dim];
        double[] result = new double[dim];
        for (int i = 0; i < dim; i++) result[i] = hCenter.coordinates[i] / last;
        return new Pnt(result);
    }
     
}

class Simplex extends AbstractSet implements Set {
    
    private List vertices;

    private long idNumber;

    private static long idGenerator = 0;

    public static boolean moreInfo = false;
    
    public Simplex (Collection collection) {
        this.vertices = Collections.unmodifiableList(new ArrayList(collection));
        this.idNumber = idGenerator++;
        Set noDups = new HashSet(this);
        if (noDups.size() != this.vertices.size())
            throw new IllegalArgumentException("Duplicate vertices in Simplex");
    }
    
    public Simplex (Object[] vertices) {
        this(Arrays.asList(vertices));
    }
    
    public String toString () {
        if (!moreInfo) return "Simplex" + idNumber;
        return "Simplex" + idNumber + super.toString();
    }
    
    public int dimension () {
        return this.vertices.size() - 1;
    }
    
    public boolean isNeighbor (Simplex simplex) {
        HashSet h = new HashSet(this);
        h.removeAll(simplex);
        return (this.size() == simplex.size()) && (h.size() == 1);
    }
    
    public List facets () {
        List theFacets = new LinkedList();
        for (Iterator it = this.iterator(); it.hasNext();) {
            Object v = it.next();
            Set facet = new HashSet(this);
            facet.remove(v);
            theFacets.add(facet);
        }
        return theFacets;
    }
    
    public static Set boundary (Set simplexSet) {
        Set theBoundary = new HashSet();
        for (Iterator it = simplexSet.iterator(); it.hasNext();) {
            Simplex simplex = (Simplex) it.next();
            for (Iterator otherIt = simplex.facets().iterator(); otherIt.hasNext();) {
                Set facet = (Set) otherIt.next();
                if (theBoundary.contains(facet)) theBoundary.remove(facet);
                else theBoundary.add(facet);
            }
        }
        return theBoundary;
    }
    
    public Iterator iterator () {
        return this.vertices.iterator();
    }
    
    public int size () {
        return this.vertices.size();
    }
    
    public int hashCode () {
        return (int)(idNumber^(idNumber>>>32));
    }
    
    public boolean equals (Object o) {
        return (this == o);
    }
}


