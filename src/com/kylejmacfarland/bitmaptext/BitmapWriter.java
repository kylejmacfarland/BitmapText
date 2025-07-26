package com.kylejmacfarland.bitmaptext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class BitmapWriter {

	public boolean createFile(String inputFile, String outputFile, boolean monochrome, int r, int g, int b) {
		byte[] secretMessage;
		try {
			secretMessage = readFile(inputFile);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.printf("Could not read file \"%s\". Exiting program.\n", inputFile);
			return false;
		}
		int width;
		if (monochrome) {
			width = (int) Math.ceil(Math.sqrt(secretMessage.length + 2));
		} else {
			width = (int) Math.ceil(Math.sqrt(Math.ceil((secretMessage.length + 2) / 3.0)));
		}
		return createFileFromBytes(inputFile, outputFile, width, secretMessage, monochrome, r, g, b);
	}
	
	public boolean createFile(String inputFile, String outputFile, int width, boolean monochrome, int r, int g, int b) {
		byte[] secretMessage;
		try {
			secretMessage = readFile(inputFile);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.printf("Could not read file \"%s\". Exiting program.\n", inputFile);
			return false;
		}
		return createFileFromBytes(inputFile, outputFile, width, secretMessage, monochrome, r, g, b);
	}
	
	private boolean createFileFromBytes(String inputFile, String outputFile, int width, byte[] secretMessage, boolean monochrome, int r, int g, int b) {
		Random rand = new Random();
		int height, pixelDataSize;
		if (monochrome) {
			height = Math.max(secretMessage.length / width, 1);
			pixelDataSize = width;
			if (pixelDataSize % 4 != 0) {
				pixelDataSize += 4 - width % 4;
			}
		} else {
			height = Math.max(secretMessage.length / width / 3, 1);
			pixelDataSize = width * 3;
			if (pixelDataSize % 4 != 0) {
				pixelDataSize += 4 - width * 3 % 4;
			}
		}
		pixelDataSize *= height;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(outputFile);
			writeHeaders(fos, width, height, pixelDataSize, monochrome);
			if (monochrome) {
				writePaletteData(fos, r, g, b);
			}
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
	
	private void writeHeaders(FileOutputStream fos, int width, int height, int pixelDataSize, boolean monochrome) throws IOException {
		int filesize = 14 + 40 + pixelDataSize;	//12-byte header, 40-byte DIB header, pixel data size
		int offset = 14 + 40;					//12-byte header, 40-byte DIB header
		if (monochrome) {
			// Allow for 1024-byte palette declaration.
			filesize += 1024;
			offset += 1024;
		}
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
		//bits per pixel
		if (monochrome) {
			fos.write(charToBytes((char) 8, 2));
		} else {
			fos.write(charToBytes((char) 24, 2));
		}
		fos.write(intToBytes(0, 4));			//no compression used
		fos.write(intToBytes(pixelDataSize, 4));//size of pixel data
		fos.write(intToBytes(2835, 4));			//horizontal pixels per meter, not very important
		fos.write(intToBytes(2835, 4));			//vertical pixels per meter, not very important
		if (monochrome) {
			fos.write(intToBytes(256, 4));		//number of colors per palette, 256 for the whole darn palette
			fos.write(intToBytes(1, 4));		//number of important colors per palette, 1 because there is only one color
		} else {
			fos.write(intToBytes(0, 4));		//number of colors per palette, 0 for all the colors
			fos.write(intToBytes(0, 4));		//number of important colors per palette, 0 because every color is important
		}
	}

	private void writePaletteData(FileOutputStream fos, int r, int g, int b) throws IOException {
		// Paint it black.
		for (int i = 0; i < 256; i++) {
			fos.write((char) b);
			fos.write((char) g);
			fos.write((char) r);
			fos.write(0x00);
		}
	}
	
}
