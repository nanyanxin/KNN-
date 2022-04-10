package ����̾���;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
/**
 * ������ When Hierarchy Meets 2-Hop-Labeling : Efficient Shortest Distance Queries on Road Networks
 * ����ƪ���ĵ����ֽ�
 * @author MSI
 *
 */
public class TreeDecomposition_When {
	 Set<Vertex>[] g;
	 Map<Integer, Vertex> v_map;//<����id,����v> ����keySet����·���Ķ��㼯V
	 Map<Integer, TreeNode> node_map = new HashMap<>();//�洢���ɵ����ڵ�, <��ɫ����id, ���ڵ�>
	 Map<String, TreeNode> encode_map = new HashMap<>();//�洢���ɵ����ڵ�, <��α���, ��Ӧ�����ڵ�>
	 int deleteOrderNum = 1;//�������ֵ
	 TreeNode root;
	//���� ��   ����Ķ��У��Զ�����VertexComparator�������ʱ����
	 ArrayList<Vertex> q = new ArrayList<Vertex>();
	 VertexComparator vc = new VertexComparator();//�Ƚ���
	
	 public TreeDecomposition_When() throws IOException {
		 generate_tree();
	}
	 
	public  TreeNode generate_tree() throws IOException {
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
			//��ÿ���ڵ��neibWeight���飬�����Ժ��� fai����
			for (int v_id : node.neibList) {
				if (v_id == node.grayId) {
					node.neibWeight.add(new Vertex(v_id, 0.0));
				} else {
					Set<Vertex> neibs = g[node.grayId];
					for (Vertex each_v : neibs) {
						if (each_v.id == v_id) {
							node.neibWeight.add(each_v);
						}
					}
				}
			}//neibWeight����������
					
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
					//������������ʽ���ޱ�ֱ����ߣ��б������Ϊ��С
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
			if (Xv.neibList.size() == 1) {
				root = Xv;//ָ������
			}
			if (Xv.neibList.size() > 1) {
				ArrayList<Integer> neibList = Xv.neibList;
				for (int neib : neibList) {//������Ĭ�ϵ�Integer
					if (neib == id) continue;
					Vertex cur_neib = v_map.get(neib);
					int curOrder = cur_neib.deleteOrder;//���ھӵ�������
					if (curOrder < minOrder) {
						minOrder = curOrder;
						u = node_map.get(neib);
					}
				}
				Xv.parent = u;//ָ��u
				u.child.add(Xv);
			}
		}	
		enCodeTree(root);
		return root;
	}
	//�������룬���ڿ��ٶ�λ�����ڵ�Ĺ�������
	private  void enCodeTree(TreeNode root) {
		LinkedList<ArrayList<TreeNode>> queue = new LinkedList<>();
		queue.add(root.child);
		root.code = "1";
		encode_map.put(root.code, root);
		while (!queue.isEmpty()) {
			ArrayList<TreeNode> childs = queue.poll();
		    for (int i = 0; i < childs.size(); i++) {
		    	TreeNode c = childs.get(i);//��i�����ӽڵ�
				String p_code = c.parent.code;
				String code = p_code + (i + 1);
				c.code = code;
				encode_map.put(c.code, c);
				queue.add(c.child);
			}
		}
	}
	
	//���� ��
	private  void updateDegree(int id) {
		int neibNum = g[id].size();//�ھ�����Ҳ���Ƕ�
		Vertex v = v_map.get(id);
		v.degree = neibNum;
	}
}
