import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DeepLearningPipeline {
	
	private final List<Layer> layers;
	private final Random random = new Random();
	
	public DeepLearningPipeline(List<Layer> layers) {
		if (layers == null) throw new IllegalArgumentException("layers is null");
		if (layers.isEmpty()) throw new IllegalArgumentException(
				"there must be at least one layer, but layers is empty");
		for(int i = 0; i < layers.size() - 1; ++i) {
			int outputDim = layers.get(i).getOutputDim();
			int inputDim = layers.get(i + 1).getInputDim();
			if(outputDim != inputDim) {
				throw new IllegalArgumentException("output dimension of layer " + i +
						" (" + outputDim + ") does not match input dimension of layer " +
						(i + 1) +"(" + inputDim + ")");
			}
		}
		this.layers = layers;
	}

	public List<List<Double>> getInitialParameterList() {
		return getInitialParameterList(new InitialParameterGenerator() {
			@Override
			public double genParam() {
				return (random.nextDouble() - 0.5) * 0.1;
			}
		});
	}
	
	public List<List<Double>> getInitialParameterList(InitialParameterGenerator ipg) {
		final List<List<Double>> retlist = new ArrayList<List<Double>>();
		int j = 0;
		for (Layer layer : layers) {
			//System.out.println("Getting parameters for layer " + j + ", number: " + layer.getParamDim());
			++j;
			//final List<Double> params = Arrays.asList(new Double[layer.getParamDim()]);
			final List<Double> params = new DoubleList(layer.getParamDim());
			for(int i = 0; i < layer.getParamDim(); ++i) {
				//if (i % 1000 == 0) System.out.println("i = " + i);
				//initialize to random number between -0.05 and 0.05
				params.set(i, ipg.genParam());
			}
			retlist.add(params);
		}
		return retlist;
	}
	
	public int getInputDim() {
		return layers.get(0).getInputDim();
	}
	
	public int getOutputDim() {
		return layers.get(layers.size() - 1).getOutputDim();
	}

	private List<List<Double>> getValsFromForwardEval(List<Double> inputVector, List<List<Double>> params) {
		List<List<Double>> intermediateVals = new ArrayList<List<Double>>();
		intermediateVals.add(inputVector);
		for (int i = 0; i < layers.size(); ++i) {
			//System.out.println("Doing forward propagation for layer " + i);
			List<Double> vals = intermediateVals.get(intermediateVals.size() - 1);
			Layer layer = layers.get(i);
			List<Double> forwardResult = layer.evalForward(vals, params.get(i));
			//System.out.println("Result for layer " + i + ": " + forwardResult);
			intermediateVals.add(forwardResult);
		}
		return intermediateVals;
	}
	
	public List<Double> classify(List<Double> inputVector, List<List<Double>> params) {
		final List<List<Double>> intermediateVals = getValsFromForwardEval(inputVector, params);
		return intermediateVals.get(intermediateVals.size() - 1);
	}
	
	//modify parameter vector in place
	public double train(List<Double> inputVector, List<Double> outputVector, List<List<Double>> params, double eta) {

		//System.out.println("Params: " + params);
		
		if(inputVector.size() != getInputDim()) throw new IllegalArgumentException(
				"input vector has wrong dimension");
		if(outputVector.size() != getOutputDim()) throw new IllegalArgumentException(
				"output vector has wrong dimension");
		
		final List<List<Double>> intermediateVals = getValsFromForwardEval(inputVector, params);
		
		final List<List<Double>> newParams = new ArrayList<List<Double>>();
		while(newParams.size() < layers.size()) newParams.add(null);
		
		List<Double> finalOutput = intermediateVals.get(intermediateVals.size() - 1);
		
		//System.out.println("Result of forward eval:");
		//System.out.println(finalOutput);
		
		double err = 0.0;
		//List<Double> sensitivity = Arrays.asList(new Double[finalOutput.size()]);
		List<Double> sensitivity = new DoubleList(finalOutput.size());
		//compute error and initial sensitivity
		
		for (int i = 0; i < finalOutput.size(); ++i) {
			double actualOutput = finalOutput.get(i);
			double targetOutput = outputVector.get(i);

			//the order matters
			double diff = actualOutput - targetOutput;
			
			err += diff * diff;
			sensitivity.set(i, 2 * diff);
		}
		
		//System.out.println("Sensitivity:");
		//System.out.println(sensitivity);

		
		for (int i = layers.size() - 1; i >= 0; --i) {
			//System.out.println("Doing backpropagation for layer " + i);
			Layer layer = layers.get(i);
			//System.out.println("Getting gradient for params");
			
			
			//System.out.println("Param gradient layer " + i + ":");
			
			
			List<Double> paramGradient = layer.getParamGradient(
				intermediateVals.get(i),
				params.get(i),
				intermediateVals.get(i + 1),
				sensitivity
			);
			
			
			//System.out.println(paramGradient);
			
			
			//System.out.println("Getting gradient for input");
			//update sensitivity
			sensitivity = layer.getInputGradient(
				intermediateVals.get(i),
				params.get(i),
				intermediateVals.get(i + 1),
				sensitivity
			);
			
			
			//System.out.println("Sensitivity layer " + i + ":");
			//System.out.println(sensitivity);
			
			
			//modify params in place
			List<Double> currParams = params.get(i);
			if (currParams.size() != paramGradient.size())
				throw new RuntimeException("param array and param gradient array dimensions don't match");
			for (int j = 0; j < currParams.size(); ++j) {
				currParams.set(j, currParams.get(j) - eta * paramGradient.get(j));
			}
		}
		
		return err;
	}

}
