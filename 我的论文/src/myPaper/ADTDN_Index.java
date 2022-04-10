package myPaper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.carrotsearch.sizeof.RamUsageEstimator;

import 求最短距离.H2H_Index;

public class ADTDN_Index {
	public Set<Integer> M;//候选集
	public H2H_Index h2hIndex;
	public TreeNode root;
	public Map<Integer, TreeNode> node_map;
	public Map<Integer, Vertex> v_map;//<顶点id,顶点v> 它的keySet就是路网的顶点集V
	public Map<Integer, Double> v_curDist_map;//<顶点id, 到查询点的当前最短距离>
	public double threshold = Integer.MAX_VALUE;
	public V_Cur_Dist_Comparator comparator = new V_Cur_Dist_Comparator();//根据 cur_dist排序的比较器
//	public Set<Integer> SP = new HashSet<>();//搜索空间
//	public int avg_sp = 0;
	
	public ADTDN_Index() throws IOException {
	//	long start1 = System.currentTimeMillis();
		generate_ADTDN_Index();
	//	long end1 = System.currentTimeMillis();
	//	System.out.println("索引构建耗时:" + (end1 - start1) + "ms");
	}
	
	public TreeNode  generate_ADTDN_Index() throws IOException{
		M = Candidates.get_Candidates();
		h2hIndex = new H2H_Index(); 
		TreeDecomposition TD = new TreeDecomposition();
		root = TD.root;
		node_map = TD.node_map;
		v_map = TD.v_map;
		v_curDist_map = new HashMap<>();
		//求每个节点的ancTable
		LinkedList<TreeNode> q = new LinkedList<>();
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//队首元素
			//全都初始为-1.0，表示没计算过
			v_curDist_map.put(Xv.grayId, -1.0);
			TreeNode p = Xv;//p是指针，一开始指向Xv
			//如果p不是根节点
			if (p.parent != null) {	//如果=null，说明p是根节点，根节点的祖先表不用处理
				p = p.parent;//跳过Xv自身
				while (p != null) {
					Vertex v = new Vertex(p.grayId, 0);
					double d = h2hIndex.H2H_Query(Xv.grayId, p.grayId);
					v.w = d;
					Xv.ancTable.add(v);
					p = p.parent;
				}
				//将祖先表排序
				Collections.sort(Xv.ancTable, new Comparator<Vertex>() {
					@Override
					public int compare(Vertex o1, Vertex o2) {
						if (o1.w - o2.w < 0) {
							return -1;
						} else if (o1.w - o2.w > 0) {
							return 1;
						} else {
							return 0;
						}
					}
				});
			}
			//孩子节点入队
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//求每个节点的childTable
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//队首元素
			//如果p不是叶节点
			if (!Xv.child.isEmpty()) {	//如果为空，说明Xv是叶节点，叶节点的孩子表不用处理	
				LinkedList<TreeNode> child_q = new LinkedList<>();
				child_q.addAll(Xv.child);
				while (!child_q.isEmpty()) {
					TreeNode cur_child = child_q.poll();
					Vertex v = new Vertex(cur_child.grayId, 0);
					double d = h2hIndex.H2H_Query(Xv.grayId, cur_child.grayId);
					v.w = d;
					Xv.childTable.add(v);
					//孩子节点入队
					child_q.addAll(cur_child.child);
				}
				//将孩子表排序
				Collections.sort(Xv.childTable, new Comparator<Vertex>() {
					@Override
					public int compare(Vertex o1, Vertex o2) {
						if (o1.w - o2.w < 0) {
							return -1;
						} else if (o1.w - o2.w > 0) {
							return 1;
						} else {
							return 0;
						}						
					}
				});
				//孩子节点入队
				ArrayList<TreeNode> childs = Xv.child;
				for (TreeNode child : childs) {
					q.add(child);
				}
			}
		}
		//将候选集M中的点，找到对应的顶点进行标记
		for (int v_id : M) {
			Vertex v = v_map.get(v_id);
			v.isCandidate = true;
		}
		return root;
	}
	
	public ArrayList<Vertex> ADTDN_Query(int u_id, int k) {//u是查询点id, k是kNN的k	
		ArrayList<Vertex> R = new ArrayList<Vertex>();
		TreeNode Xu = node_map.get(u_id);
		//先将Xu的祖先表和孩子表中的顶点到u的真实最短距离更新到 v_curDist_map
		for (Vertex vertex : Xu.ancTable) {
			v_curDist_map.put(vertex.id, vertex.w);
		}
		for (Vertex vertex : Xu.childTable) {
			v_curDist_map.put(vertex.id, vertex.w);
		}
		v_curDist_map.put(u_id, 0.0);//dist(u,u)
		
		ArrayList<Vertex> anc_u = new ArrayList<>();
		Vertex u = new Vertex(u_id, 0.0);
		anc_u.add(u);
		anc_u.addAll(Xu.ancTable);
		for (Vertex p : anc_u) {
			if (p.w >= threshold) {//p.w=dist(u, p)
				break;
			}
			if (v_map.get(p.id).isCandidate) {//我们设置的是 v_map中顶点的isCandidate，所以也得用v_map中的顶点进行判断
				Vertex v = new Vertex(p.id, 0.0);
				updateR(R, u, p, v, k);
				
			}
			TreeNode Xp = node_map.get(p.id);
			for (Vertex v : Xp.childTable) {
				if (p.w + v.w >= threshold) {
					break;
				}
				if (!v_map.get(v.id).isCandidate) {
					continue;
				}
				updateR(R, u, p, v, k);
			}
		}
		//查询结束需要重置 v_curDist_map 和 threshold ，以免影响下次查询结果
		Set<Integer> keySet = v_curDist_map.keySet();
		for (Integer key : keySet) {
			v_curDist_map.put(key, -1.0);
		}
		threshold = Integer.MAX_VALUE;
//		System.out.println("该次查询的搜索空间:" + SP.size());
//		avg_sp += SP.size();
//		SP.clear();//记得清空
		return R;
	}
	//重新捋一遍
	private void updateR(ArrayList<Vertex> R, Vertex u, Vertex p, Vertex v, int k) {//p.w=dist(u,p)    v.w=dist(p,v)
//		SP.add(p.id);
//		SP.add(v.id);
		boolean have = false;//R是否含有v
		for (Vertex vertex : R) {
			if (vertex.id == v.id) {
				have = true;
				vertex.cur_dist = Math.min(vertex.cur_dist, p.w + v.w);//有的话就更新 cur_dist
				v_curDist_map.put(v.id, vertex.cur_dist);//更新 v_curDist_map
				break;
			}
		}
		if (have && R.size() < k) {	//说明已经处理完了，不需要更新阈值		
			return ;
		}
		
		if (have && R.size() >= k) {//需要更新阈值	
			Collections.sort(R, comparator);
			int last = R.size() - 1;
			Vertex t = R.get(last);//R中最后一个顶点
			threshold = v_curDist_map.get(t.id);
			return ;
		}
		//到这说明R中不含v
		//无论v是不是u的祖先或孩子，v_curDist_map中的都是最新的cur_dist
		double v_curDist = v_curDist_map.get(v.id);
		//double类型比较相等，转为字符串
		if (String.valueOf(v_curDist).equals("-1.0")) {//说明没计算过curDist
			v_curDist = p.w + v.w;
			v_curDist_map.put(v.id, v_curDist);
		} else {//说明计算过，则更新
			v_curDist = Math.min(v_curDist, p.w + v.w);
			v_curDist_map.put(v.id, v_curDist);
		}
		if (R.size() < k) {
			v.cur_dist = v_curDist_map.get(v.id);
			R.add(v);//每次新加入元素，紧跟一个sort
			if (R.size() == k) { //该更新阈值了
				Collections.sort(R, comparator);
				int last = R.size() - 1;
				Vertex t = R.get(last);//R中最后一个顶点
				threshold = v_curDist_map.get(t.id);
			}
		} else {//R.size >= k
			int last = R.size() - 1;
			Vertex t = R.get(last);//R中最后一个顶点
			double t_cur_dist = v_curDist_map.get(t.id);
			double v_cur_dist = v_curDist_map.get(v.id);
			if (v_cur_dist < t_cur_dist) {
				R.remove(last);
				v.cur_dist = v_curDist_map.get(v.id);
				R.add(v);
				Collections.sort(R, comparator);
			} 
			Vertex l = R.get(last);
			threshold = v_curDist_map.get(l.id);
		}
	}
	
	public void Delete(int v_id) {
		Vertex v = v_map.get(v_id);
		v.isCandidate = false;
	}
	
	public void Insert(int v_id) {
		Vertex v = v_map.get(v_id);
		v.isCandidate = true;
	}
	
	public static void main(String[] args) throws IOException {
		//读入查询点集合
		String filePath = "E:\\韦方良\\对比数据集\\Bangkok_queryPoins.txt";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		ArrayList<Integer> querySet = new ArrayList<>();
		String s = null;
		while ((s = br.readLine()) != null) {
			int a = Integer.parseInt(s);
			querySet.add(a);
		}
		br.close();
		
		int k = 100;
		int q = 0;
		ADTDN_Index adtdn_index = new ADTDN_Index();
		long size = RamUsageEstimator.sizeOf(adtdn_index);//索引大小：字节
		System.out.println("索引大小为:" + (size / 1024.0 / 1024.0) + "MB");
		
	//	System.out.println("共计" + querySet.size() + "次查询");
		long start = System.currentTimeMillis();
		for (int i = 0; i < querySet.size(); i++) {
//			System.out.println("第" + i + "次查询:");
			q = querySet.get(i);
			ArrayList<Vertex> R = adtdn_index.ADTDN_Query(q, k);
		}
		long end = System.currentTimeMillis();
		long curtime = end - start;
	//	System.out.println("平均单次查询时间：" + (curtime / 1000.0) + "ms");
	//	System.out.println("平均单次搜索空间:" + adtdn_index.avg_sp / 1000.0);
	}
}
