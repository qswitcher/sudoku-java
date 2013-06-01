package sat;

import static org.junit.Assert.*;

import org.junit.Test;

import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal d = PosLiteral.make("d");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();
    Literal nd = d.getNegation();
    
   
    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test
    public void SATSolverABCDTest(){
         
         // Solution of A*B*C*D should be A = B = C = D = True
         Clause A = new Clause(a);
         Clause B = new Clause(b);
         Clause C = new Clause(c);
         Clause D = new Clause(d);
        
         Formula formula = (new Formula(A)).addClause(B).addClause(C).addClause(D);

         // compute solution        
         Environment solution = SATSolver.solve(formula);
         
         // compute expected solution
         Environment expected = new Environment();
         Variable [] variables = {a.getVariable(),b.getVariable(), c.getVariable(),d.getVariable()};
         Bool [] variableValues = {Bool.TRUE,     Bool.TRUE, Bool.TRUE,      Bool.TRUE};
         for(int i = 0; i < variables.length; i++){
             expected = expected.put(variables[i],variableValues[i]);
         }         

         
         assertTrue(environmentEqual(expected,solution,variables));  
    }    
    
    @Test
    public void SATSolverABnCDTest(){
         
         // Solution of A*B*~C*D should be A = B = D = True  C = False
         Clause A = new Clause(a);
         Clause B = new Clause(b);
         Clause nC = new Clause(nc);
         Clause D = new Clause(d);
        
         Formula formula = (new Formula(A)).addClause(B).addClause(nC).addClause(D);

         // compute solution        
         Environment solution = SATSolver.solve(formula);
         
         // compute expected solution
         Environment expected = new Environment();
         Variable [] variables = {a.getVariable(),b.getVariable(), c.getVariable(),d.getVariable()};
         Bool [] variableValues = {Bool.TRUE,     Bool.TRUE, Bool.FALSE,      Bool.TRUE};
         for(int i = 0; i < variables.length; i++){
             expected = expected.put(variables[i],variableValues[i]);
         }         

         
         assertTrue(environmentEqual(expected,solution,variables));  
    }    
    
    @Test
    public void SATSolvernAnBnCnDTest(){
         
         // Solution of ~A*~B*~C*~D should be A = B = C = D = False
         Clause nA = new Clause(na);
         Clause nB = new Clause(nb);
         Clause nC = new Clause(nc);
         Clause nD = new Clause(nd);
        
         Formula formula = (new Formula(nA)).addClause(nB).addClause(nC).addClause(nD);

         // compute solution        
         Environment solution = SATSolver.solve(formula);
         
         // compute expected solution
         Environment expected = new Environment();
         Variable [] variables = {a.getVariable(),b.getVariable(), c.getVariable(),d.getVariable()};
         Bool [] variableValues = {Bool.FALSE,     Bool.FALSE, Bool.FALSE,      Bool.FALSE};
         for(int i = 0; i < variables.length; i++){
             expected = expected.put(variables[i],variableValues[i]);
         }         

         
         assertTrue(environmentEqual(expected,solution,variables));  
    }    
    
    @Test
    public void SATSolverABnCD_UnSimpliedTest(){
         
         // Solution of A*B*~C*(~B + A)*(C + D) should be A = B = D = True  C = False
         Clause A = new Clause(a);
         Clause B = new Clause(b);
         Clause nC = new Clause(nc);
         Clause nBorA = new Clause(nb).add(a);
         Clause CorD = new Clause(c).add(d);
        
         Formula formula = (new Formula(A)).addClause(B).addClause(nC).addClause(nBorA).addClause(CorD);

         // compute solution        
         Environment solution = SATSolver.solve(formula);
         
         // compute expected solution
         Environment expected = new Environment();
         Variable [] variables = {a.getVariable(),b.getVariable(), c.getVariable(),d.getVariable()};
         Bool [] variableValues = {Bool.TRUE,     Bool.TRUE, Bool.FALSE,      Bool.TRUE};
         for(int i = 0; i < variables.length; i++){
             expected = expected.put(variables[i],variableValues[i]);
         }         

         
         assertTrue(environmentEqual(expected,solution,variables));  
    } 

    @Test
    public void SATSolverExample1Test(){
        // (a + ~b)(a + b)
        // expect: a -> True, b -> anything            
        Clause  AnB  = (new Clause(nb)).add(a);
        Clause  AB   = (new Clause(b)).add(a);
        
        // compute formula
        Formula formula = (new Formula(AB)).addClause(AnB);                 

        // compute solution
        Environment solution = SATSolver.solve(formula);
        
        // compute expected solution
        Environment expected = new Environment();
        Variable [] variables = {a.getVariable()};
        Bool [] variableValues = {Bool.TRUE};
        for(int i = 0; i < variables.length; i++){
            expected = expected.put(variables[i],variableValues[i]);
        }

        
        assertTrue(environmentEqual(expected,solution,variables));        

    }
    
    @Test
    public void SATSolverExample2Test(){
         // aba~b
         // expect: null    
        
         Clause  A = new Clause(a);
         Clause  B = new Clause(b);
         Clause nB = new Clause(nb);
         
         Formula formula = (new Formula(nB)).addClause(A).addClause(B).addClause(A);                 

         Environment solution = SATSolver.solve(formula);
         assertNull(solution);
    }

    @Test
    public void SATSolverExample3Test(){
         // ab(~b + c)
         // expect: a -> True b -> True  c -> True    
        
         Clause  A = new Clause(a);
         Clause  B = new Clause(b);
         Clause  nBC   = (new Clause(c)).add(nb);
         
         // compute formula
         Formula formula = (new Formula(nBC)).addClause(B).addClause(A);                

         // compute solution         
         Environment solution = SATSolver.solve(formula);
         
         // compute expected solution
         Environment expected = new Environment();
         Variable [] variables = {a.getVariable(),b.getVariable(), c.getVariable(),d.getVariable()};
         Bool [] variableValues = {Bool.TRUE,     Bool.TRUE, Bool.TRUE,      Bool.UNDEFINED};
         for(int i = 0; i < variables.length; i++){
             expected = expected.put(variables[i],variableValues[i]);
         }

         
         assertTrue(environmentEqual(expected,solution,variables));     
    }
    
    @Test
    public void SATSolverBigTest(){
         // Try problem shown in lecture
         // which only has one possible solution A = B = C = D = True
         Clause  nABC  = (new Clause(c)).add(b).add(na);
         Clause  ACD   = (new Clause(d)).add(c).add(a);
         Clause  ACnD  = (new Clause(nd)).add(c).add(a);
         Clause  AnCD  = (new Clause(d)).add(nc).add(a);
         Clause  AnCnD = (new Clause(nd)).add(nc).add(a);
         Clause  nBnCD = (new Clause(d)).add(nc).add(nb);
         Clause  nABnC = (new Clause(nc)).add(b).add(na);
         Clause  nAnBC = (new Clause(c)).add(nb).add(na);
         
         // compute formula
         Formula formula = (new Formula(nABC)).addClause(ACD).addClause(ACnD).addClause(AnCD).addClause(AnCnD).addClause(nBnCD).addClause(nABnC).addClause(nAnBC);                

         // compute solution
         Environment solution = SATSolver.solve(formula);

         // compute expected solution
         Environment expected = new Environment();
         Variable [] variables = {a.getVariable(),b.getVariable(), c.getVariable(),d.getVariable()};
         Bool [] variableValues = {Bool.TRUE,     Bool.TRUE, Bool.TRUE,      Bool.TRUE};
         for(int i = 0; i < variables.length; i++){
             expected = expected.put(variables[i],variableValues[i]);
         }

         
         assertTrue(environmentEqual(expected,solution,variables));     
    }
    
    /**
     *  private function which compares two Environments to see if they have the same assignments for a set of 
     *  variables
     *  
     *   @param first Environment
     *   @param second Environment
     *   @param list of variables to check. 
     *   
     *   @return true if both environments have the same Boolean settings for the given Variable
     *   list or false otherwise
     */
    private boolean environmentEqual(Environment aEnv, Environment bEnv, Variable [] localVariables){
        for(Variable var : localVariables){
            if(aEnv.get(var) != bEnv.get(var))
                return false;          
        }
        return true;
    }
}