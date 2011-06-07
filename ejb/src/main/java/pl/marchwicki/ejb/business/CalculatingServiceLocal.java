package pl.marchwicki.ejb.business;

public interface CalculatingServiceLocal {

	public int add(int... args);
	public int subtract(int... args);
	public int multiply(int... args);
	
	
}
