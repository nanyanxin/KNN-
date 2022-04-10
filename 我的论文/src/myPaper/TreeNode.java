package myPaper;
import java.util.ArrayList;

public class TreeNode {
	int grayId;//灰色顶点,X(v)的v
	ArrayList<Integer> neibList = new ArrayList<>();//X(v)包含的顶点(含v本身), Integer指顶点id
	TreeNode parent;
	ArrayList<TreeNode> child = new ArrayList<>();
	ArrayList<Vertex> ancTable = new ArrayList<>();//祖先表
	ArrayList<Vertex> childTable = new ArrayList<>();//孩子表
	
	public TreeNode(int grayId) {
		this.grayId = grayId;
	}
}
