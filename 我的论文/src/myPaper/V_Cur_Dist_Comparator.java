package myPaper;
import java.util.Comparator;

public class V_Cur_Dist_Comparator implements Comparator<Vertex>{
	@Override
	public int compare(Vertex o1, Vertex o2) {
		if (o1.cur_dist - o2.cur_dist <= 0) {
			return -1;
		} else {
			return 1;
		}
	}
}