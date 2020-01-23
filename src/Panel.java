import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

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


    private Matrix translation = new Matrix(new double[][]{
            {1, 0, 0, 100},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
    });

    private Matrix p = new Matrix(new double[][]{
            {1 / (aspect * Math.tan(fov / 2)), 0, 0, 0},
            {0, 1 / Math.tan(fov / 2), 0, 0},
            {0, 0, -(farZ + nearZ) / (farZ - nearZ), -2 * farZ * nearZ / (farZ - nearZ)},
            {0, 0, -1, 0}
    });

    private Matrix r;

    private Ball[] balls;

    public Panel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        img = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_INT_RGB);
        g2 = img.createGraphics();

        balls = new Ball[5];

        Random rdm = new Random();

        for (int i = 0; i < balls.length; i++) {
            double size = rdm.nextInt(20) + 10;
            balls[i] = new Ball(
                    rdm.nextInt(200) - 100,
                    rdm.nextInt(200) - 100,
                    rdm.nextInt(200) - 100,
                    size,
                    (int) size
            );
        }

        r = translation.mult(Matrix.rz(this.rz).mult(Matrix.ry(this.ry)).mult(Matrix.rx(this.rx)));

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isShiftDown()) {
                    p = p.scale(1.5);
                } else if (e.isControlDown()) {
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
                //System.out.println(ry);
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

    public void drawAxis() {
        g2.setColor(Color.BLUE);
        for (double x = -150; x < 150; x += 1)
            draw(g2, new Vector(new double[]{x, 0, 0, 1}));

        g2.setColor(Color.RED);
        for (double y = -150; y < 150; y += 1)
            draw(g2, new Vector(new double[]{0, y, 0, 1}));

        g2.setColor(Color.GREEN);
        for (double z = -150; z < 150; z += 1)
            draw(g2, new Vector(new double[]{0, 0, z, 1}));

        for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 10) {
            for (double rho = 0; rho < Math.PI; rho += Math.PI / 10) {
                double x = f(Algorithm.SPHERE_X, 5, theta, rho);
                double y = f(Algorithm.SPHERE_Y, 5, theta, rho);
                double z = f(Algorithm.SPHERE_Z, 5, 0, rho);
                g2.setColor(Color.BLUE);
                draw(g2, new Vector(new double[]{150 + x, y, z, 1}));
                g2.setColor(Color.RED);
                draw(g2, new Vector(new double[]{x, y - 150, z, 1}));
                g2.setColor(Color.GREEN);
                draw(g2, new Vector(new double[]{x, y, z + 150, 1}));
            }

        }
    }

    public void updateCamera() {
        Matrix rx = Matrix.rx(this.rx);
        Matrix ry = Matrix.ry(this.ry);
        Matrix rz = Matrix.rz(this.rz);
        r = translation.mult(rz.mult(ry).mult(rx));
    }

    public void clear() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
    }

    public void finish(Graphics g) {
        g.drawImage(img, 0, 0, null);
        repaint();
    }


    double c = 0;
    @Override
    protected void paintComponent(Graphics g) {
        clear();
        updateCamera();
        drawAxis();

        rx += 0.4;
        ry += 0.2;
        rz += 0.7;
        c += 0.2;

        g2.setColor(new Color(255, (int) c % 255, 255 - (int) (c / 10) % 255, 200));
        for (Ball b : balls)
            b.draw(g2);

        finish(g);
    }

    enum Algorithm {
        SPHERE,
        CLASS,
        CLASS2,
        SPHERE_X,
        SPHERE_Y,
        SPHERE_Z,
        CYL_X,
        CYL_Y,
    }

    public double f(Algorithm algorithm, double a, double b, double c) {
        switch (algorithm) {
            case SPHERE:
                return a * a + b * b + c * c;
            case CLASS:
                return a * a - 2 * a * b;
            case CLASS2:
                return b * b - a * a * a - a * a;
            case SPHERE_X:
                return a * Math.sin(c) * Math.cos(b);
            case SPHERE_Y:
                return a * Math.sin(c) * Math.sin(b);
            case SPHERE_Z:
                return a * Math.cos(c);
            case CYL_X:
                return a * Math.cos(b);
            case CYL_Y:
                return a * Math.sin(b);
        }
        return a + b + c;
    }

    private int alpha(double z) {
        if (Math.abs(z) > 255) return 255;
        return (int) Math.abs(z);
    }

    public void draw(Graphics g, Vector p) {
        p = this.p.mult(r.mult(p));
        double[] point = p.vector;
        int size = 1;
        g.fillRect((int) point[0] + Main.WIDTH / 2, (int) point[1] + Main.HEIGHT / 2, size, size);
    }

    public void draw(Graphics g, Vector p, int size, double spacing) {
        p = this.p.mult(r.mult(p));
        double[] point = p.vector;
        g.fillRect((int) (point[0] * spacing) + Main.WIDTH / 2, (int) (point[1] * spacing) + Main.HEIGHT / 2, size, size);
    }

    class Ball {

        private double x, y, z, r;
        private Vector pos;
        private Vector[][] points;


        public Ball(double x, double y, double z, double r, int resolution) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
            pos = new Vector(new double[]{x, y, z, 1});
            points = new Vector[resolution][resolution / 2];

            int i = 0, j;
            for (double theta = 0; i < resolution; theta += Math.PI * 2 / resolution, i++) {
                j = 0;
                for (double phi = 0; j < resolution / 2; phi += Math.PI * 2 / resolution, j++) {
                    points[i][j] = new Vector(new double[]{
                            Panel.this.f(Algorithm.SPHERE_X, r, theta, phi) + x,
                            Panel.this.f(Algorithm.SPHERE_Y, r, theta, phi) + y,
                            Panel.this.f(Algorithm.SPHERE_Z, r, 0, phi) + z,
                            1
                    });
                }
            }
        }

        public Ball() {
            this(0, 0, 0, 10, 100);
        }

        public void draw(Graphics g) {
            for (Vector[] vv : points)
                for (Vector v : vv)
                    Panel.this.draw(g, v);
        }
    }

}
