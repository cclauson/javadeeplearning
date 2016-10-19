import java.util.function.Function;

//class to use to create a transposed view of a tensor, specifically,
//n and p swapped
public class NPTransposed3DTensor extends AbstractTransposed3DTensor {
	
	public NPTransposed3DTensor(ThreeDTensor tensor	) {
		super(tensor, new Function<ThreeDTensorEntry, ThreeDTensorEntry>() {
			@Override
			public ThreeDTensorEntry apply(ThreeDTensorEntry t) {
				return new ThreeDTensorEntry(t.i, t.k, t.j, t.val);
			}
		});
	}
	
	@Override public int getM() { return tensor.getM(); }
	@Override public int getN() { return tensor.getP(); }
	@Override public int getP() { return tensor.getN(); }

	@Override
	public double getEntryAt(int i, int j, int k) {
		//if out of bounds, the the message of the
		//exception could be confusing, but anyways
		return tensor.getEntryAt(i, k, j);
	}

}
