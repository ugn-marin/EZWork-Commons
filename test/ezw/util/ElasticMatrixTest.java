package ezw.util;

import org.junit.jupiter.api.*;

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
        Assertions.assertEquals(expected, matrix.toString(",", "|", "null"));
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
}
