import java.util.List;

public class BilinearLayer implements Layer {

	private final ThreeDTensor tensor;
	private final MPTransposed3DTensor mpTransposedTensor;
	private final NPTransposed3DTensor npTransposedTensor;
	
	public BilinearLayer(ThreeDTensor tensor) {
		if (tensor == null) {
			throw new IllegalArgumentException("tensor is null");
		}
		this.tensor = tensor;
		//cache transposed versions as well
		this.mpTransposedTensor = new MPTransposed3DTensor(tensor);
		this.npTransposedTensor = new NPTransposed3DTensor(tensor);
	}
	
	@Override public int getParamDim() { return tensor.getM(); }
	@Override public int getInputDim() { return tensor.getN(); }
	@Override public int getOutputDim() { return tensor.getP(); }

	@Override
	public List<Double> evalForward(List<Double> inputVector, List<Double> paramVector) {
		return GeneralUtil.vectorProduct(paramVector, inputVector, tensor);
	}

	@Override
	public List<Double> getParamGradient(List<Double> inputVector, List<Double> paramVector,
			List<Double> outputVector, List<Double> outputGradient) {
		return GeneralUtil.vectorProduct(outputGradient, inputVector, mpTransposedTensor);
	}

	@Override
	public List<Double> getInputGradient(List<Double> inputVector, List<Double> paramVector,
			List<Double> outputVector, List<Double> outputGradient) {
		return GeneralUtil.vectorProduct(paramVector, outputGradient, npTransposedTensor);
	}

}
