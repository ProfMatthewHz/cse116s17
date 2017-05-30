/**
 * 
 */
package edu.buffalo.fractal;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * Class which exists to install semi-transparent buttons over a corner of the frame. This is intended to be used as the
 * &quot;glass pane&quot; for a JFrame.
 * 
 * @author Matthew Hertz
 */
public class OverlayPane extends JComponent {

  private enum Boxes {
    RESET, ESCAPE, ITERATION;
  }

  private int[][] boxDimensions;

  private boolean initialized;

  public OverlayPane() {
    super();
    initialized = false;
    boxDimensions = new int[Boxes.values().length][2];
  }

  public int getResetHeight() {
    return boxDimensions[Boxes.RESET.ordinal()][1] + 6;
  }

  public int getEscapeHeight() {
    return boxDimensions[Boxes.RESET.ordinal()][1] + boxDimensions[Boxes.ESCAPE.ordinal()][1] + 12;
  }

  public int getIterationHeight() {
    return boxDimensions[Boxes.RESET.ordinal()][1] + boxDimensions[Boxes.ESCAPE.ordinal()][1] +
           boxDimensions[Boxes.ITERATION.ordinal()][1] + 18;
  }

  @Override
  public boolean contains(int x, int y) {
    int curY = 2;
    for (int i = 0; i < boxDimensions.length; i++) {
      if ((x >= (getWidth() - (boxDimensions[i][0] + 4))) && (x < getWidth()) && (y >= curY) &&
          (y <= boxDimensions[i][1] + 2 + curY)) {
        return true;
      }
      curY += boxDimensions[i][1] + 8;
    }
    return false;
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (!initialized) {
      // Compute where the Reset button should be drawn.
      FontMetrics fontMetric = g.getFontMetrics();
      boxDimensions[Boxes.RESET.ordinal()][0] = fontMetric.stringWidth("Reset Zoom");
      boxDimensions[Boxes.RESET.ordinal()][1] = (int)fontMetric.getStringBounds("Reset", g).getHeight();

      boxDimensions[Boxes.ESCAPE.ordinal()][0] = fontMetric.stringWidth("Set Escape");
      boxDimensions[Boxes.ESCAPE.ordinal()][1] = (int)fontMetric.getStringBounds("Set Escape", g).getHeight();

      boxDimensions[Boxes.ITERATION.ordinal()][0] = fontMetric.stringWidth("Iterations");
      boxDimensions[Boxes.ITERATION.ordinal()][1] = (int)fontMetric.getStringBounds("Iterations", g).getHeight();
      initialized = true;
    }

    // Perform the typecast we need to use
    Graphics2D g2 = (Graphics2D)g;

    // Set that the component should make its drawings translucent.
    AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.5f);
    g2.setComposite(alpha);
    // Now draw the boxes for each of our components.
    g2.setColor(Color.RED);
    g2.fillRect(getWidth() - (boxDimensions[Boxes.RESET.ordinal()][0] + 4), 2, boxDimensions[Boxes.RESET.ordinal()][0] + 4,
                boxDimensions[Boxes.RESET.ordinal()][1] + 4);
    g2.fillRect(getWidth() - (boxDimensions[Boxes.ESCAPE.ordinal()][0] + 4), 8 + boxDimensions[Boxes.RESET.ordinal()][1],
                boxDimensions[Boxes.ESCAPE.ordinal()][0] + 4, boxDimensions[Boxes.ESCAPE.ordinal()][1] + 4);
    g2.fillRect(getWidth() - (boxDimensions[Boxes.ITERATION.ordinal()][0] + 4), 14 + boxDimensions[Boxes.ESCAPE.ordinal()][1] +
                                                                                boxDimensions[Boxes.RESET.ordinal()][1],
                boxDimensions[Boxes.ITERATION.ordinal()][0] + 4, boxDimensions[Boxes.ITERATION.ordinal()][1] + 4);

    g2.setComposite(AlphaComposite.SrcOver);
    g2.setColor(Color.BLACK);
    g2.drawString("Reset Zoom", getWidth() - (boxDimensions[Boxes.RESET.ordinal()][0] + 2),
                  boxDimensions[Boxes.RESET.ordinal()][1]);

    g2.drawString("Set Escape", getWidth() - (boxDimensions[Boxes.ESCAPE.ordinal()][0] + 2),
                  8 + boxDimensions[Boxes.RESET.ordinal()][1] + boxDimensions[Boxes.ESCAPE.ordinal()][1]);

    g2.drawString("Iterations", getWidth() - (boxDimensions[Boxes.ITERATION.ordinal()][0] + 2),
                  14 + boxDimensions[Boxes.RESET.ordinal()][1] + boxDimensions[Boxes.ESCAPE.ordinal()][1] +
                      boxDimensions[Boxes.ITERATION.ordinal()][1]);
  }
}
