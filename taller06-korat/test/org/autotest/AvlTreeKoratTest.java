package org.autotest;

import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class AvlTreeKoratTest {

	@Before
	public void deleteFile() {
		File out = new File("avltree.ser");
		if (out.exists()) {
			out.delete();
		}
	}

	@Test
	public void testInsertAvlScope3()throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] args = new String[] { "--class", AvlTree.class.getName(), "--args", "4,0,4", "--serialize",
				"avltree.ser" };
		korat.Korat.main(args);


		assertTrue(new File("avltree.ser").exists());

		Set<AvlTree> generatedTrees = readAvlTrees("avltree.ser");
		assertEquals(9, generatedTrees.size());
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
