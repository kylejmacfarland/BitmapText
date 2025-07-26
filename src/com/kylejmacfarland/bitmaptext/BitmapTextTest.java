package com.kylejmacfarland.bitmaptext;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BitmapTextTest {

	@Test
	void testRun() {
		//bitmaptext.jar [-m] <text-file-path> [<bmp-file-path>] [<width>] | [-h]
		BitmapText bt = new BitmapText(new String[]{});
		//assertFalse(bt.run(null));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{"-m"})); //fix this one!!!
		assertTrue(bt.run(new String[]{"-h"}));
		assertTrue(bt.run(new String[]{"-h", "5555"}));
		//-m versions of all of these!!!
		assertTrue(bt.run(new String[]{"alice.txt"}));
		assertFalse(bt.run(new String[]{"no_file.txt"}));
		assertTrue(bt.run(new String[]{"alice.txt", "no_extention"}));
		assertTrue(bt.run(new String[]{"alice.txt", "yes_extention.bmp"}));
		assertTrue(bt.run(new String[]{"alice.txt", "wrong_extention.png"}));
		assertFalse(bt.run(new String[]{""}));
		assertFalse(bt.run(new String[]{"alice.txt", "illegal_filename$$$$.bmp"}));
		assertFalse(bt.run(new String[]{"no_file.txt", "no_file_no_ext"}));
		assertFalse(bt.run(new String[]{"no_file.txt", "no_file_yes_ext.bmp"}));
		assertTrue(bt.run(new String[]{"", "empty_file_with_args_no_ext"}));
		assertTrue(bt.run(new String[]{"", "empty_file_yes_ext.bmp"}));
		assertTrue(bt.run(new String[]{"alice.txt", "weird_filename......"}));
		//as above but with width stuff
		/*assertTrue(bt.run(new String[]{"alice.txt", ""}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));
		assertTrue(bt.run(new String[]{}));*/
	}

}
