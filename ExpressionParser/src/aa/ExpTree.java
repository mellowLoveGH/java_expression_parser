package aa;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;

public class ExpTree {
	
	public static int numNode = 1;
	public static int idNode = 2;
	public static int opNode = 3;
	public static String let = "let";
	public static String and = " and ";
	public static String equal = "=";
	
	// every tree at least has a root node whose parent is always 'null'
	private Node root;
	
	public ExpTree(){
		root = null;
	}
	
	// part 2 of the assignment
	// with appropriate constructors
	// for number leaf
	public ExpTree(int type, int v, Node l, Node r) {
		root = new Node(type, v, l, r);
		// set the parent of root as 'null'
		root.parent = null;
	}
	
	// part 2 of the assignment
	// with appropriate constructors
	// for id leaf
	public ExpTree(int type, String v, Node l, Node r) {
		root = new Node(type, v, l, r);
		// set the parent of root as 'null'
		root.parent = null;
	}
	
	// part 2 of the assignment
	// with appropriate constructors
	// for operation leaf
	public ExpTree(int type, char v, ExpTree l, ExpTree r) {
		root = new Node(type, v, l.getRoot(), r.getRoot());
		// set the parent of root as 'null'
		root.parent = null;
		// set the left and right trees as sub-trees
		l.getRoot().setParent(root);
		r.getRoot().setParent(root);
	}	
	
	
	// part 6 of the assignment
	// only for definition nodes (let, and, equal)
	public ExpTree(String v, ExpTree l, ExpTree r) {
		root = new Node(v, l.getRoot(), r.getRoot());
		// set the parent of root as 'null'
		root.parent = null;
		// set the left and right trees as sub-trees
		l.getRoot().setParent(root);
		r.getRoot().setParent(root);
	}
	
	
	// part 5 of the assignment
	// In-Order Traversal 
	// because there are two different kinds of in-order
	// one is only for expression which does not include definition
	// while another is for definition
	public String inOrder() {
		if(root == null) return "";
		if(! root.getValue().equals("let")) return inOrder(root);
		
		// it is with definition sub-tree
		String str = "let ";
		
		// get left side (definition part)
		str += inOrder(root.getLchild());
		
		str += " in ";
		
		// get right side (expression part)
		str += inOrder(root.getRchild());
		
		return str;
	}
	
	public String inOrder(Node r) {
		if(r == null) return "";
		
		String str = "";
		
		// check whether the parentheses is needed 
		// if 'r' and its left child of both are operations
		// at the same time, priority of 'r' is higher than that of its left child
		// there should be a pair of parentheses for its left child
		boolean flag = false;
		if(! isLeaf(r)) {
			int node_priority = getPriority(r.getValue().toString());
			if(!isLeaf(r.getLchild())) {
				int lchild_priority = getPriority(r.getLchild().getValue().toString());
				if(lchild_priority < node_priority) {
					str += "(";
					flag = true;
				}
			}
		}
		str += inOrder(r.getLchild());
		
		if(flag) {
			str += ")";
			flag = false;
		}
		str += r.getValue();
		
		// check whether the parentheses is needed 
		// if 'r' and its right child of both are operations
		// at the same time, priority of 'r' is higher than that of its right child
		// there should be a pair of parentheses for its right child
		if(! isLeaf(r)) {
			int node_priority = getPriority(r.getValue().toString());
			if(!isLeaf(r.getRchild())) {
				int rchild_priority = getPriority(r.getRchild().getValue().toString());
				if(rchild_priority <= node_priority) {
					str += "(";
					flag = true;
				}
			}
		}
		str += inOrder(r.getRchild());
		
		if(flag) {
			str += ")";
			flag = false;
		}
		
		return str;
	}
	
	// there are 6 kinds of operation that could be classifies as 3 types
	// (^) type one, priority is 3
	// (% / *) type two, priority is 2
	// (+ -) type three, priority is 1
	// if the calculation of lower priority is before that of higher one,
	// then parenthesis is needed
	// while for definition sub-tree, the internal nodes may be "let, and, ="
	// set their priorities as 0
	public int getPriority(String str) {
		if(str.equals("let") || str.equals("and") || str.equals("equal")) {
			return 0;
		}
		return getPriority(str.charAt(0));
	}
	
	public int getPriority(char ch) {
		// for ^
		if(ch == '^') {
			return 3;
		}
		// for * / %
		else if(ch == '*' || ch == '/' || ch == '%'){
			return 2;
		}
		// for + -
		else if(ch == '+' || ch == '-') {
			return 1;
		}
		// 
		else {
			return 0;
		}
	}
	
