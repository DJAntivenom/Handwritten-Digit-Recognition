package ch.elste.hdr.util;

/**
 * The NetworkUtils class consists of helper methods.
 * 
 * @author Dillon
 *
 */
public class NetworkUtils {
	/**
	 * This is set to true, when the play button was pressed.
	 */
	public static volatile boolean ANIMATED;

	/**
	 * This is set to true for one learning iteration, when the next or previous
	 * button is pressed.
	 */
	public static volatile boolean ANIMATED_ONCE;

	/**
	 * Set to true, if the network was active since last save.
	 */
	public static volatile boolean CHANGED;

	/**
	 * This method converts a two dimensional integer array into a one dimensional
	 * double array.
	 * 
	 * @param array
	 *            the 2D integer array
	 * @return the double array
	 */
	public static double[] to1DArray(final int[][] array) {
		double[] temp = new double[array.length * array[0].length];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				temp[i * array[i].length + j] = array[i][j] + 0d;
			}
		}

		return temp;
	}

	/**
	 * This method returns the index of the biggest value in the given array.
	 * 
	 * @param array
	 *            the array to check
	 * @return the index of the biggest value in the given array
	 */
	public static double getGuessedValue(final double[] array) {
		double temp = -1;
		int index = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > temp) {
				index = i;
				temp = array[i];
			}
		}

		return index;
	}

	/**
	 * This method returns an array of length 10 consisting of doubles, all of which
	 * are initialized as 0 except the value at position <code>index</code>, which
	 * is initialized as 1.
	 * 
	 * @param index
	 *            the position in the array to be set to 1
	 * @return a double array
	 */
	public static double[] toSolutionArray(final int index) {
		double[] temp = new double[10];
		temp[index] = 1d;
		return temp;
	}
	
	/**
	 * Calculates the average value of a double array.
	 * 
	 * @param d
	 *            the double array to be averaged
	 * @return the average as a double
	 */
	public static double getAverageValue(final double[] d) {
		double avg = 0d;
		for (int i = 0; i < d.length; i++) {
			avg += d[i];
		}
		avg /= d.length;

		return avg;
	}
}
