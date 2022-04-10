package G星Tree的数据预处理;

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
	static String filePath = "C:\\Users\\MSI\\Desktop\\师兄的\\date\\example.txt";
	static String filePath2 = "F:\\论文实验数据集\\Quanzhou_Edgelist.csv";
	//cnode文件每行三个数，分别为 ：顶点id、经度、纬度 。  一个空格的间隙
	static String dir1 = "F:\\G星Tree的数据集\\When-Hierarchy-Meets的路网数据_cnode.txt";
	//edge 文件每行 4 列：edge编号 起 止 weight
	static String dir2 = "C:\\Users\\MSI\\Desktop\\师兄的\\date\\example_cedge.txt";
	//生成Metis的输入文件
	static String dir3 = "F:\\G星Tree的数据集\\When-Hierarchy-Meets的路网数据_MetisInput.txt";
	static Set<Integer> V_Set = new HashSet<>();
	static Map<Integer, Pair> map = new HashMap<>();
	static Set<Vertex>[] g;//邻接表
	static int maxId = -1;
	static Set<Integer> V = new HashSet<>();//顶点数
	static int E = 0;//边数
//	static int WEIGHT_INFLATE_FACTOR = 100000;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		//generate_cnode();
		generate_cedge();
		//data_transform();
	}
	//获取路网顶点集，边数
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
	//将路网数据转为符合Metis的合法输入格式
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
			Vertex a_nb = new Vertex(b, c);//a_nb:a的邻居
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
				//如果需要改成id从0开始的，那就nb.id-1
				sb.append(nb.id - 1).append(" ").append(nb.w).append(" ");
			}
			String str = sb.toString().trim();//去掉最后的空格
			pw.write(str);
			pw.write("\n");
		}
		pw.flush();
		pw.close();
	}

	private static void generate_cedge() throws NumberFormatException, IOException {
		//读入
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		//输出
		FileOutputStream fout = new FileOutputStream(dir2);
		PrintWriter pw = new PrintWriter(fout);
	
		Map<Pair, Double> e_map = new HashMap<>();
		//读取单一连通图的路网
		String s = null;
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			double c = Double.parseDouble(str[2]);
			Pair p = new Pair(a, b);//起点、终点
			e_map.put(p, c);
		}
		Set<Pair> keySet = e_map.keySet();
		int e_num = 1;//边的编号
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
		//读取单一连通图的路网
		String s = null;
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			V_Set.add(a);
			V_Set.add(b);
		}
		
		//获取原始数据的所有<顶点,经纬度>
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
		//遍历V_Set，挨个写到新文件中
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
	public boolean equals(Object s) {//必须是Object类
		Pair ss = (Pair)s;//转为Stu类
		return this.x == ss.y && this.y == ss.x;
	}

}