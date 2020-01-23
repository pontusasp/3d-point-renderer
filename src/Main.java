import javax.swing.*;
import java.awt.*;

public class Main {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;

    private JFrame frame;
    private Panel panel;

    public Main(int width, int height) {
        frame = new JFrame("Titel här");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        panel = new Panel(width, height);
        frame.add(panel);
        panel.setVisible(true);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Main(WIDTH, HEIGHT);


        Vector vx = new Vector(new double[] {
                10, 0, 0, 1
        });

        Vector vy = new Vector(new double[] {
                0, 10, 0, 1
        });

        Matrix vz = new Matrix(new double[][] {
                {0}, {0}, {10}, {1}
        });

        Matrix r = Matrix.ry(45);

        System.out.println(r);
        System.out.println(vz);
        System.out.println(r.mult(vz));
    }

}
