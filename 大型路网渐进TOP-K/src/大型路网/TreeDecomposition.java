package ����·��;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
/**
 * ������ ����·������TOP-K ����ƪ���ĵ����ֽ�
 * @author MSI
 *
 */
public class TreeDecomposition {
	 Set<Vertex>[] g;
	 Map<Integer, Vertex> v_map;//<����id,����v> ����keySet����·���Ķ��㼯V
	 Map<Integer, TreeNode> node_map = new HashMap<>();//�洢���ɵ����ڵ�, <��ɫ����id, ���ڵ�>
	 int deleteOrderNum = 1;//�������ֵ
	//���� ��   ����Ķ��У��Զ�����VertexComparator�������ʱ����
	 ArrayList<Vertex> q = new ArrayList<Vertex>();
	 VertexComparator vc = new VertexComparator();//�Ƚ���
	 TreeNode root;
	 int w;//����
	 int h;//����
	 
	 public TreeDecomposition() throws IOException {
		 generate_tree();
	}
	 
	public TreeNode generate_tree() throws IOException {
	    root = new TreeNode(-1);
		g = RoadProcess.generate_g();
		v_map = RoadProcess.v_map;//<����id,����v> ��map
		Set<Integer> v_id_set = v_map.keySet();
		//����ÿ������Ķ�
		for (int id : v_id_set) {
			updateDegree(id);	
		}
		//ȫ������q��
		for (int id : v_id_set) {
			q.add(v_map.get(id));
		}
		//����
		Collections.sort(q, vc);//����Ƚ���vc
		//����Լ�����
		while (!q.isEmpty()) {
			Vertex v = q.remove(0);//��ʼ����, v�ǻ�ɫ����
			//1.�������ڵ�
			TreeNode node = new TreeNode(v.id);
			Set<Vertex> v_neibs = g[v.id];
			   //���X(v)�Ķ���
			node.neibList.add(v.id);//����ӻ�ɫ����
			   //������ھ�
			for (Vertex neib : v_neibs) {
				node.neibList.add(neib.id);
			}
			//���ɽڵ�󣬸�������
			w = Math.max(w, node.neibList.size() - 1);
			node_map.put(v.id, node);//�������ڵ������
			//2.�������ھӶ�֮�����ߣ�ע�����Ȩ��
			//�������ھӴ浽������
			ArrayList<Vertex> list = new ArrayList<>();
			for (Vertex vv : v_neibs) {
				list.add(vv);
			}
			//˫ָ����������ھӶ�
			for (int i = 0; i < list.size(); i++) {
				for (int j = i + 1; j < list.size(); j++) {
					Vertex s = list.get(i);//start����
					Vertex e = list.get(j);//end����
					if (!g[s.id].contains(e)) {//(s, e)û�бߵ�ʱ������±�
						double new_w = 0;
						for (Vertex v1 : g[s.id]) {//s--->v��Ȩ��
							if (v1.id == v.id) {
								new_w += v1.w;
							}
						}
						for (Vertex v2 : g[v.id]) {//v--->e��Ȩ��
							if (v2.id == e.id) {
								new_w += v2.w;
							}
						}
						g[s.id].add(new Vertex(e.id, new_w));
						g[e.id].add(new Vertex(s.id, new_w));
					} else {//�����бߣ������Ȩ��
						double new_w = 0.0;
						for (Vertex eachneib : g[v.id]) {
							if (eachneib.id == e.id) {
								new_w += eachneib.w;//(v, e)��w
							}
							if (eachneib.id == s.id) {
								new_w += eachneib.w;//(v, s)��w
							}//new_w �� s--v--e��Ȩ��
						}
						for (Vertex eachneib : g[s.id]) {//����s-->e��Ȩ��
							if (eachneib.id == e.id) {
								eachneib.w = Math.min(eachneib.w, new_w);
							}
						}
						for (Vertex eachneib : g[e.id]) {//����e-->s��Ȩ��
							if (eachneib.id == s.id) {
								eachneib.w = Math.min(eachneib.w, new_w);
							}
						}
					}
				}
			}
			//3.��ͼ(�ڽӱ�)��ɾ��v
			Set<Vertex> v_neib = g[v.id];
			for (Vertex eachv : v_neib) {
				g[eachv.id].remove(v);
			}
			g[v.id] = null;
			//4.�����ھӵĶȣ���v�������������
			for (Vertex eachv : v_neib) {
				updateDegree(eachv.id);
			}
			//��������
			Collections.sort(q, vc);
			//��¼������
			v.deleteOrder = deleteOrderNum++;
		}
		
		//�����ڵ��������
		for (int id : v_id_set) {//v_id_set���Ƕ��㼯V
			int minOrder = Integer.MAX_VALUE;
			TreeNode u = null;//Xv�ĸ��ڵ�
			TreeNode Xv = node_map.get(id);
			if (Xv.neibList.size() == 1) root = Xv;//ָ������
			if (Xv.neibList.size() > 1) {
				ArrayList<Integer> neibList = Xv.neibList;
				for (int neib : neibList) {
					if (neib == id) continue;
					int curOrder = v_map.get(neib).deleteOrder;//���ھӵ�������
					if (curOrder < minOrder) {
						minOrder = curOrder;
						u = node_map.get(neib);
					}
				}
				Xv.parent = u;//ָ��u
				u.child.add(Xv);
			}
		}
		h = get_h(root);
		return root;
	}
	
	//���� ��
	private  void updateDegree(Integer id) {
		int neibNum = g[id].size();//�ھ�����Ҳ���Ƕ�
		Vertex v = v_map.get(id);
		v.degree = neibNum;
	}
	//������
	public int get_h(TreeNode root) {
		int h = 0;
		LinkedList<ArrayList<TreeNode>> list = new LinkedList<>();
		ArrayList<TreeNode> l = new ArrayList<TreeNode>();//��һ��Ľڵ�
		l.add(root);//ֻ��root
		list.add(l);
		while (!list.isEmpty()) {
			ArrayList<TreeNode> curLevel = list.poll();
			h++;
			ArrayList<TreeNode> nextLevel = new ArrayList<TreeNode>();
			for (TreeNode c : curLevel) {				
				if (c.child.size() != 0) nextLevel.addAll(c.child);
			}
			if (nextLevel.size() != 0) {//��һ���нڵ�ʱ�����
				list.add(nextLevel);
			}
		}
		return h;
	}
}
