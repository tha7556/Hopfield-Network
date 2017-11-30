
public class Node {
	private int data;
	private String name;
	
	public Node(String name, int data) {
		this.data = data;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public int getData() {
		return data;
	}
	public void setData(int data) {
		this.data = data;
	}
	public int getIntData() {
		if(data < 0)
			return 0;
		return 1;
	}
}
