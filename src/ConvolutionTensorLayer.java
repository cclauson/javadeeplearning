import java.util.List;

import org.jblas.FloatMatrix;

public class ConvolutionTensorLayer implements Layer {

	private final int featureMapWidth;
	private final int featureMapHeight;
	private final int numFeatureMaps;
	private final int kernelWidth;
	private final int kernelHeight;
	private final int numKernels;
	
	public ConvolutionTensorLayer(
			int featureMapWidth,
			int featureMapHeight,
			int numFeatureMaps,
			int kernelWidth,
			int kernelHeight,
			int numKernels
	) {
		this.featureMapWidth = featureMapWidth;
		this.featureMapHeight = featureMapHeight;
		this.numFeatureMaps = numFeatureMaps;
		this.kernelWidth = kernelWidth;
		this.kernelHeight = kernelHeight;
		this.numKernels = numKernels;
	}
	
	@Override
	public int getParamDim() {
		return kernelWidth * kernelHeight * numKernels;
	}

	@Override
	public int getInputDim() {
		return featureMapWidth * featureMapHeight * numFeatureMaps;
	}

	@Override
	public int getOutputDim() {
		return getInputDim() * numKernels;
	}

	//object that we use to abstractly construct a tensor
	private static interface TensorBuilder {
		public void setEntryAt(int i, int j, int k, double val);
	}
	
	//rather than store the tensor as a data structure, it's
	//better to have a routine that knows how to construct
	//the tensor against an abstract interface, this turns
	//out to be more useful and flexible.  The other way
	//is impossible because the full tensor is too large in
	//memory, and naive sparse implementations are too slow.
	private void constructConvTensor(TensorBuilder tb) {
		
		int kernelXCenter = kernelWidth / 2;
		int kernelYCenter = kernelHeight / 2;
		
		VolumeIndexer inputIndexer = new VolumeIndexer(featureMapWidth, featureMapHeight, numFeatureMaps);
		//int inputDim = inputIndexer.size();
		VolumeIndexer outputIndexer = new VolumeIndexer(featureMapWidth, featureMapHeight, numFeatureMaps * numKernels);
		int outputDim = outputIndexer.size();
		VolumeIndexer parameterIndexer = new VolumeIndexer(kernelWidth, kernelHeight, numKernels);
		//int numParams = parameterIndexer.size();		
		
		//for each input feature map
		for(int inputFeatureMap = 0; inputFeatureMap < numFeatureMaps; ++inputFeatureMap) {
			//for each kernel
			for(int d = 0; d < numKernels; ++d) {
				//for each feature map pixel
				for(int xImage = 0; xImage < featureMapWidth; ++xImage) {
					for(int yImage = 0; yImage < featureMapHeight; ++yImage) {
						//for each kernel pixel
						for(int xKernel = 0; xKernel < kernelWidth; ++xKernel) {
							for(int yKernel = 0; yKernel < kernelHeight; ++yKernel) {
								//compute source x and y, if out of bounds ignore
								int inputXIndex = xImage + xKernel - kernelXCenter;
								if (inputXIndex < 0) continue;
								if (inputXIndex >= featureMapWidth) continue;
								int inputYIndex = yImage + yKernel - kernelYCenter;
								if (inputYIndex < 0) continue;
								if (inputYIndex >= featureMapHeight) continue;

								int inputIndex = inputIndexer.paramIndexFor3DIndex(0, inputXIndex, inputYIndex);
								int paramIndex = parameterIndexer.paramIndexFor3DIndex(d, xKernel, yKernel);
								int outputIndex = outputIndexer.paramIndexFor3DIndex(inputFeatureMap * numKernels + d, xImage, yImage);
								//tensor.setEntryAt(paramIndex, inputIndex, outputIndex, 1.0);
								//data[outputIndex + outputDim * inputIndex] = paramVector.get(paramIndex).floatValue();
								tb.setEntryAt(paramIndex, inputIndex, outputIndex, 1.0);
							}
						}
					}
				}
			}
		}
	}
	
	private FloatMatrix getForwardMatrix(List<Double> paramVector) {
		float[] data = new float[getInputDim() * getOutputDim()];
		constructConvTensor(new TensorBuilder() {
			@Override
			public void setEntryAt(int i, int j, int k, double val) {
				data[k + getOutputDim() * j] = (float)val * paramVector.get(i).floatValue();
			}
		});
		final FloatMatrix ret = new FloatMatrix(getOutputDim(), getInputDim(), data);
		return ret;
	}
	
	private FloatMatrix getTransposedForwardMatrix(List<Double> paramVector) {
		float[] data = new float[getInputDim() * getOutputDim()];
		constructConvTensor(new TensorBuilder() {
			@Override
			public void setEntryAt(int i, int j, int k, double val) {
				data[j + getInputDim() * k] = (float)val * paramVector.get(i).floatValue();
			}
		});
		final FloatMatrix ret = new FloatMatrix(getInputDim(), getOutputDim(), data);
		return ret;
	}
	
	//construct matrix that takes input values to parameter gradient
	private FloatMatrix getParamGradMatrix(List<Double> outGradVector) {
		float[] data = new float[getParamDim() * getInputDim()];
		constructConvTensor(new TensorBuilder() {
			@Override
			public void setEntryAt(int i, int j, int k, double val) {
				data[i + getParamDim() * j] = (float)val * outGradVector.get(k).floatValue();
			}
		});
		return new FloatMatrix(getParamDim(), getInputDim(), data);
	}

	private List<Double> mulMatrixVector(FloatMatrix mat, List<Double> vec) {
		if (mat.columns != vec.size())
			throw new IllegalArgumentException("vector matrix dimension mismatch");
		final DoubleList inputDl = (DoubleList) vec;
		final FloatMatrix res = mat.mmul(new FloatMatrix(inputDl.asFloatArray()));
		return new DoubleList(res.data);
	}
	
	@Override
	public List<Double> evalForward(List<Double> inputVector, List<Double> paramVector) {
		System.out.println("eval forward");
		final FloatMatrix fm = getForwardMatrix(paramVector);
		System.out.println("Got matrix");
		List<Double> ret = mulMatrixVector(fm, inputVector);
		System.out.println("end eval forward");
		return ret;
	}

	@Override
	public List<Double> getParamGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {
		System.out.println("param grad");
		final FloatMatrix fm = getParamGradMatrix(outputGradient);
		System.out.println("Got matrix");
		List<Double> ret = mulMatrixVector(fm, inputVector);
		System.out.println("end param grad");
		return ret;
	}

	@Override
	public List<Double> getInputGradient(List<Double> inputVector, List<Double> paramVector, List<Double> outputVector,
			List<Double> outputGradient) {
		System.out.println("input grad");
		final FloatMatrix fm = getTransposedForwardMatrix(paramVector);
		System.out.println("Got matrix");
		List<Double> ret = mulMatrixVector(fm, outputGradient);
		System.out.println("end input grad");
		return ret;
	}

}
