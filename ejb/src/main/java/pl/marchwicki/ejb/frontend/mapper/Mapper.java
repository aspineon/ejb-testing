package pl.marchwicki.ejb.frontend.mapper;

import org.apache.log4j.Logger;

public class Mapper {

	private final static Logger logger = Logger.getLogger(Mapper.class);
	
	public Mapper() {
		logger.debug("Initializing mapper");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {}
		logger.info("Mapper initialized");
	}
	
	public String[] map(String str) {
		if (str == null) return (new String[]{});
		return str.split(";");
	}
}
