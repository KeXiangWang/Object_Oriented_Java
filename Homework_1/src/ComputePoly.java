
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

class Term{
	private int coe;
	private int deg;
	public void setTerm(int a, int b) {
		coe = a;
		deg = b;
	}
	public int coeff() {
		return coe;
	}
	public int degree() {
		return deg;
	}
	public void add(int n) {
		coe = coe + n ;
	}
	public void reverse() {
		coe = -coe;
	}
}

class Poly{
	private Term termList[] ;
	private int termNum ; 
	public Poly(int num) {
		int i;
		termNum = num;
		termList = new Term[2000];
		for(i=0;i<2000;i++) {
			termList[i]=new Term();
		}
	}
	public int count() {
		return termNum;
	}
	public int coeff(int n) {
		return termList[n].coeff();
	}
	public int degree(int n) {
		return termList[n].degree();
	}
	public int extend(int a, int b) {
		int i,j;
		for(i=0;i<termNum;i++) {
			if(termList[i].degree()==b)
				return 1;
			if(termList[i].degree()>b) {
				for(j=termNum;j>i;j--) {
					termList[j].setTerm(termList[j-1].coeff(), termList[j-1].degree());
				}
				termList[i].setTerm(a,b);
				break;
			}
		}
		if(i==termNum) {
			termList[termNum].setTerm(a,b);
		}
		if(termNum==0) {
			termList[termNum].setTerm(a,b);
		}
			
		termNum++;
		return 0;
	}
	public void addi(int i, int n) {
		termList[i].add(n);
	}
 	public void reverse() {
 		int i;
 		for(i=0;i<termNum;i++) {
 			termList[i].reverse();
 		}
 	}
	public Poly add (Poly q) {
 		int i,j;
 		for(i=0; i<termNum;i++) {
 			for(j=0; j<q.count();j++) {
 				if(termList[i].degree()==q.degree(j)) {
 					q.addi(j, termList[i].coeff() );
 					break;
 				}
 			}
 			if(j==q.count()) {
 				q.extend(termList[i].coeff(), termList[i].degree());
 			}	
 		}
		return q;
	}
	public Poly sub (Poly q) {
		int i,j;
 		for(i=0; i<termNum;i++) {
 			for(j=0; j<q.count();j++) {
 				if(termList[i].degree()==q.degree(j)) {
 					q.addi(j, -termList[i].coeff() );
 					break;
 				}
 			}
 			if(j==q.count()) {
 				q.extend(-termList[i].coeff(), termList[i].degree());
 			}	
 		}
 		q.reverse();
		return q;
	}
}
public class ComputePoly {
	private Poly polyList[];
	private Operator opList[];
	private int num;  
	enum Operator{ADD, SUB};
	private final static Pattern PATTERNFR = Pattern.compile("\\(FR,\\+?[0-9]{1,50},(DOWN|UP)\\)");
	private final static Pattern PATTERNER = Pattern.compile("\\(ER,#\\+?[0-9]{1,50},\\+?[0-9]{1,50}\\)");
	public ComputePoly() {
		polyList = new Poly[100];
		for(num=0;num<100;num++) {
			polyList[num]=new Poly(0);
		}
		num=0;
		opList = new Operator[30];
	}
	private int parsePoly(String s) {
		
		int begin=0, end=0, addOpLoc, subOpLoc, opLoc;
		int termBegin=0, termMid=0, termEnd=0;
		int a,b;

		do {
			addOpLoc= s.indexOf('+', end);
			subOpLoc= s.indexOf('-', end);
			if(addOpLoc>=0 && subOpLoc>=0) 
				opLoc= Math.min(addOpLoc, subOpLoc);
			else 
				opLoc= Math.max(addOpLoc, subOpLoc);
			begin= s.indexOf('{', end);
			end = s.indexOf('}', begin); 
			if(opLoc>begin||opLoc==-1) {
				opList[0]= Operator.ADD;
			}
			else {
				opList[num]= (s.charAt(opLoc)=='+')? Operator.ADD : Operator.SUB ;
			}
				
			do {
				termBegin= s.indexOf('(', termEnd);
				termMid= s.indexOf(',', termBegin);
				termEnd= s.indexOf(')', termBegin);
				a= Integer.parseInt(s.substring(termBegin+1,termMid));
				b= Integer.parseInt(s.substring(termMid+1,termEnd));
				
				if(b<0) {
					System.out.print("ERROR\n#"+"The degree should be larger than or equal to 0");
					System.exit(0);
				}
				else {
					if(polyList[num].extend(a, b)==1) {
						System.out.print("ERROR\n#"+"Polynomials with items of the same degree");
						System.exit(0);
					}
				}
			}while(termEnd!=end-1) ;
			num++;
		} while(end!=s.length()-1);
		return 0;
	}
	private void printPoly(Poly p) {
		int i, first=0;
		for(i=0;i<p.count();i++) {
			if(i==0) {
				System.out.print("{");
				if(p.coeff(i)==0)
					continue;
			}
			else{
				if(p.coeff(i)==0)
					continue;
				if(first>0)
					System.out.print(",");
			}
			System.out.print("("+p.coeff(i)+','+p.degree(i)+')');
			first++;
		}
		System.out.print("}");
	}
	
