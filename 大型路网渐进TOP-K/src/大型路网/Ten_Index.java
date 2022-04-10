package 大型路网;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.carrotsearch.sizeof.RamUsageEstimator;

import 求最短距离.H2H_Index;//其他同名类不用导入
/**
 * 本文实现的是最优版的TEN*-Index
 * @author MSI
 *
 */
public class Ten_Index {
	public Set<Integer> M;//候选集
	public H2H_Index h2hIndex;
	public TreeNode root;
	public Map<Integer, TreeNode> node_map;
	public Map<Integer, Double> v_curDist_map;//<顶点id, 到查询点的当前最短距离>
	public int k;
//	public Set<Integer> SP = new HashSet<>();//搜索空间
//	public int avg_sp = 0;
	
	public Ten_Index(int k) throws IOException {
		this.k = k;
	//	long start = System.currentTimeMillis();
		generate_TEN_Index();
	//	long end = System.currentTimeMillis();
	//	System.out.println("索引构建耗时:" + (end - start) + "ms");
	}
	
	public TreeNode  generate_TEN_Index() throws IOException{
		M = Candidates.get_Candidates();
		h2hIndex = new H2H_Index(); 
		TreeDecomposition TD = new TreeDecomposition();
		//System.out.println("树宽：" + TD.w);
		//System.out.println("树高:" + TD.h);
		root = TD.root;
		node_map = TD.node_map;
		v_curDist_map = new HashMap<>();
		//求每个节点的Au(自底向上的顺序)，经测试，正确！
		LinkedList<TreeNode> q = new LinkedList<>();
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//队首元素
			TreeNode p = Xv;//p是指针，一开始指向Xv
			//如果p不是根节点
			if (p.parent != null) {	//如果=null，说明p是根节点，根节点的Au不用处理		
				p = p.parent;//跳过Xv自身
				while (p != null) {
					Vertex v = new Vertex(p.grayId, 0);
					double d = h2hIndex.H2H_Query(Xv.grayId, p.grayId);
					v.w = d;
					Xv.Au.add(v);
					p = p.parent;
				}
			}
			//孩子节点入队
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//算法3第5行的Q
		LinkedList<TreeNode> Q = new LinkedList<>();
		
		//求每个节点的Cu 以及 deposit，测试正确！
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//队首元素
			//全都初始为-1.0，表示没计算过
			v_curDist_map.put(Xv.grayId, -1.0);
			if (Xv.child.isEmpty()) {//说明Xv是叶子节点
				Q.add(Xv);
			}
			TreeNode p = Xv;//p是游走指针，一开始指向Xv
			LinkedList<TreeNode> childs_q = new LinkedList<>();
			childs_q.addAll(p.child);//childs_q这个队列中不含Xv自身
			while (!childs_q.isEmpty()) {
				TreeNode child = childs_q.poll();
				for (int v_id : child.neibList) {
					if (v_id == Xv.grayId) {
						Xv.Cu.add(child.grayId);
					}
				}
				//孩子节点入队
				childs_q.addAll(child.child);
			}
			Xv.deposit = Xv.Cu.size();
			//孩子节点入队
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		
		//算法3第7行
		while (!Q.isEmpty()) {
			TreeNode Xu = Q.poll();
			ArrayList<Vertex> C = new ArrayList<>();
			if (M.contains(Xu.grayId)) {
				C.add(new Vertex(Xu.grayId, 0));
			}
			//第10行
			for (int v_id : Xu.Cu) {
				TreeNode Xv = node_map.get(v_id);
				ArrayList<Vertex> Ckv = Xv.CkNN;
				for (Vertex v : Ckv) {
					boolean have = false;
					for (Vertex each_C : C) {
						if (each_C.id == v.id) {//说明C中有这个v了
							have = true;
							break;
						}
					}
					if (!have) {
						C.add(new Vertex(v.id, 0));
					}
				}
			}
			//查找C中每个顶点到Xu的最短距离
			for (Vertex v : C) {
				TreeNode Xv = node_map.get(v.id);//对应的树节点
				for (Vertex anc : Xv.Au) {
					if (anc.id == Xu.grayId) {//(v, u)
						v.w = anc.w;//dist(v, u)
						break;
					}
				}
				
			}
			//第11-12行
			Collections.sort(C, new Comparator<Vertex>() {
				@Override
				public int compare(Vertex o1, Vertex o2) {
					if (o1.w - o2.w < 0) {//升序
						return -1;//不交换
					} else if (o1.w - o2.w > 0) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			if (C.size() <= k) {
				Xu.CkNN.addAll(C);
			} else {	
				for (int i = 0; i < k; i++) {
					Xu.CkNN.add(C.get(i));
				}
			}
			//第13行
			if (Xu.parent != null) {
				for (int p_id : Xu.neibList) {
					if (p_id != Xu.grayId) {
						TreeNode Xp = node_map.get(p_id);
						Xp.deposit--;
						if (Xp.deposit == 0) {
							Q.add(Xp);
						}
					}
				}
			}
		}
		return root;
	}
	//算法1
	public Set<Vertex> TEN_Query(int u) {//u是查询点id
		Set<Vertex> Vk = new HashSet<>();
		ArrayList<Vertex> R = new ArrayList<>();
		TreeNode Xu = node_map.get(u);
		
		ArrayList<Vertex> Au_u = new ArrayList<>();
		Au_u.add(new Vertex(u, 0.0));
		Au_u.addAll(Xu.Au);
		
		for (Vertex p : Au_u) {
			TreeNode Xp = node_map.get(p.id);
			for (Vertex v : Xp.CkNN) {
		//		SP.add(p.id);
		//		SP.add(v.id);
				double v_curDist = v_curDist_map.get(v.id);
				//double类型比较相等，转为字符串
				if (!String.valueOf(v_curDist).equals("-1.0") && v_curDist <= p.w + v.w) {//p.w=dist(u, p)   v.w=dist(p, v)
					continue;
				}
				boolean have = false;
				for (Vertex vertex : R) {
					if (vertex.id == v.id) {//若R中已有v，则更新cur_dist，p.w + v.w必然 < v_curDist
						v_curDist_map.put(v.id, p.w + v.w);
						have = true;
						break;
					}
				}
				if (!have) {
					R.add(v);
					v_curDist_map.put(v.id, p.w + v.w);
				}
				
			}
		}
		//为了便于R排序，只在这里第一次使用Vertex中的cur_dist
		for (Vertex v : R) {
			v.cur_dist = v_curDist_map.get(v.id);
		}
		Collections.sort(R, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				if (o1.cur_dist - o2.cur_dist < 0) {//升序
					return -1;//不交换
				} else if (o1.cur_dist - o2.cur_dist > 0) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		if (R.size() <= k) {
			Vk.addAll(R);
		} else {
			for (int i = 0; i < k; i++) {
				Vk.add(R.get(i));
			}
		}
		//非常重要！本次查询结束，记得把 v_curDist_map 重新置为 -1.0
		Set<Integer> keySet = v_curDist_map.keySet();
		for (int key : keySet) {
			v_curDist_map.put(key, -1.0);
		}
	//	System.out.println("该次查询的搜索空间:" + SP.size());
	//	avg_sp += SP.size();
	//	SP.clear();
		return Vk;
	}
	
	public void TEN_Delete(int u_id) {
		ArrayList<Vertex> Au_u = new ArrayList<>();
		Au_u.add(new Vertex(u_id, 0.0));
		TreeNode Xu = node_map.get(u_id);
		Au_u.addAll(Xu.Au);//Au ∪ u，自底向上的顺序
		for (Vertex p : Au_u) {
			TreeNode Xp = node_map.get(p.id);
			//查看Xp.CkNN中是否含有u
			int index = -1;//Xp.CkNN中u的下标
			for (int i = 0; i < Xp.CkNN.size(); i++) {
				Vertex v = Xp.CkNN.get(i);
				if (v.id == u_id) {//说明含u
					index = i;
					break;
				}
			}
			if (index >= 0) {
				Xp.CkNN.remove(index);//删除u之后需要再找一个顶上
				ArrayList<Vertex> C = new ArrayList<>();
				Set<Integer> CC = new HashSet<>();//顶点id，去重的
				for (int v : Xp.Cu) {
					TreeNode Xv = node_map.get(v);
					for (Vertex vertex : Xv.CkNN) {
						CC.add(vertex.id);
					}
				}//这个for是：p的所有收缩邻居v的 CkNN的并集
				//再去掉CC中 p的CkNN
				for (Vertex v : Xp.CkNN) {
					for (int cur_v : CC) {
						if (cur_v == v.id) {
							CC.remove(cur_v);
							break;
						}
					}
				}
				//以上完成了算法4第3行
				//将CC中的顶点id转为带有权重 (到p的最短距离) 的Vertex，放入C中
				for (int v : CC) {
					C.add(new Vertex(v, 0.0));
				}
				//查找C中每个顶点v到Xp的最短距离
				for (Vertex v : C) {
					TreeNode Xv = node_map.get(v.id);//Xp一定是Xv的祖先，因为是Xv是收缩邻居范围
					for (Vertex vertex : Xv.Au) {
						if (vertex.id == Xp.grayId) {
							v.w = vertex.w;
						}
					}
				}
				if (!C.isEmpty()) {
					//排序,按照 v-->p的最短距离
					Collections.sort(C, new Comparator<Vertex>() {
						@Override
						public int compare(Vertex o1, Vertex o2) {
							if (o1.w - o2.w < 0) {//升序
								return -1;//不交换
							} else if (o1.w - o2.w > 0) {
								return 1;
							} else {
								return 0;
							}
						}
					});
					Xp.CkNN.add(C.get(0));
				}
			} else {//说明不含u
				continue;
			}
		}
	}
	
	public void TEN_Insert(int u_id) {
		ArrayList<Vertex> Au_u = new ArrayList<>();
		Au_u.add(new Vertex(u_id, 0.0));
		TreeNode Xu = node_map.get(u_id);
		Au_u.addAll(Xu.Au);//Au ∪ u，自底向上的顺序
		for (Vertex p : Au_u) {
			TreeNode Xp = node_map.get(p.id);
			if (Xp.CkNN.size() < k) {
				Xp.CkNN.add(new Vertex(u_id, p.w));
			} else {
				int last = Xp.CkNN.size() - 1;
				Vertex v = Xp.CkNN.get(last);
				if (v.w > p.w) {//v.w=dist(v,p)   p.w=dist(p,u)  说明u比v离P更近
					Xp.CkNN.remove(last);
					Xp.CkNN.add(new Vertex(u_id, p.w));
				}
			}
		}
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
		
		int k = 10, q = 0;
		Ten_Index ten_index = new Ten_Index(k);//指定kNN的k值
		long size = RamUsageEstimator.sizeOf(ten_index);//索引大小：字节
		System.out.println("索引大小为:" + (size / 1024.0 / 1024.0) + "MB");
		
	//	System.out.println("k:" + k);
	//	System.out.println("共计" + querySet.size() + "次查询");
		long start = System.currentTimeMillis();
		for (int i = 0; i < querySet.size(); i++) {
	//		System.out.println("第" + i + "次查询:");
			q = querySet.get(i);
			Set<Vertex> res = ten_index.TEN_Query(q);
		}
		long end = System.currentTimeMillis();
		long curtime = end - start;
	//	System.out.println("平均单次查询时间：" + (curtime / 1000.0) + "ms");
	//	System.out.println("平均单次搜索空间:" + ten_index.avg_sp / 1000.0);
	}
}
