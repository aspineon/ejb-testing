package pl.marchwicki.ejb.frontend;

import javax.naming.InitialContext;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CalculationFacadeTest {

	CalculationFacadeLocal facade;

	@BeforeClass
	public void setup() throws Exception {
		InitialContext ctx = new InitialContext();
		facade = (CalculationFacadeLocal) ctx.lookup("CalculationFacadeLocal");
		StringToArrayMappingServiceLocal cache = (StringToArrayMappingServiceLocal) ctx.lookup("StringToArrayMappingServiceLocal");
		cache.start();
	}

	@Test
	public void simpleAdding() {
		String args = "1;2;3;4";
		
		String calculate = facade.add(args);
		Assert.assertEquals("200 OK", calculate);
	}

}
