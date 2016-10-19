import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MutableSparse3DTensor implements ThreeDTensor {

	//we define an immutable triple
	//type to be hash map keys
	private static class IndexTriple {
		public final int i;
		public final int j;
		public final int k;
		public IndexTriple(int i, int j, int k) {
			this.i = i;
			this.j = j;
			this.k = k;
		}
		@Override
		public boolean equals(Object other) {
			if(other == null) return false;
			if(other == this) return true;
			if(!(other instanceof IndexTriple)) return false;
			IndexTriple otherIndexTriple = (IndexTriple) other;
			return this.i == otherIndexTriple.i &&
					this.j == otherIndexTriple.j &&
					this.k == otherIndexTriple.k;
		}
		@Override
		public int hashCode() {
			//hash code computation involves
			//multiplying by some 32 bit primes
			return i * (int)(2725272749L) ^
			j * 329558221 ^ k * 1025302217;
		}
	}
	
	private final int m;
	private final int n;
	private final int p;
	
	private Map<IndexTriple, Double> map =
				new HashMap<IndexTriple, Double>();
	
	private void checkDimensionBounds(int val, String dimension) {
		if(val <= 0) {
			throw new IllegalArgumentException("Dimension " + dimension +
					" is " + val + " <= 0, this is not allowed");
		}
	}
	
	//creates a new sparse tensor with the given
	//dimensions with all zero entries
	public MutableSparse3DTensor(int m, int n, int p) {
		checkDimensionBounds(m, "m");
		checkDimensionBounds(n, "n");
		checkDimensionBounds(p, "p");
		this.m = m;
		this.n = n;
		this.p = p;
	}
		
	@Override public int getM() { return m; }
	@Override public int getN() { return n; }
	@Override public int getP() { return p; }
	
	private void checkIndexBounds(int index, int max, String dim) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("index for dimension " +
					dim + " " + index + "is < 0");
		}
		if (index >= max) {
			throw new IndexOutOfBoundsException("index for dimension " +
					dim + " " + index + " >= " + max + ", which is the max for this dimension");
		}
	}
	
	//returns index triple if indices are in bounds,
	//otherwise throws an exception
	private IndexTriple checkIndexBoundsForTriple(int i, int j, int k) {
		checkIndexBounds(i, m, "m");
		checkIndexBounds(j, n, "n");
		checkIndexBounds(k, p, "p");
		return new IndexTriple(i, j, k);		
	}
	
	@Override
	public double getEntryAt(int i, int j, int k) {
		IndexTriple it = checkIndexBoundsForTriple(i, j, k);
		if(map.containsKey(it)) {
			return map.get(it);
		} else {
			return 0.0;			
		}
	}

	public void setEntryAt(int i, int j, int k, double val) {
		IndexTriple it = checkIndexBoundsForTriple(i, j, k);
		map.put(it, val);
	}

	@Override
	public Iterable<ThreeDTensorEntry> getNonzeroEntries() {
		return new Iterable<ThreeDTensorEntry>() {
			@Override
			public Iterator<ThreeDTensorEntry> iterator() {
				final Iterator<Map.Entry<IndexTriple, Double>> mapIterator = map.entrySet().iterator();
				return new Iterator<ThreeDTensorEntry>() {
					@Override
					public boolean hasNext() {
						return mapIterator.hasNext();
					}
					@Override
					public ThreeDTensorEntry next() {
						Map.Entry<IndexTriple, Double> nextEntry = mapIterator.next();
						IndexTriple it = nextEntry.getKey();
						return new ThreeDTensorEntry(it.i, it.j, it.k, nextEntry.getValue());
					}
				};
			}
		};
	}
	
}
