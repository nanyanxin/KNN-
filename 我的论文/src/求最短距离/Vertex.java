package 求最短距离;

/*
 * 路网上的顶点类
 */
public class Vertex {
	int id;
	int degree;
	double w;//权重
	//Set<Integer> neibs = new HashSet<>();//v.neibs表示v的邻居id集合，包含了上面的degree信息	//直接用g[v.id]就代表了v的邻居们
	int deleteOrder;//该顶点的消点序
	
	public Vertex(int id, double w) {
		this.id = id;
		this.w = w;
	}
	//自定义判重方法:hashCode 和 equals 决定了g[]的去重功能。
	@Override
	public int hashCode() {
		return this.id;
	}
	@Override
	public boolean equals(Object c) {
		Vertex v = (Vertex)c;
		return this.id == v.id;
	}
	//按照 id, 度
	public String toString() {
		return "v" + this.id + "  " + this.degree;
	}
}
