package values;

/**
 * Eine Matrix.
 * 
 * @author kar, mhe, Marcello
 */
public class MatrixValue extends Value<MatrixValue> {
    /**
     * Anzahl der Zeilen der Matrix.
     */
    private final int rows;

    /**
     * Anzahl der Spalten der Matrix.
     */
    private final int cols;

    /**
     * Die Matrix-Werte. Die erste Dimension des Arrays enthält die Matrix-Zeilen und die zweite die
     * Werte einer Zeile.
     */
    private final double[][] values;

    /**
     * Erstellt eine Matrix mit Nullwerten.
     * 
     * @param rows Anzahl der Zeilen der Matrix.
     * @param cols Anzahl der Spalten der Matrix.
     */
    public MatrixValue(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.values = new double[rows][];
        for (int r = 0; r < rows; ++r) {
            this.values[r] = new double[cols];
            for (int c = 0; c < cols; ++c) {
                this.values[r][c] = 0.0;
            }
        }
    }

    /**
     * Gibt die Anzahl der Zeilen der Matrix.
     * 
     * @return Die Anzahl der Zeilen der Matrix
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gibt die Anzahl der Spalten der Matrix.
     * 
     * @return Die Anzahl der Spalten der Matrix
     */
    public int getCols() {
        return cols;
    }

    /**
     * Gibt einen Wert der Matrix zurück.
     * 
     * @param row Zeile des Wertes
     * @param col Spalte des Wertes
     * @return Der Wert an der angegebenen Position
     */
    public double getValue(int row, int col) {
        assert 0 <= row && row < this.rows;
        assert 0 <= col && col < this.cols;

        return this.values[row][col];
    }

    /**
     * Setzt einen Wert der Matrix.
     * 
     * @param value Der zu setzende Wert
     * @param row Zeile des Wertes
     * @param col Spalte des Wertes
     */
    public void setValue(double value, int row, int col) {
        assert 0 <= row && row < this.rows;
        assert 0 <= col && col < this.cols;

        this.values[row][col] = value;
    }

    @Override
    public MatrixValue add(MatrixValue other) {
        assert this.rows == other.rows;
        assert this.cols == other.cols;

        MatrixValue resultMatrix = new MatrixValue(this.rows, this.cols);

        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.cols; col++) {
                double addValue = this.getValue(row, col) + other.getValue(row, col);
                resultMatrix.setValue(addValue, row, col);
            }
        }
        return resultMatrix;
    }

    @Override
    public MatrixValue mul(MatrixValue other) {
        assert this.cols == other.rows;

        MatrixValue resultMatrix = new MatrixValue(this.rows, other.cols);

        double currMulValue = 0.0;

        //Die zu Multiplizierende Zeile (this)
        for (int thisRow = 0; thisRow < this.rows; thisRow++) {
            //Die zu Multiplizierende Spalte (other)
            for (int otherCol = 0; otherCol < other.cols; otherCol++) {
                //Zu multiplizierendes Element der Matrix (this * other)
                for (int idxValue = 0; idxValue < this.cols; idxValue++) {

                    currMulValue += this.getValue(thisRow, idxValue)
                            * other.getValue(idxValue, otherCol);
                }
                resultMatrix.setValue(currMulValue, thisRow, otherCol);
                currMulValue = 0.0;
            }
        }
        return resultMatrix;
    }

    @Override
    public MatrixValue sub(MatrixValue other) {
        assert this.rows == other.rows;
        assert this.cols == other.cols;

        MatrixValue resultMatrix = new MatrixValue(this.rows, this.cols);

        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.cols; col++) {
                double subValue = this.getValue(row, col) - other.getValue(row, col);
                resultMatrix.setValue(subValue, row, col);
            }
        }
        return resultMatrix;
    }

    @Override
    public StringBuilder toString(StringBuilder builder) {
        builder.append("[");
        for (int r = 0; r < this.rows; ++r) {
            builder.append("[");
            for (int c = 0; c < this.cols; ++c) {
                builder.append("[");
                builder.append(this.values[r][c]);
                builder.append("]");
            }
            builder.append("]");
        }
        builder.append("]");
        return builder;
    }
}
