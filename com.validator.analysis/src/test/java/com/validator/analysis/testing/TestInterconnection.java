package com.validator.analysis.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.jar.Attributes;

import org.junit.Before;
import org.junit.Test;

import com.validator.analysis.InterconnectionChecker;
import com.validator.analysis.JarToClasses;
import com.validator.analysis.MapAnalyser.ComparisonStatus;

public class TestInterconnection {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIfBundleJarIsAccessible() {
		
		JarToClasses bundle = new JarToClasses("/Users/Ben/eclipse/validator/com.validator.analysis/felix-cache/bundle1/version1.1.0/bundle.jar");
		assertNotNull("No classes came through the bundle jar", bundle.classes);
		Attributes atts = bundle.attributes;
		assertNotNull("Attributes not coming through", atts);
		assertNotNull("No export packages", atts.getValue("Export-package"));
		
	}
	@Test
	public void testSameInterfaceCanBeFound(){
		JarToClasses bundle = new JarToClasses("/Users/Ben/eclipse/validator/com.validator.analysis/felix-cache/bundle1/version1.1.0/bundle.jar");
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		//check the 
		Attributes bundleAtts = bundle.attributes;
		Attributes jarAtts = jar.attributes;
		String export = bundleAtts.getValue("Export-package");
		assertNotNull("No export packages", export);
		String importPackage = jarAtts.getValue("Import-package");
		assertNotNull("No import packages", importPackage);
		//Export should be "tutorial.example2.service"
		//Import should have "tutorial.example2.service" in the import header
		assertTrue("Import and export do not intersect", importPackage.contains(export));
		
	}
	@Test
	public void testInterfaceCanBeFoundThroughInterconnection(){
		//this time using the interconnection code to validate the connections
		ComparisonStatus serviceIsCorrect = null;
		//JarToClasses bundle = new JarToClasses("/Users/Ben/eclipse/validator/felix-cache/bundle1/data/version1.1.0/bundle.jar");
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		//Attributes bundleAtts = bundle.attributes;
		
		//String export = bundleAtts.getValue("Export-package");
		//Get the service class from the importing bundle
		Class<?> service = null;
		//find the interface which will have the implementing class checked
		for(Class<?> clazz : jar.classes){
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		if(service != null){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, 3);
		}
		assertTrue("Service did not show up as correct", serviceIsCorrect == ComparisonStatus.EQUAL);
	}
	@Test
	public void testIncorrectBundleNumber(){ //inverse of the above test
		ComparisonStatus serviceIsCorrect = null;
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		Class<?> service = null;
		//find the interface which will have the implementing class checked
		for(Class<?> clazz : jar.classes){
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		if(service != null){
			try{
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, 15);
			}catch (NullPointerException e) {
				assertTrue("Bundle was found", true);
			}
		}
		assertFalse("Somehow correct services were found", serviceIsCorrect == ComparisonStatus.EQUAL);
	}
	@Test
	public void testIndependentBundles(){
		ComparisonStatus serviceIsCorrect = null;
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		Class<?> service = null;
		//find the interface which will have the implementing class checked
		for(Class<?> clazz : jar.classes){
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		if(service != null){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, 2); //This bundle number will go check an independent bundle
		}
		assertFalse("Somehow there is an intersecting interface", serviceIsCorrect == ComparisonStatus.EQUAL);
	}
	@Test
	public void testIncorrectUsageOfInterface(){
		//TODO implement a class which wrongly implements a class
		//For this class the SpellChecker interface will have a different return type.
		//Return type has been changed to int[] from String[]
		ComparisonStatus serviceIsCorrect = null;
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		Class<?> service = null;
		//find the interface which will have the implementing class checked
		for(Class<?> clazz : jar.classes){
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		if(service != null){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, 4); //This bundle number will go check an independent bundle
		}
		assertFalse("Somehow there is an intersecting interface", serviceIsCorrect ==  ComparisonStatus.EQUAL);
	}
	
	@Test
	public void testInterconnectionAndVersionNumbers(){
		//isServiceUsedCorrectly(); need to get the service class loaded, then check method.
		//Test this with a whole lotta version numbers, this test comes down to the last number in the 3 number version number
		ComparisonStatus serviceIsCorrect = null;
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		Class<?> service = null;
		//find the interface which will have the implementing class checked
		for(Class<?> clazz : jar.classes){
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		if(service != null){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, 5); //This bundle number will go check an independent bundle
		}
		assertTrue("Maybe not reaching the correct bundle", serviceIsCorrect == ComparisonStatus.EQUAL);
	}
	@Test
	public void testTypeMismatch(){
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6_typemismatch/example6_typemismatch.jar");
		//JarToClasses jar2 = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		Class<?> service = null;
		ComparisonStatus serviceIsCorrect = null;
		//find the interface which will have the implementing class checked
		for(Class<?> clazz : jar.classes){
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		if(service != null){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, "/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar"); 
		}
		assertFalse("Somehow is correct", serviceIsCorrect == ComparisonStatus.EQUAL);
		
	}
	
	@Test
	public void testWorksInOSGi(){
		//in this test start up a framework programmatically and confirm that the bundle 
		//that is loaded can be accessed via the felix-cache
		//If works well could consider moving into the setUp() for the test class
		//http://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework-launching-and-embedding.html
	
	}
	
	@Test
	public void testSubtyping(){ //testing isServiceUsedCorrectly
		JarToClasses jar  = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		String path = InterconnectionChecker.getBundlePathFromNumber(6);
		//JarToClasses serviceBundle = new JarToClasses(path);
		JarToClasses bundle = new JarToClasses(path);
		ArrayList<Class<?>> serviceClasses = jar.classes;
		ArrayList<Class<?>> services = new ArrayList<Class<?>>();
		for(Class<?> clazz: serviceClasses){
			if(clazz.isInterface()){
				services.add(clazz);
				
			}
		}
		ComparisonStatus serviceIsCorrect = null;
		if(services.size() != 0){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(services, path); // need to make new implementation that takes a path
		}
		assertTrue("No subtype", serviceIsCorrect == ComparisonStatus.SUB_TYPED);
		//System.out.println("ComparisonStatus of this interface is: " + serviceIsCorrect);
		//if(serviceIsCorrect == ComparisonStatus.EQUAL){
		//	System.out.println("Passed validation against this service, feel free to update the bundle safely");
		//}else if(serviceIsCorrect == ComparisonStatus.SUB_TYPED){
		//	System.out.println("Passed validation, although the service is using a subtype.");
		//} else {
		//	System.out.println("Service was not correct in usage, revise your usage of this service before updating.");
		//}
	
	}
	@Test
	public void findService(){
		
	}

}
