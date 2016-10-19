import java.util.List;

public class LinearLayer extends AbstractParameterlessLayer {

	private final Matrix matrix;
	private final Matrix transposedMatrix;
	
	public LinearLayer(Matrix matrix) {
		if (matrix == null) throw new IllegalArgumentException("matrix is null");
		this.matrix = matrix;
		this.transposedMatrix = new TransposedMatrix(matrix);
	}
	
	@Override public int getInputDim() { return matrix.getM(); }
	@Override public int getOutputDim() { return matrix.getN(); }

	@Override
	public List<Double> evalForward(List<Double> inputVector, List<Double> paramVector) {
		return GeneralUtil.matrixVectorMul(matrix, inputVector);
	}

	@Override
	public List<Double> getInputGradient(List<Double> inputVector, List<Double> paramVector,
			List<Double> outputVector, List<Double> outputGradient) {
		return GeneralUtil.matrixVectorMul(transposedMatrix, outputGradient);
	}
	
}
