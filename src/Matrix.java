class Matrix {

    int width, height;
    double[][] matrix;

    Matrix(double[][] matrix) {
        width = matrix[0].length;
        height = matrix.length;
        this.matrix = matrix;
    }

    Matrix scale(double s) {
        double[][] m = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                m[i][j] = s * matrix[i][j];
            }
        }
        return new Matrix(m);
    }

    Matrix mult(Matrix matrix) {
        if (width != matrix.height)
            throw new NumberFormatException("Invalid matrix dimensions");
        double[][] img = new double[height][matrix.width];
        for (int n = 0; n < img[0].length; n++) {
            for (int m = 0; m < img.length; m++) {
                for (int ik = 0; ik < width; ik++) {
                    double x = this.matrix[m][ik];
                    x *= matrix.matrix[ik][n];
                    img[m][n] += x;
                }
            }
        }

        return new Matrix(img);
    }

    Matrix transpose() {
        double[][] m = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                m[i][j] = matrix[j][i];
            }
        }
        return new Matrix(m);
    }

    Vector mult(Vector v) {
        if (v.length != width)
            throw new NumberFormatException("Invalid vector length");

        double[] vec = new double[width];

        for (int i = 0; i < width; i++) {
            for (int ik = 0; ik < height; ik++) {
                vec[i] += matrix[ik][i] * v.vector[ik];
            }
        }

        return new Vector(vec);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                sb.append("\t").append(matrix[j][i]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static Matrix rx(double deg) {
        double theta = Math.toRadians(deg);
        return new Matrix(new double[][]{
                {1, 0, 0, 0},
                {0, Math.cos(theta), Math.sin(theta), 0},
                {0, -Math.sin(theta), Math.cos(theta), 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix ry(double deg) {
        double theta = Math.toRadians(deg);
        return new Matrix(new double[][]{
                {Math.cos(theta), 0, Math.sin(theta), 0},
                {0, 1, 0, 0},
                {-Math.sin(theta), 0, Math.cos(theta), 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix rz(double deg) {
        double theta = Math.toRadians(deg);
        return new Matrix(new double[][]{
                {Math.cos(theta), -Math.sin(theta), 0, 0},
                {Math.sin(theta), Math.cos(theta), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

}