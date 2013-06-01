package sudoku;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import sudoku.Sudoku.ParseException;


public class SudokuTest {
    

    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test
    public void emptySudokuTest() {        
        // Test empty Sudoku method
        Sudoku emptySudoku = new Sudoku(3);
        String expected = "";
        for(int i = 0; i < 9; i++)
            expected += " | | | | | | | | \n";              

        assertEquals(expected,emptySudoku.toString());
    }
    
    @Test
    public void partiallyFilled4x4SudokuConstructorTest() {        
        // Test with a partially filled Sudoku method
        int [][] input = {{0,1,0,0},
                          {0,0,1,2},
                          {3,0,0,0},
                          {4,0,0,3}};
        Sudoku sudoku = new Sudoku(2,input);
        String expected = " |1| | \n"+
                          " | |1|2\n"+
                          "3| | | \n"+
                          "4| | |3\n";          

        assertEquals(expected,sudoku.toString());
    }
    
    @Test
    public void partiallyFilled9x9SudokuConstructorTest() {        
        // Test with a partially filled Sudoku method
        int [][] input = {{0,6,0,1,0,4,0,5,0},
                          {0,0,8,3,0,5,6,0,0},
                          {2,0,0,0,0,0,0,0,1},
                          {8,0,0,4,0,7,0,0,6},
                          {0,0,6,0,0,0,3,0,0},
                          {7,0,0,9,0,1,0,0,4},
                          {5,0,0,0,0,0,0,0,2},
                          {0,0,7,2,0,6,9,0,0},
                          {0,4,0,5,0,8,0,7,0}};
        Sudoku sudoku = new Sudoku(3,input);
        String expected = " |6| |1| |4| |5| \n"+
                          " | |8|3| |5|6| | \n"+
                          "2| | | | | | | |1\n"+
                          "8| | |4| |7| | |6\n"+
                          " | |6| | | |3| | \n"+
                          "7| | |9| |1| | |4\n"+
                          "5| | | | | | | |2\n"+
                          " | |7|2| |6|9| | \n"+
                          " |4| |5| |8| |7| \n";          

        assertEquals(expected,sudoku.toString());
    }
    
    
    @Test(expected=AssertionError.class)
    public void checkRepTestRepeatColumn() {   
        // Test with a mal-formed Sudoku 
        // that has a repeat digit in a column
        int [][] input = {{0,0,0,0},
                          {0,0,0,0},
                          {4,0,0,0},
                          {4,0,0,0}};
        new Sudoku(2,input);  

    }
    
    @Test(expected=AssertionError.class)
    public void checkRepTestRepeatRow() {   
        // Test with a mal-formed Sudoku 
        // that has a repeat digit in a column
        int [][] input = {{0,0,0,0},
                          {0,0,0,0},
                          {4,4,0,0},
                          {0,0,0,0}};
        new Sudoku(2,input);  

    }
    
    @Test(expected=AssertionError.class)
    public void checkRepDigitOutOfRangeTooHighTest() {   
        // Test with a mal-formed Sudoku 
        // that has a digit out of range
        int [][] input = {{0,0,0,0},
                          {0,0,0,0},
                          {5,0,0,0},
                          {0,0,0,0}};
        new Sudoku(2,input);  

    }
    
    @Test(expected=AssertionError.class)
    public void checkRepDigitOutOfRangeTooLowTest() {   
        // Test with a mal-formed Sudoku 
        // that a digit out of range
        int [][] input = {{0,0,0,0},
                          {0,0,0,0},
                          {-1,0,0,0},
                          {0,0,0,0}};
       new Sudoku(2,input);  

    }
    
    @Test(expected=AssertionError.class)
    public void checkRepSubSquareRepeatTest() {   
        // Test with a mal-formed Sudoku 
        // that has a repeat digit in a sub-square
        int [][] input = {{0,0,0,0},
                          {0,0,0,0},
                          {0,0,0,4},
                          {0,0,4,0}};
        new Sudoku(2,input);  

    }
    
    @Test(expected=AssertionError.class)
    public void checkRepSizeNotDimSquaredTest() {   
        // Test with a mal-formed Sudoku such
        // that size != dim*dim
        int [][] input = {{0,0,0,0,0},
                          {0,0,0,0,0},
                          {0,0,0,0,0},
                          {0,0,0,0,0},
                          {0,0,0,0,0}};
        new Sudoku(2,input);  

    }
    
