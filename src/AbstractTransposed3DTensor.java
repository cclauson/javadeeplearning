import java.util.Iterator;
import java.util.function.Function;

public abstract class AbstractTransposed3DTensor implements ThreeDTensor {
	
	protected final ThreeDTensor tensor;
	private final Function<ThreeDTensorEntry, ThreeDTensorEntry> entryTranslator;

	//common supertype for transposed tensor classes
	public AbstractTransposed3DTensor(ThreeDTensor tensor, Function<ThreeDTensorEntry, ThreeDTensorEntry> entryTranslator) {
		if (tensor == null) throw new IllegalArgumentException("tensor is null");
		if (entryTranslator == null) throw new IllegalArgumentException("translator function is null");
		this.tensor = tensor;
		this.entryTranslator = entryTranslator;
	}

	@Override
	public Iterable<ThreeDTensorEntry> getNonzeroEntries() {
		final Iterable<ThreeDTensorEntry> iterable = tensor.getNonzeroEntries();
		return new Iterable<ThreeDTensorEntry>() {
			@Override
			public Iterator<ThreeDTensorEntry> iterator() {
				final Iterator<ThreeDTensorEntry> iterator = iterable.iterator();
				return new Iterator<ThreeDTensorEntry>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}
					@Override
					public ThreeDTensorEntry next() {
						final ThreeDTensorEntry orig = iterator.next();
						return entryTranslator.apply(orig);
					}
				};
			}
		};
	}

}
