package pl.marchwicki.ejb.view;

import javax.ejb.Local;
import javax.ejb.Stateless;

@Stateless
@Local(DisplayingServiceLocal.class)
public class DisplayingService implements DisplayingServiceLocal {

	public void print(String str) {
		System.out.println(str);
	}

}
