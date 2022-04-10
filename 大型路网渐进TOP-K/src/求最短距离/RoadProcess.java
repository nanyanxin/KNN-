package 求最短距离;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 将原始的路网数据，处理成邻接表返回
 * @author MSI
 *
 */
public class RoadProcess {
	static Set<Vertex>[] g;//邻接表
	static int N;//路网顶点数
	static Map<Integer, Vertex> v_map = new HashMap<>();//<顶点id, 对应的顶点>
	
	@SuppressWarnings("unchecked")
	public static Set<Vertex>[] generate_g() throws IOException {
		String filePath = FilePath.path;
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		N = GetVertexNumber.get(filePath);
		String s = null;
		g = new Set[N + 1];//有这一句话，所以每次调用 generate_g() 返回的 g 的地址都是不一样的。
		for (int i = 1; i <= N; i++) {
			g[i] = new HashSet<>();
		}
		//int index = 1;//g的竖链的下标
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			double c = Double.parseDouble(str[2]);
			Vertex a_nb = new Vertex(b, c);//a_nb:a的邻居
			g[a].add(a_nb);
			Vertex b_nb = new Vertex(a, c);
			g[b].add(b_nb);
			v_map.put(b, a_nb);
			v_map.put(a, b_nb);
		}
		br.close();	
		return g;
	}
}
