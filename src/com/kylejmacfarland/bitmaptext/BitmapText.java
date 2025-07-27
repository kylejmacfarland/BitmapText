package com.kylejmacfarland.bitmaptext;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class BitmapText {
	
	public BitmapText(String[] args) {
		run(args);
	}
	
	protected boolean run(String[] args) {
		if (args.length == 0) {
			printHelp();
			return true;
		}
		if (args.length == 1 && args[0].equals("-h")) {
			printHelp();
			return true;
		}
		// Read user arguments.
		boolean monochrome = false;
		String inputPath = null;
		String outputPath = null;
		int width = -1;
		int r = 255;
		int g = 255;
		int b = 255;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) {
				i++;
				if (i >= args.length) {
					System.out.println("-i flag given but no file specified. Exiting program.");
					return false;
				}
				inputPath = args[i];
			} else if (args[i].equals("-o")) {
				i++;
				if (i >= args.length) {
					break;
				}
				outputPath = args[i];
				if (!outputPath.endsWith(".bmp")) {
					outputPath += ".bmp";
				}
			} else if (args[i].equals("-w")) {
				i++;
				if (i >= args.length) {
					break;
				}
				try {
					width = Integer.parseInt(args[i]);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (args[i].equals("-m")) {
				monochrome = true;
			} else if (args[i].equals("-c")) {
				if (i + 3 >= args.length) {
					break;
				}
				try {
					r = Integer.parseInt(args[i + 1]);
					g = Integer.parseInt(args[i + 2]);
					b = Integer.parseInt(args[i + 3]);
				} catch (Exception ex) {
					ex.printStackTrace();
					r = 255;
					g = 255;
					b = 255;
				}
				if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
					r = 255;
					g = 255;
					b = 255;
				}
				i += 3;
			}
		}
		if (inputPath == null) {
			System.out.println("No input file specified. Exiting program.");
			return false;
		}
		// Add .bmp extention to output file if not present.
		if (outputPath == null) {
			if (inputPath.indexOf(".") == -1) {
				outputPath = inputPath + ".bmp";
			} else {
				outputPath = inputPath.substring(0, inputPath.lastIndexOf(".")) + ".bmp";
			}
		}
		if (!isValidPath(inputPath)) {
			System.out.printf("Input file \"%s\" is not a valid path.\n", inputPath);
			return false;
		}
		if (!isValidPath(outputPath)) {
			System.out.printf("Output file \"%s\" is not a valid path.\n", outputPath);
			return false;
		}
		BitmapWriter bw = new BitmapWriter();
		boolean successful = true;
		if (width > 0) {
			successful = bw.createFile(inputPath, outputPath, width, monochrome, r, g, b);
		} else {
			successful = bw.createFile(inputPath, outputPath, monochrome, r, g, b);
		}
		if (successful) {
			System.out.printf("Program complete. Image saved to \"%s\".", outputPath);
		} else {
			System.out.println("Failed to save image. Exiting program.");
		}
		return successful;
	}
	
	private boolean isValidPath(String path) {
		try {
			Paths.get(path);
			return true;
		} catch (InvalidPathException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	private void printHelp() {
		System.out.println("BitmapText will embed a text file in a bitmap image.");
		System.out.println("usage: bitmaptext.jar -i <input-path> [-o <output-path>] [-w <width>] [-m [-c <r> <g> <b>]] | [-h]");
		System.out.println("-i specifies the path to the file you want to embed.");
		System.out.println("-o specifies the path of the output bitmap file. The file will add \".bmp\" to the end of the path if not already present.");
		System.out.println("-w will create an image with the specified width. Without it, the program will create an image with width and height as close as possible. Non-integer arguments will be ignored.");
		System.out.println("-m creates an image with a single color. The program defaults to white when not specified with the -c flag.");
		System.out.println("-c specifies the rgb values of the final image if the -m flag was set. Each value must be an integer in range [0, 255]. Any invalid arguments will default the color to white.");
		System.out.println("-h will show this help text. This help text will also be displayed if no arguments are passed to the program.");
	}
	
	public static void main(String[] args) {
		new BitmapText(args);
	}
	
}
