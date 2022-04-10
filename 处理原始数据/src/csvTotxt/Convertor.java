package csvTotxt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * 这个类的作用：将 csv 中所有的连通子图识别出来，并将其中最大的连通子图写到
 * 一个 txt 中，作为论文程序的输入
 * 
 * 注意：每次更换数据集都要修改输入和输出
 * 输入路径是 FilePath 中的路径，输出路径是本类的 dir
 * @author WeiFangLiang
 *
 */
public class Convertor {
	static Set<Vertex>[] g;//邻接表，存储 csv 中所有信息
	static int N;//路网顶点数
	static Map<Integer, Set<Integer>> map = new HashMap<>();//<顶点id,与该顶点连通的顶点(包含其自身)>
	static boolean[] visited;
	static int maxV = 0;//具有最大连通子图的顶点
	static int maxNum = 0;//最大连通子图的顶点数
	static int cnt = 0;//连通子图的数量
	static String dir = "D:\\韦方良\\论文实验数据集\\txt版\\Nanjing.txt";//输出路径
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		String filePath = FilePath.path;
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		N = GetVertexNumber.get(filePath);
		String s = null;
		g = new Set[N + 1];//有这一句话，所以每次调用 generate_g() 返回的 g 的地址都是不一样的。
		visited = new boolean[N + 1];
		for (int i = 1; i <= N; i++) {
			g[i] = new HashSet<>();
		}
		//int index = 1;//g的竖链的下标
		s = br.readLine();//先读掉第一行
		while ((s = br.readLine()) != null) {
			String[] str = s.split(",");
			int a = Integer.parseInt(str[2]);
			int b = Integer.parseInt(str[3]);
			double c = Double.parseDouble(str[5]);
			Vertex a_nb = new Vertex(b, c);//a_nb:a的邻居
			g[a].add(a_nb);
		}
		br.close();
		
		for (int i = 1; i <= N; i++) {
			if (g[i].size() == 0) {
				continue;
			}
			if (!visited[i]) {
				bfs(i);
				cnt++;
			}
		}
		
		System.out.println("连通子图数量：" + cnt);
		
		Set<Integer> keySet = map.keySet();
		for (int key : keySet) {
			int curSize = map.get(key).size();
			if (curSize > maxNum) {
				maxNum = curSize;
				maxV = key;
			}
		}
//		System.out.println("身处最大连通子图的顶点: v" + maxV);
		
		//把最大的连通子图输出到txt文件中.。。。。。改这个！
		FileOutputStream fout = new FileOutputStream(dir);
		PrintWriter pw = new PrintWriter(fout);
		Set<Integer> maxGraph = map.get(maxV);
		for (int v : maxGraph) {
			Set<Vertex> neib = g[v];//v的邻居
			for (Vertex eachNeib : neib) {
				StringBuilder sb = new StringBuilder();
				sb.append(v).append(" ").append(eachNeib.id).append(" ").append(eachNeib.w);
				pw.write(sb.toString());
				pw.write("\n");
			}
		}
		pw.flush();
		pw.close();
	}

	private static void bfs(int v) {//以该顶点为起点，进行广搜，看看它和哪些顶点连通
		LinkedList<Integer> q = new LinkedList<>();
		q.add(v);
		//在这申明 set 的话，会频繁进行 GC(垃圾回收)
		Set<Integer> set = new HashSet<>();
		while (!q.isEmpty()) {
			int t = q.poll();
			visited[t] = true;
			set.add(t);
			for (Vertex ver : g[t]) {
				if (!visited[ver.id]) {
					q.add(ver.id);
				}
			}
		}
		map.put(v, set);
	}
}
