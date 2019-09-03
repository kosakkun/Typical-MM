import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.swing.*;
import javax.imageio.*;

public class Tester {

    public class Visualizer extends JPanel implements WindowListener {
        
        public void paint(Graphics g) {

            try {
                BufferedImage bi = new BufferedImage(VIS_SIZE, VIS_SIZE, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = (Graphics2D)bi.getGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(0xD3D3D3));
                g2.fillRect(0, 0, VIS_SIZE, VIS_SIZE);
                g2.setColor(new Color(0xFFFFFF));
                g2.fillRect(10, 10, VIS_SIZE - 20, VIS_SIZE - 20);

                g2.setColor(new Color(0x000000));
                for (int i = 0; i < input.N; i++) {
                    int a = output.perm[i];
                    int b = output.perm[(i + 1) % input.N];
                    g2.drawLine(input.posX[a] + 10, input.posY[a] + 10, 
                                input.posX[b] + 10, input.posY[b] + 10);
                }

                for (int i = 0; i < input.N; i++) {
                    g2.setColor(new Color(0xFFFFFF));
                    g2.fillOval(input.posX[i] + 6, input.posY[i] + 6, 8, 8);
                    g2.setColor(new Color(0x000000));
                    g2.drawOval(input.posX[i] + 6, input.posY[i] + 6, 8, 8);
                }

                if (numb) {
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    for (int i = 0; i < input.N; ++i) {
                        char[] ch = ("" + i).toCharArray();
                        int x = input.posX[i] + 5;
                        int y = input.posY[i] + 5;
                        g2.drawChars(ch, 0, ch.length, x, y);
                    }
                }

                g.drawImage(bi, 0, 0, VIS_SIZE, VIS_SIZE, null);
                if (save) {
                    ImageIO.write(bi, "png", new File(fileName +".png"));
                }

            } catch (Exception e) { 
                e.printStackTrace();
            }
        }

        public Visualizer () {
            
        }

        public void windowClosing(WindowEvent e) {
            if (proc != null) {
                try { 
                    proc.destroy();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.exit(0);
        }

        public void windowActivated(WindowEvent e) { }
        public void windowDeactivated(WindowEvent e) { }
        public void windowOpened(WindowEvent e) { }
        public void windowClosed(WindowEvent e) { }
        public void windowIconified(WindowEvent e) { }
        public void windowDeiconified(WindowEvent e) { }

    }

    /********************************************************************/

    JFrame jf;
    Visualizer v;
    InputStream is;
    OutputStream os;

    static Process proc;
    static String fileName, exec;
    static boolean save, vis, numb;

    InputData input;
    OutputData output;
    final int VIS_SIZE = 1020;

    /********************************************************************/

    public double runTest (String seed) {

        try {
            input = new InputData(Long.parseLong(seed));
            if (proc != null) try {
                output = new OutputData(input, is, os);
                boolean [] used = new boolean[input.N];
                for (int i = 0; i < input.N; ++i) {
                    if (output.perm[i] < 0 || output.perm[i] >= input.N) {
                        System.err.println("All elements of your return must be between 0 and " + (input.N-1) + ", and your return contained " + output.perm[i] + ".");
                        return -1;
                    }
                    if (used[output.perm[i]]) {
                        System.err.println("All elements of your return must be unique, and your return contained " + output.perm[i] + " twice.");
                        return -1;
                    }
                    used[output.perm[i]] = true;
                }
            } catch (Exception e) {
                System.err.println("Failed to get result from your answer.");
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        double score = 0.0;
        for (int i = 0; i < input.N; i++) {
            double dx = (double)(input.posX[output.perm[i]] - input.posX[output.perm[(i + 1) % input.N]]);
            double dy = (double)(input.posY[output.perm[i]] - input.posY[output.perm[(i + 1) % input.N]]);
            score += Math.sqrt(dx * dx + dy * dy);
        }

        if (vis) {
            jf.getContentPane().setPreferredSize(new Dimension(VIS_SIZE, VIS_SIZE));
            jf.pack();
            jf.setVisible(true);
        }
        
        return score;
    }

    public Tester (String seed) {
        if (vis) {
            jf = new JFrame();
            v = new Visualizer();
            jf.addWindowListener(v);
            jf.getContentPane().add(v);
        }
        if (exec != null) {
            try {
                Runtime rt = Runtime.getRuntime();
                proc = rt.exec(exec);
                os = proc.getOutputStream();
                is = proc.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Score = " + runTest(seed));
        if (proc != null) {
            try { 
                proc.destroy(); 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
    }

    public static void main (String[] args) {
        String seed = "1";
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-seed")) {
                seed = args[++i];
            } else if (args[i].equals("-exec")) {
                exec = args[++i];
            } else if (args[i].equals("-vis")) {
                vis = true;
            } else if (args[i].equals("-save")) {
                save = true;
                vis = true;
            } else if (args[i].equals("-num")) {
                numb = true;
            }
        }
        fileName = seed;
        Tester test = new Tester(seed);
    }

}
