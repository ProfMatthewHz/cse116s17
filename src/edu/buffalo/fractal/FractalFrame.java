package edu.buffalo.fractal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.IndexColorModel;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.filechooser.FileFilter;

/**
 * Defines a very simple application with which I display the fractal.
 *
 * @author Matthew Hertz
 */
@SuppressWarnings("serial")
public class FractalFrame extends JFrame {
  /** Fractal image displayed on this frame. */
  private FractalPanel fractal;

  /** Refers to the active fractal which we are using to compute. */
  private FractalOption activeFractal;

  private static final int DRAWING_TASKS = 64;

  private enum ColorOption {
    Rainbow, Grays, Blues;
  }

  private ColorOption colorUsed;

  /** The thread pool with which we will generate our fractals. */
  private ComputePool poolOfWorkers;

  /** Returns the fractal that is current being generated */
  public FractalOption getActive() {
    return activeFractal;
  }

  /** Create a new frame and provide the user with options. */
  public FractalFrame() {
    // Setup our JFrame
    super();

    // Create the menu bar
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    // Create the "File" menu
    JMenu fileMenu = new JMenu("File");
    JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
    saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
    saveItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser saveDlg = new JFileChooser();
        FileFilter fltr = new FileFilter() {

          @Override
          public boolean accept(File f) {
            return f.getName().endsWith(".png");
          }

          @Override
          public String getDescription() {
            return "PNG Files";
          }
        };
        saveDlg.setFileFilter(fltr);
        int valu = saveDlg.showSaveDialog(null);
        if (valu == JFileChooser.APPROVE_OPTION) {
          fractal.saveImage(FractalPanel.SaveFormat.PNG, saveDlg.getSelectedFile().getAbsolutePath());
        }
      }
    });
    fileMenu.add(saveItem);
    fileMenu.addSeparator();
    JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK));
    exitItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    fileMenu.add(exitItem);
    menuBar.add(fileMenu);

    // Create the "Algorithm" menu
    JMenu algorithmMenu = new JMenu("Algorithm");
    ButtonGroup algorithmGroup = new ButtonGroup();
    JRadioButtonMenuItem mandelbrotItem = new JRadioButtonMenuItem("Mandlebrot", true);
    mandelbrotItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switchTask(FractalOption.Mandelbrot);
      }
    });
    algorithmGroup.add(mandelbrotItem);
    algorithmMenu.add(mandelbrotItem);
    JRadioButtonMenuItem juliaItem = new JRadioButtonMenuItem("Julia", false);
    juliaItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switchTask(FractalOption.JuliaSet);
      }
    });
    algorithmGroup.add(juliaItem);
    algorithmMenu.add(juliaItem);
    JRadioButtonMenuItem burningShipItem = new JRadioButtonMenuItem("Burning Ship", false);
    burningShipItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switchTask(FractalOption.BurningShip);
      }
    });
    algorithmGroup.add(burningShipItem);
    algorithmMenu.add(burningShipItem);
    JRadioButtonMenuItem multibrotItem = new JRadioButtonMenuItem("Multibrot", false);
    multibrotItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switchTask(FractalOption.Multibrot);
      }
    });
    algorithmGroup.add(multibrotItem);
    algorithmMenu.add(multibrotItem);
    menuBar.add(algorithmMenu);

    JMenu colorMenu = new JMenu("Color");
    ButtonGroup colorGroup = new ButtonGroup();
    JRadioButtonMenuItem defaultItem = new JRadioButtonMenuItem("Rainbow", true);
    defaultItem.setMnemonic(KeyEvent.VK_D);
    defaultItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        redoRainbow();
        if (activeFractal != null) {
          switchTask(activeFractal);
        }
      }
    });
    colorGroup.add(defaultItem);
    colorMenu.add(defaultItem);
    JRadioButtonMenuItem grayItem = new JRadioButtonMenuItem("Grays", false);
    grayItem.setMnemonic(KeyEvent.VK_M);
    grayItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        redoGrays();
        if (activeFractal != null) {
          switchTask(activeFractal);
        }
      }
    });
    colorGroup.add(grayItem);
    colorMenu.add(grayItem);
    JRadioButtonMenuItem bluesItem = new JRadioButtonMenuItem("Blues", false);
    bluesItem.setMnemonic(KeyEvent.VK_M);
    bluesItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        redoBlues();
        if (activeFractal != null) {
          switchTask(activeFractal);
        }
      }
    });
    colorGroup.add(bluesItem);
    colorMenu.add(bluesItem);
    colorUsed = ColorOption.Rainbow;
    menuBar.add(colorMenu);

    // Create the image in which our fractal is displayed, the dragging panel in the middle, and overlay that goes on
    // top.
    fractal = new FractalPanel();
    fractal.setSize(new Dimension(2048, 2048));
    poolOfWorkers = new ComputePool();
    poolOfWorkers.changePanel(fractal);
    DraggingPane drag = new DraggingPane(this);
    JComponent overlay = new OverlayPane();
    overlay.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent me) {
        if (me.getY() <= ((OverlayPane) me.getSource()).getResetHeight()) {
          switch (activeFractal) {
            case Mandelbrot:
              activeFractal.setStartX(-2.15);
              activeFractal.setStartY(-1.3);
              activeFractal.setEndX(0.6);
              activeFractal.setEndY(1.3);
              break;
            case BurningShip:
              activeFractal.setStartX(-1.8);
              activeFractal.setStartY(-0.08);
              activeFractal.setEndX(-1.7);
              activeFractal.setEndY(0.025);
              break;
            case JuliaSet:
              activeFractal.setStartX(-1.7);
              activeFractal.setStartY(-1);
              activeFractal.setEndX(1.7);
              activeFractal.setEndY(1);
              break;
            case Multibrot:
              activeFractal.setStartX(-1);
              activeFractal.setStartY(-1.3);
              activeFractal.setEndX(1);
              activeFractal.setEndY(1.3);
              break;
          }
          switchTask(activeFractal);
        } else if (me.getY() <= ((OverlayPane) me.getSource()).getEscapeHeight()) {
          String escape = JOptionPane.showInputDialog("What is the new escape value to use?",
                                                      ComputeFractal.getEscapeValue());
          if (escape != null) {
            ComputeFractal.setEscapeValue(Integer.parseInt(escape));
            if (activeFractal != null) {
              switchTask(activeFractal);
            }
          }
        } else if (me.getY() <= ((OverlayPane) me.getSource()).getIterationHeight()) {
          String iterate = JOptionPane.showInputDialog("What is the new iteration value to use?",
                                                       ComputeFractal.getMaxIterations());
          if (iterate != null) {
            int iters = Integer.parseInt(iterate);
            if ((iters > 255) || (iters < 1)) {
              JOptionPane.showMessageDialog(null, "Iterations must be between 1 - 255, not " + iterate, "Error!",
                                            JOptionPane.ERROR_MESSAGE);
            } else {
              ComputeFractal.setMaxIterations(iters);
              switch (colorUsed) {
                case Rainbow:
                  redoRainbow();
                  break;
                case Grays:
                  redoGrays();
                  break;
                case Blues:
                  redoBlues();
                  break;
              }
              if (activeFractal != null) {
                switchTask(activeFractal);
              }
            }
          }
        }
      }
    });

    // Now create the layered pane that holds everything.
    JLayeredPane layerPane = new JLayeredPane();
    layerPane.setLayout(new OverlayLayout(layerPane));
    layerPane.add(overlay, Integer.valueOf(JLayeredPane.DRAG_LAYER + 50));
    layerPane.add(drag, JLayeredPane.DRAG_LAYER);
    layerPane.add(fractal, JLayeredPane.DEFAULT_LAYER);

    getContentPane().add(layerPane);

    addMouseListener(drag);
    addMouseMotionListener(drag);
  }

  private void redoRainbow() {
    int numColors = ComputeFractal.getMaxIterations() + 1;
    byte[] reds = new byte[numColors];
    byte[] greens = new byte[numColors];
    byte[] blues = new byte[numColors];
    for (int i = 0; i < (reds.length - 1); i++ ) {
      int rgb = Color.HSBtoRGB(i / ((float) reds.length - 1), 0.6F, 1);
      reds[i] = (byte) ((rgb & 0xFF0000) >> 16);
      greens[i] = (byte) ((rgb & 0xFF00) >> 8);
      blues[i] = (byte) (rgb & 0xFF);
    }
    IndexColorModel retVal = new IndexColorModel(8, reds.length, reds, greens, blues);
    colorUsed = ColorOption.Rainbow;
    fractal.setIndexColorModel(retVal);
  }

  private void redoGrays() {
    IndexColorModel retVal = ColorModelFactory.createGrayColorModel(ComputeFractal.getMaxIterations() + 1);
    colorUsed = ColorOption.Grays;
    fractal.setIndexColorModel(retVal);
  }

  private void redoBlues() {
    IndexColorModel retVal = ColorModelFactory.createBluesColorModel(ComputeFractal.getMaxIterations() + 1);
    colorUsed = ColorOption.Blues;
    fractal.setIndexColorModel(retVal);
  }

  protected void switchTask(FractalOption newOption) {
    ComputeFractal[] newGenerators = new ComputeFractal[DRAWING_TASKS];
    ComputeFractal.setNumProcesses(DRAWING_TASKS);
    for (int i = 0; i < newGenerators.length; i++ ) {
      switch (newOption) {
        case Mandelbrot:
          newGenerators[i] = ComputeFractal.getMandelbrotSet(i, newOption.getStartX(), newOption.getStartY(),
                                                             newOption.getEndX(), newOption.getEndY());
          break;
        case BurningShip:
          newGenerators[i] = ComputeFractal.getBurningShip(i, newOption.getStartX(), newOption.getStartY(),
                                                           newOption.getEndX(), newOption.getEndY());
          break;
        case JuliaSet:
          newGenerators[i] = ComputeFractal.getJuliaSet(i, newOption.getStartX(), newOption.getStartY(),
                                                        newOption.getEndX(), newOption.getEndY());
          break;
        case Multibrot:
          newGenerators[i] = ComputeFractal.getMultibrotSet(i, newOption.getStartX(), newOption.getStartY(),
                                                            newOption.getEndX(), newOption.getEndY());
          break;
      }
    }
    activeFractal = newOption;
    switchTask(newGenerators);
  }

  private void switchTask(ComputeFractal[] cf) {
    poolOfWorkers.clearPool();
    // Make the new task active.
    poolOfWorkers.generateFractal(2048, cf);
  }

  /**
   * Main method to start our program.
   *
   * @param args Command-line arguments, these do not get used.
   */
  @SuppressWarnings("cast")
  public static void main(String[] args) {
    Component ff = new FractalFrame();
    if (ff instanceof JFrame) {
      JFrame jf = (JFrame) ff;
      jf.setTitle("Fractal Explorer");
      jf.pack();
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    ff.setVisible(true);
  }
}