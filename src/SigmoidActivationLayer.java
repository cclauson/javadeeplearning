import java.util.Arrays;
import java.util.List;

public class SigmoidActivationLayer extends AbstractParameterlessLayer {

	private final int dim;
	
	public SigmoidActivationLayer(int dim) {
		this.dim = dim;
	}
	
	@Override public int getInputDim() { return dim; }
	@Override public int getOutputDim() { return dim; }

	@Override
	public List<Double> evalForward(List<Double> inputVector, List<Double> paramVector) {
		if (inputVector.size() != dim) throw new IllegalArgumentException("wrongly sized input vector");
		if (paramVector.size() != 0) throw new IllegalArgumentException("wrongly sized param vector");
		List<Double> retlist = new DoubleList(dim);
		for(int i = 0; i < dim; ++i) {
			double input = inputVector.get(i);
			retlist.set(i, 1 / (1 + Math.exp(-input)));
		}
		return retlist;
	}

	@Override
	public List<Double> getInputGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {
		if (inputVector.size() != dim) throw new IllegalArgumentException("wrongly sized input vector");
		if (outputVector.size() != dim) throw new IllegalArgumentException("wrongly sized output vector");
		if (outputGradient.size() != dim) throw new IllegalArgumentException("wrongly sized output gradient");
		if (paramVector.size() != 0) throw new IllegalArgumentException("wrongly sized param vector");
		List<Double> retlist = new DoubleList(dim);
		for(int i = 0; i < dim; ++i) {
			double output = outputVector.get(i);
			double outputSensitivity = outputGradient.get(i);
			retlist.set(i, output * (1 - output) * outputSensitivity);
		}
		return retlist;
	}

}
