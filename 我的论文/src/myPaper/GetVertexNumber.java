package myPaper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetVertexNumber {
	static int maxId = 0;
	
	//返回该路径文件所对应的路网顶点最大id
	public static int get(String path) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String s = null; 
		while ((s = br.readLine()) != null) {
			String[] str = s.split(" ");
			int a = Integer.parseInt(str[0]);
			int b = Integer.parseInt(str[1]);
			maxId = Math.max(maxId, a);
			maxId = Math.max(maxId, b);
		}
		br.close();
		return maxId;
	}
}
