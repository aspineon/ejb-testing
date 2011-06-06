package pl.marchwicki.ejb.controllers;

public interface MethodControllerLocal {

	public enum Operator {ADD, SUBTRACT, MULTIPLY};
	
	public String calculate(Operator operation, String... args);
	
}
