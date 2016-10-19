import java.io.*;

public class CharacterRecognitionDataset {
	
	private final InputStream images;
	private final InputStream nums;
	
	private final int w;
	private final int h;
	private final int numItems;
	
	private int numRead = 0;
	
	private static long read32BitsBigEndian(InputStream is) throws IOException {
		int acc = 0;
		for (int i = 0; i < 4; ++i) {
			int b = is.read();
			if (b < 0) throw new RuntimeException("byte read was negative");
			acc <<= 8;
			acc |= b;
		}
		return acc;
	}
	
	public CharacterRecognitionDataset(File images, File nums) {
		if (images == null) throw new IllegalArgumentException("images is null");
		if (nums == null) throw new IllegalArgumentException("nums is null");
		try {
			this.images = new FileInputStream(images);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			this.nums = new FileInputStream(nums);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		long imageMagicNumber;
		
		try {
			imageMagicNumber = read32BitsBigEndian(this.images);
			this.numItems = (int)read32BitsBigEndian(this.images);
			this.h = (int)read32BitsBigEndian(this.images);
			this.w = (int)read32BitsBigEndian(this.images);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		if (imageMagicNumber != 2051L)
			throw new RuntimeException("invalid image magic number " +
				imageMagicNumber);
		
		long labelMagicNumber;
		int labelNumItems;

		try {
			labelMagicNumber = read32BitsBigEndian(this.nums);
			labelNumItems = (int)read32BitsBigEndian(this.nums);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (labelMagicNumber != 2049L)
			throw new RuntimeException("invalid image magic number " +
				imageMagicNumber);
		
		if (numItems != labelNumItems) {
			throw new RuntimeException(
				"number of images not equal to number of numbers in dataset");
		}
		
	}

	public boolean hasMoreData() {
		return numRead < numItems;
	}
	
	public DataItem nextDataItem() {
		
		if (!this.hasMoreData()) {
			throw new RuntimeException("no more data items");
		}
		
		int res;
		try {
			res = this.nums.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (res < 0) {
			throw new RuntimeException("num unexpectedly negative");
		}
		if (res > 9) {
			throw new RuntimeException("num unexpectedly greater than 9");
		}

		DoubleList outputVector = new DoubleList(10);
		outputVector.set(res, 1.0);
		
		DoubleList inputVector = new DoubleList(w * h);
		for(int i = 0; i < w * h; ++i) {
			try {
				res = this.images.read();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (res < 0) {
				throw new RuntimeException("pixel value unexpectedly negative");
			}
			if (res > 255) {
				throw new RuntimeException("pixel value unexpectedly greater than 255");
			}
			inputVector.set(i, res / 255.0);
		}
		
		++numRead;
		return new DataItem(inputVector, outputVector);
	}
	
}
