package org.autotest;

import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class AvlTreeKoratTest {

	@Before
	public void deleteFile() {
		File out = new File("avltreeMax4.ser");
		if (out.exists()) {
			out.delete();
		}
		out = new File("avltreeMax4Add0.ser");
		if (out.exists()) {
			out.delete();
		}
	}

	@Test
	public void testKoratGenerateAVLAlmostHeight4() throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] args = new String[] { "--class", AvlTree.class.getName(), "--args", "1,0,4", "--serialize",
				"avltreeMax4.ser"};
		korat.Korat.main(args);

		assertTrue(new File("avltreeMax4.ser").exists());

		Set<AvlTree> generatedTrees = readAvlTrees("avltreeMax4.ser");
		assertEquals(5, generatedTrees.size());
	}

	@Test
	public void testKorat() throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] args = new String[]{"--class", AvlTree.class.getName(), "--args", "1,0,4", "--serialize",
				"avltreeMax4Add0.ser"};
		korat.Korat.main(args);

		assertTrue(new File("avltreeMax4Add0.ser").exists());

		Set<AvlTree> avlToAdd0 = readAvlTrees("avltreeMax4Add0.ser");
		assertEquals(5, avlToAdd0.size());

		for (AvlTree t : avlToAdd0) {
			Integer originalSize = t.size();
			t.insert(0);
			assertEquals(originalSize + 1, t.size());
		}
	}

	private Set<AvlTree> readAvlTrees(String filename)
			throws IOException, FileNotFoundException, ClassNotFoundException {
		Set<AvlTree> generatedTrees = new HashSet<AvlTree>();
		File out = new File(filename);
		if (!out.exists()) {
			throw new FileNotFoundException();
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(out));
		try {
			while (true) {
				try {
					AvlTree tree = (AvlTree) in.readObject();
					generatedTrees.add(tree);
				} catch (EOFException ex) {
					break;
				}
			}
			return generatedTrees;
		} finally {
			in.close();
		}
	}
}
