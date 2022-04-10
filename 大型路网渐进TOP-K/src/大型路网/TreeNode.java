package 大型路网;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TreeNode {
	public int grayId;//灰色顶点,X(v)的v
	public ArrayList<Integer> neibList = new ArrayList<>();//X(v)包含的顶点(含v本身), Integer指顶点id
	public TreeNode parent;
	public ArrayList<TreeNode> child = new ArrayList<>();
	public ArrayList<Vertex> Au = new ArrayList<>();
	public ArrayList<Vertex> CkNN = new ArrayList<>();
	public int deposit;
	public Set<Integer> Cu = new HashSet<>();//Integer指顶点id
	
	public TreeNode(int grayId) {
		this.grayId = grayId;
	}
}
