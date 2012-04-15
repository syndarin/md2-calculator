package md2;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		printHello();
		
		System.out.println(Byte.MIN_VALUE+" "+Byte.MAX_VALUE);
		
		String message=readInput();
		
		MD2Calculator md2=new MD2Calculator();
		
		System.out.println(md2.calculate(message));
	}
	
	private static String readInput(){
		Scanner scanner=new Scanner(System.in);
		return scanner.nextLine();
	}
	
	private static void printHello(){
		System.out.println("MD2 Calculator");
		System.out.println("enter a message:");
	}

}
