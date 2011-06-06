package pl.marchwicki.ejb.business;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CalculatingServiceTest {

	CalculatingServiceLocal service;
	
	@BeforeClass
	public void setup() {
		 service = new CalculatingService();
	}
	
	@Test
	public void noValuesAddidion() {
		int result = service.add(new int[] {});
		Assert.assertEquals(result, 0);
	}

	@Test
	public void noValuesSubstraction() {
		int result = service.subtract(new int[] {});
		Assert.assertEquals(result, 0);
	}
	
	@Test
	public void noValuesMultiplication() {
		int result = service.multiply(new int[] {});
		Assert.assertEquals(result, 1);
	}
	
	@Test
	public void addition() {
		int result = service.add(new int[] {1, 2, 3, 4});
		Assert.assertEquals(result, 10);
	}
	
	@Test
	public void subtraction() {
		int result = service.subtract(new int[] {10, 2, 3});
		Assert.assertEquals(result, -15);
	}	
	
	@Test
	public void multiplication() {
		int result = service.multiply(new int[] {2, 3});
		Assert.assertEquals(result, 6);
	}
	
	@Test
	public void multiplicationWithZero() {
		int result = service.multiply(new int[] {2, 3, 4, 5, 0});
		Assert.assertEquals(result, 0);
	}
}
