package ����·��;

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

import ����̾���.H2H_Index;//����ͬ���಻�õ���
/**
 * ����ʵ�ֵ������Ű��TEN*-Index
 * @author MSI
 *
 */
public class Ten_Index {
	public Set<Integer> M;//��ѡ��
	public H2H_Index h2hIndex;
	public TreeNode root;
	public Map<Integer, TreeNode> node_map;
	public Map<Integer, Double> v_curDist_map;//<����id, ����ѯ��ĵ�ǰ��̾���>
	public int k;
//	public Set<Integer> SP = new HashSet<>();//�����ռ�
//	public int avg_sp = 0;
	
	public Ten_Index(int k) throws IOException {
		this.k = k;
	//	long start = System.currentTimeMillis();
		generate_TEN_Index();
	//	long end = System.currentTimeMillis();
	//	System.out.println("����������ʱ:" + (end - start) + "ms");
	}
	
	public TreeNode  generate_TEN_Index() throws IOException{
		M = Candidates.get_Candidates();
		h2hIndex = new H2H_Index(); 
		TreeDecomposition TD = new TreeDecomposition();
		//System.out.println("����" + TD.w);
		//System.out.println("����:" + TD.h);
		root = TD.root;
		node_map = TD.node_map;
		v_curDist_map = new HashMap<>();
		//��ÿ���ڵ��Au(�Ե����ϵ�˳��)�������ԣ���ȷ��
		LinkedList<TreeNode> q = new LinkedList<>();
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//����Ԫ��
			TreeNode p = Xv;//p��ָ�룬һ��ʼָ��Xv
			//���p���Ǹ��ڵ�
			if (p.parent != null) {	//���=null��˵��p�Ǹ��ڵ㣬���ڵ��Au���ô���		
				p = p.parent;//����Xv����
				while (p != null) {
					Vertex v = new Vertex(p.grayId, 0);
					double d = h2hIndex.H2H_Query(Xv.grayId, p.grayId);
					v.w = d;
					Xv.Au.add(v);
					p = p.parent;
				}
			}
			//���ӽڵ����
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		//�㷨3��5�е�Q
		LinkedList<TreeNode> Q = new LinkedList<>();
		
		//��ÿ���ڵ��Cu �Լ� deposit��������ȷ��
		q.add(root);
		while (!q.isEmpty()) {
			TreeNode Xv = q.poll();//����Ԫ��
			//ȫ����ʼΪ-1.0����ʾû�����
			v_curDist_map.put(Xv.grayId, -1.0);
			if (Xv.child.isEmpty()) {//˵��Xv��Ҷ�ӽڵ�
				Q.add(Xv);
			}
			TreeNode p = Xv;//p������ָ�룬һ��ʼָ��Xv
			LinkedList<TreeNode> childs_q = new LinkedList<>();
			childs_q.addAll(p.child);//childs_q��������в���Xv����
			while (!childs_q.isEmpty()) {
				TreeNode child = childs_q.poll();
				for (int v_id : child.neibList) {
					if (v_id == Xv.grayId) {
						Xv.Cu.add(child.grayId);
					}
				}
				//���ӽڵ����
				childs_q.addAll(child.child);
			}
			Xv.deposit = Xv.Cu.size();
			//���ӽڵ����
			ArrayList<TreeNode> childs = Xv.child;
			for (TreeNode child : childs) {
				q.add(child);
			}
		}
		
		//�㷨3��7��
		while (!Q.isEmpty()) {
			TreeNode Xu = Q.poll();
			ArrayList<Vertex> C = new ArrayList<>();
			if (M.contains(Xu.grayId)) {
				C.add(new Vertex(Xu.grayId, 0));
			}
			//��10��
			for (int v_id : Xu.Cu) {
				TreeNode Xv = node_map.get(v_id);
				ArrayList<Vertex> Ckv = Xv.CkNN;
				for (Vertex v : Ckv) {
					boolean have = false;
					for (Vertex each_C : C) {
						if (each_C.id == v.id) {//˵��C�������v��
							have = true;
							break;
						}
					}
					if (!have) {
						C.add(new Vertex(v.id, 0));
					}
				}
			}
			//����C��ÿ�����㵽Xu����̾���
			for (Vertex v : C) {
				TreeNode Xv = node_map.get(v.id);//��Ӧ�����ڵ�
				for (Vertex anc : Xv.Au) {
					if (anc.id == Xu.grayId) {//(v, u)
						v.w = anc.w;//dist(v, u)
						break;
					}
				}
				
			}
			//��11-12��
			Collections.sort(C, new Comparator<Vertex>() {
				@Override
				public int compare(Vertex o1, Vertex o2) {
					if (o1.w - o2.w < 0) {//����
						return -1;//������
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
			//��13��
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
	//�㷨1
	public Set<Vertex> TEN_Query(int u) {//u�ǲ�ѯ��id
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
				//double���ͱȽ���ȣ�תΪ�ַ���
				if (!String.valueOf(v_curDist).equals("-1.0") && v_curDist <= p.w + v.w) {//p.w=dist(u, p)   v.w=dist(p, v)
					continue;
				}
				boolean have = false;
				for (Vertex vertex : R) {
					if (vertex.id == v.id) {//��R������v�������cur_dist��p.w + v.w��Ȼ < v_curDist
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
		//Ϊ�˱���R����ֻ�������һ��ʹ��Vertex�е�cur_dist
		for (Vertex v : R) {
			v.cur_dist = v_curDist_map.get(v.id);
		}
		Collections.sort(R, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				if (o1.cur_dist - o2.cur_dist < 0) {//����
					return -1;//������
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
		//�ǳ���Ҫ�����β�ѯ�������ǵð� v_curDist_map ������Ϊ -1.0
		Set<Integer> keySet = v_curDist_map.keySet();
		for (int key : keySet) {
			v_curDist_map.put(key, -1.0);
		}
	//	System.out.println("�ôβ�ѯ�������ռ�:" + SP.size());
	//	avg_sp += SP.size();
	//	SP.clear();
		return Vk;
	}
	
	public void TEN_Delete(int u_id) {
		ArrayList<Vertex> Au_u = new ArrayList<>();
		Au_u.add(new Vertex(u_id, 0.0));
		TreeNode Xu = node_map.get(u_id);
		Au_u.addAll(Xu.Au);//Au �� u���Ե����ϵ�˳��
		for (Vertex p : Au_u) {
			TreeNode Xp = node_map.get(p.id);
			//�鿴Xp.CkNN���Ƿ���u
			int index = -1;//Xp.CkNN��u���±�
			for (int i = 0; i < Xp.CkNN.size(); i++) {
				Vertex v = Xp.CkNN.get(i);
				if (v.id == u_id) {//˵����u
					index = i;
					break;
				}
			}
			if (index >= 0) {
				Xp.CkNN.remove(index);//ɾ��u֮����Ҫ����һ������
				ArrayList<Vertex> C = new ArrayList<>();
				Set<Integer> CC = new HashSet<>();//����id��ȥ�ص�
				for (int v : Xp.Cu) {
					TreeNode Xv = node_map.get(v);
					for (Vertex vertex : Xv.CkNN) {
						CC.add(vertex.id);
					}
				}//���for�ǣ�p�����������ھ�v�� CkNN�Ĳ���
				//��ȥ��CC�� p��CkNN
				for (Vertex v : Xp.CkNN) {
					for (int cur_v : CC) {
						if (cur_v == v.id) {
							CC.remove(cur_v);
							break;
						}
					}
				}
				//����������㷨4��3��
				//��CC�еĶ���idתΪ����Ȩ�� (��p����̾���) ��Vertex������C��
				for (int v : CC) {
					C.add(new Vertex(v, 0.0));
				}
				//����C��ÿ������v��Xp����̾���
				for (Vertex v : C) {
					TreeNode Xv = node_map.get(v.id);//Xpһ����Xv�����ȣ���Ϊ��Xv�������ھӷ�Χ
					for (Vertex vertex : Xv.Au) {
						if (vertex.id == Xp.grayId) {
							v.w = vertex.w;
						}
					}
				}
				if (!C.isEmpty()) {
					//����,���� v-->p����̾���
					Collections.sort(C, new Comparator<Vertex>() {
						@Override
						public int compare(Vertex o1, Vertex o2) {
							if (o1.w - o2.w < 0) {//����
								return -1;//������
							} else if (o1.w - o2.w > 0) {
								return 1;
							} else {
								return 0;
							}
						}
					});
					Xp.CkNN.add(C.get(0));
				}
			} else {//˵������u
				continue;
			}
		}
	}
	
	public void TEN_Insert(int u_id) {
		ArrayList<Vertex> Au_u = new ArrayList<>();
		Au_u.add(new Vertex(u_id, 0.0));
		TreeNode Xu = node_map.get(u_id);
		Au_u.addAll(Xu.Au);//Au �� u���Ե����ϵ�˳��
		for (Vertex p : Au_u) {
			TreeNode Xp = node_map.get(p.id);
			if (Xp.CkNN.size() < k) {
				Xp.CkNN.add(new Vertex(u_id, p.w));
			} else {
				int last = Xp.CkNN.size() - 1;
				Vertex v = Xp.CkNN.get(last);
				if (v.w > p.w) {//v.w=dist(v,p)   p.w=dist(p,u)  ˵��u��v��P����
					Xp.CkNN.remove(last);
					Xp.CkNN.add(new Vertex(u_id, p.w));
				}
			}
		}
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
		
		int k = 10, q = 0;
		Ten_Index ten_index = new Ten_Index(k);//ָ��kNN��kֵ
		long size = RamUsageEstimator.sizeOf(ten_index);//������С���ֽ�
		System.out.println("������СΪ:" + (size / 1024.0 / 1024.0) + "MB");
		
	//	System.out.println("k:" + k);
	//	System.out.println("����" + querySet.size() + "�β�ѯ");
		long start = System.currentTimeMillis();
		for (int i = 0; i < querySet.size(); i++) {
	//		System.out.println("��" + i + "�β�ѯ:");
			q = querySet.get(i);
			Set<Vertex> res = ten_index.TEN_Query(q);
		}
		long end = System.currentTimeMillis();
		long curtime = end - start;
	//	System.out.println("ƽ�����β�ѯʱ�䣺" + (curtime / 1000.0) + "ms");
	//	System.out.println("ƽ�����������ռ�:" + ten_index.avg_sp / 1000.0);
	}
}
