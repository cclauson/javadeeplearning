import java.util.Iterator;

public class TransposedMatrix implements Matrix {

	private final Matrix matrix;
	
	public TransposedMatrix(Matrix matrix) {
		if (matrix == null) throw new IllegalArgumentException("matrix is null");
		this.matrix = matrix;
	}
	
	@Override
	public int getM() { return matrix.getN(); }

	@Override
	public int getN() { return matrix.getM(); }

	@Override
	public Iterable<MatrixEntry> getNonzeroElements() {
		final Iterable<MatrixEntry> iterable = matrix.getNonzeroElements();
		return new Iterable<MatrixEntry>() {
			@Override
			public Iterator<MatrixEntry> iterator() {
				final Iterator<MatrixEntry> iterator = iterable.iterator();
				return new Iterator<MatrixEntry>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}
					@Override
					public MatrixEntry next() {
						MatrixEntry me = iterator.next();
						return new MatrixEntry(me.j, me.i, me.val);
					}
				};
			}
		};
	}
	
}
