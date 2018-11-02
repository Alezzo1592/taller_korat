package org.autotest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AvlNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5628892634440140662L;

	public int data;

	public int height;

	public AvlNode left;

	public AvlNode right;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(this.left);
		out.writeObject(this.right);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.left = (AvlNode) in.readObject();
		this.right = (AvlNode) in.readObject();
	}

	public String toStrings() {
		Set<AvlNode> visited = new HashSet<AvlNode>();
		visited.add(this);
		return toString(visited);
	}

	private String toString(Set<AvlNode> visited) {
		StringBuffer buf = new StringBuffer();
		// buf.append(" ");
		// buf.append(System.identityHashCode(this));
		buf.append(" {");
		if (left != null)
			if (visited.add(left))
				buf.append(left.toString(visited));
			else
				buf.append("!tree");

		buf.append("" + this.toStringInfoNode() + "");
		if (right != null)
			if (visited.add(right))
				buf.append(right.toString(visited));
			else
				buf.append("!tree");
		buf.append("} ");
		return buf.toString();
	}

	private String toStringInfoNode() {
		StringBuffer buf = new StringBuffer();
		buf.append(" (");
		buf.append("" + this.data + ",");
		buf.append("" + this.height + ",");
		return buf.toString();
	}

	public AvlNode() {
	}

}
