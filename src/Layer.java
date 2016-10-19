import java.util.List;

public interface Layer {

	public int getParamDim();
	public int getInputDim();
	public int getOutputDim();
	
	public List<Double> evalForward(List<Double> inputVector, List<Double> paramVector);
	public List<Double> getParamGradient(List<Double> inputVector, List<Double> paramVector,
			List<Double> outputVector, List<Double> outputGradient);
	public List<Double> getInputGradient(List<Double> inputVector, List<Double> paramVector,
			List<Double> outputVector, List<Double> outputGradient);

}
