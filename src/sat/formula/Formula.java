/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sat.formula;

import immutable.Cons;
import immutable.Empty;
import immutable.ImList;
import immutable.ImListIterator;

import java.util.Iterator;

import sat.env.Variable;

/**
 * Formula represents an immutable boolean formula in
 * conjunctive normal form, intended to be solved by a
 * SAT solver.
 * An empty formula (formula with no clauses) represents true.
 * 
 * Datatype definition:
 *      Formula = ImList<Clause>    
 *      Clause  = ImList<Literal>
 *      Literal = PosLiteral(v:Var) + NegLiteral(v:Var)
 *      Var = String
 *           
 */
public class Formula implements Iterable<Clause> {
    private final ImList<Clause> clauses;
    private final int size;
    // Rep invariant:
    //      clauses != null
    //      clauses contains no null elements (ensured by spec of ImList)
    //
    // Note: although a formula is intended to be a set,  
    // the list may include duplicate clauses without any problems. 
    // The cost of ensuring that the list has no duplicates is not worth paying.
    //
    //    
    //    Abstraction function:
    //        The list of clauses c1,c2,...,cn represents 
    //        the boolean formula (c1 and c2 and ... and cn)
    //        
    //        For example, if the list contains the two clauses (a,b) and (!c,d), then the
    //        corresponding formula is (a or b) and (!c or d).

    void checkRep() {
        assert this.clauses != null : "SATProblem, Rep invariant: clauses non-null";
    }
    
    /**
     * Private function which creases a new formula from an ImList<Clause> instance
     * 
     * @param ImList<Clause> instance
     * 
     * @return Formula with the clauses from "clauses" input. 
     */
    
    private Formula(ImList<Clause> clauses) {
        size = clauses.size();
        this.clauses = clauses;
        checkRep();
    }
    
    /**
     * Private function creates a copy of a formula
     * 
     * @param formula to copy
     * 
     * @return copy of formula
     */
    private Formula(Formula formula){
        size = formula.size;
        clauses = formula.getClauses();
        checkRep();
    }

    /**
     * Create a new problem for solving that contains no clauses (that is the
     * vacuously true problem)
     * 
     * @return the true problem
     */
    public Formula() {        
        // an empty formula is always true
        size = 0;
        clauses = new Empty<Clause>();
        checkRep();
    }

    /**
     * Create a new problem for solving that contains a single clause with a
     * single positive literal l.
     * 
     * @return the problem with a single clause containing the literal l
     */
    public Formula(Variable l) {           
        // Insert a positive literal        
        this(new Clause(PosLiteral.make(l)));
        checkRep();
    }

    /**
     * Create a new problem for solving that contains a single clause
     * 
     * @return the problem with a single clause c
     */
    public Formula(Clause c) {       
        clauses = new Cons<Clause>(c);
        size = clauses.size();
        checkRep();

    }

    /**
     * Add a clause to this problem
     * 
     * @return a new problem with the clauses of this, but c added
     */
    public Formula addClause(Clause c) {     
        
        return new Formula((clauses).addToFront(c));
    }

    /**
     * Get the clauses of the formula.
     * 
     * @return list of clauses
     */
    public ImList<Clause> getClauses() {
        return clauses;
    }

    /**
     * Iterator over clauses
     * 
     * @return an iterator that yields each clause of this in some arbitrary
     *         order
     */
    public Iterator<Clause> iterator() {
        return new ImListIterator<Clause>(clauses);        
    }

    /**
     * @return a new problem corresponding to the conjunction of this and p
     */
    public Formula and(Formula p) {
        Iterator<Clause> thisIterator = this.iterator();
        Iterator<Clause> pIterator = p.iterator();
        
        // Use iterators to build new ANDed problem.
        Formula result = new Formula();
        while(thisIterator.hasNext()){
            result = result.addClause(thisIterator.next());
        }
        while(pIterator.hasNext()){
            result = result.addClause(pIterator.next());
        }
        return result;
     
    }

