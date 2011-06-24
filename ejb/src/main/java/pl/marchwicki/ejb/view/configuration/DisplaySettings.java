package pl.marchwicki.ejb.view.configuration;

import org.jboss.annotation.ejb.Management;
import org.jboss.annotation.ejb.Service;

@Service(objectName="pl.marchwicki.view.settings:service=displaySettings")
@Management(DisplaySettingsMBean.class)
public class DisplaySettings implements DisplaySettingsMBean {

	private String template = "#####: {}";
	
	public String getDisplayTemplate() {
		return template;
	}

	public void setDisplayTemplate(String str) {
		this.template = str;
	}

}