	// just return the result from in-order method
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return inOrder();
	}
	
	// part 2 of the assignment
	// allow the production of post-order output
	// here a recursive method is used for convenience and easy to understand
	public String postOrder() {
		if(root == null) return "";
		
		// if it is with definition, just ignore the left sub-tree of the definition tree
		if(root.getValue().equals("let")) return postOrder(root.getRchild());
		
		// only for expression tree
		return postOrder(root);
	}
	
	// part 2 of the assignment
	public String postOrder(Node r) {
		if(r == null) return "";
		
		String str = "";
		
		str += postOrder(r.getLchild());
		str += postOrder(r.getRchild());
		str += r.getValue() + " ";
//		System.out.print(r.getValue() + " ");
		
		return str;
	}
	
	// part 4-6 of the assignment
	// if it is with 'let' tree, it means there is a definition sub-tree
	// or it is only a expression without definition info
	public int evaluation() {
		
		// definition sub-tree
		if(root.getValue().toString().equals("let")) {
//			System.out.println("definition processing");
			// process the definition sub-tree
			Map<String, Integer> map = getMap();
//			System.out.println("definition finished");
//			System.out.println(map.size());
			
			// return the evaluation with definition info
			return evaluation(postOrder(root.getRchild()), 2, map);
		}else {
			
			// no definition info
			return evaluation(postOrder(root), 1, null);
		}
	}
	
	
	// part 4-6 of the assignment
	// calculate the value of the given post-order expression 
	// post_order, the given post-order expression 
	// type, 1 means no definition info used, 2 means definition info should be used
	// map, it is the info after processing definition info
	// if type is 1, set 'map' parameter as 'null'
	public int evaluation(String post_order, int type, Map<String, Integer> map) {
		
		// if it is empty
		if(post_order.equals("")) {
			System.out.println("it is empty");
			return -1;
		}
		
		// because in the post-order expression, 
		// all values are divided by space (' ')
		String[] strs = post_order.split(" ");
		
		// use a stack to finish all the calculations
		Stack<String> stack = new Stack<String>();
		int i = 0;
		do {
			// iterate all the values divided by space
			String v = strs[i];
			
			// if v is operation, meaning there should be one calculation
			if(classify(v) == opNode) {
				
				// pop out last two values, notice the order
				String v2 = stack.pop();
				String v1 = stack.pop();
				
				// then convert them into integer
				int value1 = 0;
				int value2 = 0;
				
				// if type is 1, it means calculate the expression without using definition
				if(type == 1) {
					value1 = getValue(v1);
					value2 = getValue(v2);
				}
				// if type is 2, calculate the expression with values from definition
				else {
					value1 = getValue(v1, map);
					value2 = getValue(v2, map);
				}
				
				// calculation of the two integer with the given operation
				int re = calculation(v, value1, value2); 
				
				// log, to print out the calculation
				// if value2 < 0, add parentheses for the negative value
				if(value2 < 0) {
					System.out.println(value1 + v + "(" + value2 + ")" + "=" + re);
				}else {
					System.out.println(value1 + v + value2 + "=" + re);
				}
				
				
				// push in the newly-got result from the above calculation
				stack.push(re + "");
			}
			// if it is not operation, push into the stack for later calculation
			else {
				stack.push(v);
			}
			
			i++;
		}while(i < strs.length);
		
		// when finishing all the calculation
		// there should only be one value remained in the stack
//		System.out.println("stack size: " + stack.size());
		int v = Integer.parseInt(stack.pop());
		
		return v;
	}
	
	
	// calculation, given an operation with two numbers
	public int calculation(String operation, int v1, int v2) {
		char op = operation.charAt(0);
		
		if(op == '+') {
			return v1 + v2;
		}else if(op == '-') {
			return v1 - v2;
		}else if(op == '*') {
			return v1 * v2;
		}else if(op == '/') {
			return v1 / v2;
		}else if(op == '%') {
			return v1 % v2;
		}else if(op == '^') {
			if(v2 < 0) {
				System.out.println("the power value should not be negative");
				return 0;
			}
			
			int i = 0;
			int v = 1;
			while(i < v2) {
				v = v * v1;
				i++;
			}
			
			return v;
		}
		return 0;
	}
	
	// part 4 of the assignment
	// get the value whatever it is id or number
	public int getValue(String v) {
		int value;
		if(classify(v) == numNode) {
			value = Integer.parseInt(v);
		}else {
			value = idToInt(v);
		}
		
		return value;
	}
	
	// part 6 of the assignment
	// Map<String, Integer> map is generated by traversing through the definition sub-tree
	// then according to given identifier, get the value
	public int getValue(String v, Map<String, Integer> map) {
		int value = 0;
		if(classify(v) == numNode) {
			value = Integer.parseInt(v);
		}else {
			// when it is defined value, iterating through the map
			// if it is not in map, set as 0
			try {
				value = map.get(v);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("there is no such definition of " + v);
				value = 0;
			}
		}
		
		return value;
	}
	
	// part 6 of the assignment
	// traverse through the definition sub-tree
	// to get the identifier and its value
	// here use Map<String, Integer> to store the pairs of identifier-integers
	// 'String' is the identifier, 'Integer' is its given value
	public Map<String, Integer> getMap(){
		Map<String, Integer> map = new TreeMap<>();
		
		// because the definition sub-tree is starts from the left node of the root
		Node def = root.getLchild();
//		System.out.println(def.getValue());
		
		// use a queue to iterate the definition sub-tree with breadth-first (level by level)
		Queue<Node> queue = new LinkedList<>();
		
		// add the root node of the definition sub-tree
		queue.add(def);
		
		// iterate
		while(queue.size() != 0) {
			
			// remove the first node from the queue
			Node tmp = queue.poll();
//			System.out.println(tmp.getValue() + " value processed");
			
			// if the value of this node is '=', it means the left of this node is identifier
			// and the right of this node is expression
			if(tmp.getValue().toString().equals("=")) {
				
				// get the identifier
				String key = tmp.getLchild().getValue().toString();
				
				// the value of the expression
				int value = 0;
				
				// if the expression only consists of 1 node
				// there is no calculation but only a number
				// such as A=5, the right side is only one number without any calculation
				if(isLeaf(tmp.getRchild())) {
					value = Integer.parseInt(tmp.getRchild().getValue().toString());
				}
				// else, get the post-order of the expression
				// then calculate the expression
				else {
					String post_order = postOrder(tmp.getRchild());
					value = evaluation(post_order, 1, null);
				}
				System.out.println(key + ": " + value);
				
				// store the pair of key-value in the map
				map.put(key, value);
			}
			// else, its value is ' and ', continue to iterate
			else {
				Node lchild = tmp.getLchild();
				Node rchild = tmp.getRchild();
				
				if(lchild != null) {
					queue.add(lchild);
				}
				if(rchild != null) {
					queue.add(rchild);
				}
			}
		}
		
		
		return map;
	}
	
	
	// part 4 of the assignment
	// all identifiers beginning with the letter A have the value 25, 
	// those beginning with B have the value 24 and so on
	// (with those beginning with Z having the value 0). 
	public int idToInt(String str) {
		char ch = str.toUpperCase().charAt(0);
		int num = 'Z' - ch;
		return num;
	}
	
	// determine it is id, number or operation
	public int classify(String str) {
		char ch = str.trim().charAt(0);
		
		// starts with a-z or A-Z, it is id
		if(ch >= 'A' && ch <= 'Z') {
			return idNode;
		}else if(ch >= 'a' && ch <= 'a') {
			return idNode;
		}
		// starts with 0-9, it is number
		else if(ch >= '0' && ch <= '9') {
			return numNode;
		}
		// starts with - and the length is bigger than one, it is negative number
		else if(ch == '-' && str.length() > 1) {
			return numNode;
		}
		// else it should be the operation
		else {
			return opNode;
		}
	}
	
	// determine whether the node is a leaf node or not
	// if it is leaf node, it must be identifier or number
	public boolean isLeaf(Node node) {
		if(node.getLchild() == null && node.getRchild() == null) {
			return true;
		}
		return false;
	}
	
	// get the root node when needed from outside
	public Node getRoot() {
		return root;
	}
	
	
	// part 2-6 of the assignment
	// Node class used in tree
	class Node{
		
		// every node must have a parent node except the root node
		private Node parent;
		
		// for leaf, the children nodes are null
		private Node lchild;
		private Node rchild;
		
		// for number leaf, it is 'int'
		// for id leaf, it is 'String'
		// for operation, it is 'char'
		// for definition nodes such as "and, let, =(equal)", it is 'String'
		private Object value;
		
		// 1 for number
		// 2 for id
		// 3 for operation
		// 4 for definition nodes such as "and, let, =(equal)", 
		int type;
		
		public Node(){
			
		}
		
		// number leaf
		public Node(int t, int v, Node l, Node r) {
			type = t;
			value = v;
			lchild = l;
			rchild = r;
		}
		
		// id leaf
		public Node(int t, String v, Node l, Node r) {
			type = t;
			value = v;
			lchild = l;
			rchild = r;
		}
		
		// operation node
		public Node(int t, char v, Node l, Node r) {
			type = t;
			value = v;
			lchild = l;
			rchild = r;
		}
		
		// definition node
		// only three values for v: let, and, =  
		public Node(String v, Node l, Node r) {
			type = 4;
			value = v;
			lchild = l;
			rchild = r;
		}
		
		public Node getParent() {
			return parent;
		}
		
		public void setParent(Node p) {
			parent = p;
		}
		
		public Node getLchild() {
			return lchild;
		}
		
		public void setLchild(Node l) {
			lchild = l;
		}
		
		public Node getRchild() {
			return rchild;
		}
		
		public void setRchild(Node r) {
			rchild = r;
		}
		
		public Object getValue() {
			return value;
		}
		
		public void setValue(Object v) {
			value = v;
		}
		
		public int getType() {
			return type;
		}
	}
	
}
