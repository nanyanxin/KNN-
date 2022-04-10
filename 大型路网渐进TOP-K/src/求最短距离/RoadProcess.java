package ����̾���;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ��ԭʼ��·�����ݣ�������ڽӱ���
 * @author MSI
 *
 */
public class RoadProcess {
	static Set<Vertex>[] g;//�ڽӱ�
	static int N;//·��������
	static Map<Integer, Vertex> v_map = new HashMap<>();//<����id, ��Ӧ�Ķ���>
	
	@SuppressWarnings("unchecked")
	public static Set<Vertex>[] generate_g() throws IOException {
		String filePath = FilePath.path;
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		N = GetVertexNumber.get(filePath);
		String s = null;
		g = new Set[N + 1];//����һ�仰������ÿ�ε��� generate_g() ���ص� g �ĵ�ַ���ǲ�һ���ġ�
		for (int i = 1; i <= N; i++) {
			g[i] = new HashSet<>();
		}
		//int index = 1;//g���������±�
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			double c = Double.parseDouble(str[2]);
			Vertex a_nb = new Vertex(b, c);//a_nb:a���ھ�
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
