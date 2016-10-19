import java.util.Iterator;

//inner product tensors can contain a very
//large number of nonzero entries, but which
//are structured very regularly.  We therefore
//write a special class that avoids maintaining
//a hashmap
public class InnerProduct3DTensor implements ThreeDTensor {

	private final int inputDim;
	private final int outputDim;
	
	public InnerProduct3DTensor(int inputDim, int outputDim) {
		this.inputDim = inputDim;
		this.outputDim = outputDim;
	}
	
	@Override public int getM() { return inputDim * outputDim; }
	@Override public int getN() { return inputDim; }
	@Override public int getP() { return outputDim; }

	private int indicesToIndex(int j, int k) {
		return j * outputDim + k;
	}
	
	@Override
	public double getEntryAt(int i, int j, int k) {
		return (i == indicesToIndex(j, k))? 1.0 : 0.0;
	}
	
	private class EntryIterator implements Iterator<ThreeDTensorEntry> {
		
		private int j = 0;
		private int k = 0;
		
		@Override
		public boolean hasNext() {
			return j < inputDim;
		}

		@Override
		public ThreeDTensorEntry next() {
			ThreeDTensorEntry ret = new ThreeDTensorEntry(indicesToIndex(j, k), j, k, 1.0);
			++k;
			if (k == outputDim) {
				++j;
				k = 0;
			}
			return ret;
		}
	}
	
	@Override
	public Iterable<ThreeDTensorEntry> getNonzeroEntries() {
		return new Iterable<ThreeDTensorEntry>() {
			@Override
			public Iterator<ThreeDTensorEntry> iterator() {
				return new EntryIterator();
			}
		};
	}

	
	
}
