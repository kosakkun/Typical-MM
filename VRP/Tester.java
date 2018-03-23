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
                BufferedImage bi = new BufferedImage(SIZE_VIS_X, SIZE_VIS_Y, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = (Graphics2D)bi.getGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(0xD3D3D3));
                g2.fillRect(0, 0, SIZE_VIS_X, SIZE_VIS_Y);
                g2.setColor(new Color(0xFFFFFF));
                g2.fillRect(10, 10, SIZE_VIS_X - 20, SIZE_VIS_Y - 20);

                for (int i = 0; i < perm.length; i++) {
                    int x = depotX, y = depotY;
                    Color c = Color.getHSBColor((1.0f / (float)M) * (float)i, 1.0f, 0.95f);
                    g2.setColor(c);
                    for (int j = 0; j < perm[i].length; j++) {
                        int idx = perm[i][j];
                        if (j % cap[i] == 0) {
                            g2.drawLine(x * 10 + 20, y * 10 + 20, 
                                        depotX * 10 + 20, depotY * 10 + 20);
                            x = depotX;
                            y = depotY;
                        }
                        g2.drawLine(x * 10 + 20, y * 10 + 20, 
                                    posX[idx] * 10 + 20, posY[idx] * 10 + 20);
                        x = posX[idx];
                        y = posY[idx];
                    }
                    g2.drawLine(x * 10 + 20, y * 10 + 20, 
                                depotX * 10 + 20, depotY * 10 + 20);
                }

                for (int i = 0; i < perm.length; i++) {
                    for (int j = 0; j < perm[i].length; j++) {
                        int idx = perm[i][j];
                        g2.setColor(new Color(0xFFFFFF));
                        g2.fillOval(posX[idx] * 10 + 17, posY[idx] * 10 + 17, 6, 6);
                        g2.setColor(new Color(0x000000));
                        g2.drawOval(posX[idx] * 10 + 17, posY[idx] * 10 + 17, 6, 6);
                    }
                }

                g2.setColor(new Color(0x000000));
                g2.fillOval(depotX * 10 + 10, depotY * 10 + 10, 20, 20);
                
                g.drawImage(bi, 0, 0, SIZE_VIS_X, SIZE_VIS_Y, null);
                if (save) ImageIO.write(bi, "png", new File(fileName +".png"));

            } catch (Exception e) { 
                e.printStackTrace();
            }
        }

        public Visualizer () {
            jf.addWindowListener(this);
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
    Scanner sc;

    static Process proc;
    static String fileName, exec;
    static boolean save, vis, numb;

    final int SIZE = 100 + 1;
    final int SIZE_VIS_X = (SIZE + 3) * 10;
    final int SIZE_VIS_Y = (SIZE + 3) * 10;
    final int MAXN = 200, MINN = 50;
    final int MAXM = 10,  MINM = 3;
    final int MAX_CAP = 20, MIN_CAP = 5;
    final int MAX_SPEED = 20, MIN_SPEED = 1;

    int N,M;
    int depotX, depotY;
    int [] posX, posY;
    int [] cap;
    int [] speed;
    int [][] perm;

    /********************************************************************/

    public void generate (String seedStr) {

        try {
            SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            long seed = Long.parseLong(seedStr);
            rnd.setSeed(seed);

            N = rnd.nextInt(MAXN - MINN + 1) + MINN;
            M = rnd.nextInt(MAXM - MINM + 1) + MINM;

            depotX = rnd.nextInt(SIZE);
            depotY = rnd.nextInt(SIZE);
            boolean [][] usedPos = new boolean[SIZE][SIZE];
            usedPos[depotX][depotY] = true;
            posX = new int[N];
            posY = new int[N];
            for (int i = 0; i < N; i++) {
                int x,y;
                do {
                    x = rnd.nextInt(SIZE);
                    y = rnd.nextInt(SIZE);
                } while (usedPos[x][y]);
                usedPos[x][y] = true;
                posX[i] = x;
                posY[i] = y;
            }

            cap   = new int[M];
            speed = new int[M];
            for (int i = 0; i < M; i++) {
                cap[i]   = rnd.nextInt(MAX_CAP - MIN_CAP + 1) + MIN_CAP;
                speed[i] = rnd.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED;
            }
            
        } catch (Exception e) {
            System.err.println("An exception occurred while generating test case.");
            e.printStackTrace();
        }

    }

    double get_dist (int x1, int y1, int x2, int y2) {
        double lx = (double)(x1 - x2);
        double ly = (double)(y1 - y2);
        return Math.sqrt(lx * lx + ly * ly);
    }

    public double runTest (String seed) {

        try {
            generate(seed);
            if (proc == null) return -1;
            try {
                perm = getPermutation();
                boolean [] used = new boolean[N];
                for (int i = 0; i < perm.length; i++) {
                    for (int j = 0; j < perm[i].length; j++) {
                        if (used[perm[i][j]]) {
                            //System.err.println("");
                            return -1;
                        }
                        used[perm[i][j]] = true;
                    }
                }
                for (int i = 0; i < N; i++) {
                    if (!used[i]) {
                        //System.err.println("");
                        return -1;
                    }
                }
            } catch (Exception e) {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        if (vis) {
            jf.setSize(SIZE_VIS_X, SIZE_VIS_Y);
            jf.setVisible(true);
        }
        
        double score = -1.0;
        for (int i = 0; i < perm.length; i++) {
            double dist = 0.0;
            int x = depotX, y = depotY;
            for (int j = 0; j < perm[i].length; j++) {
                if (j % cap[i] == 0) {
                    dist += get_dist(x, y, depotX, depotY);
                    x = depotX;
                    y = depotY;
                }
                dist += get_dist(x, y, posX[perm[i][j]], posY[perm[i][j]]);
                x = posX[perm[i][j]];
                y = posY[perm[i][j]];
            }
            dist += get_dist(x, y, depotX, depotY);
            double time = dist / (double) speed[i];
            score = Math.max(score, time);
        }

        return score;
    }

    private int [][] getPermutation () throws IOException {
        
        StringBuffer sb = new StringBuffer();
        sb.append(N).append(' ');
        sb.append(M).append('\n');
        sb.append(depotX).append(' ');
        sb.append(depotY).append('\n');
        for (int i = 0; i < N; i++) {
            sb.append(posX[i]).append(' ');
            sb.append(posY[i]).append('\n'); 
        }
        for (int i = 0; i < M; i++) {
            sb.append(cap[i]).append(' ');
            sb.append(speed[i]).append('\n'); 
        }
        os.write(sb.toString().getBytes());
        os.flush();

        int [][] ret = new int[M][];
        for (int i = 0; i < M; ++i) {
            int L = sc.nextInt();
            ret[i] = new int[L];
            for (int j = 0; j < L; j++) {
                ret[i][j] = sc.nextInt();
            }
        }
        return ret;
    }

    public Tester (String seed) {
    	if (vis) {
    		jf = new JFrame();
            v = new Visualizer();
            jf.getContentPane().add(v);
        }
        if (exec != null) {
            try {
                Runtime rt = Runtime.getRuntime();
                proc = rt.exec(exec);
                os = proc.getOutputStream();
                is = proc.getInputStream();
                sc = new Scanner(is);
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
            }
        }
        fileName = seed;
        Tester test = new Tester(seed);
    }

}