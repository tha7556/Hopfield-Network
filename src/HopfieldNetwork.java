import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HopfieldNetwork {
	private int nodeNum;
	private double[][] trainingSet;
	private Node[] nodes;
	private double[][] weights;
	public HopfieldNetwork(Shape[] trainingSet) {
		this.trainingSet = new double[trainingSet.length][trainingSet[0].getPixels().length];
		for(int i = 0; i < trainingSet.length; i++) {
			for(int y = 0; y < trainingSet[i].getPixels().length; y++) {
				this.trainingSet[i] = trainingSet[i].getPixels();
			}
		}
		this.nodeNum = trainingSet[0].getPixels().length;
		nodes = new Node[nodeNum];
		weights = new double[nodeNum][nodeNum];
		createNetwork();
	}
	
	public HopfieldNetwork(double[][] trainingSet) {
		this.trainingSet = trainingSet;
		this.nodeNum = this.trainingSet[0].length;
		nodes = new Node[nodeNum];
		weights = new double[nodeNum][nodeNum];
		createNetwork();
	}
	
	public void createNetwork() {
		//Create Nodes and add to table
		for(int i = 0; i < nodeNum; i++) {
			Node node = new Node(i+"",0);
			nodes[i] = node;
		}
		//initialize weights
		for(int i = 0; i < nodeNum; i++) {
			for(int j = 0; j < nodeNum; j++) {
					weights[i][j] = 0.0;
			}
		}
		//update weights based on data
		for(int e = 0; e < trainingSet.length; e++) {
			for(int x = 0; x < nodeNum; x++) {
				for(int y = 0; y < nodeNum; y++) {
					if(x != y) {
						weights[x][y] += trainingSet[e][x] * trainingSet[e][y];
					}
				}
			}
		}
	}
	public void calculateOutputs(double[] input) {
		int[][] temp = new int[nodeNum][nodeNum];
		for(int i = 0; i < nodeNum; i++) {
			for(int j = 0; j < nodeNum; j++) {
				temp[i][j] = 0;
			}
		}
		
		for(int x = 0; x < nodeNum; x++) {
			for(int y = 0; y < nodeNum; y++) {
				if(x != y)
					temp[x][y] += input[x] * weights[x][y];
			}
		}
		for(int y = 0; y < nodeNum; y++) {
			int data = 0;
			for(int x = 0; x < nodeNum; x++) {
				data += temp[x][y];
			}
			data = ((data) >= 0 ? 1 : -1);
			nodes[y].setData(data);
		}
	}
	public String run(double[] input,String[] names) {
		int j = 0, maxIters = 1;
		for(Node n : nodes) {
			n.setData(0);
		}
		while(evalData(names) == null && j < maxIters) {
			calculateOutputs(input);
			j++;
		}
		if(j == maxIters)
			return "Closest: "+findClosest(names);
		return evalData(names);
	}
	public void printResult(int width) {
		for(int i = 0; i < nodeNum; i++) {
			if(i % width ==0)
				System.out.println();
			if(nodes[i].getData() >= 0)
				System.out.print("1 ");
			else
				System.out.print("0 ");
		}
		System.out.println();
	}
	public void printWeights() {
		for(int x = 0; x < nodeNum; x++) {
			for(int y = 0; y < nodeNum; y++) {
				if(x != y) {
					if(weights[x][y] > 0.0)
						System.out.print("("+(int)weights[x][y] + ")  ");
					else
						System.out.print("("+(int)weights[x][y] + ") ");
				}
				else
					System.out.print("(0)  ");
			}
			System.out.println();
		}
	}
	public void printWeightsToFile(String fileName) {
		try {
			File file = new File(fileName);
			FileWriter fWriter = new FileWriter(file);
			PrintWriter pWriter = new PrintWriter(fWriter);
			
			pWriter.print(",");
			for(int i = 1; i < nodeNum+1; i++)
				pWriter.print(i+",");
			pWriter.println();
			for(int x = 0; x < nodeNum; x++) {
				pWriter.print((x+1)+",");
				for(int y = 0; y < nodeNum; y++) {
					if(x != y) 
						pWriter.print((int)weights[x][y] + ",");
					else
						pWriter.print("0,");
				}
				pWriter.println();
			}
			
			fWriter.close();
			pWriter.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public Node getNode(int key) {
		return nodes[key];
	}
	public String evalData(String[] names) {
		for(int i = 0; i < trainingSet.length; i++) {
			for(int j = 0; j < trainingSet[i].length; j++) {
				int given = (int)trainingSet[i][j];
				if(given < 0)
					given = 0;
				else 
					given = 1;
				if(nodes[j].getData() != given) {
					break;
				}
				else if(j == trainingSet[i].length-1 && nodes[j].getData() == given) {
					return names[i];
				}
			}
		}
		return null;
	}
	public String findClosest(String[] names) {
		int maxSimilar = -1, maxIndex = -1;
		for(int i = 0; i < trainingSet.length; i++) {
			int similar = 0;
			for(int j = 0; j < trainingSet[i].length; j++) {
				int given = (int)trainingSet[i][j];
				if(given < 0)
					given = 0;
				else 
					given = 1;
				if(nodes[j].getData() == given)
					similar++;
			}
			if(similar > maxSimilar) {
				maxSimilar = similar;
				maxIndex = i;
			}
		}
		return names[maxIndex];
	}
	public static double[] addNoise(double[] original, double noise) {
		double[] result = Arrays.copyOf(original, original.length);
		Random rand = new Random();
		for(int i = 0; i < result.length; i++) {
			if(rand.nextDouble() < noise) {
				result[i] *= -1.0;
			}
		}
		return result;
	}
	public static double[] addNoise(Shape original, double noise) {
		return HopfieldNetwork.addNoise(original.getPixels(), noise);
	}
	
	public static void main(String[] args) {
		String[] shapeNames = {"Plus","X","Box","SmallBox"};
		Shape[] shapes = new Shape[shapeNames.length];
		
		for(int i = 0; i < shapeNames.length; i++)
			shapes[i] = new Shape("Images\\"+shapeNames[i]+".png");
		
		HopfieldNetwork network = new HopfieldNetwork(shapes);
		
		double noise = 0.0;
		ArrayList<Object[]> results = new ArrayList<Object[]>();
		int numCorrect = 0;
		int[] numsCorrect = {0,0,0,0};
		for(int i = 0; i < 1000; i++) {
			if(i > 0 && i % 100 == 0)
				noise += .05;
			for(int j = 0; j < shapes.length; j++) {
				network = new HopfieldNetwork(shapes);
				Shape shape = shapes[j];
				String result = network.run(HopfieldNetwork.addNoise(shape, noise), shapeNames);
				//System.out.println("Current shape: "+shapeNames[j]+", vs result: "+result+", at noise level: "+noise);
				result = result.substring(result.indexOf(":")+2);
				boolean correct = result.equals(shapeNames[j]);
				int c = 0;
				if(correct) {
					c = 1;
					numCorrect++;
					numsCorrect[j]++;
				}
				Object[] arr = {noise,shapeNames[j],c,result};
				results.add(arr);
			}
		}
		System.out.println(numCorrect+"/"+1000*shapes.length);
		//write results
		try {
		File file = new File("results.csv");
		FileWriter fWriter = new FileWriter(file);
		PrintWriter print = new PrintWriter(fWriter);
		
		for(int i = 0; i < shapeNames.length; i++) {
			print.println(shapeNames[i]+","+numsCorrect[i]);
		}
		print.println("Shape,Noise,Result,Correct");
		for(Object[] arr : results) {
			print.println(arr[1]+","+arr[0]+","+arr[3]+","+arr[2]);
		}
		
		fWriter.close();
		print.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	
	}
}
