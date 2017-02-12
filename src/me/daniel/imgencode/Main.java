package me.daniel.imgencode;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class Main extends Component
{
	private static final boolean DEBUG = false;
	
	public static void main(String[] args)
	{
		new Main();
	}
	
	public Main() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("(C)ompile or (D)ecompile: ");
		String choice = scanner.nextLine().trim();
		if(choice.equalsIgnoreCase("d")) {
			//decompiling from an image
			System.out.print("Image path: ");
			File to_decomp = new File(scanner.nextLine());
			System.out.print("Path to save to: ");
			File file = new File(scanner.nextLine());
			System.out.println("Decompiling image to file.");
			decompile(file, to_decomp);
		} else if(choice.equalsIgnoreCase("c")) {
			//compiling to an image
			System.out.print("To compile path: ");
			File to_translate = new File(scanner.nextLine());
			System.out.print("Path to save to: ");
			File file = new File(scanner.nextLine());
			System.out.printf("Translating string to an image at \"%s\".\n", to_translate, file.getAbsolutePath());
			save(file, convert(to_translate));
		} else {
			System.err.println("Invalid choice. Aborting.");
		}
		scanner.close();
	}
	
	private void decompile(File save_to, File image) {
		BufferedImage img;
		try {
			img = ImageIO.read(image);
		} catch(IOException e) {
			System.err.println("Not a valid image.");
			return;
		}
		int[] pixels = null;
		PixelGrabber grabber = new PixelGrabber(img, 0, 0, -1, -1, true);
		try {
			if(grabber.grabPixels()) {
				pixels = (int[]) grabber.getPixels();
			}
		} catch(InterruptedException e) {
			System.err.println("rip the pixels");
			return;
		}
		String converted = "";
		for(int i : pixels) {
			int r = (i >> 16) & 0xFF;
			int g = (i >> 8) & 0xFF;
			int b = (i & 0xFF);
			converted += (char)r + "" + (char)g + "" + (char)b;
		}
		try(FileWriter fw = new FileWriter(save_to)) {
			fw.write(converted);
			fw.flush();
			System.out.println("Wrote file " + save_to.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Could not open file to save to for writing.");
			return;
		}
	}

	private int[] convert(File f)
	{
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			System.exit(1);
		}
		String s = "";
		while(scanner.hasNext()) {
			s += scanner.next() + " ";
		}
		int[] converted = new int[s.length()];
		int index = 0;
		for (char c : s.toCharArray()) {
			if (c > 255) c = 255;
			if (c < 0) c = 0;
			converted[index++] = c;
		}
		return converted;
	}
	
	private void save(File file, int[] buffer) {
		int index = 0;
		List<Integer> pixels = new ArrayList<>();
		while(index < buffer.length+1) {
			int r = 50;
			int g = 50;
			int b = 50;
			try {
				r = buffer[index];
				g = buffer[index+1];
				b = buffer[index+2];
				if(DEBUG)System.out.printf("Chars: [%c, %c, %c] -> rgb [%d, %d, %d]\n", buffer[index], buffer[index+1], buffer[index+2], r, g, b);
			} catch(ArrayIndexOutOfBoundsException e) {
				if(DEBUG)System.out.println("Whoops");
			}
			pixels.add(new Color(r, g, b).getRGB());
			index+=3;
		}
		int width = 400;
		int height = 400;
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bimg.createGraphics();
		Random random = new Random();
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int r = random.nextInt(125);
				int g = random.nextInt(125);
				int b = random.nextInt(125);
				graphics.setColor(new Color(r,g,b));
				graphics.fillRect(j, i, 1, 1);
			}
		}
		int x, y;
		int xOffset = random.nextInt(width);
		int yOffset = random.nextInt(height);
		if(xOffset+pixels.size() > width) {
			xOffset = (width-pixels.size());
		}
		if(DEBUG)System.out.printf("xOffset: %s, yOffset: %s", xOffset, yOffset);
		for(int i = 0; i < pixels.size(); i++) {
			x = xOffset + (i % width);
			y = yOffset + (i / height);
			
			graphics.setColor(new Color(pixels.get(i)));
			graphics.fillRect(x, y, 1, 1);
		}
		try {
			ImageIO.write((RenderedImage)bimg, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}