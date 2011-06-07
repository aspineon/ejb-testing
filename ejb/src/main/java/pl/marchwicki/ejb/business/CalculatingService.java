package pl.marchwicki.ejb.business;

import javax.ejb.Local;
import javax.ejb.Stateless;

@Stateless
@Local(CalculatingServiceLocal.class)
public class CalculatingService implements CalculatingServiceLocal {

	public int add(int... args) {
		int result = 0;
		for (int i : args)
			result += i;
		
		return result;
	}

	public int multiply(int... args) {
		int result = 1;
		for (int i : args)
			result *= i;
		
		return result;
	}

	public int subtract(int... args) {
		int result = 0;
		for (int i : args)
			result -= i;
		
		return result;
	}

	
}
