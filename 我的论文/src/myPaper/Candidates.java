package myPaper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Candidates {
	static String path = "E:\\韦方良\\对比数据集\\Bangkok_Candidates.txt";
	
	static Set<Integer> get_Candidates() throws NumberFormatException, IOException {
		Set<Integer> M = new HashSet<>();
		FileInputStream fis = new FileInputStream(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String s = null;
		while ((s = br.readLine()) != null) {
			int a = Integer.parseInt(s);
			M.add(a);
		}
		br.close();
		return M;
	}
}
