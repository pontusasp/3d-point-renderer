class Vector {

    int length;
    double[] vector;

    Vector(double[] vector) {
        this.vector = vector;
        length = vector.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("\t").append(vector[i]).append("\n");
        }
        return sb.toString();
    }

}