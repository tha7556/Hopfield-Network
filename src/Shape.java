import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Shape {
	private BufferedImage image;
	private int height, width;
	private double[] pixels;
	public Shape(String fileName) {
		try {
			image = ImageIO.read(new File(fileName));
		} catch(Exception e) {
			e.printStackTrace();
		}
		height = image.getHeight();
		width = image.getWidth();
		pixels = new double[width*height];
		int i = 0;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(image.getRGB(x, y) == Color.BLACK.getRGB())
					pixels[i] = 1;
				else
					pixels[i] = -1;
				i++;
			}
		}
	 }
	public double[] getPixels() {
		return pixels;
	}
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public void printShape() {
		int i = 0;
		for(double d : pixels) {
			int x = 0;
			if(d > 0)
				x = 1;
			if(i < width) {
				System.out.print(x+" ");
			}
			else {
				i = 0;
				System.out.print("\n"+x+" ");
			}
			i++;
		}
		System.out.println();
	}
	public static void printArray(double[] arr,int width) {
		int i = 0;
		for(double d : arr) {
			int x = 0;
			if(d > 0)
				x = 1;
			if(i < width) {
				System.out.print(x+" ");
			}
			else {
				i = 0;
				System.out.print("\n"+x+" ");
			}
			i++;
		}
		System.out.println();
	}
}
