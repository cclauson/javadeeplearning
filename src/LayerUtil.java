import java.util.ArrayList;
import java.util.List;

public class LayerUtil {

	private LayerUtil() {}
	
	public static Layer createConvolutionLayer(
		int featureMapWidth,
		int featureMapHeight,
		int numFeatureMaps,
		int kernelWidth,
		int kernelHeight,
		int numKernels
	) {
		int kernelXCenter = kernelWidth / 2;
		int kernelYCenter = kernelHeight / 2;
		
		VolumeIndexer inputIndexer = new VolumeIndexer(featureMapWidth, featureMapHeight, numFeatureMaps);
		int inputDim = inputIndexer.size();
		
		VolumeIndexer outputIndexer = new VolumeIndexer(featureMapWidth, featureMapHeight, numFeatureMaps * numKernels);
		int outputDim = outputIndexer.size();
		VolumeIndexer parameterIndexer = new VolumeIndexer(kernelWidth, kernelHeight, numKernels);
		int numParams = parameterIndexer.size();
		
		MutableSparse3DTensor tensor = new MutableSparse3DTensor(numParams, inputDim, outputDim);
		
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
								tensor.setEntryAt(paramIndex, inputIndex, outputIndex, 1.0);
							}
						}
					}
				}
			}		
		}
		
		return new BilinearLayer(tensor);
	}
	
	public static Layer createPoolingLayer(
		int featureMapWidth,
		int featureMapHeight,
		int numFeatureMaps,
		int widthFactor,
		int heightFactor
	) {
		VolumeIndexer inputIndexer = new VolumeIndexer(featureMapWidth, featureMapHeight, numFeatureMaps);
		int pooledFeatureMapWidth = featureMapWidth / widthFactor;
		int pooledFeatureMapHeight = featureMapHeight / heightFactor;
		VolumeIndexer outputIndexer = new VolumeIndexer(pooledFeatureMapWidth, pooledFeatureMapHeight, numFeatureMaps);

		MutableSparseMatrix matrix = new MutableSparseMatrix(inputIndexer.size(), outputIndexer.size());
		for(int inputX = 0; inputX < featureMapWidth; ++inputX) {
			int outputX = inputX / widthFactor;
			if (outputX >= pooledFeatureMapWidth) continue;
			for(int inputY = 0; inputY < featureMapHeight; ++inputY) {
				int outputY = inputY / heightFactor;
				if (inputY >= pooledFeatureMapHeight) continue;
				for (int z = 0; z < numFeatureMaps; ++z) {
					int inputIndex = inputIndexer.paramIndexFor3DIndex(z, inputX, inputY);
					int outputIndex = outputIndexer.paramIndexFor3DIndex(z, outputX, outputY);
					matrix.setEntryAt(inputIndex, outputIndex, 1.0/(widthFactor * heightFactor));
				}
			}
		}
		return new LinearLayer(matrix);
	}
	
	public static List<Layer> getLayersForConvnet(
		int inputWidth,
		int inputHeight,
		int inputDepth,
		int kernelWidth,
		int kernelHeight,
		int numKernels,
		int poolingX,
		int poolingY
	) {
		List<Layer> retlayers = new ArrayList<Layer>();
		retlayers.add(
				new ConvolutionTensorLayer(
						inputWidth, inputHeight, inputDepth,
						kernelWidth, kernelHeight, numKernels)
				/*
				createConvolutionLayer(
			inputWidth, inputHeight, inputDepth,
			kernelWidth, kernelHeight, numKernels
		)
		*/
		);
		retlayers.add(createPoolingLayer(
			inputWidth, inputHeight, inputDepth * numKernels,
			poolingX, poolingY
		));
		int outputDim = inputDepth * numKernels * (inputWidth / poolingX) * (inputHeight / poolingY);
		retlayers.add(new SigmoidActivationLayer(outputDim));
		return retlayers;
	}
	
	public static Layer createInnerProductLayer(int inputDim, int outputDim) {
		/*
		int paramDim = inputDim * outputDim;
		MutableSparse3DTensor tensor = new MutableSparse3DTensor(paramDim, inputDim, outputDim);
		for(int i = 0; i < inputDim; ++i) {
			for(int j = 0; j < outputDim; ++j) {
				tensor.setEntryAt(i * outputDim + j, i, j, 1.0);
			}
		}
		*/
		return new BilinearLayer(new InnerProduct3DTensor(inputDim, outputDim));
	}
	
	public static List<Layer> getLayersForInnerProduct(int inputDim, int outputDim) {
		List<Layer> retlayers = new ArrayList<Layer>();
		//retlayers.add(createInnerProductLayer(inputDim, outputDim));
		retlayers.add(new InnerProductLayer(inputDim, outputDim));
		retlayers.add(new BiasLayer(outputDim));
		retlayers.add(new SigmoidActivationLayer(outputDim));
		return retlayers;
	}
	
}
