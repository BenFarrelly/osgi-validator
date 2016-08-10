package com.validator.analysis.testing;

import static org.junit.Assert.*;

import java.util.jar.Attributes;

import org.junit.Before;
import org.junit.Test;

import com.validator.analysis.InterconnectionChecker;
import com.validator.analysis.JarToClasses;

public class TestInterconnection {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIfBundleJarIsAccessible() {
		
		JarToClasses bundle = new JarToClasses("/Users/Ben/eclipse/validator/com.validator.analysis/felix-cache/bundle1/data/version1.1.0/bundle.jar");
		assertNotNull("No classes came through the bundle jar", bundle.classes);
		Attributes atts = bundle.attributes;
		assertNotNull("Attributes not coming through", atts);
		assertNotNull("No export packages", atts.getValue("Export-package"));
		
	}
	@Test
	public void testSameInterfaceCanBeFound(){
		JarToClasses bundle = new JarToClasses("/Users/Ben/eclipse/validator/com.validator.analysis/felix-cache/bundle1/data/version1.1.0/bundle.jar");
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
		boolean serviceIsCorrect = false;
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
		assertTrue("Service did not show up as correct", serviceIsCorrect);
	}
	@Test
	public void testIncorrectBundleNumber(){ //inverse of the above test
		boolean serviceIsCorrect = false;
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
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, 15);
		}
		assertFalse("Somehow correct services were found", serviceIsCorrect);
	}
	@Test
	public void testIndependentBundles(){
		boolean serviceIsCorrect = false;
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
		assertFalse("Somehow there is an intersecting interface", serviceIsCorrect);
	}
	@Test
	public void testIncorrectUsageOfInterface(){
		//TODO implement a class which wrongly implements a class
		//For this class the SpellChecker interface will have a different return type.
		//Return type has been changed to int[] from String[]
		boolean serviceIsCorrect = false;
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
		assertFalse("Somehow there is an intersecting interface", serviceIsCorrect);
	}
	
	@Test
	public void testInterconnectionAndVersionNumbers(){
		//isServiceUsedCorrectly(); need to get the service class loaded, then check method.
		//Test this with a whole lotta version numbers, this test comes down to the last number in the 3 number version number
		boolean serviceIsCorrect = false;
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
		assertTrue("Maybe not reaching the correct bundle", serviceIsCorrect);
	}
	@Test
	public void testTypeMismatch(){
		JarToClasses jar = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6_typemismatch/example6_typemismatch.jar");
		//JarToClasses jar2 = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		Class<?> service = null;
		boolean serviceIsCorrect = false;
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
		assertFalse("Somehow is correct", serviceIsCorrect);
		
	}
	
	@Test
	public void testWorksInOSGi(){
		//in this test start up a framework programmatically and confirm that the bundle 
		//that is loaded can be accessed via the felix-cache
		//If works well could consider moving into the setUp() for the test class
		//http://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework-launching-and-embedding.html
	
	}
	@Test
	public void findService(){
		
	}

}
