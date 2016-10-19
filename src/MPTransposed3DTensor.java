import java.util.function.Function;

//class to use to create a transposed view of a tensor, specifically,
//m and p swapped
public class MPTransposed3DTensor extends AbstractTransposed3DTensor {
	
	public MPTransposed3DTensor(ThreeDTensor tensor	) {
		super(tensor, new Function<ThreeDTensorEntry, ThreeDTensorEntry>() {
			@Override
			public ThreeDTensorEntry apply(ThreeDTensorEntry t) {
				return new ThreeDTensorEntry(t.k, t.j, t.i, t.val);
			}
		});
	}
	
	@Override public int getM() { return tensor.getP(); }
	@Override public int getN() { return tensor.getN(); }
	@Override public int getP() { return tensor.getM(); }

	@Override
	public double getEntryAt(int i, int j, int k) {
		//if out of bounds, the the message of the
		//exception could be confusing, but anyways
		return tensor.getEntryAt(k, j, i);
	}

}
