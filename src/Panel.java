import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class Panel extends JPanel {

    private BufferedImage img;
    private Graphics g2;

    private int clickX, clickY;
    private double rx = 30, ry = 30, rz = 0;

    private double xStart = -100;
    private double yStart = -100;

    private double xEnd = 100;
    private double yEnd = 100;

    private double stepX = 4;
    private double stepY = 4;

    private double farZ = -100;
    private double nearZ = 100;

    private double aspect = (double) Main.WIDTH / Main.HEIGHT;
    private double fov = Math.toRadians(70);

    public Panel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        img = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_INT_RGB);
        g2 = img.createGraphics();

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                clickX = e.getXOnScreen();
                clickY = e.getYOnScreen();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println(ry);
                ry += (clickX - e.getXOnScreen()) / (Main.WIDTH / 2.0) * 180;
                rx += (clickY - e.getYOnScreen()) / (Main.HEIGHT / 2.0) * 180;

                clickX = e.getXOnScreen();
                clickY = e.getYOnScreen();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

    }

    private Matrix p = new Matrix(new double[][]{
            {1 / (aspect * Math.tan(fov / 2)), 0, 0, 0},
            {0, 1 / Math.tan(fov / 2), 0, 0},
            {0, 0, -(farZ + nearZ) / (farZ - nearZ), -2 * farZ * nearZ / (farZ - nearZ)},
            {0, 0, -1, 0}
    });

    @Override
    protected void paintComponent(Graphics g) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);


        Matrix rx = Matrix.rx(this.rx);
        Matrix ry = Matrix.ry(this.ry);
        Matrix rz = Matrix.rz(this.rz);
        Matrix r = rz.mult(ry).mult(rx);

        g2.setColor(Color.BLUE);
        for (double x = -1000; x < 1000; x += 1)
            draw(g2, p.mult(r.mult(new Vector(new double[]{x, 0, 0, 1}))));

        g2.setColor(Color.RED);
        for (double y = -1000; y < 1000; y += 1)
            draw(g2, p.mult(r.mult(new Vector(new double[]{0, y, 0, 1}))));

        g2.setColor(Color.GREEN);
        for (double z = -1000; z < 1000; z += 1)
            draw(g2, p.mult(r.mult(new Vector(new double[]{0, 0, z, 1}))));

        g2.setColor(Color.WHITE);
        for (double x = xStart; x < xEnd; x += stepX) {
            for (double y = yStart; y < yEnd; y += stepY) {
                for (double z = yStart; z < yEnd; z += stepY) {
                    double fun = f(x, y, z);
                    if (Math.abs(fun) < 100 * 100 && Math.abs(fun) > 100 * 100 - 100)
                        //draw(g2, p.mult(r.mult(new Vector(new double[]{x, y, z, 1}))));
                        draw(g2, p.mult(r.mult(new Vector(new double[]{x, y, z, 1}))));
                }
            }
        }

        for (double x = -5; x < 5; x += 1) {
            for (double y = -5; y < 5; y += 1) {
                for (double z = -5; z < 5; z += 1) {
                    double fun = f(x, y, z);
                    if (Math.abs(fun) < 25 && Math.abs(fun) > 15) {
                        g2.setColor(Color.BLUE);
                        draw(g2, p.mult(r.mult(new Vector(new double[]{100 + x, y, z, 1}))));
                        g2.setColor(Color.RED);
                        draw(g2, p.mult(r.mult(new Vector(new double[]{x, y - 100, z, 1}))));
                        g2.setColor(Color.GREEN);
                        draw(g2, p.mult(r.mult(new Vector(new double[]{x, y, z + 100, 1}))));
                    }
                }
            }
        }

        g.drawImage(img, 0, 0, null);
        repaint();
    }

    /*public double[] persp(double[][] p, double x, double y, double z, double c) {
        return new double[]{
                (x + Main.WIDTH / 2f) * (p[0][0] + p[1][0] + p[2][0] + p[3][0]),
                (y + Main.HEIGHT / 2f) * (p[0][1] + p[1][1] + p[2][1] + p[3][1]),
                z * (p[0][2] + p[1][2] + p[2][2] + p[3][2]),
                c * (p[0][3] + p[1][3] + p[2][3] + p[3][3]),
        };
    }*/

    public double f(double x, double y, double z) {
        //return x * x - 2 * x * y;
        return x * x + y * y + z * z;
    }

    private int alpha(double z) {
        if (Math.abs(z) > 255) return 255;
        return (int) Math.abs(z);
    }

    public void draw(Graphics g, Vector p) {
        double[] point = p.vector;
        g.fillRect((int) point[0] + Main.WIDTH / 2, (int) point[1] + Main.HEIGHT / 2, 2, 2);
    }

}
