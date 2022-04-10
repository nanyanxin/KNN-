package 求最短距离;

import java.util.Comparator;

public class VertexComparator implements Comparator<Vertex>{
	@Override
	public int compare(Vertex o1, Vertex o2) {
		return o1.degree - o2.degree;//按照度升序
	}
}
