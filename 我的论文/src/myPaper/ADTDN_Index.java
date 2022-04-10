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

import ����̾���.H2H_Index;

public class ADTDN_Index {
	public Set<Integer> M;//��ѡ��
	public H2H_Index h2hIndex;
	public TreeNode root;
	public Map<Integer, TreeNode> node_map;
	public Map<Integer, Vertex> v_map;//<����id,����v> ����keySet����·���Ķ��㼯V
	public Map<Integer, Double> v_curDist_map;//<����id, ����ѯ��ĵ�ǰ��̾���>
	public double threshold = Integer.MAX_VALUE;
	public V_Cur_Dist_Comparator comparator = new V_Cur_Dist_Comparator();//���� cur_dist����ıȽ���
//	public Set<Integer> SP = new HashSet<>();//�����ռ�
//	public int avg_sp = 0;
	
	public ADTDN_Index() throws IOException {
	//	long start1 = System.currentTimeMillis();
		generate_ADTDN_Index();
	//	long end1 = System.currentTimeMillis();
	//	System.out.println("����������ʱ:" + (end1 - start1) + "ms");
	}
	
	public TreeNode  generate_ADTDN_Index() throws IOException{
		M = Candidates.get_Candidates();
		h2hIndex = new H2H_Index(); 
		TreeDecomposition TD = new TreeDecomposition();
		root = TD.root;
		node_map = TD.node_map;
		v_map = TD.v_map;
		v_curDist_map = new HashMap<>();
		//��ÿ���ڵ��ancTable
		LinkedList<TreeNode> q = new LinkedList<>();
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//����Ԫ��
			//ȫ����ʼΪ-1.0����ʾû�����
			v_curDist_map.put(Xv.grayId, -1.0);
			TreeNode p = Xv;//p��ָ�룬һ��ʼָ��Xv
			//���p���Ǹ��ڵ�
			if (p.parent != null) {	//���=null��˵��p�Ǹ��ڵ㣬���ڵ�����ȱ��ô���
				p = p.parent;//����Xv����
				while (p != null) {
					Vertex v = new Vertex(p.grayId, 0);
					double d = h2hIndex.H2H_Query(Xv.grayId, p.grayId);
					v.w = d;
					Xv.ancTable.add(v);
					p = p.parent;
				}
				//�����ȱ�����
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
			//���ӽڵ����
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//��ÿ���ڵ��childTable
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//����Ԫ��
			//���p����Ҷ�ڵ�
			if (!Xv.child.isEmpty()) {	//���Ϊ�գ�˵��Xv��Ҷ�ڵ㣬Ҷ�ڵ�ĺ��ӱ��ô���	
				LinkedList<TreeNode> child_q = new LinkedList<>();
				child_q.addAll(Xv.child);
				while (!child_q.isEmpty()) {
					TreeNode cur_child = child_q.poll();
					Vertex v = new Vertex(cur_child.grayId, 0);
					double d = h2hIndex.H2H_Query(Xv.grayId, cur_child.grayId);
					v.w = d;
					Xv.childTable.add(v);
					//���ӽڵ����
					child_q.addAll(cur_child.child);
				}
				//�����ӱ�����
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
				//���ӽڵ����
				ArrayList<TreeNode> childs = Xv.child;
				for (TreeNode child : childs) {
					q.add(child);
				}
			}
		}
		//����ѡ��M�еĵ㣬�ҵ���Ӧ�Ķ�����б��
		for (int v_id : M) {
			Vertex v = v_map.get(v_id);
			v.isCandidate = true;
		}
		return root;
	}
	
