import java.util.Collections;
import java.util.List;

//common supertype for layer with no parameters
public abstract class AbstractParameterlessLayer implements Layer {
	
	@Override public int getParamDim() { return 0; }
	
	@Override
	public List<Double> getParamGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {
		return Collections.emptyList();
	}
	
}
