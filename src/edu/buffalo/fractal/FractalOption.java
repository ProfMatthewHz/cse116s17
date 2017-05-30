package edu.buffalo.fractal;

enum FractalOption {
  Mandelbrot(-2.15, -1.3, 0.6, 1.3), BurningShip(-1.8, -0.08, -1.7, 0.025), JuliaSet(-1.7, -1, 1.7, 1),
  Multibrot(-1, -1.3, 1, 1.3);

  private double startX, startY, endX, endY;

  private FractalOption(double startX, double startY, double endX, double endY) {
    this.startX = startX;
    this.startY = startY;
    this.endX = endX;
    this.endY = endY;
  }

  public void setStartX(double startX) {
    this.startX = startX;
  }

  public void setStartY(double startY) {
    this.startY = startY;
  }

  public void setEndX(double endX) {
    this.endX = endX;
  }

  public void setEndY(double endY) {
    this.endY = endY;
  }

  public double getStartX() {
    return startX;
  }

  public double getStartY() {
    return startY;
  }

  public double getEndX() {
    return endX;
  }

  public double getEndY() {
    return endY;
  }
}