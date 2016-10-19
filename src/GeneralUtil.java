import java.util.Arrays;
import java.util.List;

public class GeneralUtil {

	private GeneralUtil() {}

	public static List<Double> createZeroListWithDim(int dim) {
		/*
		if (dim < 0) throw new IllegalArgumentException("dim: " + dim + " < 0");
		final Double[] darry = new Double[dim];
		Arrays.fill(darry, new Double(0.0));
		return Arrays.asList(darry);
		*/
		return new DoubleList(dim);
	}

	//code to "multiply" two vectors, returning a new vector, where the specifics of the
	//"multiplication" are specified by a tensor
	public static List<Double> vectorProduct(List<Double> factor1, List<Double> factor2, ThreeDTensor tensor) {
		if (factor1 == null) throw new IllegalArgumentException("factor 1 is null");
		if (factor2 == null) throw new IllegalArgumentException("factor 2 is null");
		if (tensor == null) throw new IllegalArgumentException("tensor is null");
		if (tensor.getM() != factor1.size())
			throw new IllegalArgumentException("factor 1 dimension not compatible with tensor (factor1: " +
					factor1.size() + ", tensor m: " + tensor.getM() + ")");
		if (tensor.getN() != factor2.size())
			throw new IllegalArgumentException("factor 2 dimension not compatible with tensor (factor2: " +
					factor2.size() + ", tensor n: " + tensor.getN() + ")");
		final List<Double> retlist = createZeroListWithDim(tensor.getP());
		for (ThreeDTensorEntry tensorEntry : tensor.getNonzeroEntries()) {
			double term = tensorEntry.val * factor1.get(tensorEntry.i) * factor2.get(tensorEntry.j);
			int destIndex = tensorEntry.k;
			retlist.set(destIndex, retlist.get(destIndex) + term);
		}
		return retlist;
	}

	public static List<Double> matrixVectorMul(Matrix matrix, List<Double> vector) {
		if (matrix == null) throw new IllegalArgumentException("matrix is null");
		if (vector == null) throw new IllegalArgumentException("vector is null");
		if (matrix.getM() != vector.size()) throw new IllegalArgumentException("matrix and vector dimensions are not compatible");
		final List<Double> retlist = createZeroListWithDim(matrix.getN());
		for (MatrixEntry matrixEntry : matrix.getNonzeroElements()) {
			double term = matrixEntry.val * vector.get(matrixEntry.i);
			int destIndex = matrixEntry.j;
			retlist.set(destIndex, retlist.get(destIndex) + term);
		}
		return retlist;
	}
	
}
