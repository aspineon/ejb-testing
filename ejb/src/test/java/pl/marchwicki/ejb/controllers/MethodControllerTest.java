package pl.marchwicki.ejb.controllers;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pl.marchwicki.ejb.controllers.MethodControllerLocal;
import pl.marchwicki.ejb.controllers.MethodControllerLocal.Operator;

public class MethodControllerTest {

	MethodControllerLocal controller;
	
	@BeforeClass
	public void setup() throws NamingException {
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
				"org.apache.openejb.client.LocalInitialContextFactory");
		props.setProperty("openejb.deployments.classpath.include",
				".*ejb-testing.*");
		
		InitialContext ctx = new InitialContext(props);
		controller = (MethodControllerLocal) ctx.lookup("MethodControllerLocal");
	}
	
	@Test
	public void notSupportedOperation() {
		String calculate = controller.calculate(Operator.MULTIPLY, new String[] {"1", "2"});
		Assert.assertEquals("501 Not Implemented", calculate);
	}
	
	@Test
	public void convertionException() {
		String calculate = controller.calculate(Operator.ADD, new String[] {"1", "2", "foo", "bar"});
		Assert.assertEquals("400 Bad Request", calculate);
	}

	@Test
	public void successfulRequest() {
		String calculate = controller.calculate(Operator.ADD, new String[] {"1", "2", "3", "4"});
		Assert.assertEquals("200 OK", calculate);
	}

}