	private void compute(){
		int i, notZero=0;
		Poly p = polyList[0]; 
		if(opList[0]==Operator.SUB) {
			p.reverse();
		}
		//printPoly(p);
		Poly p1,p2; 
		for(i =1;i<num; i++){
			p2 = polyList[i];
			Operator op=opList[i]; 
			if(op==Operator.ADD) 
				p1=p.add(p2); 
			else 
				p1=p.sub(p2); 
			p=p1; 
		} 

		for(i =0, notZero=0;i<p.count(); i++) {
			if(p.coeff(i)!=0) {
				notZero++;
			}
		}
		if(notZero==0)
			System.out.print("0");
		else
			printPoly(p);
	}
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str;
		int i;
		try{
			str = br.readLine();
			String str2 = str.replaceAll(" ", ""); 
		    if(str2.matches("^[+-]?\\{[0-9,()+-]*}([+-]{1}\\{[0-9,()+-]*})*$")) {
		    	String[] buff = str2.split("[+-]?\\{");
		    	for(i=1;i<buff.length;i++){
		            if(!buff[i].matches("^\\([+-]?[0-9]*,[+-]?[0-9]*\\)(,\\([+-]?[0-9]*,[+-]?[0-9]*\\))*\\}$")) {
		            	System.out.print("ERROR\n#"+"Illegal input");
		            	System.exit(0);
		            }
		        }
		    	if(buff.length>21) {
		    		System.out.print("ERROR\n#"+"Too many polynomials");
		    		System.exit(0);
		    	}
		    	for(i=1;i<buff.length;i++){
		            if(!buff[i].matches("^\\([+-]?[0-9]*,[+-]?[0-9]*\\)(,\\([+-]?[0-9]*,[+-]?[0-9]*\\)){0,49}\\}$")) {
		            	System.out.print("ERROR\n#"+"Too many polynomial Terms");
		            	System.exit(0);
		            }
		        }
		    	for(i=1;i<buff.length;i++){
		            if(!buff[i].matches("^\\([+-]?[0-9]{1,6},[+-]?[0-9]{1,6}\\)(,\\([+-]?[0-9]{1,6},[+-]?[0-9]{1,6}\\)){0,49}\\}$")) {
		            	System.out.print("ERROR\n#"+"The number is too long");
		            	System.exit(0);
		            }
		        }
	    		ComputePoly cp = new ComputePoly();
			    if(cp.parsePoly(str2)==0) {
			    	cp.compute();
			    }
			    else {
			    	System.out.print("ERROR\n#"+"Illegal input");
			    }
		    }
		    else {
		    	System.out.print("ERROR\n#"+"Illegal input");
		    }
		}
		catch(IOException e) {
			System.out.print("ERROR\n#"+"Can't read in");
		}
		catch(NullPointerException e) {
			System.out.print("ERROR\n#"+"Illegal input");
		}
		catch(StackOverflowError e) {
			System.out.print("ERROR\n#"+"Stackoverflow");{}
		}
		catch(Throwable e) {
			System.out.print("ERROR\n#"+"Maybe I don't konw why, but there must be something wrong in your input");
		}	
	}
}

