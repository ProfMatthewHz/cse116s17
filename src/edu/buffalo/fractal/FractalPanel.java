package edu.buffalo.fractal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Class that displays (and optionally saves) a generated image. The image is constructed in a
 * &quot;color-by-number&quot; style -- it is specified as a series of integers. These integers are then mapped to a
 * color using an {@link IndexColorModel}. These images are therefore easy to recolor and will automatically fit the
 * entire image into the panel (and so are easy to resize).
 *
 * @author Matthew Hertz
 */
@SuppressWarnings("serial")
public class FractalPanel extends JPanel {
  /** Enumeration of the file formats that we can use to save a fractal image. */
  public enum SaveFormat {
    /**
     * Save the file as a &quot;.gif&quot; file. This provides translucency and could be used for animation, but we only
     * support 256 colors.
     */
    GIF,
    /**
     * Save the file as a &quot;.png&quot; file. This is a good, detailed image format that can be read by most, but not
     * all, web browsers.
     */
    PNG,
    /**
     * Save the file as a &quot;jpg&quot; file which uses good, but lossy, compression. Its compression makes saving
     * many files easier, but cannot by used for applications where details are critical.
     */
    JPG;
  }

  /** Image size to which we will default. */
  private static final Dimension DEFAULT_DIMENSION = new Dimension(512, 512);

  /** Store the model which specifies how each value will be colored. */
  private IndexColorModel colorModel;

  /** Actual image for which we are providing a facade. */
  private BufferedImage fractal;

  /** Create a new, blank image which uses the default size &amp; color scheme. */
  public FractalPanel() {
    this(DEFAULT_DIMENSION, getDefaultColorModel());
  }

  /**
   * Create a new, blank image with the given size and given color scheme.
   *
   * @param d Size for this image.
   * @param cMod Color scheme initially used to color in the image specified by this program.
   */
  public FractalPanel(Dimension d, IndexColorModel cMod) {
    super(null);
    setSize(d);
    colorModel = cMod;
  }

  @Override
  public void setSize(Dimension d) {
    super.setSize(d);
    setPreferredSize(d);
    fractal = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
    fractal.setAccelerationPriority(0.7f);
  }

  /**
   * Create the default color model we will use.
   *
   * @return Default color model used to color our pictures.
   */
  private static IndexColorModel getDefaultColorModel() {
    byte[] reds = new byte[101];
    byte[] greens = new byte[101];
    byte[] blues = new byte[101];
    for (int i = 0; i < (reds.length - 1); i++ ) {
      int rgb = Color.HSBtoRGB(i / 100f, 0.6F, 1);
      reds[i] = (byte) ((rgb & 0xFF0000) >> 16);
      greens[i] = (byte) ((rgb & 0xFF00) >> 8);
      blues[i] = (byte) (rgb & 0xFF);
    }
    IndexColorModel retVal = new IndexColorModel(8, 101, reds, greens, blues);
    return retVal;
  }

  /**
   * Save a copy of the current image to disk. The caller must select a supported format and provide the filename to use
   * (do not specify an extension -- this will be done automatically). This method overwrites any data that previously
   * existed in that file. Upon completion, the method returns whether it was successful. This method will also print
   * out a message if an error occurs.
   *
   * @param format Format in which the file should be saved.
   * @param fileName Full name, including path information but no extension, of the file to which the image should be
   *          saved.
   * @return True if the image was saved successfully; false otherwise.
   */
  public boolean saveImage(SaveFormat format, String fileName) {
    String extension = format.name().toLowerCase();
    File outputFile = new File(fileName + "." + extension);
    try {
      ImageIO.write(fractal, extension, outputFile);
      return true;
    } catch (IOException e) {
      System.err.println("ERROR: Could not output fractal image");
      e.printStackTrace();
      System.err.println();
      return false;
    }
  }

  @Override
  public void paint(Graphics g) {
    if ((fractal.getWidth() != getWidth()) || (fractal.getHeight() != getHeight())) {
      Image drawMe = fractal.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
      g.drawImage(drawMe, 0, 0, Color.LIGHT_GRAY, null);
    } else {
      g.drawImage(fractal, 0, 0, Color.LIGHT_GRAY, null);
    }
  }

  /**
   * Set the color model used to render the fractal. The programmer is responsible for making sure that the model has as
   * many slots available as are used to detect whether a pixel &quot;escapes&quot;.
   *
   * @param newModel Color model with which we will render the fractal.
   */
  public void setIndexColorModel(IndexColorModel newModel) {
    colorModel = newModel;
  }

  /**
   * Get the current width of the component in which the image is displayed.
   *
   * @return Actual width, in pixels, of the component displaying the image.
   */
  public int getImageWidth() {
    // return fractal.getWidth();
    int wide = getWidth();
    return Math.max(wide, DEFAULT_DIMENSION.width);
  }

  /**
   * Get the current height of the component in which the image is displayed.
   *
   * @return Actual height, in pixels, of the component displaying the image.
   */
  public int getImageHeight() {
    // return fractal.getHeight();
    int high = getHeight();
    return Math.max(high, DEFAULT_DIMENSION.height);
  }

  /**
   * Update the image to display the given data. This method will automatically scale the data up to match the image
   * size.
   *
   * @param escapeSteps Array showing how many steps transpired before a pixel escaped. These values are used to color
   *          the pixels.
   */
  public void updateImage(int[][] escapeSteps) {
    if ((fractal.getWidth() < escapeSteps.length) || (fractal.getHeight() < escapeSteps[0].length)) {
      fractal = new BufferedImage(escapeSteps.length, escapeSteps[0].length, BufferedImage.TYPE_INT_RGB);
    }
    int repeatX = fractal.getWidth() / escapeSteps.length;
    int repeatY = fractal.getHeight() / escapeSteps[0].length;
    int r = 0;
    for (int[] row : escapeSteps) {
      int c = 0;
      for (int value : row) {
        int rgb = colorModel.getRGB(value);
        for (int i = 0; i < repeatX; i++ ) {
          for (int j = 0; j < repeatY; j++ ) {
            fractal.setRGB(r + i, c + j, rgb);
          }
        }
        c += repeatY;
      }
      r += repeatX;
    }
    repaint();
  }
}