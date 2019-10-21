import java.io.File;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.imageio.ImageIO;

public class Visualizer extends JFrame
{
    final int FIELD_HEIGHT = 1000;
    final int FIELD_WIDTH  = 1000;
    final int PADDING = 10;
    final int VIS_SIZE_X = FIELD_WIDTH + PADDING * 2;
    final int VIS_SIZE_Y = FIELD_HEIGHT + PADDING * 2;
    final Tester tester;

    public Visualizer (final Tester _tester)
    {
        this.tester = _tester;
    }

    public void saveImage (String fileName)
    {
        try {
            BufferedImage bi = drawImage();
            ImageIO.write(bi, "png", new File(fileName +".png"));
        } catch (Exception e) {
            System.err.println("Visualizer failed to save the image.");
            e.printStackTrace();
        }
    }

    public void visualize ()
    {
        this.setVisible(true);
        Insets insets = getInsets();
        final int width  = VIS_SIZE_X + insets.left + insets.right;
        final int height = VIS_SIZE_Y + insets.top + insets.bottom;
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    @Override
    public void paint (Graphics g)
    {
        try {
            BufferedImage bi = drawImage();
            g.drawImage(bi, getInsets().left, getInsets().top, VIS_SIZE_X, VIS_SIZE_Y, null);
        } catch (Exception e) {
            System.err.println("Visualizer failed to draw.");
            e.printStackTrace();
        }
    }

    /**
     * 
     *
     * @see Tester
     */
    private BufferedImage drawImage ()
    {
        BufferedImage bi = new BufferedImage(VIS_SIZE_X, VIS_SIZE_Y, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
        /* Draw background */
        g2.setColor(new Color(0xD3D3D3));
        g2.fillRect(0, 0, VIS_SIZE_X, VIS_SIZE_Y);
        g2.setColor(new Color(0xFFFFFF));
        g2.fillRect(PADDING, PADDING, FIELD_WIDTH, FIELD_HEIGHT);

        /* Converts the origin of the graphics context to a 
           point (x, y) in the current coordinate system.*/
        g2.translate(PADDING, PADDING);

        /* Draw edges */
        g2.setColor(new Color(0x000000));
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < tester.MST.length; i++) {
            g2.drawLine(tester.MST[i].x1, tester.MST[i].y1, 
                        tester.MST[i].x2, tester.MST[i].y2);
        }

        /* Draw vertex */
        final int R1 = 6;
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < tester.N; i++) {
            g2.setColor(new Color(0xDC143C));
            g2.fillOval(tester.x[i] - R1 / 2, tester.y[i] - R1 / 2, R1, R1);
            g2.setColor(new Color(0x000000));
            g2.drawOval(tester.x[i] - R1 / 2, tester.y[i] - R1 / 2, R1, R1);
        }
        
        final int R2 = 6;
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < tester.M; i++) {
            g2.setColor(new Color(0x4169E1));
            g2.fillOval(tester.ax[i] - R2 / 2, tester.ay[i] - R2 / 2, R2, R2);
            g2.setColor(new Color(0x000000));
            g2.drawOval(tester.ax[i] - R2 / 2, tester.ay[i] - R2 / 2, R2, R2);
        }

        return bi;
    }
}
