package pl.marchwicki.ejb.controllers;

import javax.ejb.EJB;

import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.junit.Module;
import org.apache.openejb.testng.AbstractOpenEJBTestNG;
import org.testng.Assert;
import org.testng.annotations.Test;

import pl.marchwicki.ejb.business.CalculatingService;
import pl.marchwicki.ejb.controllers.MethodControllerLocal.Operator;
import pl.marchwicki.ejb.view.TestingDisplayingService;

public class MethodControllerTest extends AbstractOpenEJBTestNG {

	@EJB
	MethodControllerLocal controller;

	@Module
	public EjbJar beans() {
        EjbJar ejbJar = new EjbJar("calculation-beans");
        ejbJar.addEnterpriseBean(new StatelessBean(TestingDisplayingService.class));
        ejbJar.addEnterpriseBean(new StatelessBean(CalculatingService.class));
        ejbJar.addEnterpriseBean(new StatelessBean(MethodController.class));
        return ejbJar;		
	}
	
	@Test
	public void notSupportedOperation() {
		String calculate = controller.calculate(Operator.MULTIPLY,
				new String[] { "1", "2" });
		Assert.assertEquals("501 Not Implemented", calculate);
	}

	@Test
	public void convertionException() {
		String calculate = controller.calculate(Operator.ADD, new String[] {
				"1", "2", "foo", "bar" });
		Assert.assertEquals("400 Bad Request", calculate);
	}

	@Test
	public void successfulRequest() {
		String calculate = controller.calculate(Operator.ADD, new String[] {
				"1", "2", "3", "4" });
		Assert.assertEquals("200 OK", calculate);
	}

}
