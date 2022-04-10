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
 * ���������ã��� csv �����е���ͨ��ͼʶ���������������������ͨ��ͼд��
 * һ�� txt �У���Ϊ���ĳ��������
 * 
 * ע�⣺ÿ�θ������ݼ���Ҫ�޸���������
 * ����·���� FilePath �е�·�������·���Ǳ���� dir
 * @author WeiFangLiang
 *
 */
public class Convertor {
	static Set<Vertex>[] g;//�ڽӱ��洢 csv ��������Ϣ
	static int N;//·��������
	static Map<Integer, Set<Integer>> map = new HashMap<>();//<����id,��ö�����ͨ�Ķ���(����������)>
	static boolean[] visited;
	static int maxV = 0;//���������ͨ��ͼ�Ķ���
	static int maxNum = 0;//�����ͨ��ͼ�Ķ�����
	static int cnt = 0;//��ͨ��ͼ������
	static String dir = "D:\\Τ����\\����ʵ�����ݼ�\\txt��\\Nanjing.txt";//���·��
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		String filePath = FilePath.path;
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		N = GetVertexNumber.get(filePath);
		String s = null;
		g = new Set[N + 1];//����һ�仰������ÿ�ε��� generate_g() ���ص� g �ĵ�ַ���ǲ�һ���ġ�
		visited = new boolean[N + 1];
		for (int i = 1; i <= N; i++) {
			g[i] = new HashSet<>();
		}
		//int index = 1;//g���������±�
		s = br.readLine();//�ȶ�����һ��
		while ((s = br.readLine()) != null) {
			String[] str = s.split(",");
			int a = Integer.parseInt(str[2]);
			int b = Integer.parseInt(str[3]);
			double c = Double.parseDouble(str[5]);
			Vertex a_nb = new Vertex(b, c);//a_nb:a���ھ�
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
		
		System.out.println("��ͨ��ͼ������" + cnt);
		
		Set<Integer> keySet = map.keySet();
		for (int key : keySet) {
			int curSize = map.get(key).size();
			if (curSize > maxNum) {
				maxNum = curSize;
				maxV = key;
			}
		}
//		System.out.println("�������ͨ��ͼ�Ķ���: v" + maxV);
		
		//��������ͨ��ͼ�����txt�ļ���.�����������������
		FileOutputStream fout = new FileOutputStream(dir);
		PrintWriter pw = new PrintWriter(fout);
		Set<Integer> maxGraph = map.get(maxV);
		for (int v : maxGraph) {
			Set<Vertex> neib = g[v];//v���ھ�
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

	private static void bfs(int v) {//�Ըö���Ϊ��㣬���й��ѣ�����������Щ������ͨ
		LinkedList<Integer> q = new LinkedList<>();
		q.add(v);
		//�������� set �Ļ�����Ƶ������ GC(��������)
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
