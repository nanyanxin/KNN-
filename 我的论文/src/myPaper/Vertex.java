package myPaper;
/*
 * ·���ϵĶ�����
 */
public class Vertex {
	int id;
	//int degree;
	//Set<Integer> neibs = new HashSet<>();//v.neibs��ʾv���ھ�id���ϣ������������degree��Ϣ	//ֱ����g[v.id]�ʹ�����v���ھ���
	double w;//Ȩ��
	boolean isCandidate = false;//�Ƿ�Ϊ��ѡ��
	double avg_degree;//Ȩ��/��
	int needAddEdgeNum;//ɾ���ö������Ҫ��ӵı���
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
	//���� id, ƽ����, ɾ��֮����Ҫ��ӵı���
	public String toString() {
		return "v" + this.id;
	}
}
