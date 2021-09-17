package ezw.util;

import org.junit.jupiter.api.*;

import java.util.List;

public class MatrixTest {

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void afterEach() {
        System.out.println();
    }

    private void assertData(String expected, Matrix<?> matrix) {
        System.out.println(matrix);
        Assertions.assertEquals(expected, matrix.toString(",", "|", "null", false));
        var columns = matrix.getColumns();
        if (!columns.isEmpty()) {
            int rows = matrix.size().getY();
            columns.forEach(column -> Assertions.assertEquals(rows, column.size()));
        }
    }

    @SafeVarargs
    private <T> void assertData(Matrix<T> matrix, T... expected) {
        var size = matrix.size();
        int i = 0;
        for (int y = 0; y < size.getY(); y++) {
            for (int x = 0; x < size.getX(); x++) {
                Assertions.assertEquals(expected[i++], matrix.get(x, y));
            }
        }
    }

    @SafeVarargs
    private <T> void assertData(List<T> list, T... expected) {
        Assertions.assertEquals(expected.length, list.size());
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertEquals(expected[i], list.get(i));
        }
    }

    private void assertBadIndex(Runnable runnable) {
        try {
            runnable.run();
            Assertions.fail();
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void empty() {
        var matrix = new Matrix<Integer>();
        Assertions.assertTrue(matrix.isEmpty());
        Assertions.assertTrue(matrix.size().equals(0, 0));
        assertData("", matrix);
        Assertions.assertTrue(matrix.getRows().isEmpty());
        Assertions.assertTrue(matrix.getColumns().isEmpty());
    }

    @Test
    void firstAddEmptyRow() {
        var matrix = new Matrix<Integer>();
        matrix.addRow();
        Assertions.assertFalse(matrix.isEmpty());
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("null", matrix);
    }

    @Test
    void stretchByEmptyRow() {
        var matrix = new Matrix<Integer>();
        matrix.addRow();
        matrix.addRow();
        Assertions.assertTrue(matrix.size().equals(1, 2));
        assertData("null|null", matrix);
    }

    @Test
    void firstAddEmptyColumn() {
        var matrix = new Matrix<Integer>();
        matrix.addColumn();
        Assertions.assertFalse(matrix.isEmpty());
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("null", matrix);
    }

    @Test
    void stretchByEmptyColumn() {
        var matrix = new Matrix<Integer>();
        matrix.addColumn();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(2, 1));
        assertData("null,null", matrix);
    }

    @Test
    void build2x2EmptyRows() {
        var matrix = new Matrix<Integer>();
        matrix.addRow();
        matrix.addRow();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void stretch2x2EmptyRows() {
        var matrix = new Matrix<Integer>();
        matrix.addRow();
        matrix.addRowBefore(0);
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void build2x2EmptyColumns() {
        var matrix = new Matrix<Integer>();
        matrix.addColumn();
        matrix.addColumn();
        matrix.addRow();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void stretch2x2EmptyColumns() {
        var matrix = new Matrix<Integer>();
        matrix.addColumn();
        matrix.addColumnBefore(0);
        matrix.addRow();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
    }

    @Test
    void buildAndFill2x2() {
        var matrix = new Matrix<Character>();
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
        var matrix = new Matrix<Character>();
        matrix.addRow('a');
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("a", matrix);
        Assertions.assertTrue(matrix.contains('a'));
    }

    @Test
    void buildRow2() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        Assertions.assertTrue(matrix.size().equals(2, 1));
        assertData("a,b", matrix);
    }

    @Test
    void build2x2Rows() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        assertData(matrix, 'a', 'b', 'c', 'd');
    }

    @Test
    void buildColumn1() {
        var matrix = new Matrix<Character>();
        matrix.addColumn('a');
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("a", matrix);
        Assertions.assertTrue(matrix.contains('a'));
    }

    @Test
    void buildColumn2() {
        var matrix = new Matrix<Character>();
        matrix.addColumn('a', 'c');
        Assertions.assertTrue(matrix.size().equals(1, 2));
        assertData("a|c", matrix);
    }

    @Test
    void build2x2Columns() {
        var matrix = new Matrix<Character>();
        matrix.addColumn('a', 'c');
        matrix.addColumn('b', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        assertData(matrix, 'a', 'b', 'c', 'd');
    }

    @Test
    void indexOf() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        Assertions.assertTrue(matrix.indexOf('a').equals(0, 0));
        Assertions.assertTrue(matrix.indexOf('b').equals(1, 0));
        Assertions.assertTrue(matrix.indexOf('c').equals(0, 1));
        Assertions.assertTrue(matrix.indexOf('d').equals(1, 1));
        Assertions.assertTrue(matrix.lastIndexOf('a').equals(0, 0));
        Assertions.assertTrue(matrix.lastIndexOf('b').equals(1, 0));
        Assertions.assertTrue(matrix.lastIndexOf('c').equals(0, 1));
        Assertions.assertTrue(matrix.lastIndexOf('d').equals(1, 1));
    }

    @Test
    void getRows() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        assertData(matrix.getRow(0), 'a', 'b');
        assertData(matrix.getFirstRow(), 'a', 'b');
        assertData(matrix.getRow(1), 'c', 'd');
        assertData(matrix.getLastRow(), 'c', 'd');
        var rows = matrix.getRows();
        assertData(rows.get(0), 'a', 'b');
        assertData(rows.get(1), 'c', 'd');
    }

    @Test
    void getColumns() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        assertData(matrix.getColumn(0), 'a', 'c');
        assertData(matrix.getFirstColumn(), 'a', 'c');
        assertData(matrix.getColumn(1), 'b', 'd');
        assertData(matrix.getLastColumn(), 'b', 'd');
        var columns = matrix.getColumns();
        assertData(columns.get(0), 'a', 'c');
        assertData(columns.get(1), 'b', 'd');
    }

    @Test
    void insertInto2x2EmptyRow_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(0);
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("null,null|a,b|c,d", matrix);
    }

    @Test
    void insertInto2x2EmptyRow0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(0);
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|null,null|c,d", matrix);
    }

    @Test
    void insertInto2x2EmptyRow_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(1);
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|null,null|c,d", matrix);
    }

    @Test
    void insertInto2x2EmptyRow1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(1);
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|c,d|null,null", matrix);
    }

    @Test
    void insertInto2x2Row_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(0, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("X,Y|a,b|c,d", matrix);
    }

    @Test
    void insertInto2x2Row0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(0, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|X,Y|c,d", matrix);
    }

    @Test
    void insertInto2x2Row_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(1, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|X,Y|c,d", matrix);
    }

    @Test
    void insertInto2x2Row1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(1, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|c,d|X,Y", matrix);
    }

    @Test
    void insertInto2x2PartialRow_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(0, 'X');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("X,null|a,b|c,d", matrix);
    }

    @Test
    void insertInto2x2PartialRow0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(0, 'X');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|X,null|c,d", matrix);
    }

    @Test
    void insertInto2x2PartialRow_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(1, 'X');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|X,null|c,d", matrix);
    }

    @Test
    void insertInto2x2PartialRow1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(1, 'X');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|c,d|X,null", matrix);
    }

    @Test
    void insertInto2x2StretchRow_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(0, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("X,Y,Z|a,b,null|c,d,null", matrix);
    }

    @Test
    void insertInto2x2StretchRow0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(0, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,null|X,Y,Z|c,d,null", matrix);
    }

    @Test
    void insertInto2x2StretchRow_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowBefore(1, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,null|X,Y,Z|c,d,null", matrix);
    }

    @Test
    void insertInto2x2StretchRow1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addRowAfter(1, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,null|c,d,null|X,Y,Z", matrix);
    }

    @Test
    void insertInto2x2EmptyColumn_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(0);
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("null,a,b|null,c,d", matrix);
    }

    @Test
    void insertInto2x2EmptyColumn0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(0);
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,null,b|c,null,d", matrix);
    }

    @Test
    void insertInto2x2EmptyColumn_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(1);
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,null,b|c,null,d", matrix);
    }

    @Test
    void insertInto2x2EmptyColumn1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(1);
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,b,null|c,d,null", matrix);
    }

    @Test
    void insertInto2x2Column_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(0, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("X,a,b|Y,c,d", matrix);
    }

    @Test
    void insertInto2x2Column0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(0, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,X,b|c,Y,d", matrix);
    }

    @Test
    void insertInto2x2Column_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(1, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,X,b|c,Y,d", matrix);
    }

    @Test
    void insertInto2x2Column1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(1, 'X', 'Y');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,b,X|c,d,Y", matrix);
    }

    @Test
    void insertInto2x2PartialColumn_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(0, 'X');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("X,a,b|null,c,d", matrix);
    }

    @Test
    void insertInto2x2PartialColumn0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(0, 'X');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,X,b|c,null,d", matrix);
    }

    @Test
    void insertInto2x2PartialColumn_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(1, 'X');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,X,b|c,null,d", matrix);
    }

    @Test
    void insertInto2x2PartialColumn1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(1, 'X');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,b,X|c,d,null", matrix);
    }

    @Test
    void insertInto2x2StretchColumn_0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(0, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("X,a,b|Y,c,d|Z,null,null", matrix);
    }

    @Test
    void insertInto2x2StretchColumn0_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(0, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,X,b|c,Y,d|null,Z,null", matrix);
    }

    @Test
    void insertInto2x2StretchColumn_1() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnBefore(1, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,X,b|c,Y,d|null,Z,null", matrix);
    }

    @Test
    void insertInto2x2StretchColumn1_() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        matrix.addColumnAfter(1, 'X', 'Y', 'Z');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,X|c,d,Y|null,null,Z", matrix);
    }

    @Test
    void removeFrom3x3Row0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeFirstRow(), 'a', 'b', 'c');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("d,e,f|g,h,i", matrix);
    }

    @Test
    void removeFrom3x3Row1() {
        var matrix = new Matrix<Character>();
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
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeLastRow(), 'g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 2));
        assertData("a,b,c|d,e,f", matrix);
    }

    @Test
    void removeFrom3x3Column0() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeFirstColumn(), 'a', 'd', 'g');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("b,c|e,f|h,i", matrix);
    }

    @Test
    void removeFrom3x3Column1() {
        var matrix = new Matrix<Character>();
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
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b', 'c');
        matrix.addRow('d', 'e', 'f');
        matrix.addRow('g', 'h', 'i');
        Assertions.assertTrue(matrix.size().equals(3, 3));
        assertData("a,b,c|d,e,f|g,h,i", matrix);
        assertData(matrix.removeLastColumn(), 'c', 'f', 'i');
        Assertions.assertTrue(matrix.size().equals(2, 3));
        assertData("a,b|d,e|g,h", matrix);
    }

    @Test
    void updates() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        Assertions.assertEquals('d', matrix.set(1, 1, 'a'));
        assertData("a,b|c,a", matrix);
        Assertions.assertTrue(matrix.indexOf('a').equals(0, 0));
        Assertions.assertTrue(matrix.lastIndexOf('a').equals(1, 1));
        Assertions.assertNull(matrix.indexOf('d'));
        Assertions.assertNull(matrix.lastIndexOf('d'));
        matrix.swap(matrix.indexOf('b'), matrix.indexOf('c'));
        assertData("a,c|b,a", matrix);
        matrix.swapRows(0, 1);
        assertData("b,a|a,c", matrix);
        matrix.swapColumns(0, 1);
        assertData("a,b|c,a", matrix);
    }

    @Test
    void packRows() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        matrix.addRow();
        matrix.addRow();
        matrix.addColumn();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(4, 4));
        assertData("a,b,null,null|c,d,null,null|null,null,null,null|null,null,null,null", matrix);
        matrix.pack(true, false);
        Assertions.assertTrue(matrix.size().equals(4, 2));
        assertData("a,b,null,null|c,d,null,null", matrix);
    }

    @Test
    void packColumns() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        matrix.addRow();
        matrix.addRow();
        matrix.addColumn();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(4, 4));
        assertData("a,b,null,null|c,d,null,null|null,null,null,null|null,null,null,null", matrix);
        matrix.pack(false, true);
        Assertions.assertTrue(matrix.size().equals(2, 4));
        assertData("a,b|c,d|null,null|null,null", matrix);
    }

    @Test
    void packBoth() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        matrix.addRow();
        matrix.addRow();
        matrix.addColumn();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(4, 4));
        assertData("a,b,null,null|c,d,null,null|null,null,null,null|null,null,null,null", matrix);
        matrix.pack(true, true);
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
    }

    @Test
    void packEmpty() {
        var matrix = new Matrix<Character>();
        matrix.addRow();
        matrix.addRow();
        matrix.addColumn();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("null,null|null,null", matrix);
        matrix.pack(true, true);
        Assertions.assertTrue(matrix.isEmpty());
    }

    @Test
    void reverseX() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        matrix.reverseX();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("b,a|d,c", matrix);
    }

    @Test
    void reverseY() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        matrix.reverseY();
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("c,d|a,b", matrix);
    }

    @Test
    void equals() {
        var matrix1 = new Matrix<Character>();
        matrix1.addRow('a', 'b');
        matrix1.addRow('c', 'd');
        var matrix2 = new Matrix<Character>();
        matrix2.addRow('a', 'b');
        matrix2.addRow('c', 'd');
        Assertions.assertEquals(matrix1, matrix2);
        Assertions.assertEquals('d', matrix2.set(1, 1, 'a'));
        Assertions.assertNotEquals(matrix1, matrix2);
    }

    @Test
    void unmodifiable() {
        var matrix = new Matrix<Character>();
        matrix.addRow('a', 'b');
        matrix.addRow('c', 'd');
        matrix.getRow(0).add('X');
        matrix.getRows().get(0).add('X');
        matrix.getColumn(0).add('Y');
        matrix.getColumns().get(0).add('Y');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
    }

    @Test
    void badIndexes() {
        var matrix = new Matrix<Character>();
        assertBadIndexesNegative(matrix);
        assertBadIndexesEmpty(matrix);
        matrix.addRow('a', 'b');
        Assertions.assertTrue(matrix.size().equals(2, 1));
        assertData("a,b", matrix);
        assertBadIndexesNegative(matrix);
        assertBadIndex(() -> matrix.get(2, 0));
        assertBadIndex(() -> matrix.get(0, 1));
        assertBadIndex(() -> matrix.getRow(1));
        assertBadIndex(() -> matrix.getColumn(2));
        assertBadIndex(() -> matrix.addRowAfter(1));
        assertBadIndex(() -> matrix.addColumnAfter(2));
        assertBadIndex(() -> matrix.addRowAfter(1, 'X'));
        assertBadIndex(() -> matrix.addColumnAfter(2, 'X'));
        assertBadIndex(() -> matrix.addRowBefore(2));
        assertBadIndex(() -> matrix.addColumnBefore(3));
        assertBadIndex(() -> matrix.addRowBefore(2, 'X'));
        assertBadIndex(() -> matrix.addColumnBefore(3, 'X'));
        assertBadIndex(() -> matrix.removeRow(1));
        assertBadIndex(() -> matrix.removeColumn(2));
        matrix.addRow('c', 'd');
        Assertions.assertTrue(matrix.size().equals(2, 2));
        assertData("a,b|c,d", matrix);
        assertBadIndexesNegative(matrix);
        assertBadIndex(() -> matrix.get(2, 2));
        assertBadIndex(() -> matrix.getRow(2));
        assertBadIndex(() -> matrix.addRowAfter(2));
        assertBadIndex(() -> matrix.addRowAfter(2, 'X'));
        assertBadIndex(() -> matrix.addRowBefore(3));
        assertBadIndex(() -> matrix.addRowBefore(3, 'X'));
        assertBadIndex(() -> matrix.removeRow(2));
        matrix.removeRow(1);
        Assertions.assertTrue(matrix.size().equals(2, 1));
        assertData("a,b", matrix);
        assertBadIndexesNegative(matrix);
        assertBadIndex(() -> matrix.get(2, 0));
        assertBadIndex(() -> matrix.get(0, 1));
        assertBadIndex(() -> matrix.getRow(1));
        assertBadIndex(() -> matrix.getColumn(2));
        assertBadIndex(() -> matrix.addRowAfter(1));
        assertBadIndex(() -> matrix.addColumnAfter(2));
        assertBadIndex(() -> matrix.addRowAfter(1, 'X', 'Y'));
        assertBadIndex(() -> matrix.addColumnAfter(2, 'X', 'Y'));
        assertBadIndex(() -> matrix.addRowBefore(2));
        assertBadIndex(() -> matrix.addColumnBefore(3));
        assertBadIndex(() -> matrix.addRowBefore(2, 'X', 'Y'));
        assertBadIndex(() -> matrix.addColumnBefore(3, 'X', 'Y'));
        assertBadIndex(() -> matrix.removeRow(1));
        assertBadIndex(() -> matrix.removeColumn(2));
        matrix.removeColumn(1);
        Assertions.assertTrue(matrix.size().equals(1, 1));
        assertData("a", matrix);
        assertBadIndexesNegative(matrix);
        assertBadIndex(() -> matrix.get(1, 0));
        assertBadIndex(() -> matrix.get(0, 1));
        assertBadIndex(() -> matrix.getRow(1));
        assertBadIndex(() -> matrix.getColumn(1));
        assertBadIndex(() -> matrix.addRowAfter(1));
        assertBadIndex(() -> matrix.addColumnAfter(1));
        assertBadIndex(() -> matrix.addRowAfter(1, 'X', 'Y'));
        assertBadIndex(() -> matrix.addColumnAfter(1, 'X', 'Y'));
        assertBadIndex(() -> matrix.addRowBefore(2));
        assertBadIndex(() -> matrix.addColumnBefore(2));
        assertBadIndex(() -> matrix.addRowBefore(2, 'X', 'Y'));
        assertBadIndex(() -> matrix.addColumnBefore(2, 'X', 'Y'));
        assertBadIndex(() -> matrix.removeRow(1));
        assertBadIndex(() -> matrix.removeColumn(1));
        matrix.clear();
        Assertions.assertTrue(matrix.size().equals(0, 0));
        assertData("", matrix);
        assertBadIndexesNegative(matrix);
        assertBadIndexesEmpty(matrix);
    }

    private void assertBadIndexesEmpty(Matrix<Character> matrix) {
        assertBadIndex(() -> matrix.get(0, 0));
        assertBadIndex(matrix::getFirstRow);
        assertBadIndex(matrix::getFirstColumn);
        assertBadIndex(() -> matrix.addRowAfter(0));
        assertBadIndex(() -> matrix.addColumnAfter(0));
        assertBadIndex(() -> matrix.addRowAfter(0, 'X'));
        assertBadIndex(() -> matrix.addColumnAfter(0, 'X'));
        assertBadIndex(() -> matrix.addRowBefore(1));
        assertBadIndex(() -> matrix.addColumnBefore(1));
        assertBadIndex(() -> matrix.addRowBefore(1, 'X'));
        assertBadIndex(() -> matrix.addColumnBefore(1, 'X'));
        assertBadIndex(matrix::removeFirstRow);
        assertBadIndex(matrix::removeFirstColumn);
    }

    private void assertBadIndexesNegative(Matrix<Character> matrix) {
        assertBadIndex(() -> matrix.get(-1, 0));
        assertBadIndex(() -> matrix.get(0, -1));
        assertBadIndex(() -> matrix.getRow(-1));
        assertBadIndex(() -> matrix.getColumn(-1));
        assertBadIndex(() -> matrix.addRowAfter(-1));
        assertBadIndex(() -> matrix.addColumnAfter(-1));
        assertBadIndex(() -> matrix.addRowAfter(-1, 'X'));
        assertBadIndex(() -> matrix.addColumnAfter(-1, 'X'));
        assertBadIndex(() -> matrix.addRowBefore(-1));
        assertBadIndex(() -> matrix.addColumnBefore(-1));
        assertBadIndex(() -> matrix.addRowBefore(-1, 'X'));
        assertBadIndex(() -> matrix.addColumnBefore(-1, 'X'));
        assertBadIndex(() -> matrix.removeRow(-1));
        assertBadIndex(() -> matrix.removeColumn(-1));
    }
}
