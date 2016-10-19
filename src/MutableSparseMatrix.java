import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MutableSparseMatrix implements Matrix {

	//we define an immutable pair
	//type to be hash map keys
	private static class IndexPair {
		public final int i;
		public final int j;
		public IndexPair(int i, int j) {
			this.i = i;
			this.j = j;
		}
		@Override
		public boolean equals(Object other) {
			if(other == null) return false;
			if(other == this) return true;
			if(!(other instanceof IndexPair)) return false;
			IndexPair otherIndexPair = (IndexPair) other;
			return this.i == otherIndexPair.i &&
					this.j == otherIndexPair.j;
		}
		@Override
		public int hashCode() {
			//hash code computation involves
			//multiplying by some 32 bit primes
			return i * (int)(2725272749L) ^
			j * 329558221;
		}
	}

	private final int m;
	private final int n;
	
	private Map<IndexPair, Double> map =
				new HashMap<IndexPair, Double>();
	
	private void checkDimensionBounds(int val, String dimension) {
		if(val <= 0) {
			throw new IllegalArgumentException("Dimension " + dimension +
					" is " + val + " <= 0, this is not allowed");
		}
	}
	
	//creates a new sparse tensor with the given
	//dimensions with all zero entries
	public MutableSparseMatrix(int m, int n) {
		checkDimensionBounds(m, "m");
		checkDimensionBounds(n, "n");
		this.m = m;
		this.n = n;
	}

	@Override public int getM() { return m; }
	@Override public int getN() { return n; }

	@Override
	public Iterable<MatrixEntry> getNonzeroElements() {
		return new Iterable<MatrixEntry>() {
			@Override
			public Iterator<MatrixEntry> iterator() {
				final Iterator<Map.Entry<IndexPair, Double>> mapIterator = map.entrySet().iterator();
				return new Iterator<MatrixEntry>() {
					@Override
					public boolean hasNext() {
						return mapIterator.hasNext();
					}
					@Override
					public MatrixEntry next() {
						Map.Entry<IndexPair, Double> nextEntry = mapIterator.next();
						IndexPair it = nextEntry.getKey();
						return new MatrixEntry(it.i, it.j, nextEntry.getValue());
					}
				};
			}
		};
	}
	
	private void checkIndexBounds(int index, int max, String dim) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("index for dimension " +
					dim + " is < 0");
		}
		if (index >= max) {
			throw new IndexOutOfBoundsException("index for dimension " +
					dim + " is >= " + max + ", which is the max for this dimension");
		}
	}
	
	//returns index triple if indices are in bounds,
	//otherwise throws an exception
	private IndexPair checkIndexBoundsForPair(int i, int j) {
		checkIndexBounds(i, m, "m");
		checkIndexBounds(j, n, "n");
		return new IndexPair(i, j);		
	}
	
	public void setEntryAt(int i, int j, double val) {
		IndexPair it = checkIndexBoundsForPair(i, j);
		map.put(it, val);
	}
}
