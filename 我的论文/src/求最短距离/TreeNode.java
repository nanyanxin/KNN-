package ����̾���;

import java.util.ArrayList;

public class TreeNode {
	public int grayId;//��ɫ����,X(v)��v
	public ArrayList<Integer> neibList = new ArrayList<>();//X(v)�����Ķ���(��v����), Integerָ����id
	public TreeNode parent;
	public ArrayList<TreeNode> child = new ArrayList<>();
	public String code;//��α���
	public ArrayList<Integer> anc = new ArrayList<>();
	public ArrayList<Double> dis = new ArrayList<>();
	public ArrayList<Integer> pos = new ArrayList<>();
	public ArrayList<Double> fai = new ArrayList<>();//Ȩ������ fai[i]��neibList�е�i�����㵽Xv��Ȩ��
	//��������飬Vertex����Ϣ��ȫ���������������faiʱʹ��
	public ArrayList<Vertex> neibWeight = new ArrayList<>();//X(v)�����Ķ���(��v����)����Vertex�� w ָ���Ǹö��㵽grayId��Ȩֵ
	
	public TreeNode(int grayId) {
		this.grayId = grayId;
	}
}