    /**
     * @return a new problem corresponding to the disjunction of this and p
     */
    public Formula or(Formula p) {
        // Hint: you'll need to use the distributive law to preserve conjunctive normal form, i.e.:
        //   for a non-CNF formula such as (a & b) | (c & d),
        //   you'll need to make it (a | c) & (a | d) & (b | c) & (b | d)        
        
        
        // Do a recursive call to factor Formulae into CNF using identify
        // A + BC = (A + B)(A + C) 
        ImList<Clause> leftFormulaList = this.getClauses();
        ImList<Clause> rightFormulaList = p.getClauses();    
        
        // Check if this or p is empty (i.e. 1 + p = 1) if this is empty and vice versa
        if(leftFormulaList.isEmpty()){
            return this;
        } else if(rightFormulaList.isEmpty()){
            return p;
        }       
                      
        // Otherwise both are non-empty and we can peel off a clause from each formula and store the remainder of the formula
        Clause leftFirst = leftFormulaList.first();
        ImList<Clause> leftRest = leftFormulaList.rest();       
        Clause rightFirst = rightFormulaList.first();
        ImList<Clause> rightRest = rightFormulaList.rest();
        
        if(!(rightRest.isEmpty())){
            // Can the right operand be factored? (i.e. is rest not empty?). If so,
            // recurse
            
            // A + BC = (A + B)*(A + C) 
            // where A = this and BC = p
            Formula A = new Formula(this);
            Formula B = new Formula(rightFirst);
            Formula C = new Formula(rightRest);
            return (A.or(B)).and(A.or(C));
        } else if(!(leftRest.isEmpty())){
            // If the right can't be factored, can the left? If so, recurse
            
            // AB + C = (A + C)*(B + C)
            // where AB = this and C = p
            Formula A = new Formula(leftFirst);
            Formula B = new Formula(leftRest);
            Formula C = new Formula(p);
            return (A.or(C)).and(B.or(C));
        } else{
            // else neither can be factored and the recursion bottoms out
            
            // Do the + operation by combining the two clauses using the Clause.merge method
            Clause result = leftFirst.merge(rightFirst);
            // merge returns null if you merge something like A + ~A
            if(result == null)
                return new Formula();
            else
                return new Formula(result);
        }
    }

    /**
     * @return a new problem corresponding to the negation of this
     */
    public Formula not() {
        // Hint: you'll need to apply DeMorgan's Laws (http://en.wikipedia.org/wiki/De_Morgan's_laws)
        // to move the negation down to the literals, and the distributive law to preserve 
        // conjunctive normal form, i.e.:
        //   if you start with (a | b) & c,
        //   you'll need to make !((a | b) & c) 
        //                       => (!a & !b) | !c           (moving negation down to the literals)
        //                       => (!a | !c) & (!b | !c)    (conjunctive normal form)
        
        
        // Get clauses
        ImList<Clause> clauseList = this.getClauses();
        
        // check if empty, if Formula is empty, that represents True
        // thus we should return False which is a Formula with an empty Clause in it
        if(clauseList.isEmpty()){
            return new Formula(new Clause());
        }
        
        // else Formula is not empty        
        Clause firstClause = clauseList.first();
        
        // Now iterate over the Literals in Clause and make new clauses out of each
        // i.e. if Clause[a,b,c,d] we need Clause[~a]Clause[~b]Clause[~c]Clause[~d]
        // and negate each literal
        Iterator<Literal> literalIterator = firstClause.iterator();
        Formula firstClauseNegated = new Formula();
        while(literalIterator.hasNext()){
            firstClauseNegated =  firstClauseNegated.addClause(new Clause(literalIterator.next().getNegation()));
        }
        
        // If the rest of the ClauseList is empty
        if(clauseList.rest().isEmpty()){
            return new Formula(firstClauseNegated);
        } else{            
            // Or our result with the NOT of the rest
            return (new Formula(firstClauseNegated)).or((new Formula(clauseList.rest())).not());
        }
        
    }

    /**
     * 
     * @return number of clauses in this
     */
    public int getSize() {
        return size;
    }

    /**
     * @return string representation of this formula
     */
    public String toString() {
        String result = "Problem[";
        for (Clause c : clauses)
            result += "\n" + c;
        return result + "]";
    }
}
