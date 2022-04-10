package csvTotxt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class GetVertexNumber {
	static int maxId = 0;
	static Set<Integer> V = new HashSet<>();
	static Set<Integer> E = new HashSet<>();
	
	//返回该路径文件所对应的路网顶点最大id
	public static int get(String path) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String s = null; 
	//	s = br.readLine();//原始数据第一行不是数字
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
		//	int c = Integer.parseInt(str[4]);
		//	V.add(a);
		//	V.add(b);
		//	E.add(c);
			maxId = Math.max(maxId, a);
			maxId = Math.max(maxId, b);
		}
		br.close();
		return maxId;
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		String file = "F:\\论文实验数据集\\Paris_Edgelist.csv";
		get(file);
		System.out.println("顶点数:" + V.size());
		System.out.println("边数:" + E.size());
	}
}
