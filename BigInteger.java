package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;

	/**
	 * Number of digits in this integer
	 */
	int numDigits;

	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	DigitNode front;

	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}

	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
			throws IllegalArgumentException {

		BigInteger ret = new BigInteger();
		DigitNode front = null;
		DigitNode back = null;

		int uselessZeros = 0;
		int n=0;


		do {
			if(integer.charAt(n)=='0')
				uselessZeros++;	
			else if (integer.charAt(n)!='0'&&integer.charAt(n)!='-'&&integer.charAt(n)!='+')
				break;
			if(n==integer.length()-1)
				break;
			else
				n++;
		}
		while(integer.charAt(n)!='0' || n<integer.length());

		for(int c=0; c<integer.length(); c++) {
			
			int asc = (int)integer.charAt(c);
			if((!(asc>=48 && asc<=57))&& (!(asc==43||asc==45)))
				throw new IllegalArgumentException("Invalid input");
				
			if((c!=0 && (asc==43||asc==45)))
				throw new IllegalArgumentException ("Invalid input");
				
			}
			
		
	
		if(integer.charAt(0)=='-'||integer.charAt(0)=='+') {
			if(integer.charAt(0)=='-') {
				ret.negative = true;
				uselessZeros++;
			}
			if(integer.charAt(0)=='+') {
				ret.negative = false;
				uselessZeros++;
			}
		}

		for (int i=integer.length()-1;i>=0+uselessZeros;i--)
		{

			if(integer.charAt(i)!='-')
			{	
				if(front==null) {
					front=new DigitNode (Character.getNumericValue(integer.charAt(i)),null);
					ret.front= front;
					back = ret.front;
					ret.numDigits++;
				}
				else {
					back.next = new DigitNode (Character.getNumericValue(integer.charAt(i)),null);
					back = back.next;
					ret.numDigits++;
				}	
			}
		}	
		return ret;
	}

	/**
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {

		BigInteger totSum = new BigInteger();
		DigitNode shorter = null;
		DigitNode longer = null;


		int sumNums = 0;
		int carryOver =0;
		int diffNums = 0;
		


		if(first.front==null)
			return second;
		else if (second.front == null)
			return first;


		if(first.numDigits<second.numDigits) {
			shorter = first.front;
			longer = second.front;
		}
		else if(first.numDigits>second.numDigits) {
			shorter = second.front;
			longer = first.front;
		}
		else 
		{
			shorter = first.front;
			longer = second.front;
		}

		totSum.front = new DigitNode (0,null);
		DigitNode counter = totSum.front;
		carryOver=0;

		//if both are positive or both are negative just carry sign over and add

		if((first.negative==true && second.negative == true) || (first.negative==false && second.negative == false) )
		{

			if(first.negative==true && second.negative == true)
				totSum.negative=true;
			else if (first.negative==false && second.negative == false)
				totSum.negative =false;

		
			for ( ; shorter!=null && longer!=null; shorter= shorter.next, longer=longer.next) {

				sumNums = shorter.digit + longer.digit + carryOver;
				carryOver = sumNums/10;
				counter.digit= sumNums%10;

				if(shorter.next!=null || longer.next!=null) {
					counter.next = new DigitNode (0,null);
					counter= counter.next;	
				}
			}

			for( ; longer!=null ; longer=longer.next) {

				sumNums = longer.digit + carryOver;
				carryOver = sumNums/10;
				counter.digit= sumNums%10;

				if(longer.next!=null) {
					counter.next = new DigitNode (0,null);
					counter= counter.next;	
				}

			}

			if(carryOver != 0) {
				counter.next = new DigitNode(carryOver, null);
			}

		}

		// subtraction if one is negative and the other is positive

		else if((first.negative==false && second.negative == true) || (first.negative==true && second.negative == false)) {

			BigInteger Longer = new BigInteger();
			BigInteger Shorter = new BigInteger();
			
			
			boolean secgreat=false;
			//Assigns longer to the larger number and shorter to the smaller number

			if(first.numDigits>second.numDigits) {
				Longer = first;
				Shorter = second;
			}
			else if (second.numDigits>first.numDigits) {
				Longer = second;
				Shorter = first;
			} 
			else if(first.numDigits==second.numDigits) {
				DigitNode firstpt = first.front, secondpt = second.front;
				while(firstpt != null && secondpt != null) {
					if(secondpt.digit > firstpt.digit) {
						secgreat = true;
					} else if(secondpt.digit < firstpt.digit) {
						secgreat = false;
					}
					firstpt = firstpt.next;
					secondpt = secondpt.next;
				}
				if (secgreat) {
					Longer=second;
					Shorter=first;
				}
				else {
					Longer=first;
					Shorter=second;
				}
				
			}
			totSum.negative = Longer.negative;
			
			
			//subtraction now
			DigitNode longPoint= Longer.front;
			DigitNode shortPoint = Shorter.front;
			boolean didBorrow=false, borrow = false;
			
			for ( ; shortPoint != null && longPoint !=null; shortPoint=shortPoint.next, longPoint = longPoint.next, counter=counter.next) {
				 
				
					int bigDig = longPoint.digit;
					if(borrow)
						bigDig--;
					
					if(bigDig<shortPoint.digit || bigDig<0){
						bigDig+=10;
						didBorrow=true;
					}
					
					diffNums = bigDig - shortPoint.digit;
					counter.digit = diffNums;
					
					if(longPoint.next!=null || shortPoint.next!=null) 
						counter.next = new DigitNode (0, null);
					
					
					borrow=didBorrow;
					didBorrow=false;
			}
			
			for( ; longPoint!=null ; longPoint=longPoint.next) {
				
				int bigDig = longPoint.digit;
				if(borrow)
					bigDig--;
				
				if(bigDig<0) {
					bigDig+=10;
					didBorrow=true;
				}
				
				counter.digit=bigDig;
				
				if (longPoint.next!=null) {
					counter.next = new DigitNode (0,null);
					counter = counter.next;
				}
				
				borrow = didBorrow;
				didBorrow=false;
				/*counter.digit = longPoint.digit;
				
				if(longPoint.next!=null) {
					counter.next = new DigitNode (0,null);
					counter= counter.next;	
				}*/
			}
				


		}

		// gets rid of leading 0s by parsing
		
		String stringTot = totSum.toString();
		totSum = parse(stringTot);
		return totSum;

	}

	/**
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {
				
		BigInteger answer = new BigInteger();
		
		int numZeros = 0;
		
		for(DigitNode secondPt = second.front; secondPt != null; secondPt = secondPt.next, numZeros++) {
			
			BigInteger scMultiple = new BigInteger();
			scMultiple.front = new DigitNode(0, null);
			DigitNode scalarPtr = scMultiple.front;
		
			for(int j = 0; j < numZeros; j++) {
				scalarPtr.digit = 0;
				scalarPtr.next = new DigitNode(0,null);
				scalarPtr = scalarPtr.next;
			}
			
			int carry = 0;
			for(DigitNode firstPt = first.front; firstPt != null; firstPt = firstPt.next) {
				
				int product = firstPt.digit * secondPt.digit + carry;
				
				carry = product / 10;
				scalarPtr.digit = product % 10;
				
				if(firstPt.next != null) {
					scalarPtr.next = new DigitNode(0,null);
					scalarPtr = scalarPtr.next;
				} else {
					if(carry != 0)
						scalarPtr.next = new DigitNode(carry,null);
					break;
				}
			}
			
			answer = BigInteger.add(answer, scMultiple);
		}
		
		if(first.negative == second.negative) {
			answer.negative = false;
		} else {
			answer.negative = true;
		}
		
		int count = 0, lastNonzero = 0;
		answer.numDigits = 0;
		for(DigitNode ptr = answer.front; ptr != null; ptr = ptr.next, count++) {
			if(ptr.digit != 0) {
				lastNonzero = count;
			}
		}
		
		count = 0;
		for(DigitNode ptr = answer.front; ptr != null; ptr = ptr.next, count++) {
			answer.numDigits++;
			if(count == lastNonzero) {
				ptr.next = null;
				break;
			}
		}

		if(answer.numDigits == 1 && answer.front.digit == 0 || answer.numDigits == 0) {
			answer.negative = false;
			answer.front = null;
			answer.numDigits = 0;
		}

		return answer;

	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
			retval = curr.digit + retval;
		}

		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
}
