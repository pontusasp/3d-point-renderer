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
                if(e.isShiftDown()) {
                    p = p.scale(1.5);
                } else if(e.isControlDown()) {
                    p = p.scale(0.667);
                } else {
                    clickX = e.getXOnScreen();
                    clickY = e.getYOnScreen();
                }
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
                ry -= (clickX - e.getXOnScreen()) / (Main.WIDTH / 2.0) * 180;
                rx -= (clickY - e.getYOnScreen()) / (Main.HEIGHT / 2.0) * 180;

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
    private Matrix translation = new Matrix(new double[][]{
            {1, 0, 0, 100},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
    });

    double theta = 0;
    @Override
    protected void paintComponent(Graphics g) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);


        Matrix rx = Matrix.rx(this.rx);
        Matrix ry = Matrix.ry(this.ry);
        Matrix rz = Matrix.rz(this.rz);
        Matrix r = translation.mult(rz.mult(ry).mult(rx));


        g2.setColor(Color.BLUE);
        for (double x = -150; x < 150; x += 1)
            draw(g2, p.mult(r.mult(new Vector(new double[]{x, 0, 0, 1}))));

        g2.setColor(Color.RED);
        for (double y = -150; y < 150; y += 1)
            draw(g2, p.mult(r.mult(new Vector(new double[]{0, y, 0, 1}))));

        g2.setColor(Color.GREEN);
        for (double z = -150; z < 150; z += 1)
            draw(g2, p.mult(r.mult(new Vector(new double[]{0, 0, z, 1}))));

        /*g2.setColor(Color.WHITE);
        for (double x = xStart; x < xEnd; x += stepX) {
            for (double y = yStart; y < yEnd; y += stepY) {
                for (double z = yStart; z < yEnd; z += stepY) {
                    double fun = f(Algorithm.SPHERE, x, y, z);
                    if (Math.abs(fun) < 100 * 100 && Math.abs(fun) > 100 * 100 - 100)
                        draw(g2, p.mult(r.mult(new Vector(new double[]{x, y, z, 1}))));
                }
            }
        }*/

        for (double x = -5; x < 5; x += 1) {
            for (double y = -5; y < 5; y += 1) {
                for (double z = -5; z < 5; z += 1) {
                    double fun = f(Algorithm.SPHERE, x, y, z);
                    if (Math.abs(fun) < 25 && Math.abs(fun) > 15) {
                        g2.setColor(Color.BLUE);
                        draw(g2, p.mult(r.mult(new Vector(new double[]{150 + x, y, z, 1}))));
                        g2.setColor(Color.RED);
                        draw(g2, p.mult(r.mult(new Vector(new double[]{x, y - 150, z, 1}))));
                        g2.setColor(Color.GREEN);
                        draw(g2, p.mult(r.mult(new Vector(new double[]{x, y, z + 150, 1}))));
                    }
                }
            }
        }

        theta += 0.2;
        g2.setColor(new Color(255, 0, 255, 200));
        for (double x = -200; x < 200; x += 5) {
            for (double y = -200; y < 200; y += 2) {
                //for (double z = -100; z < 100; z += 20)
                    draw(g2, p.mult(r.mult(new Vector(new double[] {x, y, (Math.cos(x / 10 + theta) - Math.cos(theta/4 - (x + y) / 100) * 3 + Math.sin(y / 10 + theta)) * 10, 1}))));
            }
        }

        /*for(double s = -5; s < 5; s += .1) {
            for(double t = -5; t < 5; t += .1) {
                if(!(0 < s + t && s + t < 1)) continue;
                g2.setColor(Color.CYAN);
                draw(g2, p.mult(r.mult(new Vector(new double[] {1 + s, 2 + t, 0, 1}))), 4, 5);
                draw(g2, p.mult(r.mult(new Vector(new double[] {2 + s, 3 + t, 0, 1}))), 4, 5);
                draw(g2, p.mult(r.mult(new Vector(new double[] {-1 + s, -1 + t, 0, 1}))), 4, 5);
            }
        }*/

        g.drawImage(img, 0, 0, null);
        repaint();
    }

    enum Algorithm {
        SPHERE,
        CLASS,
        CLASS2,
        SPHERE_X,
        SPHERE_Y,
        SPHERE_Z,
    }

    public double f(Algorithm a, double x, double y, double z) {
        switch (a) {
            case SPHERE:
                return x * x + y * y + z * z;
            case CLASS:
                return x * x - 2 * x * y;
            case CLASS2:
                return y*y - x*x*x - x*x;
            case SPHERE_X:
                return x * Math.sin(z) * Math.cos(y);
            case SPHERE_Y:
                return x * Math.sin(z) * Math.sin(y);
            case SPHERE_Z:
                return x * Math.cos(z);
        }
        return x + y + z;
    }

    private int alpha(double z) {
        if (Math.abs(z) > 255) return 255;
        return (int) Math.abs(z);
    }

    public void draw(Graphics g, Vector p) {
        double[] point = p.vector;
        int size = 1;
        g.fillRect((int) point[0] + Main.WIDTH / 2, (int) point[1] + Main.HEIGHT / 2, size, size);
    }
    public void draw(Graphics g, Vector p, int size, double spacing) {
        double[] point = p.vector;
        g.fillRect((int) (point[0] * spacing) + Main.WIDTH / 2, (int) (point[1] * spacing) + Main.HEIGHT / 2, size, size);
    }

}
