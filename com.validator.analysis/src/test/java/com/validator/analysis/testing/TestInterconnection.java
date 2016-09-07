package com.validator.analysis.testing;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.Attributes;

import org.junit.Before;
import org.junit.Test;

import com.validator.analysis.InterconnectionChecker;
import com.validator.analysis.JarToClasses;
import com.validator.analysis.MapAnalyser;
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
	
	//@Test   Test has already passed, not a problem
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
//	@Test
	public void testLarger(){
		JarToClasses jar  = new JarToClasses("/Users/Ben/testing_bundles/com.springsource.org.apache.tools.ant-1.8.3.jar");
		String path = "/Users/Ben/felix-framework-5.4.0/felix-cache/bundle77/version0.1/bundle.jar";
		//JarToClasses serviceBundle = new JarToClasses(path);
		JarToClasses bundle = new JarToClasses(path);
		ArrayList<Class<?>> serviceClasses = jar.classes;
		ArrayList<Class<?>> services = new ArrayList<Class<?>>();
		for(Class<?> clazz: serviceClasses){
			if(clazz != null){
				if(clazz.isInterface()){
					services.add(clazz);
				
				}
			}
		}
		ComparisonStatus serviceIsCorrect = null;
		if(services.size() != 0){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(services, path); // need to make new implementation that takes a path
		}
		assertFalse("No subtype", serviceIsCorrect == ComparisonStatus.SUB_TYPED);
		assertTrue("For some reason not equal", serviceIsCorrect == ComparisonStatus.EQUAL);
		System.out.println("ComparisonStatus of this interface is: " + serviceIsCorrect);
		if(serviceIsCorrect == ComparisonStatus.EQUAL){
			System.out.println("Passed validation against this service, feel free to update the bundle safely");
		}else if(serviceIsCorrect == ComparisonStatus.SUB_TYPED){
			System.out.println("Passed validation, although the service is using a subtype.");
		} else {
			System.out.println("Service was not correct in usage, revise your usage of this service before updating.");
		}
		System.out.println();
		System.out.println("--------------- We are analysing "+services.size() + " service(s) ------------------");
		System.out.println();
		HashMap<String, ComparisonStatus> results = new HashMap<String, ComparisonStatus>();
		if(!services.isEmpty()){
			for(Class<?> service : services){
				System.out.println("------------ Analysing " + service.getName() + "------------");
				results.put(service.getName(), InterconnectionChecker.isServiceUsedCorrectly(service, path)); 
			}
		} // need to make new implementation that takes a path
		System.out.println();
		int equalCount = 0;
		for(String key : results.keySet() ){
			if(results.get(key) == ComparisonStatus.EQUAL){
				equalCount++;
				System.out.println("Comparison status for " + key + " was equal");
			}
			else if(results.get(key) == ComparisonStatus.SUB_TYPED){
				equalCount++;
				System.out.println("Comparison status for " + key + " was sub typed");
			}
			else if(results.get(key) == ComparisonStatus.TYPE_MISMATCH){
				System.out.println("Comparison status for " + key + " was Type Mismatch, revise this before updating");
			}
			else if(results.get(key) == ComparisonStatus.NO_METHOD){
				System.out.println("Comparison status for " + key + " was missing a method, revise this class before updating");
			}

		}
	
	}
	@Test
	public void testLargerDiff(){
		JarToClasses jar  = new JarToClasses("/Users/Ben/testing_bundles/com.springsource.org.apache.tools.ant-1.8.3.jar");
		String path = "/Users/Ben/felix-framework-5.4.0/felix-cache/bundle77/version0.1/bundle.jar";
		//JarToClasses serviceBundle = new JarToClasses(path);
		JarToClasses bundle = new JarToClasses(path);
		ArrayList<Class<?>> classes = bundle.classes;
		ArrayList<Class<?>> serviceClasses = jar.classes;
		ArrayList<Class<?>> services = new ArrayList<Class<?>>();
		for(Class<?> clazz: serviceClasses){
			if(clazz != null){
				if(clazz.isInterface()){
					services.add(clazz);
				
				}
			}
		}
		
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = 
				MapAnalyser.updateJarAnalysis(services, classes);
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		while(classIter.hasNext()){
			tempClass = classIter.next();
			assertNotNull("Class isn't null", tempClass);
			
			tempMap = methodEqualityMap.get(tempClass);
			if(!tempMap.isEmpty()){
				if(tempMap.containsValue(ComparisonStatus.NO_METHOD)){
					Set<Method> s =tempMap.keySet();
					for(Iterator<Method> it = s.iterator(); it.hasNext(); ){
						Method result = it.next();
						if(tempMap.get(result) == ComparisonStatus.NO_METHOD){
							System.out.println(result + " is a missing method...");
						}
					}
					
				}
				assertFalse("Map does not contain anything other than EQUAL (NO_METHOD)", tempMap.containsValue(ComparisonStatus.NO_METHOD));
				assertFalse("Map does not contain anything other than EQUAL (NOT_EQUAL)", tempMap.containsValue(ComparisonStatus.NOT_EQUAL));
				assertFalse("Map does not contain anything other than EQUAL (SUB TYPED)", tempMap.containsValue(ComparisonStatus.SUB_TYPED));
				assertFalse("Map does not contain anything other than EQUAL (TYPE MISMATCH)", tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH));
				assertTrue("Map does not have EQUAL comparison status", tempMap.containsValue(ComparisonStatus.EQUAL));
			}
		}
	}
	
	@Test
	public void findService(){
		
	}

}
