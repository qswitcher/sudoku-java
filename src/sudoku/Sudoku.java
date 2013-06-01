/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

/**
 * Sudoku is an immutable abstract datatype representing instances of Sudoku.
 * Each object is a partially completed Sudoku puzzle.
 */
public class Sudoku {
    // dimension: standard puzzle has dim 3
    private final int dim;
    // number of rows and columns: standard puzzle has size 9
    private final int size;
    // known values: square[i][j] represents the square in the ith row and jth
    // column,
    // contains -1 if the digit is not present, else i>=0 to represent the digit
    // i+1
    // (digits are indexed from 0 and not 1 so that we can take the number k
    // from square[i][j] and
    // use it to index into occupies[i][j][k])
    private final int[][] square;
    // occupies [i,j,k] means that kth symbol occupies entry in row i, column j
    private final Variable[][][] occupies;

    // Rep invariant:
    //    - dim > 1
    //    - size = dim*dim
    //    - square[i][j] must be an integer from -1 to size-1.
    //    - Each column of square must contain at most one instance of an integer from 0 to size-1
    //    - Each row of square must contain at most one instance of an integer from 0 to size-1
    //    - Each sub-square of dimension dim x dim defined by the set of values square[i+dim*n,j+dim*m]
    //          where i = 0,...,dim-1 and n = 0,...,dim-1, each for each k from 0 to size-1, must
    //          contain at most one instance of an integer from 0 to size-1
    //          
    private void checkRep() {
        Set<Integer> set = new HashSet<Integer>();
       
        // check dim and size
        assert dim > 1;
        assert size == dim*dim;
       
        // Assert that each integer only occurs once in each row
        for(int i = 0; i < size; i++){
            set.clear();    // clear the set
            for(int j = 1; j < size; j++){         
                // assert that the value of square[i][j] hasn't occurred before
                assert !set.contains(square[i][j]);              
                // digit must be in the appropriate range
                assert square[i][j] >= -1 && square[i][j] < size;
                // add it to the set
                if(square[i][j] != -1)
                    set.add(square[i][j]);
            }            
        }
        
        // Assert that each integer only occurs once in each column
        for(int j = 0; j < size; j++){
            set.clear();    // clear the set
            for(int i = 1; i < size; i++){         
                // assert that the value of square[i][j] hasn't occurred before
                assert !set.contains(square[i][j]);              
                // digit must be in the appropriate range
                assert square[i][j] >= -1 && square[i][j] < size;
                // add it to the set
                if(square[i][j] != -1)
                    set.add(square[i][j]);
            }            
        }
        
        // assert that each subarray at most contains one occurrence of an int from 0 to size-1
        // iterate over subarrays
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                // check the elements of subarray
                set.clear();    // clear set              
                for(int k = 0; k < dim; k++){
                    for(int l = 0; l < dim; l++){   
                        if(square[k+dim*i][l+dim*j] != -1){
                            // make sure there's no repeats of a digit in a sub-square
                            assert !set.contains(square[k + dim*i][l + dim*j]);
                            set.add(square[k+dim*i][l+dim*j]);                        
                        }
                        
                    }                    
                }
            }
        }

    }

    /**
     * create an empty Sudoku puzzle of dimension dim.
     * 
     * @param dim
     *            size of one block of the puzzle. For example, new Sudoku(3)
     *            makes a standard Sudoku puzzle with a 9x9 grid. Dim must be larger
     *            than 1. 
     */
    public Sudoku(int dim) {
        
        // set size
        this.dim = dim;
        size = dim*dim;
        
        // allocate space
        square = new int[size][size];
        occupies = new Variable[size][size][size];
        
        // Fill up square with -1's
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                square[i][j] = -1;
            }
        }
        
        // check rep
        this.checkRep();
    }

    /**
     * create Sudoku puzzle
     * 
     * @param squareWithBlanks
     *            digits or blanks of the Sudoku grid. square[i][j] represents
     *            the square in the ith row and jth column, contains 0 for a
     *            blank, else i to represent the digit i. So 
     *            { { 0, 0, 0, 1 }, 
     *              { 2, 3, 0, 4 }, 
     *              { 0, 0, 0, 3 }, 
     *              { 4, 1, 0, 2 } } 
     *            represents the dimension-2 Sudoku grid: 
     *                | | |1 
     *               2|3| |4 
     *                | | |3
     *               4|1| |2
     * 
     * @param dim
     *            dimension of puzzle Requires that dim*dim == square.length ==
     *            square[i].length for 0<=i<dim.
     */
    public Sudoku(int dim, int[][] squareWithBlanks) {
        // Throw null pointer exception if we get a null input
        if(squareWithBlanks == null)
            throw new NullPointerException("Input can't be null!");
                
        // matrix must be square. Throw runtime exception if it's not. 
        if(squareWithBlanks.length != squareWithBlanks[0].length)
            throw new RuntimeException("Input matrix must be square.");
        
        // set size
        this.dim = dim;
        size = squareWithBlanks.length;     // checkRep will check if size == dim*dim
               
        // allocate space
        square = new int[size][size];
        occupies = new Variable[size][size][size];                       
        
        // Copy over int's and subtract by 1.
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                square[i][j] = squareWithBlanks[i][j] -1;              
            }
        }
        
        // check rep
        this.checkRep();

    }

    /**
     * Reads in a file containing a Sudoku puzzle.
     * 
     * @param dim
     *            Dimension of puzzle.
     * @param filename
     *            of file containing puzzle. The file should contain one line
     *            per row, with each square in the row represented by a number (if known)
     *            or blanks (if unknown). Squares in a row are separated by bars ("|").
     *            An example of a dim=2 puzzle file:
     *            		 |2|3|4
	 *					3|4|1| 
	 *					2|1|4| 
 	 *					 |3|2|1
 	 *
     *            With dimension dim, the file should contain dim*dim rows, 
     *            and each row should contain dim*dim numbers, each number occupies x characters,
     *            where x=number of digits in the integer dim*dim, with front padding of spaces.
     *             
     *            For example, a dim=4 puzzle file will have each number occupy 2 characters
     *            (since 4*4=16, and 16 has 2 digits). For dim=4 puzzle single-digit numbers will be 
     *            padded with a single space at front. For example, digit "6" will be " 6", 
     *            which is 2 characters long. See Sudoku_16x16.txt in /samples for a full example.
     *            
     * @return Sudoku object corresponding to file contents
     * @throws IOException
     *             if file reading encounters an error
     * @throws ParseException
     *             if file has error in its format, i.e. doesn't fit the format description above.
     */
    public static Sudoku fromFile(int dim, String filename) throws IOException,
            ParseException {
        
        BufferedReader inputStream = null;
        
        // try and read file and put the contents into an int[][]
        try{
            inputStream = new BufferedReader(new FileReader(filename));
            
            int [][] inputInts = new int[dim*dim][dim*dim];
            
            String line;
            for(int i = 0; i < dim*dim; i++){
                // if we run out of lines prematurely, throw exception
                if((line = inputStream.readLine()) == null)
                    throw new ParseException("File is not formatted correctly! Not enough lines!");
                String [] digits = new String[dim*dim];
                digits = line.split("[|]");
                
                // If we didn't read the right number of digits on a line throw an exception
                if(digits.length != dim*dim)
                    throw new ParseException("File is not formatted correctly! Not right number of digits in a line!");
                
                // Convert the strings to ints and store them in the input
                for(int j = 0; j < dim*dim; j++){  
                    try{
                        // make sure digits[j] is padded correctly as specified.
                        if(digits[j].length() != String.valueOf(dim*dim).length())
                            throw new ParseException("File is not formatted correctly! Digits in file not properly padded width whitespace!");
                        
                        // Attempt to parse as an int
                        inputInts[i][j] = (new Integer(digits[j].trim())).intValue();
                    }catch(NumberFormatException e){
                        // Else, it throws a NumberFormatException and thus that 
                        // spot must be blank space
                        inputInts[i][j] = 0;
                    }              
                }
            }
            
            // Try and read one more line, if it doesn't fail as expected, thorw
            // a ParseException
            line = inputStream.readLine();
            if(line != null)
                throw new ParseException("File is not formatted correctly! Too many lines!");
            
            // Make the Sudoku problem
            return new Sudoku(dim,inputInts);
        } finally{
            if(inputStream != null)
                inputStream.close();
        }       
            
    }

    /**
     * Exception used for signaling grammatical errors in Sudoku puzzle files
     */
    @SuppressWarnings("serial")
    public static class ParseException extends Exception {
        public ParseException(String msg) {
            super(msg);
        }
    }

    /**
     * Produce readable string representation of this Sukoku grid, e.g. for a 4
     * x 4 sudoku problem: 
     *    |2|3|4
	 *   3|4|1| 
	 *	 2|1|4| 
 	 *	  |3|2|1
     * 
     * @return a string corresponding to this grid
     */
    public String toString() {
    	// calculate length of a square
    	String sz = String.valueOf(size);
    	int len = sz.toCharArray().length;
    	
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (square[i][j] < 0) {
                    result.append(String.format("%"+ len + "s", " "));
                } else {
                    result.append(String.format("%"+ len + "d", square[i][j] + 1));
                }
                if (j < size - 1) {
                	result.append("|");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * @return a SAT problem corresponding to the puzzle, using variables with
     *         names of the form occupies(i,j,k) to indicate that the kth symbol
     *         occupies the entry in row i, column j
     */
    public Formula getProblem() {       
        // make a starting formula
        Formula problemFormula = new Formula();
        
        // Initialize variables in occupies
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                for(int k = 0; k < size; k++){
                    occupies[i][j][k] = new Variable(String.format("v%d_%d_%d" ,i,j,k));              
                }
            }
        }        

        // Formula which holds Clause which prevents multiple digits from occuring on the same square
        Formula oneDigitPerSquare = new Formula();                 
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(square[i][j] != -1){                
                    // Solution must be consistent with starting grid        
                    // For given numbers, add the corresponding variables as single literal clauses
                    problemFormula = problemFormula.addClause(new Clause(PosLiteral.make(occupies[i][j][square[i][j]])));
                } 
                // At most one digit per square                
                for(int k = 0; k < size; k++){
                    Formula tempOneDigitPerSquare = new Formula();                    
                    for(int m = 0; m < size; m++){                    
                        if(m != k){
                            // ~Vijk + ~Vijm
                            Clause clause = new Clause(NegLiteral.make(occupies[i][j][k])).add(NegLiteral.make(occupies[i][j][m]));
                            tempOneDigitPerSquare = tempOneDigitPerSquare.addClause(clause);
                        }
                    }                   
                    
                    // AND it with the other terms
                    oneDigitPerSquare = oneDigitPerSquare.and(tempOneDigitPerSquare);
                        
                    
                }                
            }
        }
        
        
        // Combine the two Formulae
        problemFormula = problemFormula.and(oneDigitPerSquare);

        
        // In each row, each digit must appear exactly once
        Formula eachRowEachDigitOnce = new Formula();
        for(int i = 0; i < size; i++){
            for(int k = 0; k < size; k++){
                Clause atLeastOnce = new Clause();                
                for(int j = 0; j < size; j++){
                    
                    // (Vi1k + Vi2k + Vi3k + ... + Vimk)
                    atLeastOnce = atLeastOnce.add(PosLiteral.make(occupies[i][j][k]));
                    
                    
                    for(int l = 0; l < size; l++){
                        if(l != j){
                            // (!Vilk + !Vijk)  for j != l
                            Literal notVijk = NegLiteral.make(occupies[i][j][k]);
                            Literal notVilk = NegLiteral.make(occupies[i][l][k]);
                            Clause notVijkORnotVilk = (new Clause(notVijk)).add(notVilk);
                            eachRowEachDigitOnce = eachRowEachDigitOnce.addClause(notVijkORnotVilk);
                        }
                    }
                    
                }
                
                eachRowEachDigitOnce = eachRowEachDigitOnce.addClause(atLeastOnce);
            }
        }

        // combine with final formula
        problemFormula = problemFormula.and(eachRowEachDigitOnce);
        
        // In each column, each digit must appear exactly once
        Formula eachColumnEachDigitOnce = new Formula();
        for(int j = 0; j < size; j++){
            for(int k = 0; k < size; k++){
                Clause atLeastOnce = new Clause();                
                for(int i = 0; i < size; i++){
                    
                    // (V1jk + V2jk + V3jk + ... + Vnjk)
                    atLeastOnce = atLeastOnce.add(PosLiteral.make(occupies[i][j][k]));
                    
                    
                    for(int l = 0; l < size; l++){
                        if(l != i){
                            // (!Vijk + !Vljk)  for i != l
                            Literal notVijk = NegLiteral.make(occupies[i][j][k]);
                            Literal notVljk = NegLiteral.make(occupies[l][j][k]);
                            Clause notVijkORnotVljk = (new Clause(notVijk)).add(notVljk);
                            eachColumnEachDigitOnce = eachColumnEachDigitOnce.addClause(notVijkORnotVljk);
                        }
                    }
                }
                
                eachColumnEachDigitOnce = eachColumnEachDigitOnce.addClause(atLeastOnce);
            }
        }

        // combine with final formula
        problemFormula = problemFormula.and(eachColumnEachDigitOnce);
        
        
        // In each block, each digit must appear exactly once
        Formula eachBlockEachDigitOnce = new Formula();
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                for(int k = 0; k < size; k++){
                    Clause atLeastOnce = new Clause();
                    for(int l = 0; l < dim; l++){
                        for(int m = 0; m < dim; m++){   
                            atLeastOnce = atLeastOnce.add(PosLiteral.make(occupies[l + dim*i][m + dim*j][k]));         
                        }                    
                    }
                    eachBlockEachDigitOnce = eachBlockEachDigitOnce.addClause(atLeastOnce);
                }
            }
        }

        // combine with final formula
        problemFormula = problemFormula.and(eachBlockEachDigitOnce);
        
        
        return problemFormula;
    }

    /**
     * Interpret the solved SAT problem as a filled-in grid.
     * 
     * @param e
     *            Assignment of variables to values that solves this puzzle.
     *            Requires that e came from a solution to this.getProblem().
     * @return a new Sudoku grid containing the solution to the puzzle, with no
     *         blank entries.
     */
    public Sudoku interpretSolution(Environment e) {
        int [][] solution = new int[size][size];
        
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                for(int k = 0; k < size; k++){
                    if( Bool.TRUE == e.get(occupies[i][j][k])){
                        solution[i][j] = k+1;                            
                    }                                        
                }
            }
        }
            
        return new Sudoku(dim, solution);
    }

}
