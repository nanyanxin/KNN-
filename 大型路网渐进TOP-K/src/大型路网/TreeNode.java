package ����·��;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TreeNode {
	public int grayId;//��ɫ����,X(v)��v
	public ArrayList<Integer> neibList = new ArrayList<>();//X(v)�����Ķ���(��v����), Integerָ����id
	public TreeNode parent;
	public ArrayList<TreeNode> child = new ArrayList<>();
	public ArrayList<Vertex> Au = new ArrayList<>();
	public ArrayList<Vertex> CkNN = new ArrayList<>();
	public int deposit;
	public Set<Integer> Cu = new HashSet<>();//Integerָ����id
	
	public TreeNode(int grayId) {
		this.grayId = grayId;
	}
}
