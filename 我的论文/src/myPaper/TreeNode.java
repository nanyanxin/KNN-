package myPaper;
import java.util.ArrayList;

public class TreeNode {
	int grayId;//��ɫ����,X(v)��v
	ArrayList<Integer> neibList = new ArrayList<>();//X(v)�����Ķ���(��v����), Integerָ����id
	TreeNode parent;
	ArrayList<TreeNode> child = new ArrayList<>();
	ArrayList<Vertex> ancTable = new ArrayList<>();//���ȱ�
	ArrayList<Vertex> childTable = new ArrayList<>();//���ӱ�
	
	public TreeNode(int grayId) {
		this.grayId = grayId;
	}
}
