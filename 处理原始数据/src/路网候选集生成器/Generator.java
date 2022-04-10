package 路网候选集生成器;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
/**
 * 路网数据集说明：
 * 第一列是 v1, 第二列是 v2，第三列是权重
 * 
 * 本文件使用说明：
 * 只需改动  path , dir , dir2 , p , queryNum 即可。
 * @author MSI
 *
 */
public class Generator {
	//路网路径
	static String path = "F:\\对比数据集\\Quanzhou.txt";//输入路径
	//候选集的目标路径
	static String dir = "F:\\对比数据集\\Quanzhou_Candidates.txt";//输出路径
	//查询点集合的目标路径
	static String dir2 = "F:\\对比数据集\\Quanzhou_queryPoins.txt";//输出路径
	static int N;//路网顶点总数
	static double p = 0.3;//候选集密度，选取N*p个顶点作为候选顶点
	static int queryNum = 1000;//查询点的数量
	static Set<Integer> M = new HashSet<>();
	static Set<Integer> V = new HashSet<>();//路网顶点集合
	static Set<Integer> QuerySet = new HashSet<>();
	
	//返回该路径文件所对应的路网顶点
	public static void get() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String s = null; 
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			V.add(a);
			V.add(b);
		}
		br.close();
	}
	//输出候选集的txt
	public static void generate_M() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(dir)) ;
		Random r = new Random();
		N = V.size();
		int num = (int) (N * p);//候选点数量
		int[] list = new int[N];//将顶点集合V转为数组
		int index = 0 ;
		for (int v : V) {
			list[index++] = v;
		}
		while (M.size() < num) {
			int i = r.nextInt(N - 1);//随机出一个下标i , i的范围是 [0, N-1]
			M.add(list[i]);
		}
		for (int n : M) {
			bw.write(String.valueOf(n));
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	
	//输出查询点集合的txt
	public static void generate_queryPoints() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(dir2)) ;
		Random r = new Random();
		N = V.size();
		int[] list = new int[N];//将顶点集合V转为数组
		int index = 0 ;
		for (int v : V) {
			list[index++] = v;
		}
		while (QuerySet.size() < queryNum) {
			int i = r.nextInt(N - 1);//随机出一个下标i , i的范围是 [0, N-1]
			QuerySet.add(list[i]);
		}
		for (int n : QuerySet) {
			bw.write(String.valueOf(n));
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		get();
		generate_M();
		generate_queryPoints();
	}
}
