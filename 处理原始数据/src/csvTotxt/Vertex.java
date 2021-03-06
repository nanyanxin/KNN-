package csvTotxt;

/*
 * 路网上的顶点类
 */
public class Vertex {
	public int id;
	public double w;//权重
	
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
		return "v" + this.id;
	}
}
