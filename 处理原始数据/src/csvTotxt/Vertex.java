package csvTotxt;

/*
 * ·���ϵĶ�����
 */
public class Vertex {
	public int id;
	public double w;//Ȩ��
	
	public Vertex(int id, double w) {
		this.id = id;
		this.w = w;
	}
	//�Զ������ط���:hashCode �� equals ������g[]��ȥ�ع��ܡ�
	@Override
	public int hashCode() {
		return this.id;
	}
	@Override
	public boolean equals(Object c) {
		Vertex v = (Vertex)c;
		return this.id == v.id;
	}
	//���� id, ��
	public String toString() {
		return "v" + this.id;
	}
}
