package pl.marchwicki.ejb.controllers;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import pl.marchwicki.ejb.business.CalculatingServiceLocal;
import pl.marchwicki.ejb.view.DisplayingServiceLocal;

@Stateless
@Local(MethodControllerLocal.class)
public class MethodController implements MethodControllerLocal {

	private final static Logger logger = Logger.getLogger(MethodController.class);
	
	@EJB
	DisplayingServiceLocal displayingService;
	
	@EJB
	CalculatingServiceLocal calculatingService;

	/**
	 * Returns "200 OK" for if no errors
	 */
	public String calculate(Operator operation, String... args) {
		int[] numbers;
		try {
			numbers = convertToIntArray(args);
			
			if (Operator.ADD.equals(operation)) {
				int result = calculatingService.add(numbers);
				logger.debug("displayingService implementation [" + displayingService + "]");
				displayingService.print("Result of " + operation + " operation is: " + result);
			} else {
				return "501 Not Implemented";
			}
		} catch (NumberFormatException e) {
			logger.error("Problems converting elements: ", e);
			return "400 Bad Request";
		} catch (Exception e) {
			logger.error("Generic exception: ", e);
			return "500 Internal Server Error";
		}
		
		return "200 OK";
	}
	
	private int[] convertToIntArray(String... args) {
		int[] result = new int[args.length];
		
		for (int i=0; i<args.length;i++) {
			result[i] = Integer.valueOf(args[i]).intValue();
		}
		
		return result;
	}

}
