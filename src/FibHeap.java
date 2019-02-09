/*
 * Author: Anurag Bagalwadi
 * UFID: 4936-9125
 * Email: anuragbagalwadi@ufl.edu
 */



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FibHeap {
	
	List<Node> circularList = new ArrayList<Node>();
	Node first = null;
	Node left = null;
	Node max = null;
	
	
	/* Function to insert nodes into the Fibonacci Heap.
	 * Each node gets inserted into the circular list maintaining the root nodes */
	public void insert(Node newNode) {	
		
		if(circularList.isEmpty()) {
			first = newNode;
			left = first;
			max = first;
		}
		else {
			circularList.get(circularList.size()-1).right = newNode;	
			first.left = newNode;
			newNode.left = left;
			newNode.right = first;
			left = newNode;
			
			if(max.value<newNode.value) {
				max = newNode;
			}
		}
		circularList.add(newNode);
		
	}
	
	/* Function gets called every time the frequency value of an existing node in the Fibonacci Heap is increased.
	 * If the new value is greater than its parent node, we re-insert the node into the data structure.
	 * Cascading cut is performed on the basis of it's parent's ChildCut value.
	 */
	public void increaseKey(Node node,int val) {	
		
		node.setValue(val);
		if(val>max.value) {
			max = node;
		}
		
		if(node.parent!=null && node.parent.value<val) {
			performCascadingCut(node);
		}
		
		
	}
	
	/*
	 * Function implements cascading cut until it's parent node's ChildCut value is False.
	 * If parent's ChildCut value is False, we change it to True and then exit.
	 */
	private void performCascadingCut(Node node) {
		
		if(node.parent!=null) {
			Node parentNode = node.parent;
			parentNode.removeChild(node);
			parentNode.degree = parentNode.degree - 1;
			boolean childCutVal = parentNode.childCut;
			node.setParent(null);
			this.insert(node);
			
			if (childCutVal) {			
				performCascadingCut(parentNode);			
			}
			else {
				parentNode.setChildCut(true);				
				
			}
		}
		
	}

	/* 
	 * This function performs pairwise combine after a removeMax call occurs.
	 * Two heaps in the circular list are combined if they have the same degree, iteratively.
	 */
	private void meld() {
		
		HashMap<Integer,Node> degreeNodeMap = new HashMap<>();
		
		Node removeNode = null;
		boolean reIterate = false;
		List<Node> removeNodeList = new ArrayList<>();
		do {
			
			for(Node eachNode: circularList) {
				if(degreeNodeMap.get(eachNode.degree) == null) {
					degreeNodeMap.put(eachNode.degree, eachNode);
				}
				else {
					if(degreeNodeMap.get(eachNode.degree).value < eachNode.value) {
						eachNode.addChild(degreeNodeMap.get(eachNode.degree));
						degreeNodeMap.get(eachNode.degree).setParent(eachNode);
						removeNode = degreeNodeMap.get(eachNode.degree);
						degreeNodeMap.remove(eachNode.degree);
						eachNode.degree = eachNode.degree + 1;
					}
					else {
						degreeNodeMap.get(eachNode.degree).addChild(eachNode);
						eachNode.setParent(degreeNodeMap.get(eachNode.degree));
						removeNode = eachNode;
						int degreeToRemove = degreeNodeMap.get(eachNode.degree).degree;
						degreeNodeMap.get(eachNode.degree).degree = degreeNodeMap.get(eachNode.degree).degree + 1;
						degreeNodeMap.remove(degreeToRemove);
						
					}
					removeNodeList.add(removeNode);
				}
			}
			circularList.removeAll(removeNodeList);
			
			
			if(degreeNodeMap.size() == circularList.size()) {
				reIterate = false;
			}
			else {
				reIterate = true;
			}
			
			degreeNodeMap = new HashMap<>();
			
			
		}
		while(reIterate);
		
		
	}
	
	/* 
	 * Extracts maximum node from the Fibonacci Heap.
	 */
	public Node removeMax() {
		
		Node curMax = max;
		circularList.remove(curMax);
				
		if(curMax.children!=null && curMax.children.size()>0) {
			for(Node eachChild: curMax.children) {
				eachChild.setParent(null);
				circularList.add(eachChild);
			}			
		}		
		
		meld();
		max = findMax();
		
		return curMax;
	}
	
	/*
	 * Finds new max node after each removeMax operation
	 */
	private Node findMax() {
		
		Node maxNode = null;
		int maxVal = 0;
		for(Node each: circularList) {
			
			if(each.value>maxVal) {
				maxNode = each;
				maxVal = each.value;
			}
			
		}
		return maxNode;
	}	
	
	
}


class Node {
	
	Node parent;
	Node left;
	Node right;
	ArrayList<Node> children;
	String key;
	int value;
	int degree;
	boolean isRoot = false;
	boolean childCut;
	
	public Node(String k, int val) {
		parent = null;
		left = null;
		right = null;
		children = new ArrayList<>();
		degree = 0;
		value = val;
		key = k;
		isRoot = true;
		childCut = false;
	}
	
	/* Sets parent node */
	public void setParent(Node p) {
		this.parent = p;
	}
	
	/* Adds Child node c to current node instance */
	public void addChild(Node c) {
		c.childCut = false;
		this.children.add(c);
	}
	
	/* Removes Child node c from current node instance */
	public void removeChild(Node c) {
		this.children.remove(c);
	}
	
	/* Sets left sibling node */
	public void setLeft(Node l) {
		this.left = l;
	}
	
	/* Sets right sibling node */
	public void setRight(Node r) {
		this.right = r;
	}
	
	/* Sets frequency value of node */
	public void setValue(int v) {
		this.value = v;
	}
	
	/* Sets ChildCut value of current node instance */
	public void setChildCut(boolean cCut) {
		this.childCut = cCut;
	}
	
	
}

class keywordcounter {
	public static void main(String args[]) {

		HashMap<String,Node> wordListMap = new HashMap<>();
		FibHeap f = new FibHeap();
		
		try {
			
			File file = new File("output_file.txt");
			file.createNewFile();
			@SuppressWarnings("resource")
			FileWriter wr = new FileWriter(file);
			String pathToFile = args[0];
			FileReader fr = new FileReader(pathToFile);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr); 
			String newLine;
			while((newLine = br.readLine().trim())!=null) {
				if(newLine.charAt(0) == '$') {
					newLine = newLine.substring(1, newLine.length());
					
					String[] pair = newLine.split(" ");
					if(wordListMap.containsKey(pair[0])) {
						
						f.increaseKey(wordListMap.get(pair[0]), Integer.parseInt(pair[1])+wordListMap.get(pair[0]).value);
						
					} else {

						Node newNode = new Node(pair[0],Integer.parseInt(pair[1])); 	
						f.insert(newNode);
						wordListMap.put(pair[0],newNode);

						
					}
				}
				else if(newLine.equalsIgnoreCase("stop")) {
					wr.close();
					System.exit(1);
				}
				else {
					int queryCount = Integer.parseInt(newLine);
					List<Node> removedNodes = new ArrayList<Node>();
					
					while(queryCount>0) {
						Node max = f.removeMax();
						
						if(queryCount!=1) {
							wr.write(max.key+",");
						}
						else {
							wr.write(max.key);							
						}
						wr.flush();

						removedNodes.add(max);
						queryCount--;
					}
					wr.write("\n");

					for(Node each : removedNodes) {
						each.degree = 0;
						each.children = new ArrayList<>();
						f.insert(each);
					}
					
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
