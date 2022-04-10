package G��Tree������Ԥ����;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import csvTotxt.Vertex;

public class Predo {
	static String filePath = "C:\\Users\\MSI\\Desktop\\ʦ�ֵ�\\date\\example.txt";
	static String filePath2 = "F:\\����ʵ�����ݼ�\\Quanzhou_Edgelist.csv";
	//cnode�ļ�ÿ�����������ֱ�Ϊ ������id�����ȡ�γ�� ��  һ���ո�ļ�϶
	static String dir1 = "F:\\G��Tree�����ݼ�\\When-Hierarchy-Meets��·������_cnode.txt";
	//edge �ļ�ÿ�� 4 �У�edge��� �� ֹ weight
	static String dir2 = "C:\\Users\\MSI\\Desktop\\ʦ�ֵ�\\date\\example_cedge.txt";
	//����Metis�������ļ�
	static String dir3 = "F:\\G��Tree�����ݼ�\\When-Hierarchy-Meets��·������_MetisInput.txt";
	static Set<Integer> V_Set = new HashSet<>();
	static Map<Integer, Pair> map = new HashMap<>();
	static Set<Vertex>[] g;//�ڽӱ�
	static int maxId = -1;
	static Set<Integer> V = new HashSet<>();//������
	static int E = 0;//����
//	static int WEIGHT_INFLATE_FACTOR = 100000;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		//generate_cnode();
		generate_cedge();
		//data_transform();
	}
	//��ȡ·�����㼯������
	public static void get_road_info() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String s = null;
		while ((s = br.readLine()) != null) {
			E++;
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			maxId = Math.max(maxId, a);
			maxId = Math.max(maxId, b);
			V.add(a);
			V.add(b);
		}
	}
	//��·������תΪ����Metis�ĺϷ������ʽ
	@SuppressWarnings("unchecked")
	private static void data_transform() throws NumberFormatException, IOException {
		get_road_info();
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		g = new Set[maxId + 1];
		for (int i = 0; i <= maxId; i++) {
			g[i] = new HashSet<>();
		}
		String s = null;
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			double c = Double.parseDouble(str[2]);
			Vertex a_nb = new Vertex(b, c);//a_nb:a���ھ�
			g[a].add(a_nb);
		}
		br.close();
		
		FileOutputStream fout = new FileOutputStream(dir3);
		PrintWriter pw = new PrintWriter(fout);
		StringBuilder sb1 = new StringBuilder();
		sb1.append(V.size()).append(" ").append(E).append(" ").append("001");
		pw.write(sb1.toString());
		pw.write("\n");
		for (int i = 1; i <= maxId; i++) {
			if (g[i].size() == 0) continue;
			StringBuilder sb = new StringBuilder();
			for (Vertex nb : g[i]) {
				//�����Ҫ�ĳ�id��0��ʼ�ģ��Ǿ�nb.id-1
				sb.append(nb.id - 1).append(" ").append(nb.w).append(" ");
			}
			String str = sb.toString().trim();//ȥ�����Ŀո�
			pw.write(str);
			pw.write("\n");
		}
		pw.flush();
		pw.close();
	}

	private static void generate_cedge() throws NumberFormatException, IOException {
		//����
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		//���
		FileOutputStream fout = new FileOutputStream(dir2);
		PrintWriter pw = new PrintWriter(fout);
	
		Map<Pair, Double> e_map = new HashMap<>();
		//��ȡ��һ��ͨͼ��·��
		String s = null;
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			double c = Double.parseDouble(str[2]);
			Pair p = new Pair(a, b);//��㡢�յ�
			e_map.put(p, c);
		}
		Set<Pair> keySet = e_map.keySet();
		int e_num = 1;//�ߵı��
		for (Pair p : keySet) {
			Double w = e_map.get(p);
			StringBuilder sb = new StringBuilder();
			sb.append(e_num).append(" ").append((int)p.x).append(" ").append((int)p.y).append(" ").append(w);
			pw.write(sb.toString());
			pw.write("\n");
			e_num++;
		}
		pw.flush();
		pw.close();
	}

	public static void generate_cnode () throws NumberFormatException, IOException {
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		//��ȡ��һ��ͨͼ��·��
		String s = null;
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			V_Set.add(a);
			V_Set.add(b);
		}
		
		//��ȡԭʼ���ݵ�����<����,��γ��>
		FileInputStream fis2 = new FileInputStream(filePath2);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
		String s2 = null;
		s2 = br2.readLine();
		while ((s2 = br2.readLine()) != null) {
			String[] str = s2.split(",");
			double x = Double.parseDouble(str[0]);
			double y = Double.parseDouble(str[1]);
			int v = Integer.parseInt(str[2]);
			map.put(v, new Pair(x, y));
		}
		//����V_Set������д�����ļ���
		FileOutputStream fout = new FileOutputStream(dir1);
		PrintWriter pw = new PrintWriter(fout);
		for (int v : V_Set) {
			Pair pair = map.get(v);
			StringBuilder sb = new StringBuilder();
			sb.append(v).append(" ").append(pair.x).append(" ").append(pair.y);
			pw.write(sb.toString());
			pw.write("\n");
			
		}
		pw.flush();
		pw.close();
	}
}
class Pair {
	double x;
	double y;
	
	public Pair(double x, double y) {
		this.x = x;
		this.y = y;
	}
	@Override    
	public int hashCode() {
		return (int)this.x + (int)this.y;
	}
	@Override
	public boolean equals(Object s) {//������Object��
		Pair ss = (Pair)s;//תΪStu��
		return this.x == ss.y && this.y == ss.x;
	}

}