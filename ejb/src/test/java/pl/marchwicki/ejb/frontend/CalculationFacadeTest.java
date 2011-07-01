package pl.marchwicki.ejb.frontend;

import javax.ejb.EJB;

import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.junit.Module;
import org.apache.openejb.testng.AbstractOpenEJBTestNG;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pl.marchwicki.ejb.business.CalculatingService;
import pl.marchwicki.ejb.controllers.MethodController;
import pl.marchwicki.ejb.view.TestingDisplayingService;

public class CalculationFacadeTest extends AbstractOpenEJBTestNG {

	@EJB
	CalculationFacadeLocal facade;
	
	@EJB
	StringToArrayMappingServiceLocal cachedMapper;
	
	@BeforeClass
	public void setupTest() throws Exception {
		cachedMapper.start();
	}
	
	@Module
	public EjbJar beans() {
        EjbJar ejbJar = new EjbJar("calculation-beans");
        ejbJar.addEnterpriseBean(new StatelessBean(TestingDisplayingService.class));
        ejbJar.addEnterpriseBean(new StatelessBean(CalculatingService.class));
        ejbJar.addEnterpriseBean(new StatelessBean(MethodController.class));
        ejbJar.addEnterpriseBean(new StatelessBean(StringToArrayMappingService.class));
        ejbJar.addEnterpriseBean(new StatelessBean(CalculationFacade.class));
        return ejbJar;		
	}
	
	@Test
	public void simpleAdding() throws Exception {
		String args = "1;2;3;4";
		
		String calculate = facade.add(args);
		Assert.assertEquals("200 OK", calculate);
	}

}
