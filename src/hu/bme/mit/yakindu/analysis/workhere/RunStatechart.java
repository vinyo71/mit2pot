package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.Scanner;

import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;

public class RunStatechart {
	
	public static void main(String[] args) throws IOException {
		ExampleStatemachine s = new ExampleStatemachine();
		s.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(s, 200);
		s.init();
		s.enter();
		s.runCycle();
		
		Scanner in = new Scanner(System.in);
		String next;
		while (true) {
			next = in.nextLine();
			if (next.equals("start")) {
				s.raiseStart();
				s.runCycle();
				print(s);
			} else if (next.equals("white")) {
				s.raiseWhite();
				s.runCycle();
				print(s);
			} else if (next.equals("black")) {
				s.raiseBlack();
				s.runCycle();
				print(s);
			} else if (next.equals("exit")) {
				System.exit(0);
			}
		}
	}

	public static void print(IExampleStatemachine s) {
		System.out.println("W = " + s.getSCInterface().getWhiteTime());
		System.out.println("B = " + s.getSCInterface().getBlackTime());
	}
}
