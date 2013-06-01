package sat.formula;

import static org.junit.Assert.*;

import java.util.Iterator;

import immutable.Cons;
import immutable.Empty;
import immutable.ImList;

import org.junit.Test;

import sat.env.Variable;

public class FormulaTest {    
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }    
    
    @Test
    public void emptyInputConstructorTest(){      
        // Test empty Formula constructor
        Formula formula = new Formula();
        assertTrue(clauseListEqual(formula.getClauses(),new Empty<Clause>()));
    }   
     
    @Test
    public void literalInputConstructorTest(){
        // Test Formula(Literal) constructor
        // with a positive literal
        // Result: Problem[[x]]
        Variable var = new Variable("x");
        Formula formula = new Formula(var);      
        
        // expected list
        ImList<Clause> expected = new Cons<Clause>(new Clause(PosLiteral.make(var)));
        
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }  
    
    @Test
    public void clauseInputConstructorEmptyClauseTest(){
        // Test Formula(Clause) with an empty clause
        // Result: Problem[[]]
        Formula formula = new Formula(make());
        
        // expected list
        ImList<Clause> expected = new Cons<Clause>(new Clause());
        
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }
    
    @Test
    public void clauseInputConstructorOnePosLiteralClauseTest(){
        // Test Formula(Clause) with an clause with one Posliteral
        // Result: Problem[[x]]
        Literal posLiteral = PosLiteral.make("x");
        Formula formula = new Formula(make(posLiteral));
        
        // expected list
        ImList<Clause> expected = new Cons<Clause>(new Clause(posLiteral));
        
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }
    
    @Test
    public void clauseInputConstructorOneNegLiteralClauseTest(){
        // Test Formula(Clause) with an clause with one Posliteral
        // Result: Problem[[~x]]
        Literal negLiteral = NegLiteral.make("x");
        Formula formula = new Formula(make(negLiteral));
        
        // expected list
        ImList<Clause> expected = new Cons<Clause>(new Clause(negLiteral));
        
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }
    
    @Test
    public void clauseInputConstructorManyPosLiteralClauseTest(){
        // Test Formula(Clause) with an clause with several Posliterals
        // Result: Problem[[x,y,z]]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        Clause inputClause = make(xLit).add(yLit).add(zLit);
        
        // constructed formula
        Formula formula = new Formula(inputClause);
        
        // expected result
        ImList<Clause> expected = new Cons<Clause>(inputClause);
        
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }
    
    @Test
    public void clauseInputConstructorManyMixedLiteralClauseTest(){
        // Test Formula(Clause) with an clause with Posliteral and NegLiterals
        // Result: Problem[[x,y,~z]]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal nzLit = NegLiteral.make("z");
        Clause inputClause = make(xLit).add(yLit).add(nzLit);
        
        // construct formula
        Formula formula = new Formula(inputClause);
        
        // expected result
        ImList<Clause> expected = new Cons<Clause>(inputClause);
        
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }    
    
    @Test
    public void addClauseToEmptyFormulaTest(){
        // Test Formula.addClause(Clause) by making an empty
        // formula and adding a Clause to it clause
        // Problem[].addClause([x,y,z]) = Problem[[x,y,z]]
        
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause = make(xLit).add(yLit).add(zLit);
        
        Formula formula = (new Formula()).addClause(inputClause);
        ImList<Clause> expected = new Cons<Clause>(inputClause);
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }
    
    @Test
    public void addClauseToNonEmptyFormulaTest(){
        // Test Formula.addClause(Clause) by making a formula
        // with a clause and then try and add another clause to it
        // Problem[[x,y]].addClause([x,y,z]) = Problem[[x,y,z],[x,y]]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause = make(xLit).add(yLit).add(zLit);
        
        Formula startingFormula = new Formula(make(xLit).add(yLit));
        
        Formula formula = startingFormula.addClause(inputClause);
        ImList<Clause> tempExpected = new Cons<Clause>(inputClause);
        ImList<Clause> expected = tempExpected.addToFront(make(xLit).add(yLit));
        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }
    
    @Test
    public void getClausesNonEmptyTest(){
        // Test the getClauses method.
        // Result: Clauses: [[x],[y]]
        Literal posLiteral1 = PosLiteral.make("x");
        Literal posLiteral2 = PosLiteral.make("y");
        Formula tempFormula = new Formula(make(posLiteral1));
        Formula formula = tempFormula.addClause(make(posLiteral2));
        
        ImList<Clause> tempExpected = new Cons<Clause>(make(posLiteral1));
        ImList<Clause> expected = tempExpected.addToFront(make(posLiteral2));       

        assertTrue(clauseListEqual(formula.getClauses(),expected));

    }
    
    @Test
    public void getClausesEmptyTest(){
        // Test the getClauses method with empty formula        
        ImList<Clause> expected = new Empty<Clause>();

        assertTrue(clauseListEqual(new Formula().getClauses(),expected));
    }
    
    @Test
    public void getIteratorEmptyFormulaTest(){
        // test iterator with empty formula
        Formula formula = new Formula();        
        
        Iterator<Clause> iterator = formula.iterator();        
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void getIteratorNonEmptyFormulaTest(){
        // test iterator with non-empty formula
        // Result: Problem[[x,z],[x,y],[y,z]]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause1 = make(xLit).add(zLit);
        Clause inputClause2 = make(xLit).add(yLit);
        Clause inputClause3 = make(yLit).add(zLit);        
        
        Formula formula = (new Formula(inputClause1)).addClause(inputClause2).addClause(inputClause3);
        
        Iterator<Clause> iterator = formula.iterator();  
        
        // Test iterator by using it to create an ImList<Clause> instance
        ImList<Clause> expected = new Empty<Clause>();
        while(iterator.hasNext()){
            expected = expected.addToFront(iterator.next());
        }       

        assertTrue(clauseListEqual(formula.getClauses(),expected));
    }
    
    @Test
    public void andMethodTwoEmptyFormulaeTest(){
        // Test "and" with two empty formulae
        Formula input1 = new Formula();
        Formula input2 = new Formula();
        
        Formula result = input1.and(input2);
        
        assertTrue(clauseListEqual(result.getClauses(), new Empty<Clause>()));

    }
    
    @Test
    public void andMethodRightNonEmptyLeftEmptyTest(){
        // Test "and" with
        // Problem[[x,y],[z]] "and" Problem[]
        // Expected: Problem[[x,y],[z]]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause1 = make(xLit).add(yLit);
        Clause inputClause2 = make(zLit);
        
        Formula input = new Formula(inputClause1).addClause(inputClause2);
        
        Formula result = input.and(new Formula());
        
        assertTrue(clauseListEqual(result.getClauses(), input.getClauses()));

    }
    
    @Test
    public void andMethodRightEmptyLeftNonEmptyTest(){
        // Test "and" with
        // Problem[] "and" Problem[[x,y],[z]] 
        // Expected: Problem[[x,y],[z]]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause1 = make(xLit).add(yLit);
        Clause inputClause2 = make(zLit);
        
        Formula input = new Formula(inputClause1).addClause(inputClause2);
        
        Formula result = new Formula().and(input);
        
        assertTrue(clauseListEqual(result.getClauses(), input.getClauses()));

    }
    
    @Test
    public void andMethodOperandsNonEmptyTest(){
        // Test "and" with
        // Problem[[[y,z][y]] "and" Problem[[x,y],[z]] 
        // Expected: Problem[[y,z],[y],[x,y],[z]]
       
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause1 = make(yLit).add(zLit);
        Clause inputClause2 = make(yLit);
        Clause inputClause3 = make(xLit).add(yLit);
        Clause inputClause4 = make(zLit);
        
        Formula input1 = new Formula(inputClause1).addClause(inputClause2);
        Formula input2 = new Formula(inputClause3).addClause(inputClause4);
        
        Formula result = input1.and(input2);
        Formula expectedResult = new Formula(inputClause1).addClause(inputClause2).addClause(inputClause3).addClause(inputClause4);
        
        assertTrue(clauseListEqual(result.getClauses(), expectedResult.getClauses()));

    }
    
    @Test
    public void orMethodTwoEmptyFormulaeTest(){
        // Test "or" with two empty formulae

        Formula result = new Formula().or(new Formula());
        
        assertTrue(clauseListEqual(result.getClauses(), new Empty<Clause>()));

    }
    
    @Test
    public void orMethodRightNonEmptyLeftEmptyTest(){
        // Test "or" with
        // Problem[[x,y],[z]] "and" Problem[]
        // Expected: Problem[]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause1 = make(xLit).add(yLit);
        Clause inputClause2 = make(zLit);
        
        Formula input = new Formula(inputClause1).addClause(inputClause2);
        
        Formula result = input.or(new Formula());
        
        assertTrue(clauseListEqual(result.getClauses(), new Formula().getClauses()));

    }
    
    @Test
    public void orMethodRightEmptyLeftNonEmptyTest(){
        // Test "and" with
        // Problem[] "and" Problem[[x,y],[z]] 
        // Expected: Problem[]
        Literal xLit = PosLiteral.make("x");
        Literal yLit = PosLiteral.make("y");
        Literal zLit = PosLiteral.make("z");
        
        Clause inputClause1 = make(xLit).add(yLit);
        Clause inputClause2 = make(zLit);
        
        Formula input = new Formula(inputClause1).addClause(inputClause2);
        
        Formula result = new Formula().or(input);
        
        assertTrue(clauseListEqual(result.getClauses(), new Formula().getClauses()));

    }
    
    @Test
    public void orMethodTestCNFForm(){
        // Test A + B = (A + B)
        // that is, it does nothing to something already in CNF
        
        Formula A = new Formula(make(PosLiteral.make("A")));
        Formula B = new Formula(make(PosLiteral.make("B")));        
        
        // Compute result
        Formula result = A.or(B);        
       
        // Compute expected result
        ImList<Clause> expected = (new Cons<Clause>(make(PosLiteral.make("A")).add(PosLiteral.make("B"))));
        
        assertTrue(clauseListEqual(result.getClauses(), expected));
    }  
    
    
    @Test
    public void orMethodTest1(){
        // Test A + BC = (A + B)*(A + C)
        
        Formula A = new Formula(make(PosLiteral.make("A")));
        Formula temp = new Formula(make(PosLiteral.make("B")));
        Formula BC = temp.addClause(make(PosLiteral.make("C")));
        
        // Compute result
        Formula result = A.or(BC);        
       
        // Compute expected result
        Clause temp1 = make(PosLiteral.make("A")).add(PosLiteral.make("B"));
        Clause temp2 = make(PosLiteral.make("A")).add(PosLiteral.make("C"));
        ImList<Clause> expected = (new Cons<Clause>(temp2)).addToFront(temp1);
        
        assertTrue(clauseListEqual(result.getClauses(), expected));
    }    
    
    
    @Test
    public void orMethodTest2(){
        // Test AB + C = (A + C)*(B + C)
        
        Formula temp = new Formula(make(PosLiteral.make("A")));
        Formula AB = temp.addClause(make(PosLiteral.make("B")));
        Formula C = new Formula(make(PosLiteral.make("C")));

        // Compute result
        Formula result = AB.or(C);        
       
        // Compute expected result
        Clause temp1 = make(PosLiteral.make("A")).add(PosLiteral.make("C"));
        Clause temp2 = make(PosLiteral.make("B")).add(PosLiteral.make("C"));
        ImList<Clause> expected = (new Cons<Clause>(temp2)).addToFront(temp1);       
        
        assertTrue(clauseListEqual(result.getClauses(), expected));
    }  
    
    @Test
    public void orMethodTest3(){
        // Test AB + CD = (A + C)*(A + D)*(B + C)*(B + D)
        
        Formula temp1 = new Formula(make(PosLiteral.make("A")));
        Formula AB = temp1.addClause(make(PosLiteral.make("B")));
        Formula temp2 = new Formula(make(PosLiteral.make("C")));
        Formula CD = temp2.addClause(make(PosLiteral.make("D")));

        // Compute result
        Formula result = AB.or(CD);        

        // Compute expected result
        Clause AorC = make(PosLiteral.make("A")).add(PosLiteral.make("C"));
        Clause AorD = make(PosLiteral.make("A")).add(PosLiteral.make("D"));
        Clause BorC = make(PosLiteral.make("B")).add(PosLiteral.make("C"));
        Clause BorD = make(PosLiteral.make("B")).add(PosLiteral.make("D"));
        ImList<Clause> expected = (((new Cons<Clause>(BorD)).addToFront(AorD)).addToFront(AorC)).addToFront(BorC);       
        
        assertTrue(clauseListEqual(result.getClauses(), expected));
    }  
    
    @Test
    public void orMethodTest4(){
        // Test ~A + A = 1
        
        Formula nA = new Formula(make(NegLiteral.make("A")));
        Formula A = new Formula(make(PosLiteral.make("A")));

        // Compute result
        Formula result = nA.or(A);        

        // Compute expected result
        Formula expected = new Formula();
        
        assertTrue(clauseListEqual(expected.getClauses(),result.getClauses()));
    }  
    
    @Test
    public void orMethodTest5(){
        // Test ~AB + AC = (~A + C)(B + A)(B + C)
        
        Formula temp1 = new Formula(make(NegLiteral.make("A")));
        Formula nAB = temp1.addClause(make(PosLiteral.make("B")));
        Formula temp2 = new Formula(make(PosLiteral.make("A")));
        Formula AC = temp2.addClause(make(PosLiteral.make("C")));

        // Compute result
        Formula result = nAB.or(AC);        

        // Compute expected result
        Clause nAorC = make(NegLiteral.make("A")).add(PosLiteral.make("C"));
        Clause BorA = make(PosLiteral.make("B")).add(PosLiteral.make("A"));
        Clause BorC = make(PosLiteral.make("B")).add(PosLiteral.make("C"));
        Formula expected = new Formula(nAorC).addClause(BorA).addClause(BorC);
        
        assertTrue(clauseListEqual(result.getClauses(), expected.getClauses()));
    }   
    
    @Test
    public void orMethodTest6(){
        // Test AC + ~AB = (~A + C)(B + A)(B + C)
        
        Formula temp1 = new Formula(make(NegLiteral.make("A")));
        Formula nAB = temp1.addClause(make(PosLiteral.make("B")));
        Formula temp2 = new Formula(make(PosLiteral.make("A")));
        Formula AC = temp2.addClause(make(PosLiteral.make("C")));

        // Compute result
        Formula result = AC.or(nAB);        

        // Compute expected result
        Clause nAorC = make(NegLiteral.make("A")).add(PosLiteral.make("C"));
        Clause BorA = make(PosLiteral.make("B")).add(PosLiteral.make("A"));
        Clause BorC = make(PosLiteral.make("B")).add(PosLiteral.make("C"));
        Formula expected = new Formula(nAorC).addClause(BorA).addClause(BorC);
        
        assertTrue(clauseListEqual(result.getClauses(), expected.getClauses()));
    } 
    
    @Test
    public void orMethodTest7(){
        // Test A + B(C + D) = (A + B)(A + C + D)
        
        Formula input1 = new Formula(make(PosLiteral.make("A")));
        Formula input2 = new Formula(make(PosLiteral.make("B"))).addClause(make(PosLiteral.make("C")).add(PosLiteral.make("D")));

        // Compute result
        Formula result = input1.or(input2);        

        // Compute expected result
        Clause AorB = make(PosLiteral.make("A")).add(PosLiteral.make("B"));
        Clause AorCorD = make(PosLiteral.make("A")).add(PosLiteral.make("C")).add(PosLiteral.make("D"));
        Formula expected = new Formula(AorB).addClause(AorCorD);
               
        assertTrue(clauseListEqual(result.getClauses(), expected.getClauses()));
    } 
    
    @Test
    public void notMethodTest1(){
        // Basic test with a formula with one Clause
        // if input A + B + C + D, should return ~A~B~C~D
        Formula AorBorCorD = new Formula(make(PosLiteral.make("A"),PosLiteral.make("B"),PosLiteral.make("C"),PosLiteral.make("D")));
        
        // Compute result
        Formula result = AorBorCorD.not();        

        // Compute expected result
        Clause negA = make(NegLiteral.make("A"));
        Clause negB = make(NegLiteral.make("B"));
        Clause negC = make(NegLiteral.make("C"));
        Clause negD = make(NegLiteral.make("D"));
        ImList<Clause> expected = (((new Cons<Clause>(negD)).addToFront(negC)).addToFront(negB)).addToFront(negA);       
        
        assertTrue(expected.equals(result.getClauses()));
    }  
    
    @Test
    public void notMethodTest2(){
        // Basic test with a formula with one Clause
        // if input (A + B)(C + D) should return (~A + ~C)(~B + ~C)(~A + ~D)(~B + ~D)
        Clause AorB = make(PosLiteral.make("A")).add(PosLiteral.make("B"));
        Clause CorD = make(PosLiteral.make("C")).add(PosLiteral.make("D"));
        Formula input = (new Formula(AorB)).addClause(CorD);
        
        // Compute expected result
        Clause negAorNegC = make(NegLiteral.make("C")).add(NegLiteral.make("A"));
        Clause negBorNegC = make(NegLiteral.make("C")).add(NegLiteral.make("B"));
        Clause negAorNegD = make(NegLiteral.make("D")).add(NegLiteral.make("A"));
        Clause negBorNegD = make(NegLiteral.make("D")).add(NegLiteral.make("B"));

        // Compute result
        Formula result = input.not();

        // Compute expected result
        ImList<Clause> expected = (((new Cons<Clause>(negAorNegC)).addToFront(negAorNegD)).addToFront(negBorNegD)).addToFront(negBorNegC);       
        assertTrue(clauseListEqual(expected,result.getClauses()));
    } 
    
    @Test
    public void notMethodEmptyFormulaTest(){
        // Not[Problem[]] should return Problem[[]]
        
        Formula input = new Formula();
        Formula result = input.not();
        
        Formula expected = new Formula(make());
        
        // Compute expected result
        assertTrue(clauseListEqual(expected.getClauses(),result.getClauses()));
    } 
    
    @Test
    public void notMethodEmptyClauseTest(){
        // Not[Problem[[]]] should return Problem[]
        
        Formula input = new Formula(make());
        Formula result = input.not();
        
        Formula expected = new Formula();
        
        // Compute expected result
        assertTrue(clauseListEqual(expected.getClauses(),result.getClauses()));
    } 
    
    
    // Simple helper function to test the equality of ImLists of Clauses
    private boolean clauseListEqual(ImList<Clause> aList,ImList<Clause> bList){
        
        if(a == null || b == null)
            return false;
        if(aList.size() != bList.size())
            return false;
        for(Clause aMember : aList){
            if(!bList.contains(aMember))
                return false;
        }
        return true;        
    }
    
    // Helper function for constructing a clause.  Takes
    // a variable number of arguments, e.g.
    //  clause(a, b, c) will make the clause (a or b or c)
    // @param e,...   literals in the clause
    // @return clause containing e,...
    private Clause make(Literal... e) {
        Clause c = new Clause();
        for (int i = 0; i < e.length; ++i) {
            c = c.add(e[i]);
        }
        return c;
    }
}