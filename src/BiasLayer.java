import java.util.Arrays;
import java.util.List;

public class BiasLayer implements Layer {

	private final int dim;
	
	public BiasLayer(int dim) {
		this.dim = dim;
	}
	
	@Override public int getParamDim() { return dim; }
	@Override public int getInputDim() { return dim; }
	@Override public int getOutputDim() { return dim; }

	private void dimCheck(String name, List<Double> vector) {
		if(vector.size() != dim) throw new IllegalArgumentException("vector " + name +
			" does not have the right size (found " + vector.size() + " should be " + dim + ")");
	}
	
	@Override
	public List<Double> evalForward(List<Double> inputVector, List<Double> paramVector) {
		dimCheck("input", inputVector);
		dimCheck("param", paramVector);
		List<Double> ret = new DoubleList(dim);
		for(int i = 0; i < dim; ++i) {
			ret.set(i, inputVector.get(i) + paramVector.get(i));
		}
		return ret;
	}

	@Override
	public List<Double> getParamGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {
		dimCheck("input", inputVector);
		dimCheck("param", paramVector);
		dimCheck("output", outputVector);
		dimCheck("output gradient", outputGradient);
		return outputGradient;
	}

	@Override
	public List<Double> getInputGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {
		dimCheck("input", inputVector);
		dimCheck("param", paramVector);
		dimCheck("output", outputVector);
		dimCheck("output gradient", outputGradient);
		return outputGradient;		
	}

}
