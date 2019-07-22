package steps;

import org.junit.Assert;

import io.cucumber.java.en.Given;

public class Statements {
	
	 @Given("Create MT940")
	 public void create_mt940(){
		System.out.println("Hello");
		Assert.fail("Failure here");
	 }
	 
	 @Given("Create MT942")
	 public void create_mt942(){
		System.out.println("MT942");
		Assert.assertTrue(true);
	 }
}
