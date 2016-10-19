import java.util.List;

public class DataItem {
	public final List<Double> x;
	public final List<Double> y;
	
	public DataItem(List<Double> x, List<Double> y) {
		if (x == null) throw new IllegalArgumentException("x is null");
		if (y == null) throw new IllegalArgumentException("y is null");
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return x + "\n" + y;
	}
	
}
