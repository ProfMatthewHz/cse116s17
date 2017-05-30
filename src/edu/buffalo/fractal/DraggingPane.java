package edu.buffalo.fractal;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/**
 * Pane used by the user to select a region of the fractal to be zoomed in on. To limit the need to disrupt other areas
 * of the program, the selection rectangle will be drawn in this pane.
 *
 * @author Matthew Hertz
 */
@SuppressWarnings("serial")
public class DraggingPane extends JComponent implements MouseInputListener {

  /** GUI frame in which this panel will be added. */
  private FractalFrame daddy;

  /** Records if the mouse is currently being dragged in the pane. */
  private boolean inDrag = false;

  /** Location on the pane where the mouse started to be dragged. */
  private Point start;

  /**
   * Location on the pane where the mouse is currently located while it is being dragged. This is used to draw the
   * appropriate selection box.
   */
  private Point current;

  /**
   * Creates a new pane with the given parent JFrame.
   *
   * @param parent The JFrame in which this pane will be placed.
   */
  public DraggingPane(FractalFrame parent) {
    daddy = parent;
  }

  /**
   * Called when the user start the zoom region selection process; this records the point on the screen where the
   * dragging started
   *
   * @param e Instance describing the mouse being clicked.
   */
  public void mousePressed(MouseEvent e) {
    start = e.getLocationOnScreen();
    SwingUtilities.convertPointFromScreen(start, this);
  }

  /**
   * Called when the user releases the mouse button. This checks that the user has properly selected a region in which
   * to zoom. When this has happened, the region in which to zoom will be calculated and the fractal regenerated.
   *
   * @param e Instance describing the mouse being released.
   */
  public void mouseReleased(MouseEvent e) {
    inDrag = false;
    FractalOption active = daddy.getActive();
    if ((active != null) && (start != null) && (current != null)) {
      double init = active.getStartX();
      double end = active.getEndX();
      double newStart;
      double newEnd;
      if (start.x < current.x) {
        newStart = init + (((end - init) / getWidth()) * start.x);
        newEnd = init + (((end - init) / getWidth()) * current.x);
      } else {
        newStart = init + (((end - init) / getWidth()) * current.x);
        newEnd = init + (((end - init) / getWidth()) * start.x);
      }

      active.setStartX(newStart);
      active.setEndX(newEnd);
      init = active.getStartY();
      end = active.getEndY();
      if (start.y < current.y) {
        newStart = init + (((end - init) / getHeight()) * start.y);
        newEnd = init + (((end - init) / getHeight()) * current.y);
      } else {
        newStart = init + (((end - init) / getHeight()) * current.y);
        newEnd = init + (((end - init) / getHeight()) * start.y);
      }
      active.setStartY(newStart);
      active.setEndY(newEnd);
      daddy.switchTask(active);
    }

    repaint();
    start = null;
    current = null;

  }

  /**
   * Called when the user drags the mouse while the button is pressed. This will calculate the current location of the
   * cursor in the pane.
   *
   * @param e Instance describing the mouse being dragged.
   */

  public void mouseDragged(MouseEvent e) {
    inDrag = true;
    current = e.getLocationOnScreen();
    SwingUtilities.convertPointFromScreen(current, this);
    if (current.x < 0) {
      current.x = 0;
    } else if (current.x >= getWidth()) {
      current.x = getWidth() - 1;
    }
    if (current.y < 0) {
      current.y = 0;
    } else if (current.y >= getHeight()) {
      current.y = getHeight() - 1;
    }
    repaint();
  }

  /**
   * When a region is being selected, this draws the selection box in the current pane. Otherwise, this is the only part
   * of the pane which is not transparent.
   */
  @Override
  protected void paintComponent(Graphics g) {
    if (inDrag) {
      // Get the area to be repainted
      Rectangle clipped = g.getClipBounds();

      // Set that the component should make its drawings invisible.
      AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.0f);
      Graphics2D g2 = (Graphics2D) g;
      g2.setComposite(alpha);
      g2.fillRect(clipped.x, clipped.y, clipped.width, clipped.height);

      // Now draw the dashed selection box
      g2.setComposite(AlphaComposite.SrcOver);
      g2.setColor(Color.BLACK);
      int x1, y1, width, height;
      if (start.x < current.x) {
        x1 = start.x;
        width = current.x - start.x;
      } else {
        x1 = current.x;
        width = start.x - current.x;
      }
      if (start.y < current.y) {
        y1 = start.y;
        height = current.y - start.y;
      } else {
        y1 = current.y;
        height = start.y - current.y;
      }
      g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 2f }, 0f));
      g2.drawRect(x1, y1, width, height);
    }
  }

  public void mouseClicked(MouseEvent arg0) {
    // Do nothing
  }

  public void mouseEntered(MouseEvent arg0) {
    // Do nothing
  }

  public void mouseExited(MouseEvent arg0) {
    // Do nothing

  }

  public void mouseMoved(MouseEvent arg0) {
    // Do nothing
  }
}
