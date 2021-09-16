package ezw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElasticMatrix<T> {
    private final List<List<T>> content = new ArrayList<>();

    public Coordinate size() {
        return new Coordinate(content.size(), rows());
    }

    private int rows() {
        return content.isEmpty() ? 0 : Sugar.first(content).size();
    }

    public T get(int x, int y) {
        return content.get(x).get(y);
    }

    public T set(int x, int y, T element) {
        return content.get(x).set(y, element);
    }

    public Coordinate indexOf(T element) {
        for (int x = 0; x < content.size(); x++) {
            int y = content.get(x).indexOf(element);
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
        if (content.isEmpty())
            content.add(new ArrayList<>());
        Sugar.repeat(Math.max(row.length - content.size(), 0), this::addColumn);
        for (int x = 0; x < content.size(); x++) {
            content.get(x).add(y, x < row.length ? row[x] : null);
        }
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
        Sugar.repeat(Math.max(column.length - rows(), 0), this::addRow);
        var columnContent = fill(Math.max(rows(), 1));
        for (int y = 0; y < rows() && y < column.length; y++) {
            columnContent.set(y, column[y]);
        }
        content.add(x, columnContent);
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

    public List<List<T>> getRows() {
        List<List<T>> rows = new ArrayList<>();
        for (int y = 0; y < rows(); y++) {
            int fy = y;
            rows.add(content.stream().map(column -> column.get(fy)).collect(Collectors.toList()));
        }
        return List.copyOf(rows);
    }

    public List<List<T>> getColumns() {
        return List.copyOf(content.stream().map(ArrayList::new).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return toString(" ", System.lineSeparator(), "");
    }

    public String toString(String cellsDelimiter, String rowsDelimiter, String nullDefault) {
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
                row.append(strings[x][y]).append(" ".repeat(maxLength[x] - strings[x][y].length()));
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
            return '[' + x + ", " + y + ']';
        }
    }
}
