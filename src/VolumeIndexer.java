//in some situations we have something which we operate
//on as a vector, but is easier to think of as a volume
//We create an object that allows us to translate coordinates
//in the volume to indices in the vector
public class VolumeIndexer {
	private int width;
	private int height;
	private int depth;

	public VolumeIndexer(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	private void checkIndex(int index, String stIndex, int dim, String stDim) {
		if (index < 0) throw new IndexOutOfBoundsException(stIndex + " (" + index + ") is less than zero");
		if (index >= dim) throw new IndexOutOfBoundsException(stIndex + " (" + index + ") is greater than or equal to allowed " +
				stDim + " (" + dim + ")");
	
	}
	
	public int paramIndexFor3DIndex(int z, int x, int y) {
		checkIndex(x, "x", width, "width");
		checkIndex(y, "y", height, "height");
		checkIndex(z, "z", depth, "depth");
		int ret = z * width * height + y * width + x;
		if (ret >= this.size()) throw new IndexOutOfBoundsException("returning index which is too large");
		return ret;
	}
	
	public int size() {
		return width * height * depth;
	}
	

}
