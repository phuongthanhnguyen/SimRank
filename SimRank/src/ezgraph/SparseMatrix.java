package ezgraph;

import es.yrbcn.graph.weighted.*;
import it.unimi.dsi.webgraph.*;
import it.unimi.dsi.webgraph.labelling.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class SparseMatrix {

      private static final long serialVersionUID = -2396174756253967431L;

      private final List<Int2FloatMap> data;
  
      public SparseMatrix clone ( ) { 
	SparseMatrix aux = new SparseMatrix();
	for ( Int2FloatMap map : data ) { 
		Int2FloatMap map2 = new Int2FloatOpenHashMap(map);
		aux.data.add(map2);
	}
	return aux;
      }

      public SparseMatrix() { data = new ObjectArrayList<Int2FloatMap>(); }
 
      public SparseMatrix (int nrows) {
          data = new ObjectArrayList<Int2FloatMap>(nrows);
          for (int i = 0; i < nrows; i++) {
              Int2FloatMap m = new Int2FloatOpenHashMap();
              m.defaultReturnValue((float)0.0);
              data.add(m);
          }
      }
  
      public void set(int row, int col, double value) {
          while (row >= data.size()) {
              Int2FloatMap m = new Int2FloatOpenHashMap();
              m.defaultReturnValue((float)0.0);
              data.add(m);
          }
          data.get(row).put(col, (float)value);
      }
  
      public double get(int row, int col) {
          if (row >= data.size()) return 0.0;
          else return data.get(row).get(col);
      }
  
      public Int2FloatMap row(int row) {
          if (row >= data.size()) return Int2FloatMaps.EMPTY_MAP;
          else return Int2FloatMaps.unmodifiable(data.get(row));
      }

}