	public ArrayList<Vertex> ADTDN_Query(int u_id, int k) {//u�ǲ�ѯ��id, k��kNN��k	
		ArrayList<Vertex> R = new ArrayList<Vertex>();
		TreeNode Xu = node_map.get(u_id);
		//�Ƚ�Xu�����ȱ�ͺ��ӱ��еĶ��㵽u����ʵ��̾�����µ� v_curDist_map
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
			if (v_map.get(p.id).isCandidate) {//�������õ��� v_map�ж����isCandidate������Ҳ����v_map�еĶ�������ж�
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
		//��ѯ������Ҫ���� v_curDist_map �� threshold ������Ӱ���´β�ѯ���
		Set<Integer> keySet = v_curDist_map.keySet();
		for (Integer key : keySet) {
			v_curDist_map.put(key, -1.0);
		}
		threshold = Integer.MAX_VALUE;
//		System.out.println("�ôβ�ѯ�������ռ�:" + SP.size());
//		avg_sp += SP.size();
//		SP.clear();//�ǵ����
		return R;
	}
	//������һ��
	private void updateR(ArrayList<Vertex> R, Vertex u, Vertex p, Vertex v, int k) {//p.w=dist(u,p)    v.w=dist(p,v)
//		SP.add(p.id);
//		SP.add(v.id);
		boolean have = false;//R�Ƿ���v
		for (Vertex vertex : R) {
			if (vertex.id == v.id) {
				have = true;
				vertex.cur_dist = Math.min(vertex.cur_dist, p.w + v.w);//�еĻ��͸��� cur_dist
				v_curDist_map.put(v.id, vertex.cur_dist);//���� v_curDist_map
				break;
			}
		}
		if (have && R.size() < k) {	//˵���Ѿ��������ˣ�����Ҫ������ֵ		
			return ;
		}
		
		if (have && R.size() >= k) {//��Ҫ������ֵ	
			Collections.sort(R, comparator);
			int last = R.size() - 1;
			Vertex t = R.get(last);//R�����һ������
			threshold = v_curDist_map.get(t.id);
			return ;
		}
		//����˵��R�в���v
		//����v�ǲ���u�����Ȼ��ӣ�v_curDist_map�еĶ������µ�cur_dist
		double v_curDist = v_curDist_map.get(v.id);
		//double���ͱȽ���ȣ�תΪ�ַ���
		if (String.valueOf(v_curDist).equals("-1.0")) {//˵��û�����curDist
			v_curDist = p.w + v.w;
			v_curDist_map.put(v.id, v_curDist);
		} else {//˵��������������
			v_curDist = Math.min(v_curDist, p.w + v.w);
			v_curDist_map.put(v.id, v_curDist);
		}
		if (R.size() < k) {
			v.cur_dist = v_curDist_map.get(v.id);
			R.add(v);//ÿ���¼���Ԫ�أ�����һ��sort
			if (R.size() == k) { //�ø�����ֵ��
				Collections.sort(R, comparator);
				int last = R.size() - 1;
				Vertex t = R.get(last);//R�����һ������
				threshold = v_curDist_map.get(t.id);
			}
		} else {//R.size >= k
			int last = R.size() - 1;
			Vertex t = R.get(last);//R�����һ������
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
		//�����ѯ�㼯��
		String filePath = "E:\\Τ����\\�Ա����ݼ�\\Bangkok_queryPoins.txt";
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
		long size = RamUsageEstimator.sizeOf(adtdn_index);//������С���ֽ�
		System.out.println("������СΪ:" + (size / 1024.0 / 1024.0) + "MB");
		
	//	System.out.println("����" + querySet.size() + "�β�ѯ");
		long start = System.currentTimeMillis();
		for (int i = 0; i < querySet.size(); i++) {
//			System.out.println("��" + i + "�β�ѯ:");
			q = querySet.get(i);
			ArrayList<Vertex> R = adtdn_index.ADTDN_Query(q, k);
		}
		long end = System.currentTimeMillis();
		long curtime = end - start;
	//	System.out.println("ƽ�����β�ѯʱ�䣺" + (curtime / 1000.0) + "ms");
	//	System.out.println("ƽ�����������ռ�:" + adtdn_index.avg_sp / 1000.0);
	}
}
