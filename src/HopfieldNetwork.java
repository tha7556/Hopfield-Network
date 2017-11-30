import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HopfieldNetwork {
	private int nodeNum;
	private double[][] trainingSet;
	private Node[] nodes;
	private Map<String,Double> weights;
	public HopfieldNetwork(Shape[] trainingSet) {
		this.trainingSet = new double[trainingSet.length][trainingSet[0].getPixels().length];
		for(int i = 0; i < trainingSet.length; i++) {
			for(int y = 0; y < trainingSet[i].getPixels().length; y++) {
				this.trainingSet[i] = trainingSet[i].getPixels();
			}
		}
		this.nodeNum = trainingSet[0].getPixels().length;
		nodes = new Node[nodeNum];
		weights = new HashMap<String,Double>();
		createNetwork();
	}
	
	public HopfieldNetwork(double[][] trainingSet) {
		this.trainingSet = trainingSet;
		this.nodeNum = this.trainingSet[0].length;
		nodes = new Node[nodeNum];
		weights = new HashMap<String,Double>();
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
			Node a = nodes[i];
			for(int j = 0; j < nodeNum; j++) {
				if(i != j) {
					Node b = nodes[j];
					weights.put(a.getName()+","+b.getName(), 0.0);
				}
			}
		}
		//update weights based on data
		for(int e = 0; e < trainingSet.length; e++) {
			for(int x = 0; x < nodeNum; x++) {
				for(int y = 0; y < nodeNum; y++) {
					if(x != y) {
						double prevWeight = weights.get(x+","+y);
						weights.put(x+","+y, prevWeight+(trainingSet[e][x] * trainingSet[e][y]));
					}
				}
			}
		}
	}
	public void calculateOutputs(double[] input) {
		double[][] temp = new double[nodeNum][nodeNum];
		for(double[] arr : temp)
			for(double d : arr)
				d = 0.0;
		for(int x = 0; x < nodeNum; x++) {
			Node xNode = getNode(x);
			for(int y = 0; y < nodeNum; y++) {
				if(x != y) {
				Node yNode = getNode(y);
				temp[x][y] += input[x] * getWeight(xNode,yNode);
				}
			}
		}
		for(int x = 0; x < nodeNum; x++) {
			Node xNode = getNode(x);
			double data = xNode.getData();
			for(int y = 0; y < nodeNum; y++) {
				data += temp[x][y];
			}
			if(data >= 0)
				data = 1.0;
			else
				data = 0.0;
			xNode.setData((int)data);
		}
	}
	public void run(double[] input) {
		boolean changed = true;
		for(int f = 0; f < 10000; f++){
			//System.out.println("running");
			double[] prevData = new double[nodeNum];
			for(int i = 0; i < nodeNum; i++) {
				prevData[i] = nodes[i].getData();
			}
			calculateOutputs(input);
			changed = false;
			for(int i = 0; i < nodeNum; i++) {
				if(prevData[i] != nodes[i].getData()) {
					changed = true;
					break;
				}
			}
		}
	}
	public void printResult(int width) {
		int i = 0;
		for(Node node : nodes) {
			if(i < width) {
				System.out.print(node.getData()+" ");
			}
			else {
				i = 0;
				System.out.print("\n"+node.getData()+" ");
			}
			i++;
		}
		System.out.println();
	}
	public void printWeights() {
		for(int x = 0; x < nodeNum; x++) {
			for(int y = 0; y < nodeNum; y++) {
				if(x != y) {
					if(getWeight(x,y) > 0)
						System.out.print("("+(int)getWeight(x,y) + ")  ");
					else
						System.out.print("("+(int)getWeight(x,y) + ") ");
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
						pWriter.print((int)getWeight(x,y) + ",");
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
	public double getWeight(String key) {
		return weights.get(key);
	}
	public double getWeight(Node a, Node b) {
		return weights.get(a.getName()+","+b.getName());
	}
	public double getWeight(int a, int b) {
		return getWeight(a+","+b);
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
		String[] shapeNames = {"Plus","X","Backslash","Forwardslash","Line","Minus"};
		Shape[] shapes = new Shape[shapeNames.length];
		double[][] tSet = {{1.0,-1.0},
						   {-1.0,1.0}		};
		for(int i = 0; i < shapeNames.length; i++)
			shapes[i] = new Shape("Images\\"+shapeNames[i]+".png");
		
		HopfieldNetwork network = new HopfieldNetwork(shapes);
		Shape input = shapes[1];
		double[] noisy = HopfieldNetwork.addNoise(input, .1);
		network.run(noisy);

		network.printResult(input.getWidth());
		System.out.println("   vs");
		Shape.printArray(noisy, input.getWidth());
		System.out.println();
		
		network.printWeightsToFile("weights.csv");
	}
}
