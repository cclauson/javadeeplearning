
//represents an entry in a 3D tensor, includes
//three indices and value
public class ThreeDTensorEntry {
	public final int i;
	public final int j;
	public final int k;
	public final double val;
	
	public ThreeDTensorEntry(int i, int j, int k, double val) {
		if (i < 0) throw new IllegalArgumentException("i: " + i + "< 0");
		if (j < 0) throw new IllegalArgumentException("j: " + j + "< 0");
		if (k < 0) throw new IllegalArgumentException("k: " + k + "< 0");
		this.i = i;
		this.j = j;
		this.k = k;
		this.val = val;
	}	
}
