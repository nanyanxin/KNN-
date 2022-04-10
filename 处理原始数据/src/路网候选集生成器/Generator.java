package ·����ѡ��������;

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
 * ·�����ݼ�˵����
 * ��һ���� v1, �ڶ����� v2����������Ȩ��
 * 
 * ���ļ�ʹ��˵����
 * ֻ��Ķ�  path , dir , dir2 , p , queryNum ���ɡ�
 * @author MSI
 *
 */
public class Generator {
	//·��·��
	static String path = "F:\\�Ա����ݼ�\\Quanzhou.txt";//����·��
	//��ѡ����Ŀ��·��
	static String dir = "F:\\�Ա����ݼ�\\Quanzhou_Candidates.txt";//���·��
	//��ѯ�㼯�ϵ�Ŀ��·��
	static String dir2 = "F:\\�Ա����ݼ�\\Quanzhou_queryPoins.txt";//���·��
	static int N;//·����������
	static double p = 0.3;//��ѡ���ܶȣ�ѡȡN*p��������Ϊ��ѡ����
	static int queryNum = 1000;//��ѯ�������
	static Set<Integer> M = new HashSet<>();
	static Set<Integer> V = new HashSet<>();//·�����㼯��
	static Set<Integer> QuerySet = new HashSet<>();
	
	//���ظ�·���ļ�����Ӧ��·������
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
	//�����ѡ����txt
	public static void generate_M() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(dir)) ;
		Random r = new Random();
		N = V.size();
		int num = (int) (N * p);//��ѡ������
		int[] list = new int[N];//�����㼯��VתΪ����
		int index = 0 ;
		for (int v : V) {
			list[index++] = v;
		}
		while (M.size() < num) {
			int i = r.nextInt(N - 1);//�����һ���±�i , i�ķ�Χ�� [0, N-1]
			M.add(list[i]);
		}
		for (int n : M) {
			bw.write(String.valueOf(n));
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
	
	//�����ѯ�㼯�ϵ�txt
	public static void generate_queryPoints() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(dir2)) ;
		Random r = new Random();
		N = V.size();
		int[] list = new int[N];//�����㼯��VתΪ����
		int index = 0 ;
		for (int v : V) {
			list[index++] = v;
		}
		while (QuerySet.size() < queryNum) {
			int i = r.nextInt(N - 1);//�����һ���±�i , i�ķ�Χ�� [0, N-1]
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
