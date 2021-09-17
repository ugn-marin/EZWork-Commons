package ezw.util;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A matrix of flexible size, supporting spreadsheet-like manipulation of cells, rows and columns.
 * @param <T> The type of elements in the matrix.
 */
public class Matrix<T> {
    private final List<List<T>> content = new ArrayList<>();

    /**
     * Constructs an empty matrix.
     */
    public Matrix() {
        this(0, 0);
    }

    /**
     * Constructs a matrix of the specified size.
     * @param size The matrix size, where X means columns and Y means rows.
     * @throws IndexOutOfBoundsException If a coordinate is negative, or only one of the coordinates is zero.
     */
    public Matrix(Coordinates size) {
        this(size.x, size.y);
    }

    /**
     * Constructs a matrix of the specified size.
     * @param x The columns number.
     * @param y The rows number.
     * @throws IndexOutOfBoundsException If a coordinate is negative, or only one of the coordinates is zero.
     */
    public Matrix(int x, int y) {
        validateNegative(x);
        validateNegative(y);
        if (x * y == 0 && x != y)
            throw new IndexOutOfBoundsException("The matrix size can't be zero in one dimension.");
        Sugar.repeat(x, () -> content.add(fill(y)));
    }

    /**
     * Constructs a matrix containing the data from the provided two-dimensional array.
     * @param matrix The matrix data.
     * @throws IndexOutOfBoundsException If rows length is zero.
     */
    public Matrix(T[][] matrix) {
        for (var row : matrix) {
            if (row.length == 0)
                throw new IndexOutOfBoundsException("The matrix size can't be zero in one dimension.");
            addRow(row);
        }
    }

    /**
     * Returns true if the matrix size is [0, 0].
     */
    public boolean isEmpty() {
        return content.isEmpty();
    }

    /**
     * Returns the matrix size, where X means columns and Y means rows. Null cells, including whole rows/columns of
     * nulls, are included. A zero coordinate always means the other coordinate is also zero.
     */
    public Coordinates size() {
        return new Coordinates(content.size(), rows());
    }

    private int rows() {
        return content.isEmpty() ? 0 : Sugar.first(content).size();
    }

