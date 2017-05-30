package edu.buffalo.fractal;

import javax.swing.SwingWorker;

public abstract class ComputeFractal extends SwingWorker<WorkerResult, Void> {

  /**
   * Create an image from the Burning ship class of fractals.
   *
   * @author Matthew Hertz
   */
  private static class BurningShip extends ComputeFractal {

    public BurningShip(int threadNum, double initX, double initY, double endX, double endY) {
      super(threadNum, initX, initY, endX, endY);

    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextX(double, double, double, double)
     */
    @Override
    public double computeNextX(double x, double y, double x0, double y0) {
      return ((x * x) - (y * y)) + x0;
    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextY(double, double, double, double)
     */
    @Override
    public double computeNextY(double x, double y, double x0, double y0) {
      return Math.abs(2 * x * y) + y0;
    }
  }

  /**
   * Create an image from the Julia set.
   *
   * @author Matthew Hertz
   */
  private static class JuliaSet extends ComputeFractal {
    private double xOffset;

    private double yOffset;

    public JuliaSet(int threadNum, double cx, double cy, double initX, double initY, double endX, double endY) {
      super(threadNum, initX, initY, endX, endY);
      xOffset = cx;
      yOffset = cy;

    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextX(double, double, double, double)
     */
    @Override
    public double computeNextX(double x, double y, double x0, double y0) {
      return ((x * x) - (y * y)) + xOffset;
    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextY(double, double, double, double)
     */
    @Override
    public double computeNextY(double x, double y, double x0, double y0) {
      return (2 * x * y) + yOffset;
    }
  }

  /**
   * Create an image from the Mandelbrot set.
   *
   * @author Matthew Hertz
   */
  private static class MandlebrotSet extends ComputeFractal {
    public MandlebrotSet(int threadNum, double initX, double initY, double endX, double endY) {

      super(threadNum, initX, initY, endX, endY);

    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextX(double, double, double, double)
     */
    @Override
    public double computeNextX(double x, double y, double x0, double y0) {
      return ((x * x) - (y * y)) + x0;
    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextY(double, double, double, double)
     */
    @Override
    public double computeNextY(double x, double y, double x0, double y0) {
      return (2 * x * y) + y0;
    }

  }

  /**
   * Create an image from the Multibrot set.
   *
   * @author Matthew Hertz
   */
  private static class Multibrot extends ComputeFractal {

    public Multibrot(int threadNum, double initX, double initY, double endX, double endY) {
      super(threadNum, initX, initY, endX, endY);

    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextX(double, double, double, double)
     */
    @Override
    public double computeNextX(double x, double y, double x0, double y0) {
      return ((x * x * x) - (3 * x * y * y)) + x0;
    }

    /*
     * (non-Javadoc)
     * @see edu.canisius.graphics.ComputeFractal#computeNextY(double, double, double, double)
     */
    @Override
    public double computeNextY(double x, double y, double x0, double y0) {
      return ((3 * x * x * y) - (y * y * y)) + y0;
    }

  }

  /**
   *
   */

  private double startX;

  private double startY;

  private static int numColumns = 2048;

  private static int numRows = 2048;

  private double endX;

  private double endY;

  private int threadNum;

  private static int numProcesses;

  private static int maxIterations = 255;

  private static int escapeValue = 2;

  private ComputeFractal(int threadIdx, double initX, double initY, double endX, double endY) {
    threadNum = threadIdx;
    startX = initX;
    startY = initY;
    this.endX = endX;
    this.endY = endY;
  }

  /**
   * @return the startX
   */
  public double getStartX() {
    return startX;
  }

  /**
   * @param startX the startX to set
   */
  public void setStartX(double startX) {
    this.startX = startX;
  }

  /**
   * @return the startY
   */
  public double getStartY() {
    return startY;
  }

  /**
   * @param startY the startY to set
   */
  public void setStartY(double startY) {
    this.startY = startY;
  }

  /**
   * @return the stepX
   */
  public double getEndX() {
    return endX;
  }

  /**
   * @param stepX the stepX to set
   */
  public void setEndX(double stepX) {
    endX = stepX;
  }

  /**
   * @return the stepY
   */
  public double getEndY() {
    return endY;
  }

  /**
   * @param endY the stepY to set
   */
  public void setEndY(double endY) {
    this.endY = endY;
  }

  /**
   * @param cols the width (in pixels) of the fractal we will generate
   */
  public static void setImageWidth(int cols) {
    numColumns = cols;
  }

  /**
   * @param rows the height (in pixels) of the fractal we will generate
   */
  public static void setImageHeight(int rows) {
    numRows = rows;
  }

  /**
   * @param threads the number of threads being used to generate the fractal
   */
  public static void setNumProcesses(int threads) {
    numProcesses = threads;
  }

  /**
  *
  */
  public static int getMaxIterations() {
    return maxIterations;
  }

  /**
   * @param maxIterations the maxIterations to set
   */
  public static void setMaxIterations(int maxIterations) {
    ComputeFractal.maxIterations = maxIterations;
  }

  /**
   *
   */
  public static int getEscapeValue() {
    return escapeValue;
  }

  /**
   * @param escapeValue the escapeValue to set
   */
  public static void setEscapeValue(int escapeValue) {
    ComputeFractal.escapeValue = escapeValue;
  }

  public static ComputeFractal getBurningShip(int threadNum, double initX, double initY, double endX, double endY) {
    return new BurningShip(threadNum, initX, initY, endX, endY);
  }

  public static ComputeFractal getJuliaSet(int threadNum, double initX, double initY, double endX, double endY) {

    return new JuliaSet(threadNum, -0.726895347709114071439, 0.188887129043845954792, initX, initY, endX, endY);
  }

  public static ComputeFractal getMandelbrotSet(int threadNum, double initX, double initY, double endX, double endY) {
    return new MandlebrotSet(threadNum, initX, initY, endX, endY);
  }

  public static ComputeFractal getMultibrotSet(int threadNum, double initX, double initY, double endX, double endY) {
    return new Multibrot(threadNum, initX, initY, endX, endY);

  }

  private double distance(double x, double y) {
    return Math.pow(x, 2) + Math.pow(y, 2);
  }

  @Override
  public WorkerResult doInBackground() {
    int height = numRows / numProcesses;
    final int[][] stepsToEscape = new int[height][numColumns];
    double stepX = ((endX - startX) / (numRows - 1));
    double stepY = ((endY - startY) / (stepsToEscape[0].length - 1));
    double x0 = startX + (stepX * threadNum * height);
    for (int[] element : stepsToEscape) {
      double y0 = startY;
      for (int j = 0; j < element.length; j++ ) {
        double x = x0;
        double y = y0;
        double dist = distance(x, y);
        while ((element[j] < maxIterations) && (Math.sqrt(dist) <= escapeValue)) {
          element[j] += 1;
          double xNext = computeNextX(x, y, x0, y0);
          double yNext = computeNextY(x, y, x0, y0);
          y = yNext;
          x = xNext;
          dist = distance(x, y);
        }
        y0 += stepY;
      }
      x0 += stepX;
    }
    WorkerResult myResult = new WorkerResult(threadNum * height, stepsToEscape);
    return myResult;
  }

  public abstract double computeNextX(double x, double y, double x0, double y0);

  public abstract double computeNextY(double x, double y, double x0, double y0);
}