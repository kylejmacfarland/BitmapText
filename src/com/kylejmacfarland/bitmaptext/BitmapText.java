package com.kylejmacfarland.bitmaptext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class BitmapText {
	
	BitmapText(String[] args) {
		if (args.length == 0) {
			printHelp();
			return;
		}
		if (args.length == 1 && args[0].equals("-h")) {
			printHelp();
			return;
		}
		String outputFile;
		if (args.length == 1) {
			if (args[0].indexOf(".") == -1) {
				outputFile = args[0] + ".bmp";
			} else {
				outputFile = args[0].substring(0, args[0].lastIndexOf(".")) + ".bmp";
			}
		} else {
			outputFile = args[1];
			if (!outputFile.endsWith(".bmp")) {
				outputFile += ".bmp";
			}
		}
		if (createFile(args[0], outputFile)) {
			System.out.printf("Program complete. Image saved to \"%s\".", outputFile);
		}
	}
	
	private boolean createFile(String inputFile, String outputFile) {
		Random rand = new Random();
		byte[] secretMessage;
		try {
			secretMessage = readFile(inputFile);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.printf("Could not read file \"%s\". Exiting program.\n", inputFile);
			return false;
		}
		int width = (int) Math.ceil(Math.sqrt(Math.ceil((secretMessage.length + 2) / 3.0)));
		int height = width;
		int pixelDataSize = width * 3;
		if (pixelDataSize % 4 != 0) {
			pixelDataSize += 4 - width * 3 % 4;
		}
		pixelDataSize *= height;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(outputFile);
			writeHeaders(fos, width, height, pixelDataSize);
			fos.write("\n".getBytes());
			fos.write(secretMessage);
			fos.write("\n".getBytes());
			for (int i = 0; i < pixelDataSize - secretMessage.length - 2; i++) {
				fos.write(rand.nextInt(95) + 32);
			}
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.printf("Could not write to file \"%s\". Exiting program.\n", outputFile);
			return false;
		}
		return true;
	}
	
	private byte[] intToBytes(int num, int size) {
		return ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN).putInt(num).array();
	}
	
	private byte[] charToBytes(char num, int size) {
		return ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN).putChar(num).array();
	}
	
	private byte[] readFile(String filename) throws IOException {
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		byte[] result = new byte[(int) f.length()];
		fis.read(result);
		fis.close();
		return result;
	}
	
	private void writeHeaders(FileOutputStream fos, int width, int height, int pixelDataSize) throws IOException {
		int filesize = 14 + 40 + pixelDataSize;	//12-byte header, 40-byte DIB header, pixel data size
		int offset = 14 + 40;					//12-byte header, 40-byte DIB header
		// BMP Header
		fos.write("BM".getBytes());				//file always starts with "BM"
		fos.write(intToBytes(filesize, 4));		//total file size
		fos.write("\0\0\0\0".getBytes());		//unused
		fos.write(intToBytes(offset, 4));		//offset where pixel data starts
		// DIB Header
		fos.write(intToBytes(40, 4));			//DIB Header Size
		fos.write(intToBytes(width, 4));		//image width
		fos.write(intToBytes(height, 4));		//image height
		fos.write(charToBytes((char) 1, 2));	//num planes=1
		fos.write(charToBytes((char) 24, 2));	//bits per pixel
		fos.write(intToBytes(0, 4));			//no compression used
		fos.write(intToBytes(pixelDataSize, 4));//size of pixel data
		fos.write(intToBytes(2835, 4));			//horizontal pixels per meter, not very important
		fos.write(intToBytes(2835, 4));			//vertical pixels per meter, not very important
		fos.write(intToBytes(0, 4));			//number of colors per palette, 0 for all the colors
		fos.write(intToBytes(0, 4));			//number of important colors per palette, 0 because every color is important
	}
	
	private void printHelp() {
		System.out.println("usage: bitmaptext.jar <text-file-path> <bmp-file-path> | [-h]");
		System.out.println("Program takes a specified text file at the provided path and saves a bitmap image with the text file embedded.");
	}
	
	public static void main(String[] args) {
		new BitmapText(args);
	}
	
}
