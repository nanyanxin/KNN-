package ����̾���;

/*
 * ·���ϵĶ�����
 */
public class Vertex {
	int id;
	int degree;
	double w;//Ȩ��
	//Set<Integer> neibs = new HashSet<>();//v.neibs��ʾv���ھ�id���ϣ������������degree��Ϣ	//ֱ����g[v.id]�ʹ�����v���ھ���
	int deleteOrder;//�ö����������
	
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
		return "v" + this.id + "  " + this.degree;
	}
}
