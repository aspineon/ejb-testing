package pl.marchwicki.ejb.view;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import pl.marchwicki.ejb.view.configuration.DisplaySettingsMBean;

@Stateless
@Local(DisplayingServiceLocal.class)
public class DisplayingService implements DisplayingServiceLocal {

	@EJB
	DisplaySettingsMBean settings;
	
	public void print(String str) {
		String template = settings.getDisplayTemplate();
		System.out.println(template.replace("{}", str));
	}

}
