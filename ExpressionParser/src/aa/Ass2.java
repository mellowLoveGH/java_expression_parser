package aa;

import java.util.Scanner;

public class Ass2 {
	
	// part 3-6 of the assignment
	public static void main(String[] args) {
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		
		// registration number
		String reg_number = "";
		String log = "Welcome to Fred¡¯s expression evaluation program. Please type an expression ";
		// 
		String msg = "";
		//
		Parser p = new Parser(); 
		do {
			
			System.out.println("Reg number " + reg_number);
			System.out.println(log);
			
			
			ExpTree myTree = p.parseLine();
			
			// print the post-order of the expression
			String po = "";
			try {
				po = myTree.postOrder();
				System.out.println("Post-order: " + po);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("the expression should end with semicolon ';' ");
			}
			
			// print the in-order of the expression
			String io = "";
			try {
				io = myTree.toString();
				System.out.println("in-order: " + io);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("in-order error");
				System.err.print(e.getStackTrace());
			}
			
			// calculate the value
			int value = 0;
			try {
				System.out.println("after the following processing and calculation");
				value = myTree.evaluation();
				System.out.println("get the value: " + value);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("evaluation error");
			}
			
			// continue or not
			System.out.println("Another expression (y/n)? ");
			msg = scan.next().trim();
		}while(msg.equals("y"));
		
	}
	
}
