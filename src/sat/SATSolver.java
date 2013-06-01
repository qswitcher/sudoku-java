package sat;

import java.util.Iterator;

import immutable.Empty;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment guess = new Environment();    
        return solve(formula.getClauses(),guess);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {        
        if(clauses.isEmpty()) return env;
        
        // pick Clause with fewest literals
        Iterator<Clause> clauseIterator = clauses.iterator();
        Clause minClause = clauseIterator.next();
        int minSize = minClause.size();        
        while(clauseIterator.hasNext()){    
            Clause iterClause = clauseIterator.next();
            if(iterClause.size() <= minSize){
               minSize = iterClause.size();
               minClause = iterClause;
            }
        }
        
        //System.out.println(minClause.toString());
        // if the minclause is empty, return null        
        if(minClause.isEmpty()) return null;      
        
        // choose variable
        Literal literal = minClause.chooseLiteral();
        Variable variable = literal.getVariable();       
        
        // Find assignment which makes literal true
        Environment env2 = (literal.isPositive()) ? env.putTrue(variable) : env.putFalse(variable);

        // single literal case
        if(minClause.isUnit()){
            return solve(substitute(clauses,literal),env2);
        }
        
        // Multi-literal case
        Environment answer = solve(substitute(clauses,literal),env2);
       
        // we found an answer!
        if(answer != null) return answer;
        
        // oops! guess not.... try setting literal to false
        Environment env3 = (literal.isPositive()) ? env.putFalse(variable) : env.putTrue(variable);       
       
        // recurse
        return solve(substitute(clauses,literal.getNegation()),env3);            
    }

    /**
     * given a list of clauses and a literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            a list of clauses
     * @param l
     *            a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {     
       
        // check if Clauses is empty
        if(clauses.isEmpty()){
            return clauses;
        }     
  
        
        // Find clauses which have literal in them and remove them (since they're equal to 1 now)
        ImList<Clause> reducedClauseList = new Empty<Clause>();
        for(Clause clause : clauses){
            Clause reducedClause = clause.reduce(l);
            if(reducedClause != null){
                reducedClauseList = reducedClauseList.addToFront(reducedClause);
            }
        }
        
        return reducedClauseList;
          
    }

}
