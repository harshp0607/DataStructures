package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	boolean f = true;
    	
    	delims = "0123456789\t*+-/([])";
    	
    	expr = expr.replace(" ","");
    	
    	
    	StringTokenizer tok ;
    	tok = new StringTokenizer (expr, delims, true);
    	
    	StringTokenizer tok2;
    	tok2 = new StringTokenizer(expr, delims, true);
    	
    	tok2.nextToken();
    	
    	while(tok2.hasMoreTokens()) {
    		
    		String point = tok.nextToken();

    		String secPoint = tok2.nextToken();
  
	    	if (secPoint.equals("[")) {
	    		for(int i = 0; i < arrays.size(); i++) {
	    			if (point.contentEquals(arrays.get(i).name))
	    				f = false;
	    		}
	    		if(f) 
	    			arrays.add(new Array(point));
	    		
	    		f = true;
	    		
	    		continue;
	    		
	    	} else if(delims.contains(point))
	    		continue;
	    	
	    	else {
	    		for(int i = 0; i < vars.size(); i++) {
	    			if (point.contentEquals(vars.get(i).name)) 
	    				f = false;
	    		}
	    		
	    		if(f) 
	    			vars.add(new Variable(point));
	    	
	    		f = true;
	    		continue;
	    	}
    	}
    	
    	
    	String point = tok.nextToken();
    	if(!delims.contains(point))
    		vars.add(new Variable(point));

    	return;
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	if (expr.contains("["))
            return evaluate(repArr(expr, vars, arrays), vars, arrays);
       
    	else 
            return solveEx(expr, vars);
        
    }

    private static float solveEx(String expression, ArrayList<Variable> vars) {

        expression = expression.replaceAll("[ \\t]", "");
        
        String ops = "()/*+-";

        Stack<Float> operation;
        operation = new Stack<>();
        
        Stack<Character> stackOp;
        stackOp = new Stack<>();

        int endPos = -1;
        
        for (int i = 0; i < expression.length() + 1; i++) 
        {

            if (i == expression.length()) {
                if (expression.charAt(i - 1) != ')') 
                {
                	
                    int posVar = vars.indexOf(new Variable(expression.substring(endPos + 1)));
                    
                    if (posVar >= 0) {
                    	
                        Variable trial = vars.get(posVar);
                        operation.push((float) trial.value);
                        
                    } 
                    else 
                        operation.push(Float.parseFloat(expression.substring(endPos + 1)));
                }
                break;
            }

            char seen = expression.charAt(i);
            
            if (ops.contains(String.valueOf(seen))) {
                
                if (!expression.substring(endPos + 1, i).equals("")) {
                    int posVar = vars.indexOf(new Variable(expression.substring(endPos + 1, i)));
                    
                    if (posVar >= 0)
                        operation.push((float) vars.get(posVar).value);
                    else 
                        operation.push(Float.parseFloat(expression.substring(endPos + 1, i)));
                    
                }

                if (seen == '(') 
                	stackOp.push(seen);
                
                else if (seen == ')') {
                    while (stackOp.peek() != '(') {
                        yerrStack(operation, stackOp);
                    	}
                    	stackOp.pop();
                } else {
                    while (!stackOp.isEmpty() && priority(stackOp.peek()) >= priority(seen)) {
                        yerrStack(operation, stackOp);
                    }
                    stackOp.push(seen);
                }

                endPos = i;
            }
        }

        while (!stackOp.isEmpty()) 
            yerrStack(operation, stackOp);
        
        
        return operation.peek();
    }
   
   
    private static int priority(char oop) {
        switch (oop) {
            case '-':
            case '+':
                return 1;
            case '*':
            case '/':
                return 3;
        }
        return 0;
    }

    
    
    private static void yerrStack(Stack<Float> operation, Stack<Character> stackOperator) {
    	
        float right = operation.pop();
        float left = operation.pop();
        
        char c = stackOperator.pop();
        
        float res = getResult(left, c , right);
        operation.push(res);
        
    }
    
    //cool 
    


   

    private static String repArr(String expression, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
        expression = expression.replaceAll("[ \\t]", "");
        

        int lastIn = -1;
        
        for (int i = 0; i < expression.length(); i++) {
        	
            char seen = expression.charAt(i);
            
            if (seen == '[') {
            	
               
                int arrIndex = arrays.indexOf(new Array(expression.substring(lastIn + 1, i)));

                int close = 0;
                int count = 1;
                int n = i + 1;
                
                while (count > 0) {
                	
                    char test = expression.charAt(n);
                    
                    if (test == '[') 
                    	count++;
                    else if (test == ']') 
                    	count--;
                    if (count == 0) {
                        close = n;
                        break;
                    }
                    n++;
                }

                int in = (int) evaluate(expression.substring(i + 1, close), vars, arrays);
                return expression.replace(expression.substring(lastIn + 1, i) + "[" + expression.substring(i + 1, close) + "]", String.valueOf(arrays.get(arrIndex).values[in]));
            }
            
            if (delims.contains(String.valueOf(seen))) 
                lastIn = i;
            
        }
        return expression;
    }



private static float getResult(float leftOperand, char op, float rightOperand) {
    switch (op) {
        case '+':
            return leftOperand + rightOperand;
        case '/':
            return leftOperand / rightOperand;
        case '-':
            return leftOperand - rightOperand;
        case '*':
            return leftOperand * rightOperand;
    }
    return 0;
}

}
