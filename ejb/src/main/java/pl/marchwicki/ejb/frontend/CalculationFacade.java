package pl.marchwicki.ejb.frontend;

import javax.ejb.EJB;

import org.jboss.annotation.ejb.Management;
import org.jboss.annotation.ejb.Service;

import pl.marchwicki.ejb.controllers.MethodControllerLocal;
import pl.marchwicki.ejb.controllers.MethodControllerLocal.Operator;

@Service(objectName="pl.marchwicki.ejb:service=calculationFacade")
@Management(CalculationFacadeLocal.class)
public class CalculationFacade implements CalculationFacadeLocal {

	@EJB
	MethodControllerLocal controller;
	
	@EJB
	StringToArrayMappingServiceLocal mapper;
	
	public String add(String str) {
		String[] args = mapper.map(str);
		return controller.calculate(Operator.ADD, args);
	}
	
}