    @Test
    public void fileInput16x16SodokuTest(){
        // Test fromFile method with the 16x16 file.         
        Sudoku sudoku = new Sudoku(4);
        String expectedOutput =    "  |11| 9|  |  |16|13| 4|  |  |14|  |10| 6|15|  \n"+ 
                                " 4|12|15|  | 3| 6|  |11|  | 5|  | 1|16| 7|14| 2\n"+
                                " 1|  | 6|  |15| 2|  |  |11| 9|10|  |  |  | 8|  \n"+
                                "  |13|  |  |  | 1|  |  | 4| 6|  |15|  |  |  |  \n"+
                                "  |  |  |  |  |  |15|  | 8| 1| 5| 3|  | 4|11| 7\n"+
                                " 6|  | 1|  |  |12| 8|  | 9|  |  | 2|  |  | 3|  \n"+
                                "14|  | 4|13| 6|  |  | 3|  |12| 7|10| 8|  | 2|  \n"+
                                " 3| 8|  |  | 4| 7| 2|  | 6|  |  |  |  |12|16| 5\n"+
                                "13|  |  |16|  | 8|14|10| 3| 4|15|  |12| 5| 1|11\n"+
                                "  |  |  | 6| 2|  |  | 1|10|  |11|  |15| 3|  | 9\n"+
                                " 7|  |  |12|  | 4|  |15| 5|  | 9|14|  |  |  |  \n"+
                                "10|  |  | 8|  |  |11|  |  |  | 1|12| 4|  |13|16\n"+
                                "  |  |  |  |  |  | 7|  |15| 2|  |  |  |  |12| 3\n"+
                                "  |  | 7|  |  |10| 6|  | 1| 8|  |13|11|  | 9|14\n"+
                                " 8| 6| 5|  |  | 3|  |  |14|  |  | 9|  |  |  |  \n"+
                                "  |16|  | 2|  |  |  |14|  |10|  |  |  |  |  |  \n" ; 
        

        
        try {
            sudoku = Sudoku.fromFile(4, "samples\\sudoku_16x16.txt");
            assertEquals(expectedOutput,sudoku.toString());            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
    }
    
    
    @Test(expected = ParseException.class)
    public void fileInputTooManyRowsTest() throws ParseException {
        // Test fromFile with a mal-formed file with too many rows
        
        try {
            Sudoku.fromFile(2, "samples\\sudoku_too_many_rows.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test(expected = ParseException.class)
    public void fileInputTooManyColsTest() throws ParseException {
        // Test fromFile with a mal-formed file with too many rows
       
        try {
            Sudoku.fromFile(2, "samples\\sudoku_too_many_cols.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test(expected = ParseException.class)
    public void fileInputWrongDimTest() throws ParseException {
        // Test fromFile with dim set wrong (2 instead of 3 for the regular Sudoku files)
       
        try {
            Sudoku.fromFile(2, "samples\\sudoku_easy.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test(expected = ParseException.class)
    public void fileInputNotRectangularTest() throws ParseException {
        // Test fromFile with a file with a non-rectangular input (some rows are not the same size)
       
        try {
            Sudoku.fromFile(3, "samples\\sudoku_not_rectangular.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test(expected = ParseException.class)
    public void fileInputTooLitteWitespacePaddingTest() throws ParseException {
        // Test fromFile with a file with a file that does not have enough whitespace padding
        
        try {
            Sudoku.fromFile(4, "samples\\sudoku_no_whitespace_padding.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test(expected = ParseException.class)
    public void fileInputTooMuchWhitespacePaddingTest() throws ParseException {
        // Test fromFile with a file with a file that has too much whitespace padding
       
        try {
            Sudoku.fromFile(4, "samples\\sudoku_too_much_whitespace.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @Test(expected = IOException.class)
    public void fileInputIOExceptionTest() throws IOException {
        // Test fromFile with a file that doesn't exist.
                
        try {
            Sudoku.fromFile(4, "samples\\yo_mammas_so_fat$#@%@#%^%&#$.txt");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
    }   
       
    
}