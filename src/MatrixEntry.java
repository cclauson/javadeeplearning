
//element of a matrix, includes i, j
//indices and value at that index
public class MatrixEntry {

	public final int i;
	public final int j;
	public final double val;
	
	public MatrixEntry(int i, int j, double val) {
		if (i < 0) throw new IllegalArgumentException("i: " + i + "< 0");
		if (j < 0) throw new IllegalArgumentException("j: " + j + "< 0");
		this.i = i;
		this.j = j;
		this.val = val;
	}
	
}
