package myPaper;
import java.util.Comparator;

public class VertexComparator implements Comparator<Vertex>{
	@Override
	public int compare(Vertex o1, Vertex o2) {
		//优先按照平均度
		if (o1.avg_degree != o2.avg_degree) {
			if (o1.avg_degree < o2.avg_degree) {					
				return -1;//不交换
			} else {
				return 1;
			}
			//其次是删掉该顶点后需要添加的边数
		} else if (o1.needAddEdgeNum != o2.needAddEdgeNum) {
			return o1.needAddEdgeNum - o2.needAddEdgeNum;
		} else {//最后才按照id
			return o1.id - o2.id;//按照id升序
		}
	}
}
