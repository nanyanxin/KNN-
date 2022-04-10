package myPaper;
import java.util.Comparator;

public class VertexComparator implements Comparator<Vertex>{
	@Override
	public int compare(Vertex o1, Vertex o2) {
		//���Ȱ���ƽ����
		if (o1.avg_degree != o2.avg_degree) {
			if (o1.avg_degree < o2.avg_degree) {					
				return -1;//������
			} else {
				return 1;
			}
			//�����ɾ���ö������Ҫ��ӵı���
		} else if (o1.needAddEdgeNum != o2.needAddEdgeNum) {
			return o1.needAddEdgeNum - o2.needAddEdgeNum;
		} else {//���Ű���id
			return o1.id - o2.id;//����id����
		}
	}
}
