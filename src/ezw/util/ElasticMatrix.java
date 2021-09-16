package ezw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElasticMatrix<T> {
    private final List<List<T>> content = new ArrayList<>();

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public Coordinate size() {
        return new Coordinate(content.size(), rows());
    }

    private int rows() {
        return content.isEmpty() ? 0 : Sugar.first(content).size();
    }

    public T get(Coordinate coordinate) {
        return get(coordinate.x, coordinate.y);
    }

    public T get(int x, int y) {
        return content.get(x).get(y);
    }

    public T set(Coordinate coordinate, T element) {
        return set(coordinate.x, coordinate.y, element);
    }

    public T set(int x, int y, T element) {
        return content.get(x).set(y, element);
    }

    public List<T> getRow(int y) {
        return getRows().get(y);
    }

    public List<T> getFirstRow() {
        return getRow(0);
    }

    public List<T> getLastRow() {
        return getRow(rows() - 1);
    }

    public List<T> getColumn(int x) {
        return getColumns().get(x);
    }

    public List<T> getFirstColumn() {
        return getColumn(0);
    }

    public List<T> getLastColumn() {
        return getColumn(content.size() - 1);
    }

    public boolean contains(T element) {
        return indexOf(element) != null;
    }

    public Coordinate indexOf(T element) {
        for (int x = 0; x < content.size(); x++) {
            int y = content.get(x).indexOf(element);
            if (y >= 0)
                return new Coordinate(x, y);
        }
        return null;
    }

    public Coordinate lastIndexOf(T element) {
        for (int x = content.size() - 1; x >= 0; x--) {
            int y = content.get(x).lastIndexOf(element);
            if (y >= 0)
                return new Coordinate(x, y);
        }
        return null;
    }

    @SafeVarargs
    public final void addRow(T... row) {
        addRowBefore(rows(), row);
    }

    @SafeVarargs
    public final void addRowBefore(int y, T... row) {
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

    @SafeVarargs
    public final void addRowAfter(int y, T... row) {
        addRowBefore(y + 1, row);
    }

    @SafeVarargs
    public final void addColumn(T... column) {
        addColumnBefore(content.size(), column);
    }

    @SafeVarargs
    public final void addColumnBefore(int x, T... column) {
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

    @SafeVarargs
    public final void addColumnAfter(int x, T... column) {
        addColumnBefore(x + 1, column);
    }

    private List<T> fill(int size) {
        List<T> list = new ArrayList<>();
        Sugar.repeat(size, () -> list.add(null));
        return list;
    }

    public List<T> removeRow(int y) {
        if (y >= rows())
            throw new IndexOutOfBoundsException("Row " + y + " doesn't exist in a total of " + rows());
        return content.stream().map(column -> column.remove(y)).collect(Collectors.toList());
    }

    public List<T> removeColumn(int x) {
        if (x >= content.size())
            throw new IndexOutOfBoundsException("Column " + x + " doesn't exist in a total of " + content.size());
        return content.remove(x);
    }

    public void clear() {
        content.clear();
    }

    public void swap(Coordinate coordinate1, Coordinate coordinate2) {
        swap(coordinate1.x, coordinate1.y, coordinate2.x, coordinate2.y);
    }

    public void swap(int x1, int y1, int x2, int y2) {
        T temp = get(x1, y1);
        set(x1, y1, get(x2, y2));
        set(x2, y2, temp);
    }

    public void swapRows(int y1, int y2) {
        for (int x = 0; x < content.size(); x++) {
            swap(x, y1, x, y2);
        }
    }

    public void swapColumns(int x1, int x2) {
        for (int y = 0; y < rows(); y++) {
            swap(x1, y, x2, y);
        }
    }

    public List<List<T>> getRows() {
        List<List<T>> rows = new ArrayList<>();
        for (int y = 0; y < rows(); y++) {
            int fy = y;
            rows.add(content.stream().map(column -> column.get(fy)).collect(Collectors.toList()));
        }
        return rows;
    }

    public List<List<T>> getColumns() {
        return content.stream().map(ArrayList::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ElasticMatrix<?> that = (ElasticMatrix<?>) o;
        return size().equals(that.size()) && content.equals(that.content);
    }

    @Override
    public String toString() {
        return toString(" ", System.lineSeparator(), "", ' ');
    }

    public String toString(String cellsDelimiter, String rowsDelimiter, String nullDefault, Character tabFiller) {
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
                if (tabFiller != null)
                    row.append(tabFiller.toString().repeat(maxLength[x] - strings[x][y].length()));
            }
            sb.append(row.toString().stripTrailing());
        }
        return sb.toString().stripTrailing();
    }

    public static class Coordinate {
        private final int x;
        private final int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Coordinate of(int x, int y) {
            return new Coordinate(x, y);
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
            return ((Coordinate) o).equals(x, y);
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