    private void validateNegative(int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    /**
     * Returns the cell at the coordinates provided.
     * @throws IndexOutOfBoundsException If a coordinate is out of bounds.
     */
    public T get(Coordinates coordinates) {
        return get(coordinates.x, coordinates.y);
    }

    /**
     * Returns the cell at the coordinates provided.
     * @throws IndexOutOfBoundsException If a coordinate is out of bounds.
     */
    public T get(int x, int y) {
        return content.get(x).get(y);
    }

    /**
     * Updates the cell at the coordinates provided.
     * @return The replaced element.
     * @throws IndexOutOfBoundsException If a coordinate is out of bounds.
     */
    public T set(Coordinates coordinates, T element) {
        return set(coordinates.x, coordinates.y, element);
    }

    /**
     * Updates the cell at the coordinates provided.
     * @return The replaced element.
     * @throws IndexOutOfBoundsException If a coordinate is out of bounds.
     */
    public T set(int x, int y, T element) {
        return content.get(x).set(y, element);
    }

    /**
     * Returns the row at the specified index. Modifying the result will have no effect on the matrix.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public List<T> getRow(int y) {
        if (isEmpty())
            throw new IndexOutOfBoundsException("Matrix is empty, can't get row " + y);
        return content.stream().map(column -> column.get(y)).collect(Collectors.toList());
    }

    /**
     * Returns the first row. Modifying the result will have no effect on the matrix.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> getFirstRow() {
        return getRow(0);
    }

    /**
     * Returns the last row. Modifying the result will have no effect on the matrix.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> getLastRow() {
        return getRow(rows() - 1);
    }

    /**
     * Returns the column at the specified index. Modifying the result will have no effect on the matrix.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public List<T> getColumn(int x) {
        return new ArrayList<>(content.get(x));
    }

    /**
     * Returns the first column. Modifying the result will have no effect on the matrix.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> getFirstColumn() {
        return getColumn(0);
    }

    /**
     * Returns the last column. Modifying the result will have no effect on the matrix.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> getLastColumn() {
        return getColumn(content.size() - 1);
    }

    /**
     * Returns true if the matrix contains the element.
     */
    public boolean contains(T element) {
        return indexOf(element) != null;
    }

    /**
     * Returns the coordinates of the first occurrence of the element (smallest x and y), or null if not found. The
     * search is done by columns (column 0 from row 0 to Y, column 1 from row 0 etc.).
     */
    public Coordinates indexOf(T element) {
        for (int x = 0; x < content.size(); x++) {
            int y = content.get(x).indexOf(element);
            if (y >= 0)
                return new Coordinates(x, y);
        }
        return null;
    }

    /**
     * Returns the coordinates of the last occurrence of the element (greatest x and y), or null if not found. The
     * search is done by columns (column X from row Y to 0, column X-1 from row Y etc.).
     */
    public Coordinates lastIndexOf(T element) {
        for (int x = content.size() - 1; x >= 0; x--) {
            int y = content.get(x).lastIndexOf(element);
            if (y >= 0)
                return new Coordinates(x, y);
        }
        return null;
    }

    /**
     * Returns the matrix cells as an ordered list of rows. Modifying the result will have no effect on the matrix.
     */
    public List<List<T>> getRows() {
        List<List<T>> rows = new ArrayList<>();
        for (int y = 0; y < rows(); y++) {
            int fy = y;
            rows.add(content.stream().map(column -> column.get(fy)).collect(Collectors.toList()));
        }
        return rows;
    }

    /**
     * Returns the matrix cells as an ordered list of columns. Modifying the result will have no effect on the matrix.
     */
    public List<List<T>> getColumns() {
        return content.stream().map(ArrayList::new).collect(Collectors.toList());
    }

    /**
     * Returns a flat stream of the matrix elements. The order is column 0 from row 0 to Y, column 1 from row 0 etc.
     */
    public Stream<T> stream() {
        return content.stream().flatMap(Collection::stream);
    }

    /**
     * Adds a row at the end of the matrix, stretching it by 1 row.
     * @param row Optional values of the new row. If empty or smaller than the number of columns in the matrix, the
     *            missing values are padded with nulls. If greater than the number of columns in the matrix, including
     *            if the matrix is empty, it is stretched to accommodate the new value(s) by adding columns, padded with
     *            nulls where necessary. If both the row and matrix are empty, the matrix is stretched to size [1, 1],
     *            containing null.
     */
    @SafeVarargs
    public final void addRow(T... row) {
        addRowBefore(rows(), row);
    }

    /**
     * Adds a row before the specified row, stretching the matrix by 1 row, effectively making the new one row y and
     * shifting subsequent rows (if any) by 1.
     * @param y The row index. May be equal to the rows number - the row will be added at the end of the matrix.
     * @param row Optional values of the new row. If empty or smaller than the number of columns in the matrix, the
     *            missing values are padded with nulls. If greater than the number of columns in the matrix, including
     *            if the matrix is empty, it is stretched to accommodate the new value(s) by adding columns, padded with
     *            nulls where necessary. If both the row and matrix are empty, the matrix is stretched to size [1, 1],
     *            containing null.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @SafeVarargs
    public final void addRowBefore(int y, T... row) {
        validateNegative(y);
        if (y > rows())
            throw new IndexOutOfBoundsException("Row " + y + " can't be added having a total of " + rows());
        boolean wasEmpty = content.isEmpty();
        if (wasEmpty)
            content.add(fill(1));
        Sugar.repeat(Math.max(row.length - content.size(), 0), this::addColumn);
        for (int x = 0; x < content.size(); x++) {
            content.get(x).add(y, x < row.length ? row[x] : null);
        }
        if (wasEmpty)
            removeRow(y + 1);
    }

    /**
     * Adds a row after the specified row, stretching the matrix by 1 row, effectively making the new one row y + 1 and
     * shifting subsequent rows (if any) by 1.
     * @param y The row index.
     * @param row Optional values of the new row. If empty or smaller than the number of columns in the matrix, the
     *            missing values are padded with nulls. If greater than the number of columns in the matrix, including
     *            if the matrix is empty, it is stretched to accommodate the new value(s) by adding columns, padded with
     *            nulls where necessary.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @SafeVarargs
    public final void addRowAfter(int y, T... row) {
        validateNegative(y);
        addRowBefore(y + 1, row);
    }

    /**
     * Adds a column at the end of the matrix, stretching it by 1 column.
     * @param column Optional values of the new column. If empty or smaller than the number of rows in the matrix, the
     *               missing values are padded with nulls. If greater than the number of rows in the matrix, including
     *               if the matrix is empty, it is stretched to accommodate the new value(s) by adding rows, padded with
     *               nulls where necessary. If both the column and matrix are empty, the matrix is stretched to size
     *               [1, 1], containing null.
     */
    @SafeVarargs
    public final void addColumn(T... column) {
        addColumnBefore(content.size(), column);
    }

    /**
     * Adds a column before the specified column, stretching the matrix by 1 column, effectively making the new one
     * column x and shifting subsequent columns (if any) by 1.
     * @param x The column index. May be equal to the columns number - the column will be added at the end of the
     *          matrix.
     * @param column Optional values of the new column. If empty or smaller than the number of rows in the matrix, the
     *               missing values are padded with nulls. If greater than the number of rows in the matrix, including
     *               if the matrix is empty, it is stretched to accommodate the new value(s) by adding rows, padded with
     *               nulls where necessary. If both the column and matrix are empty, the matrix is stretched to size
     *               [1, 1], containing null.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @SafeVarargs
    public final void addColumnBefore(int x, T... column) {
        validateNegative(x);
        if (x > content.size())
            throw new IndexOutOfBoundsException("Column " + x + " can't be added having a total of " + content.size());
        boolean wasEmpty = content.isEmpty();
        Sugar.repeat(Math.max(column.length - rows(), 0), this::addRow);
        var columnContent = fill(Math.max(rows(), 1));
        for (int y = 0; y < rows() && y < column.length; y++) {
            columnContent.set(y, column[y]);
        }
        content.add(x, columnContent);
        if (wasEmpty && column.length > 0)
            removeColumn(x + 1);
    }

    /**
     * Adds a column after the specified column, stretching the matrix by 1 column, effectively making the new one
     * column x + 1 and shifting subsequent columns (if any) by 1.
     * @param x The column index.
     * @param column Optional values of the new column. If empty or smaller than the number of rows in the matrix, the
     *               missing values are padded with nulls. If greater than the number of rows in the matrix, including
     *               if the matrix is empty, it is stretched to accommodate the new value(s) by adding rows, padded with
     *               nulls where necessary.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @SafeVarargs
    public final void addColumnAfter(int x, T... column) {
        validateNegative(x);
        addColumnBefore(x + 1, column);
    }

    private List<T> fill(int size) {
        List<T> list = new ArrayList<>();
        Sugar.repeat(size, () -> list.add(null));
        return list;
    }

    /**
     * Removes the row at the specified index, and shrinks the matrix by 1 row.
     * @param y The row index.
     * @return The removed row.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public List<T> removeRow(int y) {
        validateNegative(y);
        if (y >= rows())
            throw new IndexOutOfBoundsException("Row " + y + " doesn't exist in a total of " + rows());
        var row = content.stream().map(column -> column.remove(y)).collect(Collectors.toList());
        if (rows() == 0)
            clear();
        return row;
    }

    /**
     * Removes the first row, and shrinks the matrix by 1 row.
     * @return The removed row.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> removeFirstRow() {
        return removeRow(0);
    }

    /**
     * Removes the last row, and shrinks the matrix by 1 row.
     * @return The removed row.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> removeLastRow() {
        return removeRow(rows() - 1);
    }

    /**
     * Removes the column at the specified index, and shrinks the matrix by 1 column.
     * @param x The column index.
     * @return The removed column.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public List<T> removeColumn(int x) {
        validateNegative(x);
        if (x >= content.size())
            throw new IndexOutOfBoundsException("Column " + x + " doesn't exist in a total of " + content.size());
        return content.remove(x);
    }

    /**
     * Removes the first column, and shrinks the matrix by 1 column.
     * @return The removed column.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> removeFirstColumn() {
        return removeColumn(0);
    }

    /**
     * Removes the last column, and shrinks the matrix by 1 column.
     * @return The removed column.
     * @throws IndexOutOfBoundsException If the matrix is empty.
     */
    public List<T> removeLastColumn() {
        return removeColumn(content.size() - 1);
    }

    /**
     * Updates the row at the specified index.
     * @param y The row index.
     * @param row Optional values of the new row. If empty or smaller than the number of columns in the matrix, the
     *            missing values are padded with nulls. If greater than the number of columns in the matrix, it is
     *            stretched to accommodate the new value(s) by adding columns, padded with nulls where necessary.
     * @return The replaced row.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @SafeVarargs
    public final List<T> setRow(int y, T... row) {
        var previous = removeRow(y);
        addRowBefore(y, row);
        Sugar.repeat(Math.max(previous.size() - content.size(), 0), this::addColumn);
        return previous;
    }

    /**
     * Updates the column at the specified index.
     * @param x The column index.
     * @param column Optional values of the new column. If empty or smaller than the number of rows in the matrix, the
     *               missing values are padded with nulls. If greater than the number of rows in the matrix, it is
     *               stretched to accommodate the new value(s) by adding rows, padded with nulls where necessary.
     * @return The replaced column.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @SafeVarargs
    public final List<T> setColumn(int x, T... column) {
        var previous = removeColumn(x);
        addColumnBefore(x, column);
        Sugar.repeat(Math.max(previous.size() - rows(), 0), this::addRow);
        return previous;
    }

    /**
     * Removes trailing rows and/or columns where all cells are null.
     * @param rows True if packing rows is required.
     * @param columns True if packing columns is required.
     */
    public void pack(boolean rows, boolean columns) {
        if (rows)
            pack(this::getLastRow, this::removeLastRow);
        if (columns)
            pack(this::getLastColumn, this::removeLastColumn);
    }

    private void pack(Supplier<List<T>> getLast, Runnable removeLast) {
        if (isEmpty())
            return;
        var last = getLast.get();
        while (last.stream().allMatch(Objects::isNull)) {
            removeLast.run();
            if (isEmpty())
                return;
            last = getLast.get();
        }
    }

    /**
     * Clears the matrix elements and shrinks it to [0, 0].
     */
    public void clear() {
        content.clear();
    }

    /**
     * Swaps between the two cells.
     * @throws IndexOutOfBoundsException If a coordinate is out of bounds.
     */
    public void swap(Coordinates coordinates1, Coordinates coordinates2) {
        swap(coordinates1.x, coordinates1.y, coordinates2.x, coordinates2.y);
    }

    /**
     * Swaps between the two cells.
     * @throws IndexOutOfBoundsException If a coordinate is out of bounds.
     */
    public void swap(int x1, int y1, int x2, int y2) {
        T temp = get(x1, y1);
        set(x1, y1, get(x2, y2));
        set(x2, y2, temp);
    }

    /**
     * Swaps between the two rows.
     * @throws IndexOutOfBoundsException If an index is out of bounds.
     */
    public void swapRows(int y1, int y2) {
        for (int x = 0; x < content.size(); x++) {
            swap(x, y1, x, y2);
        }
    }

    /**
     * Swaps between the two columns.
     * @throws IndexOutOfBoundsException If an index is out of bounds.
     */
    public void swapColumns(int x1, int x2) {
        for (int y = 0; y < rows(); y++) {
            swap(x1, y, x2, y);
        }
    }

    /**
     * Reverses the order of the columns.
     */
    public void reverseX() {
        Collections.reverse(content);
    }

    /**
     * Reverses the order of the rows.
     */
    public void reverseY() {
        content.forEach(Collections::reverse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Matrix<?> that = (Matrix<?>) o;
        return size().equals(that.size()) && content.equals(that.content);
    }

    @Override
    public String toString() {
        return toString(" ", System.lineSeparator(), "", true);
    }

    /**
     * Returns a custom string representation of the matrix using the provided parameters.
     * @param cellsDelimiter The delimiter between cells in a row. Default is space.
     * @param rowsDelimiter The delimiter between rows. Default is the line separator.
     * @param nullDefault The representation of null cells. Default is empty string.
     * @param tabFiller True if tab-like spacing in cells is required. Generally speaking, should be true if the rows
     *                  delimiter is newline. Default is true.
     * @return The string representation of the matrix.
     */
    public String toString(String cellsDelimiter, String rowsDelimiter, String nullDefault, boolean tabFiller) {
        Sugar.requireNoneNull(List.of(cellsDelimiter, rowsDelimiter, nullDefault));
        String[][] strings = new String[content.size()][rows()];
        int[] maxLength = new int[content.size()];
        for (int x = 0; x < content.size(); x++) {
            for (int y = 0; y < rows(); y++) {
                strings[x][y] = Objects.toString(get(x, y), nullDefault);
                maxLength[x] = Math.max(maxLength[x], strings[x][y].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < rows(); y++) {
            if (y > 0)
                sb.append(rowsDelimiter);
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < content.size(); x++) {
                if (x > 0)
                    row.append(cellsDelimiter);
                row.append(strings[x][y]);
                if (tabFiller)
                    row.append(" ".repeat(maxLength[x] - strings[x][y].length()));
            }
            sb.append(row.toString().stripTrailing());
        }
        return sb.toString().stripTrailing();
    }

    /**
     * X and Y coordinates.
     */
    public static class Coordinates {
        private final int x;
        private final int y;

        private Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Coordinates of(int x, int y) {
            return new Coordinates(x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            return ((Coordinates) o).equals(x, y);
        }

        public boolean equals(int x, int y) {
            return this.x == x && this.y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + "]";
        }
    }
}
