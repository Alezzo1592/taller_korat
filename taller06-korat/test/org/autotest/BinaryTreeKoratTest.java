package org.autotest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;


public class BinaryTreeKoratTest {

	@Before
	public void deleteFile() {
		File out = new File("binarytree.ser");
		if (out.exists()) {
			out.delete();
		}
	}

	@Test
	public void runKorat2Node() throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("class: "+BinaryTree.class.getName());
		String[] args = new String[] { "--class", BinaryTree.class.getName(), "--args", "2,2,2", "--serialize",
				"binarytree.ser" };
		korat.Korat.main(args);
		assertTrue(new File("binarytree.ser").exists());

		List<BinaryTree> generatedTrees = readBinaryTrees("binarytree.ser");
		assertEquals(2, generatedTrees.size());
	}

	private List<BinaryTree> readBinaryTrees(String filename)
			throws IOException, FileNotFoundException, ClassNotFoundException {
		List<BinaryTree> generatedTrees = new LinkedList<BinaryTree>();
		File out = new File(filename);
		if (!out.exists()) {
			throw new FileNotFoundException();
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(out));
		try {
			while (true) {
				try {
					BinaryTree binTree0 = (BinaryTree) in.readObject();
					generatedTrees.add(binTree0);
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
