package ezw.util;

import org.junit.jupiter.api.*;

import java.util.List;

public class ElasticMatrixTest {

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void afterEach() {
        System.out.println();
    }

    private void assertData(String expected, ElasticMatrix<?> matrix) {
        System.out.println(matrix);
        Assertions.assertEquals(expected, matrix.toString(",", "|", "null", null));
        var columns = matrix.getColumns();
        if (!columns.isEmpty()) {
            int rows = matrix.size().getY();
            columns.forEach(column -> Assertions.assertEquals(rows, column.size()));
        }
    }

    @SafeVarargs
    private <T> void assertData(List<T> list, T... data) {
        Assertions.assertEquals(data.length, list.size());
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertEquals(data[i], list.get(i));
        }
    }

    @Test
    void empty() {
        var matrix = new ElasticMatrix<Integer>();
        Assertions.assertTrue(matrix.size().equals(0, 0));
        assertData("", matrix);
    }

    @Test
    void firstAddEmptyRow() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addRow();
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("null", matrix);
    }

    @Test
    void stretchByEmptyRow() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addRow();
        matrix.addRow();
        Assertions.assertTrue(matrix.size().equals(1, 2));
        assertData("null|null", matrix);
    }

    @Test
    void firstAddEmptyColumn() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("null", matrix);
    }

    @Test
    void stretchByEmptyColumn() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addColumn();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(2, 1));
        assertData("null,null", matrix);
    }

    @Test
    void build2x2EmptyRows() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addRow();
        matrix.addRow();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void stretch2x2EmptyRows() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addRow();
        matrix.addRowBefore(0);
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void build2x2EmptyColumns() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addColumn();
        matrix.addColumn();
        matrix.addRow();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void stretch2x2EmptyColumns() {
        var matrix = new ElasticMatrix<Integer>();
        matrix.addColumn();
        matrix.addColumnBefore(0);
        matrix.addRow();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void buildAndFill2x2() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow();
        matrix.addRow();
        matrix.addColumn();
        matrix.set(0, 0, 'a');
        matrix.set(1, 0, 'b');
        matrix.set(0, 1, 'c');
        matrix.set(1, 1, 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
    }

    @Test
    void buildRow1() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a');
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("a", matrix);
    }

    @Test
    void buildRow2() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b');
        Assertions.assertTrue(matrix.size().equals(2, 1));
        assertData("a,b", matrix);
    }

    @Test
    void build2x2Rows() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
    }

    @Test
    void buildColumn1() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addColumn('a');
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("a", matrix);
    }

    @Test
    void buildColumn2() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addColumn('a', 'c');
        Assertions.assertTrue(matrix.size().equals(1, 2));
        assertData("a|c", matrix);
    }

    @Test
    void build2x2Columns() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addColumn('a', 'c');
        matrix.addColumn('b', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
    }

    @Test
    void removeFrom3x3Row0() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeRow(0), 'a', 'b', 'c');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("d,e,f|g,h,i", matrix);
    }

    @Test
    void removeFrom3x3Row1() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeRow(1), 'd', 'e', 'f');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,b,c|g,h,i", matrix);
    }

    @Test
    void removeFrom3x3Row2() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeRow(2), 'g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,b,c|d,e,f", matrix);
    }

    @Test
    void removeFrom3x3Column0() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeColumn(0), 'a', 'd', 'g');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("b,c|e,f|h,i", matrix);
    }

    @Test
    void removeFrom3x3Column1() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeColumn(1), 'b', 'e', 'h');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,c|d,f|g,i", matrix);
    }

    @Test
    void removeFrom3x3Column2() {
        var matrix = new ElasticMatrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeColumn(2), 'c', 'f', 'i');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|d,e|g,h", matrix);
    }
}
