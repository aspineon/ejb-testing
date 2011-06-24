package pl.marchwicki.ejb.frontend;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.Management;
import org.jboss.annotation.ejb.Service;

import pl.marchwicki.ejb.frontend.mapper.Mapper;

@Service(objectName="pl.marchwicki.ejb:service=mapper")
@Management(StringToArrayMappingServiceLocal.class)
public class StringToArrayMappingService implements StringToArrayMappingServiceLocal {

	private final static Logger logger = Logger.getLogger(StringToArrayMappingService.class);
	
	private Mapper mapper;
	
	public String[] map(String str) {
		return mapper.map(str);
	}

	//lifecycle method
	public void start() throws Exception {
		logger.debug("Setting up mapper");
		if (mapper == null) {
			mapper = new Mapper();
		}
		logger.info("Mapper set");
	}
}
