package ����·��;

/*
 * ·���ϵĶ�����
 */
public class Vertex {
	int id;
	int degree;
	double w;//Ȩ��
	int deleteOrder;//�ö����������
	double cur_dist = -1.0;
	
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
