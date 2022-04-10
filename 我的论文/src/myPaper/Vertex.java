package myPaper;
/*
 * 路网上的顶点类
 */
public class Vertex {
	int id;
	//int degree;
	//Set<Integer> neibs = new HashSet<>();//v.neibs表示v的邻居id集合，包含了上面的degree信息	//直接用g[v.id]就代表了v的邻居们
	double w;//权重
	boolean isCandidate = false;//是否为候选点
	double avg_degree;//权重/度
	int needAddEdgeNum;//删掉该顶点后需要添加的边数
	int deleteOrder;//该顶点的消点序
	double cur_dist = -1.0;
	
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
	//按照 id, 平均度, 删掉之后需要添加的边数
	public String toString() {
		return "v" + this.id;
	}
}
