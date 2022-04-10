package 求最短距离;
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
	public Set<Vertex>[] real_g;//需要用到路网数据
	public int N;
	public Map<Integer, TreeNode> node_map;
	public Map<String, TreeNode> encode_map;
	
	//new 这个类时，会自动调用构造方法
	public H2H_Index() throws IOException {
		generate_H2H_Index();
	}
	
	public TreeNode generate_H2H_Index() throws IOException {
		N = GetVertexNumber.get(FilePath.path);
		real_g = RoadProcess.generate_g();
		TreeDecomposition_When tree = new TreeDecomposition_When();
	    root = tree.root;//对该root的修改不会影响TEN_Index中的root
	    node_map = tree.node_map;
	    encode_map = tree.encode_map;
		LinkedList<TreeNode> q = new LinkedList<>();
		//计算每个节点的anc数组
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//队首元素
			TreeNode p = Xv;//p是指针，一开始指向Xv
			ArrayList<Integer> anc = new ArrayList<>();
			while (p.parent != null) {
				anc.add(p.grayId);//按照往上走的顺序加入
				p = p.parent;
			}
			anc.add(p.grayId);//加上根
			//再反转
			Collections.reverse(anc);
			Xv.anc = anc;
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//将每个节点中的顶点按照消点序降序排序，并求fai
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//队首元素
			ArrayList<Integer> neibList = Xv.neibList;//顶点id
			ArrayList<Vertex> list = new ArrayList<>();
			for (Integer eachV : neibList) {
				Vertex v = v_map.get(eachV);
				list.add(v);
			}
			Collections.sort(list, new Comparator<Vertex>() {
				@Override
				public int compare(Vertex o1, Vertex o2) {
					return o2.deleteOrder - o1.deleteOrder;//消点序降序
				}
			});
			neibList.clear();
			for (Vertex v : list) {
				neibList.add(v.id);
			}//消点序降序完成
			//求每个节点的fai数组
			ArrayList<Double> fai = Xv.fai;
			for (int eachV : neibList) {
				for (Vertex v : Xv.neibWeight) {//根据顶点id去neibWeight中找该Vertex
					if (v.id == eachV) {
						fai.add(v.w);
					}
				}
			}//求完fai了，测试正确
			
			//孩子节点入队
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//以上是处理完了DP树分解的部分，下面是实现论文中的算法5部分
		//求每个节点的pos数组和dis数组	
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//队首元素			
			ArrayList<Integer> neibList = Xv.neibList;
			ArrayList<Integer> anc = Xv.anc;
			//求pos数组
			for (int i = 0; i < neibList.size(); i++) {
				int Xi = neibList.get(i);//获取第i个顶点
				//去anc数组找对应的位置
				for (int j = 0; j < anc.size(); j++) {
					if (Xi == anc.get(j)) {
						Xv.pos.add(j);
					}
				}
			}
			
			//求dis数组
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
			//孩子节点入队
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
		//求LCA
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
		for (int i : LCA.pos) {//这里的i是pos数组中的元素
			d = Math.min(d, Xv1.dis.get(i) + Xv2.dis.get(i));
		}
		return d;
	}
}
