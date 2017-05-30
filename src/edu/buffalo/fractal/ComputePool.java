package edu.buffalo.fractal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.SwingWorker;

/**
 * This class is used to manage sets of tasks that should be executed in parallel. In particular, it makes it much
 * simpler to perform a set of related operations.
 *
 * @author Matthew Hertz
 */
public class ComputePool implements PropertyChangeListener {

  /**
   * Panel used to display the fractal. This receives updates as soon as the workers complete.
   */
  private FractalPanel thePanel;

  /** This records the fractal data as it is completed by the worker threads. */
  private int[][] generatedFractal;

  /** Tasks submitted to the class to be executed as soon as possible. */
  private ArrayList<SwingWorker<WorkerResult, Void>> submittedTasks;

  /**
   * Create a new instance of this class that is ready to accept jobs to execute in parallel. This also takes in the
   * panel to be updated whenever we completely calculate a fractal.
   */
  public ComputePool() {
    // Create the space where we record all of the executing tasks.
    submittedTasks = new ArrayList<>();
  }

  /**
   * This methods allows the program to change the instance of FractalPanel which will be updated when a fractal
   * finishes generating.
   *
   * @param newPanel The new instance of FractalPanel used to display the fractal.
   */
  public void changePanel(FractalPanel newPanel) {
    thePanel = newPanel;
  }

  /**
   * Clear the pool of any requests in preparation for a new set of tasks to perform. This will stop performing any
   * on-going computations and cancel any requests for tasks that have not been completed.
   */
  public void clearPool() {
    for (Future<?> f : submittedTasks) {
      f.cancel(true);
    }
    submittedTasks.clear();
    generatedFractal = null;
  }

  /**
   * Add the given job to the end of the list of tasks that should be completed by this worker pool.
   *
   * @param instances Array of the SwingWorker instances that will actually generate the fractal. This adds information
   *          so that it can track each worker instance and then tells it to start executing. This insures we process
   *          the data in a thread-safe manner.
   */
  public void generateFractal(int rows, SwingWorker<WorkerResult, Void>[] instances) {
    generatedFractal = new int[rows][];
    for (SwingWorker<WorkerResult, Void> instance : instances) {
      submittedTasks.add(instance);
      instance.addPropertyChangeListener(this);
      instance.execute();
    }
  }

  /**
   * Triggered whenever a process completes its calculations. This records any results and, once all of the workers have
   * completed, updates the panel with the completed image.
   *
   * @param evt PropertyChangeEvent instance detailing what has just happened.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // The following typecast is safe because this class is listening to property changes from exactly one type of
    // object.
    SwingWorker<WorkerResult, Void> updated = (SwingWorker<WorkerResult, Void>) evt.getSource();
    if (updated.isDone() && !updated.isCancelled()) {
      // Remove the completed SwingWorker from the list of active workers.
      submittedTasks.remove(updated);
      try {
        WorkerResult val = updated.get();
        int rowStart = val.getFractalStartRow();
        for (int i = 0; i < val.getNumberRows(); i++ ) {
          generatedFractal[i + rowStart] = val.getRow(i);
        }
      } catch (InterruptedException e) {
        // This can never happen, but is needed for compilation.
      } catch (ExecutionException e) {
        System.err.println("Oops. There was an error in generating this fractal.");
        e.printStackTrace();
      }
      if (submittedTasks.isEmpty()) {
        thePanel.updateImage(generatedFractal);
      }
    }
  }
}
