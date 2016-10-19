import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DoubleList implements List<Double> {

	private final float[] data;
	
	public DoubleList(int dim) {
		if (dim < 0) throw new IllegalArgumentException("dim is < 0");
		data = new float[dim];
	}
	
	public DoubleList(float[] data) {
		this.data = data;
	}
	
	public float[] asFloatArray() {
		return data;
	}
	
	@Override
	public Double set(int arg0, Double arg1) {
		float ret = data[arg0];
		data[arg0] = (float)arg1.doubleValue();
		return (double)ret;
	}

	@Override
	public int size() {
		return data.length;
	}

	@Override
	public Double get(int arg0) {
		return (double)data[arg0];
	}

	@Override
	public boolean isEmpty() {
		return data.length == 0;
	}

	private class DoubleListIterator implements Iterator<Double> {
		int i = 0;
		@Override
		public boolean hasNext() {
			return i != data.length;
		}
		@Override
		public Double next() {
			Double ret = (double)data[i];
			++i;
			return ret;
		}
	}
	
	@Override
	public Iterator<Double> iterator() {
		return new DoubleListIterator();
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.0000000");
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (double d : this) {
			if (!first)
				sb.append(", ");
			sb.append(df.format(d));
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public boolean add(Double arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int arg0, Double arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Double> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends Double> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<Double> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<Double> listIterator(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Double remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Double> subList(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		throw new UnsupportedOperationException();
	}

}
