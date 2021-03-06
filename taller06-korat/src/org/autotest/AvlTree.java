package org.autotest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import korat.finitization.IFinitization;
import korat.finitization.IIntSet;
import korat.finitization.IObjSet;
import korat.finitization.impl.FinitizationFactory;

public class AvlTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1804587522402026743L;

	private AvlNode root;

	private int size;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(this.root);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.root = (AvlNode) in.readObject();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(size);
		buf.append("{");
		if (root != null)
			buf.append(root.toStrings());
		buf.append("}");
		return buf.toString();
	}

	boolean contains(Integer value) {
		AvlNode current = root;
		while (current != null && current.data != value) {
			// if (value.data_lt(current.data))
			if (value.compareTo(current.data) < 0)
				current = current.left;
			else
				current = current.right;
		}
		return current == null;
	}

	/**
	 * @Modifies_Everything
	 * 
	 * @Requires value != null && freshNode !in this.nodes && freshNode != null &&
	 *           freshNode.left = null && freshNode.right = null;
	 *
	 * @Ensures ((some n: AvlNode | n in @old(this.nodes) && n.data = value && ( (
	 *          this.nodes = @old(this.nodes) ) )) && ((no n: AvlNode | n
	 *          in @old(this.nodes) && n.data = value) => ( ( this.nodes =
	 *          ( @old(this.nodes) @+ freshNode ) ) && ( freshNode.data = value )
	 *          ));
	 */
	public void insert(Integer value) {
		AvlNode freshNode = new AvlNode();
		try {
			freshNode.data = value;
			root = insert(root, freshNode);
			// size++;
		} catch (RuntimeException e) {
		}
	}

	private AvlNode insert(AvlNode node, AvlNode freshNode) {
		if (node == null) {
			node = freshNode;
		} else if (freshNode.data < node.data)
			node.left = insert(node.left, freshNode);
		else if (freshNode.data > node.data)
			node.right = insert(node.right, freshNode);
		else
			throw new RuntimeException();
		return restoreBalance(node);
	}

	void delete(Integer value) {
		AvlNode[] nodes = { null, null };
		root = delete(root, value, nodes);
	}

	private AvlNode delete(AvlNode node, Integer value, AvlNode[] nodes) {
		if (node == null)
			return null;
		nodes[1] = node;
		if (value.compareTo(node.data) < 0) {
			node.left = delete(node.left, value, nodes);
		} else {
			nodes[0] = node;
			node.right = delete(node.right, value, nodes);
		}
		if (node == nodes[1]) {
			if (nodes[0] != null && nodes[0].data == value) {
				if (nodes[0] == nodes[1]) {
					node = node.left;
				} else {
					nodes[0].data = node.data;
					node = node.right;
				}
				size--;
			}
		} else {
			node = restoreBalance(node);
		}
		return node;
	}

	int size() {
		if (!repOK())
			return -1;

		return size;
	}

	private AvlNode restoreBalance(AvlNode node) {
		int l_Height = node.left == null ? 0 : node.left.height;
		int r_Height = node.right == null ? 0 : node.right.height;

		if (l_Height > r_Height) {
			int ll_Height = node.left.left == null ? 0 : node.left.left.height;
			int lr_Height = node.left.right == null ? 0 : node.left.right.height;
			if (ll_Height < lr_Height)
				node.left = rotateLeft(node.left);
			node = rotateRight(node);

		} else if (l_Height < r_Height) {
			int rl_Height = node.right.left == null ? 0 : node.right.left.height;
			int rr_Height = node.right.right == null ? 0 : node.right.right.height;
			if (rl_Height > rr_Height)
				node.right = rotateRight(node.right);
			node = rotateLeft(node);
		}

		// fixHeights(node);
		return node;
	}

	private AvlNode rotateLeft(AvlNode node) {
		AvlNode r_node = node.right;
		AvlNode rl_node = r_node.left;
		r_node.left = node;
		node.right = rl_node;
		fixHeights(node);
		fixHeights(r_node);
		return r_node;
	}

	private AvlNode rotateRight(AvlNode rt) {
		AvlNode l_node = rt.left;
		AvlNode lr_node = l_node.right;
		l_node.right = rt;
		rt.left = lr_node;
		fixHeights(l_node);
		fixHeights(rt);
		return l_node;
	}

	private void fixHeights(AvlNode node) {
		int leftHeight = node.left == null ? 0 : node.left.height;
		int rightHeight = node.right == null ? 0 : node.right.height;
		// node.height = 1 + (leftHeight > rightHeight ? leftHeight : rightHeight);
		node.height = (leftHeight > rightHeight ? leftHeight : rightHeight);
	}

	/**
	 * invariante de representación de la estructura.
	 * 
	 * @return true si y sólo si la estructura es internamente consistente @pre.
	 *         true @post. Se retorna true si y sólo si el árboles es un AVL, es
	 *         decir es un árbol ordenado (árbol binario de búsqueda) y balanceado,
	 *         y la altura de cada nodo alcanzable desde la raíz corresponde a la
	 *         diferencia de altura de sus subárboles izquierdo y derecho. Además,
	 *         size coincide con el número de elementos del árbol.
	 */
	public boolean repOK() {
		Queue nodesQueue = new LinkedList();
		int totalOfNodes = 0;
		if(root == null ||!isBalancedTree(root,null,null) || avlContainsCicles(root))
			return false;
		boolean isValid = true;
		nodesQueue.add(root);
		while(!nodesQueue.isEmpty() && isValid){
			AvlNode actualNode = (AvlNode) nodesQueue.poll();
			if(actualNode != null) {
				totalOfNodes ++;
				isValid &= balancedAvl(actualNode);
				nodesQueue.add(actualNode.left);
				nodesQueue.add(actualNode.right);
			}
		}

		return isValid && (totalOfNodes == this.size);
	}

	public boolean balancedAvl(AvlNode actualNode){
		Integer leftHeight = (actualNode.left== null)?0:actualNode.left.height;
		Integer rightHeight = (actualNode.right== null)?0:actualNode.right.height;
		return  Math.abs(leftHeight-rightHeight) <= 1 && (Math.max(leftHeight,rightHeight) == actualNode.height-1);
	}

	private boolean avlContainsCicles(AvlNode root) {
		return root.toStrings().contains("!tree");
	}
	private boolean isBalancedTree(AvlNode actualNode,Integer min, Integer max){
		if(actualNode == null || (actualNode.left == null && actualNode.right == null))
			return true;
		if(min == null && max == null){
			return isBalancedTree(actualNode.left,null,actualNode.data) && isBalancedTree(actualNode.right,actualNode.data,null);
		}
		else{
			if(min != null && max != null){
				return (min <= actualNode.data && actualNode.data < max) &&  isBalancedTree(actualNode.left,min,actualNode.data) && isBalancedTree(actualNode.right,actualNode.data,max);
			}else if(min == null){
				return (min <= actualNode.data) &&  isBalancedTree(actualNode.left,min,actualNode.data) && isBalancedTree(actualNode.right,actualNode.data,max);

			}else{
				return (actualNode.data < max) &&  isBalancedTree(actualNode.left,min,actualNode.data) && isBalancedTree(actualNode.right,actualNode.data,max);
			}
		}
	}

	public static IFinitization finAvlTree(int avlHeight, int minSize, int maxSize) {
		IFinitization f = FinitizationFactory.create(AvlTree.class);
		IIntSet AvlSize = f.createIntSet(minSize, maxSize);

		Double nodesNum = Math.pow(2, avlHeight);
		IObjSet nodes = f.createObjSet(AvlNode.class, nodesNum.intValue(), true);

		f.set("root", nodes);

		f.set("AvlNode.left",   nodes);
		f.set("AvlNode.right",  nodes);

		f.set("size", AvlSize);
		f.set("AvlNode.data",   AvlSize);
		f.set("AvlNode.height", AvlSize);

		return f;

	}

}
