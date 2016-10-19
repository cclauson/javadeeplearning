
//actually don't do this, this will be enormous
//for the test cases that interest us
public class JBlas3DTensor implements Mutable3DTensor {

	private final int m;
	private final int n;
	private final int p;
	
	public JBlas3DTensor(int m, int n, int p) {
		this.m = m;
		this.n = n;
		this.p = p;
	}
	
	@Override
	public int getM() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getN() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getP() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEntryAt(int i, int j, int k) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterable<ThreeDTensorEntry> getNonzeroEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEntryAt(int i, int j, int k, double val) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
