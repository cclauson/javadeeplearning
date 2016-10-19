import java.io.File;
import java.util.*;

import org.jblas.FloatMatrix;

public class Main {

	private Main() {}
	
	public static void main(String[] args) {
		//doSimpleAnnTest();
		//testInnerProductLayer();
		doMainImageClassification();
	}

	private static void testInnerProductLayer() {
		Layer layer = new InnerProductLayer(3, 4);
		List<Layer> layers = Collections.singletonList(layer);
		DeepLearningPipeline dlp = new DeepLearningPipeline(layers);
		List<List<Double>> params = dlp.getInitialParameterList(
				new InitialParameterGenerator() {
					private int i = -5;
					@Override
					public double genParam() {
						return (double) i++;
					}
				}
		);
		DoubleList in = new DoubleList(3);
		in.set(0, 1.0);
		in.set(1, 2.0);
		in.set(2, 3.0);

		DoubleList out = new DoubleList(4);
		out.set(0, 4.0);
		out.set(1, 5.0);
		out.set(2, 6.0);
		out.set(3, 7.0);

		dlp.train(in, out, params, 0.05);
		System.out.println(params);
		System.out.println("forward: " + out);
		
	}
	
	private static void doSimpleAnnTest() {
		List<Layer> pipeLayers = new ArrayList<Layer>();
		int outerDim = 8;
		int innerDim = 3;
		
		List<Layer> layerList;
		layerList = LayerUtil.getLayersForInnerProduct(outerDim, innerDim);
		pipeLayers.addAll(layerList);
		layerList = LayerUtil.getLayersForInnerProduct(innerDim, outerDim);
		pipeLayers.addAll(layerList);
		DeepLearningPipeline dlp = new DeepLearningPipeline(pipeLayers);
		
		List<DataItem> rgdi = new ArrayList<DataItem>();
		for (int i = 0; i < outerDim; ++i) {
			DataItem di = new DataItem(new DoubleList(outerDim), new DoubleList(outerDim));
			di.x.set(i, 1.0);
			di.y.set(i, 1.0);
			rgdi.add(di);
		}
		
		List<List<Double>> params = dlp.getInitialParameterList(
				/*
				new InitialParameterGenerator() {
					private int i = -3;
					@Override
					public double genParam() {
						return (double) i / 100;
					}
				}
				*/
		);
		
		
		for (int i = 0; i < 10000; ++i) {
			DataItem di = rgdi.get(i % outerDim);
			System.out.println(di);
			double error = dlp.train(di.x, di.y, params, 0.05);
			System.out.println("i: " + i + ", error: " + error);
		}
		
		System.out.println("======Printing final result======");
		for (int i = 0; i < outerDim; ++i) {
			List<Double> x = rgdi.get(i).x;
			List<Double> y = dlp.classify(x, params);
			System.out.println("x: " + x);
			System.out.println("y: " + y);
		}		
	}

	private static void doMainImageClassification() {
		List<Layer> pipeLayers = new ArrayList<Layer>();
		
		System.out.println("Creating first convnet");
		List<Layer> layerList = LayerUtil.getLayersForConvnet(
				28, 28, 1, //bitmaps are 28 x 28
				5, 5, 20, //use 20 kernels, 5 x 5 each
				2, 2 //pool in 2 x 2 squares
		);
		pipeLayers.addAll(layerList);
		System.out.println("Creating second convnet");
		layerList = LayerUtil.getLayersForConvnet(
				14, 14, 20, //now we have 20 feature maps, 14 x 14 each
				5, 5, 50, //use 50 kernels, 5 x 5 each
				2, 2 //pool in 2 x 2 squares
		);
		pipeLayers.addAll(layerList);
		//we should now have 1000 feature maps each 7 x 7 pixels,
		//so the total number of pixels is
		int numPixels = 20 * 7 * 7 * 50;
		//put all pixels from all feature maps into neural net layer,
		//funnel down to 500 neurons
		System.out.println("Creating first inner product");
		layerList = LayerUtil.getLayersForInnerProduct(numPixels, 500);
		pipeLayers.addAll(layerList);
		//now funnel down to 10 neurons, one for each numeral
		System.out.println("Creating second inner product");
		layerList = LayerUtil.getLayersForInnerProduct(500, 10);
		pipeLayers.addAll(layerList);
		
		DeepLearningPipeline dlp = new DeepLearningPipeline(pipeLayers);
		
		System.out.println("Creating initial parameter vector");
		List<List<Double>> params = dlp.getInitialParameterList();
		
		CharacterRecognitionDataset crd = new CharacterRecognitionDataset(
			new File("train-images-idx3-ubyte"),
			new File("train-labels-idx1-ubyte")
		);
		
		for (int k = 0; k < 1000; ++k) {
			DataItem di = crd.nextDataItem();
			System.out.println("Doing training round " + k);
			double err = dlp.train(di.x, di.y, params, 0.01);
			System.out.println("Error: " + err);
		}
		
		CharacterRecognitionDataset crdTest = new CharacterRecognitionDataset(
				new File("t10k-images-idx3-ubyte"),
				new File("t10k-labels-idx1-ubyte")
		);
		
		int numLookedAt = 0;
		int numSuccessful = 0;
		int i = 0;
		while (crdTest.hasMoreData()) {
			//if (i % 100 == 0)
				System.out.println("Testing accuracy, i = " + i);
			
			if (i >= 50) break;
			
			DataItem di = crdTest.nextDataItem();
			List<Double> out = dlp.classify(di.x, params);
			int outIndex = interpretOutputVector(out);
			int outIndexTheoretical = interpretOutputVector(di.y);
			++numLookedAt;
			if (outIndex == outIndexTheoretical) ++numSuccessful;
			++i;
		}
		System.out.println("Percent accuracy: " +
				(100.0 * numSuccessful) / numLookedAt);
	}

	//specifically, interpret for the numeral recognition
	//problem, find the index in the vector with the highest
	//probability and return the index
	private static int interpretOutputVector(List<Double> out) {
		int index = 0;
		double val = out.get(0);
		for(int i = 1; i < out.size(); ++i) {
			double currVal = out.get(i);
			if (currVal >= val) {
				val = currVal;
				index = i;
			}
		}
		return index;
	}
	
	private void dafdsf() {
		/*
		for (int i = 0; i < 28 * 28; ++i) {
			if (i % 28 == 0) System.out.println();
			char c;
			if (di.x.get(i) > 0.8)
				c = 'x';
			else
				c = ' ';
			System.out.print(c);
		}
		System.out.println();
		for (int i = 0; i < 10; ++i) {
			if (di.y.get(i) == 1.0) {
				System.out.println(i);
				break;
			}
		}
		*/
	}
	
}
