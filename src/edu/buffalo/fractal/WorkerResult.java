package edu.buffalo.fractal;

/**
 * Instances of this class represent the results of a thread's escape time calculations. Each instance stores the 2-d
 * array of escape times as well as the index in the main fractal at which the data should be stored.
 *
 * @author Matthew Hertz
 */
public class WorkerResult {
  /** Index at which these escape times will need to be stored in the main fractal array. */
  private int firstRow;

  /** Escape times calculated by the thread and returned to the compute pool. */
  private int[][] data;

  /**
   * Create a new instance of these results. This will store the starting index for these results and the escape times
   * calculated for the associated pixels.
   *
   * @param start Index at which these results should appear in the actual fractal
   * @param results Escape times calculated for the rows starting at the specified index
   */
  public WorkerResult(int start, int[][] results) {
    firstRow = start;
    data = results;
  }

  /**
   * Return the row in the fractal where the results from this instance's data should be stored
   *
   * @return Index in the final escape time array at which to start storing these results.
   */
  public int getFractalStartRow() {
    return firstRow;
  }

  /**
   * Return the number of rows of data stored in this instance.
   *
   * @return Total number of 1-d arrays held by this instance.
   */
  public int getNumberRows() {
    return data.length;
  }

  /**
   * Get the specified row of data from the results stored in this instance.
   *
   * @param i Data row to retrieve
   * @return 1-d array of escape times held by this instance.
   */
  public int[] getRow(int i) {
    return data[i];
  }
}
