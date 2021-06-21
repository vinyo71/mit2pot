package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// previous state
		State statePrev = null;
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> it = s.eAllContents();
		while (it.hasNext()) {
			EObject content = it.next();
			if(content instanceof State) {
				State state = (State) content;
				if(statePrev != null)
					System.out.println(statePrev.getName()+" -> "+state.getName());
				statePrev = state;
			}
		}
		
		// 4.3
		/*
		it = s.eAllContents();
		while(it.hasNext()) {
			EObject obj = it.next();
			if(obj instanceof VariableDefinition) {
				VariableDefinition vardef = (VariableDefinition) obj;
				System.out.println(vardef.getName());
			}
			if(obj instanceof EventDefinition) {
				EventDefinition eventdef = (EventDefinition) obj;
				System.out.println(eventdef.getName());
			}		
		}
		*/
		
		// 4.4
		/*
		it = s.eAllContents();
		System.out.println("public static void print(IExampleStatemachine s) {");
		while(it.hasNext()) {
			EObject obj = it.next();
			if(obj instanceof VariableDefinition) {
			VariableDefinition var = (VariableDefinition) obj;
				String varname = var.getName();
				String name = varname.substring(0, 1).toUpperCase().concat(varname.substring(1));
				System.out.println("System.out.println(\""+varname+" = \" + s.getSCInterface().get"+name+"());");
			}
		}
		System.out.println("}");
		*/
		
		// 4.5
		it = s.eAllContents();
		System.out.println("");
		System.out.println("public static void main(String[] args) throws IOException {");
		System.out.println("ExampleStatemachine s = new ExampleStatemachine();");
		System.out.println("s.setTimer(new TimerService());");
		System.out.println("RuntimeService.getInstance().registerStatemachine(s, 200);");
		System.out.println("s.init();");
		System.out.println("s.enter();");
		System.out.println("s.runCycle();");
		System.out.println("");
		System.out.println("Scanner in = new Scanner(System.in);");
		System.out.println("String next;");
		System.out.println("while (true) {");
		System.out.println("next = in.nextLine();");
		boolean first = true;
		while(it.hasNext()) {
			EObject obj = it.next();
			if(obj instanceof EventDefinition) {
				EventDefinition event = (EventDefinition) obj;
				String eventname = event.getName();
				String raisename = eventname.substring(0, 1).toUpperCase().concat(eventname.substring(1));
				if (first)
					System.out.println("if (next.equals(\""+eventname+"\")) {");
				else
					System.out.println("} else if (next.equals(\""+eventname+"\")) {");
				System.out.println("s.raise"+raisename+"();");
				System.out.println("s.runCycle();");
				System.out.println("print(s);");
			}
		}
		System.out.println("} else if (next.equals(\"exit\")) {");
		System.out.println("System.exit(0);");
		System.out.println("}");
		System.out.println("}");
		System.out.println("}");
		it = s.eAllContents();
		System.out.println("");
		System.out.println("public static void print(IExampleStatemachine s) {");
		while(it.hasNext()) {
		EObject obj = it.next();
		if(obj instanceof VariableDefinition) {
			VariableDefinition var = (VariableDefinition) obj;
			String varname = var.getName();
			String name = varname.substring(0, 1).toUpperCase().concat(varname.substring(1));
			System.out.println("System.out.println(\""+varname+" = \" + s.getSCInterface().get"+name+"());");
		}
		}
		System.out.println("}");
		
		// check if statechart has trap state
		//hasTrap(s);
		// check if statechart has nameless state and advise name for that
		//adviseName(s);
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	public static void hasTrap(Statechart s) {
		TreeIterator<EObject> it = s.eAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if(obj instanceof State) {
				State state = (State) obj;
				if (state.getOutgoingTransitions().isEmpty() == true) {
					System.out.println(state.getName()+" is a trap state!");
				}
			}
		}
	}
	
	public static boolean checkname(Statechart s, String name) {
		TreeIterator<EObject> it = s.eAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if(obj instanceof State) {
				State state = (State) obj;
				if (state.getName().equals(name)) {
					System.out.println("Statechart has an identical state name: " +name);
					return true;
				}
			}
		}
		return false;
	}
	
	public static void adviseName(Statechart s) {
		TreeIterator<EObject> it = s.eAllContents();
		while (it.hasNext()) {
			EObject obj = it.next();
			if(obj instanceof State) {
				State state = (State) obj;
				if (state.getName().isEmpty() == true) {
					String name = "from_"+state.getIncomingTransitions().get(0).getSource().getName()+"_to_"+state.getOutgoingTransitions().get(0).getTarget().getName();
					checkname(s, name);
					System.out.println("Found a nameless state. Advise name: " +name);
				}
			}
		}
	}
}
