import java.util.List;

import org.jblas.FloatMatrix;

public class InnerProductLayer implements Layer {
	
	private int inputDim;
	private int outputDim;
	
	public InnerProductLayer(int inputDim, int outputDim) {
		this.inputDim = inputDim;
		this.outputDim = outputDim;
	}
	
	@Override public int getParamDim() { return inputDim * outputDim; }
	@Override public int getInputDim() { return inputDim; }
	@Override public int getOutputDim() { return outputDim; }

	@Override
	public List<Double> evalForward(List<Double> inputVector, List<Double> paramVector) {
		DoubleList dlParams = (DoubleList) paramVector;
		FloatMatrix matrix = new FloatMatrix(this.outputDim, this.inputDim, dlParams.asFloatArray());
		DoubleList dlInput = (DoubleList) inputVector;
		FloatMatrix invec = new FloatMatrix(dlInput.asFloatArray());
		FloatMatrix fm = matrix.mmul(invec);
		return new DoubleList(fm.data);
	}

	@Override
	public List<Double> getParamGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {

		DoubleList dlInput = (DoubleList) inputVector;
		FloatMatrix inputVec = new FloatMatrix(dlInput.asFloatArray());
		DoubleList dlOutGrad = (DoubleList) outputGradient;
		FloatMatrix outGradVec = new FloatMatrix(dlOutGrad.asFloatArray());
		
		FloatMatrix paramVec = outGradVec.mmul(inputVec.transpose());
		//FloatMatrix paramVec = outGradVec.mmul(inputVec.transpose());
		return new DoubleList(paramVec.data);
	}

	@Override
	public List<Double> getInputGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {
		DoubleList dlParams = (DoubleList) paramVector;
		FloatMatrix matrix = new FloatMatrix(this.outputDim, this.inputDim, dlParams.asFloatArray());
		DoubleList dlOutGrad = (DoubleList) outputGradient;
		FloatMatrix outGradVec = new FloatMatrix(dlOutGrad.asFloatArray());
		FloatMatrix fm = outGradVec.transpose().mmul(matrix);
		List<Double> ret = new DoubleList(fm.data);
		return ret;
	}

}
