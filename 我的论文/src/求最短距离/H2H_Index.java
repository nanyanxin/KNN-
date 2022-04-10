package ����̾���;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class H2H_Index {
	public Map<Integer, Vertex> v_map = RoadProcess.v_map;//<id, Vertex>
	public TreeNode root;
	public Set<Vertex>[] real_g;//��Ҫ�õ�·������
	public int N;
	public Map<Integer, TreeNode> node_map;
	public Map<String, TreeNode> encode_map;
	
	//new �����ʱ�����Զ����ù��췽��
	public H2H_Index() throws IOException {
		generate_H2H_Index();
	}
	
	public TreeNode generate_H2H_Index() throws IOException {
		N = GetVertexNumber.get(FilePath.path);
		real_g = RoadProcess.generate_g();
		TreeDecomposition_When tree = new TreeDecomposition_When();
	    root = tree.root;//�Ը�root���޸Ĳ���Ӱ��TEN_Index�е�root
	    node_map = tree.node_map;
	    encode_map = tree.encode_map;
		LinkedList<TreeNode> q = new LinkedList<>();
		//����ÿ���ڵ��anc����
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//����Ԫ��
			TreeNode p = Xv;//p��ָ�룬һ��ʼָ��Xv
			ArrayList<Integer> anc = new ArrayList<>();
			while (p.parent != null) {
				anc.add(p.grayId);//���������ߵ�˳�����
				p = p.parent;
			}
			anc.add(p.grayId);//���ϸ�
			//�ٷ�ת
			Collections.reverse(anc);
			Xv.anc = anc;
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//��ÿ���ڵ��еĶ��㰴�������������򣬲���fai
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//����Ԫ��
			ArrayList<Integer> neibList = Xv.neibList;//����id
			ArrayList<Vertex> list = new ArrayList<>();
			for (Integer eachV : neibList) {
				Vertex v = v_map.get(eachV);
				list.add(v);
			}
			Collections.sort(list, new Comparator<Vertex>() {
				@Override
				public int compare(Vertex o1, Vertex o2) {
					return o2.deleteOrder - o1.deleteOrder;//��������
				}
			});
			neibList.clear();
			for (Vertex v : list) {
				neibList.add(v.id);
			}//�����������
			//��ÿ���ڵ��fai����
			ArrayList<Double> fai = Xv.fai;
			for (int eachV : neibList) {
				for (Vertex v : Xv.neibWeight) {//���ݶ���idȥneibWeight���Ҹ�Vertex
					if (v.id == eachV) {
						fai.add(v.w);
					}
				}
			}//����fai�ˣ�������ȷ
			
			//���ӽڵ����
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//�����Ǵ�������DP���ֽ�Ĳ��֣�������ʵ�������е��㷨5����
		//��ÿ���ڵ��pos�����dis����	
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//����Ԫ��			
			ArrayList<Integer> neibList = Xv.neibList;
			ArrayList<Integer> anc = Xv.anc;
			//��pos����
			for (int i = 0; i < neibList.size(); i++) {
				int Xi = neibList.get(i);//��ȡ��i������
				//ȥanc�����Ҷ�Ӧ��λ��
				for (int j = 0; j < anc.size(); j++) {
					if (Xi == anc.get(j)) {
						Xv.pos.add(j);
					}
				}
			}
			
			//��dis����
			for (int i = 0; i < anc.size() - 1; i++) {
				double d = 0.0;
				double res = Integer.MAX_VALUE;
				for (int j = 0; j < neibList.size() - 1; j++) {
					int Xj = neibList.get(j);
					d = 0;
					if (Xv.pos.get(j) > i) {
						d = node_map.get(Xj).dis.get(i);
					} else {
						d = node_map.get(Xv.anc.get(i)).dis.get(Xv.pos.get(j));
					}
					res = Math.min(res, Xv.fai.get(j) + d);
				}
				
				Xv.dis.add(res);
			}
			Xv.dis.add(0.0);
			//���ӽڵ����
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		return root;
	}
	
	public  double H2H_Query(int v1, int v2) {
		TreeNode Xv1 = node_map.get(v1);
		TreeNode Xv2 = node_map.get(v2);
		//��LCA
		String code1 = Xv1.code;
		String code2 = Xv2.code;
		int len = Math.min(code1.length(), code2.length());
		StringBuilder common_prefix = new StringBuilder();
		for (int i = 0; i < len; i++) {
			if (code1.charAt(i) == code2.charAt(i)) {
				common_prefix.append(code1.charAt(i));
			} else {
				break;
			}
		}
		String cp = common_prefix.toString();
		TreeNode LCA = encode_map.get(cp);
		double d = Integer.MAX_VALUE;
		for (int i : LCA.pos) {//�����i��pos�����е�Ԫ��
			d = Math.min(d, Xv1.dis.get(i) + Xv2.dis.get(i));
		}
		return d;
	}
}
