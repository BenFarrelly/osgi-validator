package com.validator.analysis.testing;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.validator.analysis.InterconnectionChecker;
import com.validator.analysis.JarToClasses;
import com.validator.analysis.MapAnalyser;
import com.validator.analysis.MapAnalyser.ComparisonStatus;

public class TestUpdatingBundle {
//The tests in the class follow similar execution.
//Firstly loading a bundle that I developed with the following similarities and differences
//Exactly the same, one method missing, one subtyped data type, and a type mismatch.
//The bundle used in this testing class is from the Apache Felix Tutorials 
//Found at "felix.apache.org/documentation/tutorials-examples-and-presentations/apache-felix-osgi-tutorial.html"
	@Before
	public void setUp() throws Exception {
		//TODO implement the set up, may need to have separate classes for separate set up.
		
	}

	@Test
	public void testExactSameBundle()  {
		JarToClasses j2c = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/example1.jar");
		ArrayList<Class<?>> classes = j2c.classes;
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(classes, classes);
		
		//first check
		assertNotNull("The map contains equalities", methodEqualityMap.keySet());
		assertTrue("The map has the number of classes it should", methodEqualityMap.keySet().size() == 1);
		//Following two assert statements checking if the mapp inccorrectly has null values 
		assertFalse("The map keys do not contain null values", methodEqualityMap.keySet().contains(null));
		assertFalse("The may values do not contain null values", methodEqualityMap.values().contains(null));
		//assertTrue("The map has the corresponding value for the key")
		
		//More robust test for getting key(class) for getting the hashmap, confirming the hashmap has a comparision value with a method
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		while(classIter.hasNext()){
			tempClass = classIter.next();
			assertNotNull("Class isn't null", tempClass);
			tempMap = methodEqualityMap.get(tempClass);
			
			assertFalse("Map does not contain anything other than EQUAL (NO_METHOD)", tempMap.containsValue(ComparisonStatus.NO_METHOD));
			assertFalse("Map does not contain anything other than EQUAL (NOT_EQUAL)", tempMap.containsValue(ComparisonStatus.NOT_EQUAL));
			assertFalse("Map does not contain anything other than EQUAL (SUB TYPED)", tempMap.containsValue(ComparisonStatus.SUB_TYPED));
			assertFalse("Map does not contain anything other than EQUAL (TYPE MISMATCH)", tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH));
			assertTrue("Map contains EQUAL comparison status", tempMap.containsValue(ComparisonStatus.EQUAL));
		}
	}
	
	@Test
	public void testMissingMethodBundle(){
		JarToClasses j2c=null;
		try{
		j2c = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		} catch(Exception e){
			fail("Exception caught");
		}
		JarToClasses j2c2 = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6_missingmethod/example6_missingmethod.jar");
		ArrayList<Class<?>> classes = j2c.classes;
		ArrayList<Class<?>> classes2 = j2c2.classes;
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(classes, classes2);
		
		//first check
		assertNotNull("The map contains equalities", methodEqualityMap.keySet());
		assertTrue("The map has the number of classes it should", methodEqualityMap.keySet().size() >= 1);
		//Following two assert statements checking if the mapp inccorrectly has null values 
		assertFalse("The map keys do not contain null values", methodEqualityMap.keySet().contains(null));
		assertFalse("The may values do not contain null values", methodEqualityMap.values().contains(null));
		//assertTrue("The map has the corresponding value for the key")
		
		//More robust test for getting key(class) for getting the hashmap, confirming the hashmap has a comparision value with a method
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		int noMethodCount = 0;
		while(classIter.hasNext()){
			tempClass = classIter.next();
			assertNotNull("Class isn't null", tempClass);
			tempMap = methodEqualityMap.get(tempClass);
			if(tempMap.containsValue(ComparisonStatus.NO_METHOD)){
				assertTrue("Map does not contain anything other than EQUAL (NO_METHOD)", tempMap.containsValue(ComparisonStatus.NO_METHOD));
				noMethodCount++;
			}
			assertFalse("Map does not contain anything other than EQUAL (NOT_EQUAL)", tempMap.containsValue(ComparisonStatus.NOT_EQUAL));
			assertFalse("Map does not contain anything other than EQUAL (SUB TYPED)", tempMap.containsValue(ComparisonStatus.SUB_TYPED));
			assertFalse("Map does not contain anything other than EQUAL (TYPE MISMATCH)", tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH));
			//assertTrue("Map contains EQUAL comparison status", tempMap.containsValue(ComparisonStatus.EQUAL));
		}
		assertTrue("No nomethods :(", noMethodCount > 0);
	}
	@Test 
	public void testSubTypedMethodBundle(){ 
		JarToClasses j2c = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6_subtyped/example6_subtyped.jar");
		JarToClasses j2c2 = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		ArrayList<Class<?>> classes = j2c.classes;
		ArrayList<Class<?>> classes2 = j2c2.classes;
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(classes, classes2);
		
		//first check
		assertNotNull("The map contains equalities", methodEqualityMap.keySet());
		assertTrue("The map has the number of classes it should", methodEqualityMap.keySet().size() >= 1);
		//Following two assert statements checking if the mapp inccorrectly has null values 
		assertFalse("The map keys do not contain null values", methodEqualityMap.keySet().contains(null));
		assertFalse("The may values do not contain null values", methodEqualityMap.values().contains(null));
		//assertTrue("The map has the corresponding value for the key")
		
		//More robust test for getting key(class) for getting the hashmap, confirming the hashmap has a comparision value with a method
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		int subtypedCount = 0;
		while(classIter.hasNext()){
			tempClass = classIter.next();
			assertNotNull("Class isn't null", tempClass);
			tempMap = methodEqualityMap.get(tempClass);
			
			assertFalse("Map does not contain anything other than EQUAL (NO_METHOD)", tempMap.containsValue(ComparisonStatus.NO_METHOD));
			assertFalse("Map does not contain anything other than EQUAL (NOT_EQUAL)", tempMap.containsValue(ComparisonStatus.NOT_EQUAL));
			if(tempMap.containsValue(ComparisonStatus.SUB_TYPED)){
				assertTrue("Map does not contain anything other than EQUAL (SUB TYPED)", tempMap.containsValue(ComparisonStatus.SUB_TYPED));
				subtypedCount++;
			}
			
			assertFalse("Map does not contain anything other than EQUAL (TYPE MISMATCH)", tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH));
			//assertTrue("Map contains EQUAL comparison status", tempMap.containsValue(ComparisonStatus.EQUAL));
		}
		assertTrue("Nothing was subtyped :(", subtypedCount > 0);
	}
	@Test
	public void testForTypeMismatch(){
		JarToClasses j2c = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6_typemismatch/example6_typemismatch.jar");
		JarToClasses j2c2 = new JarToClasses("/Users/Ben/eclipse/felixtutorial/tutorial/src/tutorial/example6/example6.jar");
		ArrayList<Class<?>> classes = j2c.classes;
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(classes, j2c2.classes);
		Set<?> tempClassSet = methodEqualityMap.keySet();
		if(tempClassSet == null) fail("ket set is null");
		Collection<HashMap<Method, ComparisonStatus>> col = methodEqualityMap.values();
		assertNotNull("Map is null", col);
		//first check
		assertNotNull("The map contains equalities", methodEqualityMap.keySet());
		assertFalse("No classes have come through", methodEqualityMap.keySet().size() == 0);
		assertTrue("The map has the number of classes it should", methodEqualityMap.keySet().size() > 0);
		//Following two assert statements checking if the mapp inccorrectly has null values 
		assertFalse("The map keys do not contain null values", methodEqualityMap.keySet().contains(null));
		assertFalse("The may values do not contain null values", methodEqualityMap.values().contains(null));
		//assertTrue("The map has the corresponding value for the key")
		
		//More robust test for getting key(class) for getting the hashmap, confirming the hashmap has a comparision value with a method
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		int mismatchCount = 0;
		while(classIter.hasNext()){
			//Not everyclass has the mistmatch, there are 3 classes, only confirm there is a mismatch when there is a mismatch
			tempClass = classIter.next();
			assertNotNull("Class isn't null", tempClass);
			tempMap = methodEqualityMap.get(tempClass);
			assertNotNull("Values for map is null",tempMap.values());
			assertFalse("Map does not contain anything other than EQUAL (NO_METHOD)", tempMap.containsValue(ComparisonStatus.NO_METHOD));
			assertFalse("Map does not contain anything other than EQUAL (NOT_EQUAL)", tempMap.containsValue(ComparisonStatus.NOT_EQUAL));
			assertFalse("Map does not contain anything other than EQUAL (SUB TYPED)", tempMap.containsValue(ComparisonStatus.SUB_TYPED));
			if(tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH)){
				assertTrue("Map does not contain anything other than EQUAL (TYPE MISMATCH)", tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH));
				mismatchCount++;
			}
			
			//assertTrue("Map contains EQUAL comparison status", tempMap.containsValue(ComparisonStatus.EQUAL));
		}
		assertTrue("mismatch did not exist", mismatchCount > 0);
	}
	@Test
	public void testOnLargerJar(){
		JarToClasses j2c = new JarToClasses("/Users/Ben/testing_bundles/com.springsource.javax.ws.rs-1.0.0.jar");
		ArrayList<Class<?>> classes = j2c.classes;
		JarToClasses jar = new JarToClasses("/Users/Ben/felix-framework-5.4.0/felix-cache/bundle50/version0.0/bundle.jar");
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(classes, jar.classes);
		
		//first check
		assertNotNull("The map contains equalities", methodEqualityMap.keySet());
		assertTrue("The map doesn't have the number of classes it should", methodEqualityMap.keySet().size() > 0);
		//Following two assert statements checking if the mapp inccorrectly has null values 
		assertFalse("The map keys do not contain null values", methodEqualityMap.keySet().contains(null));
		assertFalse("The may values do not contain null values", methodEqualityMap.values().contains(null));
		//assertTrue("The map has the corresponding value for the key")
		
		//More robust test for getting key(class) for getting the hashmap, confirming the hashmap has a comparision value with a method
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		while(classIter.hasNext()){
			tempClass = classIter.next();
			assertNotNull("Class isn't null", tempClass);
			
			tempMap = methodEqualityMap.get(tempClass);
			if(!tempMap.isEmpty()){
				assertFalse("Map does not contain anything other than EQUAL (NO_METHOD)", tempMap.containsValue(ComparisonStatus.NO_METHOD));
				assertFalse("Map does not contain anything other than EQUAL (NOT_EQUAL)", tempMap.containsValue(ComparisonStatus.NOT_EQUAL));
				assertFalse("Map does not contain anything other than EQUAL (SUB TYPED)", tempMap.containsValue(ComparisonStatus.SUB_TYPED));
				assertFalse("Map does not contain anything other than EQUAL (TYPE MISMATCH)", tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH));
				assertTrue("Map does not have EQUAL comparison status", tempMap.containsValue(ComparisonStatus.EQUAL));
			}
		}
	}
}
