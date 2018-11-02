package org.autotest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import korat.finitization.IFinitization;
import korat.finitization.IIntSet;
import korat.finitization.IObjSet;
import korat.finitization.impl.FinitizationFactory;

public class BinaryTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5445704034354604383L;

	/**
	 * 
	 */

	public static class Node implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5703691565130187072L;
		Node left;
		Node right;

		private void writeObject(ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();
			out.writeObject(this.left);
			out.writeObject(this.right);
		}

		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.defaultReadObject();
			this.left = (Node) in.readObject();
			this.right = (Node) in.readObject();
		}

	}

	private Node root;
	private int size;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(this.root);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.root = (Node) in.readObject();
	}

	public boolean repOK() {
		if (root == null)
			return size == 0;
		// checks that tree has no cycle
		Set visited = new HashSet();
		visited.add(root);
		LinkedList workList = new LinkedList();
		workList.add(root);
		while (!workList.isEmpty()) {
			Node current = (Node) workList.removeFirst();
			if (current.left != null) {
				if (!visited.add(current.left))
					return false;
				workList.add(current.left);
			}
			if (current.right != null) {
				if (!visited.add(current.right))
					return false;
				workList.add(current.right);
			}
		}
		// checks that size is consistent
		return (visited.size() == size);
	}

	public static IFinitization finBinaryTree(int nodesNum, int minSize, int maxSize) {
		IFinitization f = FinitizationFactory.create(BinaryTree.class);
		IObjSet nodes = f.createObjSet(Node.class, nodesNum, true);
		f.set("root", nodes);
		f.set("Node.left", nodes);
		f.set("Node.right", nodes);
		IIntSet sizes = f.createIntSet(minSize, maxSize);
		f.set("size", sizes);
		return f;
	}
}
