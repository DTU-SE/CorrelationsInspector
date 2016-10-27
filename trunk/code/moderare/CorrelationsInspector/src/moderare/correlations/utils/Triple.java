package moderare.correlations.utils;


/**
 * This class represents a triple of elements
 * 
 * @author Andrea Burattin
 */
public class Triple<F, S, T> {

	protected final F first;
	protected final S second;
	protected final T third;
	
	/**
	 * This constructor builds a new instance of the pair
	 * 
	 * @param first the first element of the pair
	 * @param second the second element of the pair
	 */
	public Triple(F first, S second, T third) {
		this.first = first;
		this.second = second;
		this.third=third;
	}
	
	/**
	 * This method returns the first element of the pair
	 * 
	 * @return the first element
	 */
	public F getFirst() {
		return first;
	}
	
	/**
	 * This method returns the second element of the pair
	 * 
	 * @return the second element
	 */
	public S getSecond() {
		return second;
	}
	
	/**
	 * This method returns the third element of the pair
	 * 
	 * @return the third element
	 */
	public T getThird() {
		return third;
	}

	/**
	 * An utility method for the comparison of two elements
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean equals(Object x, Object y) {
		return ((x == null) && (y == null)) || ((x != null) && x.equals(y));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) {
		return (other instanceof Triple) && equals(first, ((Triple<F, S, T>) other).first)
				&& equals(second, ((Triple<F, S, T>) other).second)
				&& equals(third, ((Triple<F, S, T>) other).third);
	}

	@Override
	public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^
        		(second == null ? 0 : second.hashCode()) ^
        		(third == null ? 0 : third.hashCode());
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
	}
}