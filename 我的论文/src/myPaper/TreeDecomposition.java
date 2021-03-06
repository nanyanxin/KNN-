package myPaper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class TreeDecomposition {
	public TreeNode root;
	public Set<Vertex>[] g;
	public Map<Integer, Vertex> v_map;//<顶点id,顶点v> 它的keySet就是路网的顶点集V
	public Map<Integer, TreeNode> node_map = new HashMap<>();//存储生成的树节点, <灰色顶点id, 树节点>
	public int deleteOrderNum = 1;//消点序的值
	//按照 1.平均度 2.删掉该顶点后需要添加的边数  3.顶点id  排序的队列，自定义了VertexComparator，排序的时候传入
	public ArrayList<Vertex> q = new ArrayList<Vertex>();
	public VertexComparator vc = new VertexComparator();//比较器
	int w;//树宽
	int h;//树高
	
	public TreeDecomposition() throws IOException {
		generate_tree();
	}
	
	public TreeNode generate_tree() throws IOException {
		g = RoadProcess.generate_g();
		v_map = RoadProcess.v_map;//<顶点id,顶点v> 的map
		Set<Integer> v_id_set = v_map.keySet();
		//计算每个顶点的权重/度
		for (int id : v_id_set) {
			updateAvgDegree(id);	
		}
		//求每个顶点删掉之后需要加的边数：n*(n-1)/2 - 已有邻居对之间的边数，n是邻居数
		for (int id : v_id_set) {
			updateNeedAddEdges(id);	
		}
		//求完上述两者之后，全都加入q中
		for (int id : v_id_set) {
			q.add(v_map.get(id));
		}
		//排序
		Collections.sort(q, vc);//传入比较器vc
		//顶点约简过程
		while (!q.isEmpty()) {
			Vertex v = q.remove(0);//开始处理, v是灰色顶点
			//1.生成树节点
			TreeNode node = new TreeNode(v.id);
			Set<Vertex> v_neibs = g[v.id];
			   //添加X(v)的顶点
			node.neibList.add(v.id);//先添加灰色顶点
			   //再添加邻居
			for (Vertex neib : v_neibs) {
				node.neibList.add(neib.id);
			}
			//生成节点后，更新树宽
			w = Math.max(w, node.neibList.size() - 1);
			node_map.put(v.id, node);//将该树节点存起来
			//2.让所有邻居对之间连边，注意更新权重
			//将所有邻居存到数组里
			ArrayList<Vertex> list = new ArrayList<>();
			for (Vertex vv : v_neibs) {
				list.add(vv);
			}
			//双指针遍历所有邻居对
			for (int i = 0; i < list.size(); i++) {
				for (int j = i + 1; j < list.size(); j++) {
					Vertex s = list.get(i);//start顶点
					Vertex e = list.get(j);//end顶点
					if (!g[s.id].contains(e)) {//(s, e)没有边的时候加上新边
						double new_w = 0;
						for (Vertex v1 : g[s.id]) {//s--->v的权重
							if (v1.id == v.id) {
								new_w += v1.w;
							}
						}
						for (Vertex v2 : g[v.id]) {//v--->e的权重
							if (v2.id == e.id) {
								new_w += v2.w;
							}
						}
						g[s.id].add(new Vertex(e.id, new_w));
						g[e.id].add(new Vertex(s.id, new_w));
					} else {//若是有边，则更新权重
						double new_w = 0.0;
						for (Vertex eachneib : g[v.id]) {
							if (eachneib.id == e.id) {
								new_w += eachneib.w;//(v, e)的w
							}
							if (eachneib.id == s.id) {
								new_w += eachneib.w;//(v, s)的w
							}//new_w 是 s--v--e的权重
						}
						for (Vertex eachneib : g[s.id]) {//更新s-->e的权重
							if (eachneib.id == e.id) {
								eachneib.w = Math.min(eachneib.w, new_w);
							}
						}
						for (Vertex eachneib : g[e.id]) {//更新e-->s的权重
							if (eachneib.id == s.id) {
								eachneib.w = Math.min(eachneib.w, new_w);
							}
						}
					}
				}
			}
			//3.从图(邻接表)中删掉v
			Set<Vertex> v_neib = g[v.id];
			for (Vertex eachv : v_neib) {
				g[eachv.id].remove(v);
			}
			g[v.id] = null;
			//4.更新邻居的权重/度，需要添加的边数，将v加入消点序队列
			for (Vertex eachv : v_neib) {
				updateAvgDegree(eachv.id);
			}
			for (Vertex eachv : v_neib) {
				updateNeedAddEdges(eachv.id);
			}
			//重新排序
			Collections.sort(q, vc);
			//记录消点序
			v.deleteOrder = deleteOrderNum++;
		}
		
		//对树节点进行连接
		for (int id : v_id_set) {//v_id_set就是顶点集V
			int minOrder = Integer.MAX_VALUE;
			TreeNode u = null;//Xv的父节点
			TreeNode Xv = node_map.get(id);
			if (Xv.neibList.size() == 1) root = Xv;//指定树根
			if (Xv.neibList.size() > 1) {
				ArrayList<Integer> neibList = Xv.neibList;
				for (int neib : neibList) {
					if (neib == id) continue;
					int curOrder = v_map.get(neib).deleteOrder;//该邻居的消点序
					if (curOrder < minOrder) {
						minOrder = curOrder;
						u = node_map.get(neib);
					}
				}
				Xv.parent = u;//指向u
				u.child.add(Xv);
			}
		}
		h = get_h(root);
		return root;
	}
	
	private void updateNeedAddEdges(int id) {
		int n = g[id].size();//邻居数，也就是度
		int sum = n * (n - 1) / 2;//邻居对的总边数
		//求邻居对现有的边数
		int cur = 0;//现有边数
		Vertex v = v_map.get(id);
		Set<Vertex> v_neibs = g[id];//v的邻居们
		//存到数组里
		ArrayList<Vertex> list = new ArrayList<>();
		for (Vertex vv : v_neibs) {
			list.add(vv);
		}
		//双指针查找现存的邻居对的边数
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				Vertex s = list.get(i);//start顶点
				Vertex e = list.get(j);//end顶点
				if (g[s.id].contains(e)) {
					cur++;
				}
			}
		}
		v.needAddEdgeNum = sum - cur;
	}
	//更新 权重/度
	private void updateAvgDegree(int id) {
		double sum = 0.0;//该顶点邻居们的权重和
		for (Vertex v_neib : g[id]) {
			sum += v_neib.w;
		}
		int neibNum = g[id].size();//邻居数，也就是度
		Vertex v = v_map.get(id);
		v.avg_degree = sum / neibNum;
	}
	
	//求树高
	public int get_h(TreeNode root) {
		int h = 0;
		LinkedList<ArrayList<TreeNode>> list = new LinkedList<>();
		ArrayList<TreeNode> l = new ArrayList<TreeNode>();//第一层的节点
		l.add(root);//只有root
		list.add(l);
		while (!list.isEmpty()) {
			ArrayList<TreeNode> curLevel = list.poll();
			h++;
			ArrayList<TreeNode> nextLevel = new ArrayList<TreeNode>();
			for (TreeNode c : curLevel) {				
				if (c.child.size() != 0) nextLevel.addAll(c.child);
			}
			if (nextLevel.size() != 0) {//下一层有节点时才入队
				list.add(nextLevel);
			}
		}
		return h;
	}
	
	public static void main(String[] args) throws IOException {
		TreeDecomposition tree = new TreeDecomposition();
		System.out.println("高:" + tree.h);
		System.out.println("宽:" + tree.w);
	}
}
