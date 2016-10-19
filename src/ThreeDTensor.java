
public interface ThreeDTensor {
	//dimensionality of first dimension
	public int getM();
	//dimensionality of second dimension
	public int getN();
	//dimensionality of third dimension
	public int getP();
	//get entry at the given indices
	public double getEntryAt(int i, int j, int k);
	//get an iterable object that represents the set of nonzero
	//entries, sometimes we want to iterate through these
	//efficiently and not look at zero'd entries
	public Iterable<ThreeDTensorEntry> getNonzeroEntries();
}
