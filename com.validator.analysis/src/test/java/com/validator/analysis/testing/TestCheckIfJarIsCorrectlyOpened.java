package com.validator.analysis.testing;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.validator.analysis.AnalysisStarter;
import com.validator.analysis.JarToClasses;
import com.validator.analysis.MapAnalyser;
import com.validator.analysis.MapAnalyser.ComparisonStatus;

public class TestCheckIfJarIsCorrectlyOpened {

	@BeforeClass
	public static void setUp()  {
		//AnalysisStarter a = new AnalysisStarter();
		try{
			AnalysisStarter.main(new String[] {"/Users/Ben/Desktop/com.acme.prime.upper.api.jar", "/Users/Ben/Desktop/com.acme.prime.upper.api.jar"});
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void testJarHasClassesAccessed() {
		
		assertNotNull("Classes are contained", AnalysisStarter.classes);
		assertNotNull("List is not empty", AnalysisStarter.classes.get(0));
		assertNotNull("Classes are contained 2", AnalysisStarter.classes2);
		
	}
	@Test
	public void testClassesAreEqual(){
		ArrayList<Class<?>> classes = AnalysisStarter.classes;
		ArrayList<Class<?>> classes2 = AnalysisStarter.classes2;
		for(int i = 0; i < classes.size()-1; i++){
			if(classes.get(i).getClass().getName().equals(classes2.get(i).getName())){
				continue;
			} else {
				fail("Classes found are not equal "+ 
						classes.get(i).getName() + " " + classes2.get(i).getName());
			}
			
		}
		assertTrue("All classes have been found to be equal", true);
	}
	
	@Test
	public void testClassesArePutInMapCorrectly(){
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(AnalysisStarter.classes , AnalysisStarter.classes2);
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		if(methodEqualityMap.size() == 0){
			fail("dear god no");
		}
		Iterator<Class<?>> classIter =  classSet.iterator();
		ArrayList<String> classStrings = new ArrayList<String>();
		
		while(classIter.hasNext()){
			Class<?> c = classIter.next();
			classStrings.add(c.getName());
		}
		if(classStrings.size() == 0){
			fail("WHY IS THERE NOTHING IN HERE");
		}
		ArrayList<String> notInMap = new ArrayList<String>();
		ArrayList<String> inMap = new ArrayList<String>();
		//if this doesn't work, convert al into strings
		for(Class<?> c : AnalysisStarter.classes){
			if(classStrings.contains(c.getName())){
				inMap.add(c.getName());
				continue;
			} else {
				//not contained, fail, should be in there
				notInMap.add(c.getName());
				
			}
			
		}
		
		//find out if anything is in the ArrayList
		if(notInMap.size() == AnalysisStarter.classes.size()){
			fail("Everything went into the bloody notInMap");
		}
		assertFalse("Nothing was 'the same' ", inMap.isEmpty());
	}
	@Test
	public void testMethodsAreEqual(){
		//Later change test to use Jar from JarToClasses

		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(AnalysisStarter.classes , AnalysisStarter.classes2);

		Collection<HashMap<Method, ComparisonStatus>> col = methodEqualityMap.values();
		HashMap[] colIter =  col.toArray(new HashMap[col.size()]);
		Collection<ComparisonStatus> comparisonCol = null;
		
		int equalCount = 0;
		for(HashMap<Method, ComparisonStatus> hash : colIter){
			
			if(hash == null){
				fail("Entry was null");
			}
			comparisonCol = hash.values();
			Iterator<ComparisonStatus> iter = comparisonCol.iterator();
			
			while(iter.hasNext()){
				
				ComparisonStatus c = iter.next();
				if(c != ComparisonStatus.EQUAL){
					fail("WASN'T EQUAL");
				} else {
					//is equal
					equalCount++;
				}

			}
		}
		if(comparisonCol != null){
		assertEquals("Equal count  to collection size, they are all equal", equalCount, comparisonCol.size());
		}
		else {
			//comparisonCol == null, did not get through loop
			fail("Did not get through loop at all");
		}
		//assertTrue("All must be true", true);
	}

	@Test
	public void testMethodsAreNotEqual(){
		//Testing that two different jars do not have the same methods (That my code is sort of correct)
		JarToClasses jar = new JarToClasses("/Users/Ben/Desktop/aether-api-1.0.2.v20150114.jar");
		//These Jar's are completely different, therefore "methodEqualityMap" will not have methods that are the same.
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(AnalysisStarter.classes , jar.classes);
		Collection<HashMap<Method, ComparisonStatus>> col = methodEqualityMap.values();
		
		Set<Class<?>> classSet =  methodEqualityMap.keySet();
		Iterator<HashMap<Method, ComparisonStatus>> colIter = col.iterator();
		
		while(colIter.hasNext()){
			HashMap<Method, ComparisonStatus> map = colIter.next();
			Collection<ComparisonStatus> comparisonCol = map.values();
			Iterator<ComparisonStatus> iter = comparisonCol.iterator();
			
			while(iter.hasNext()){
				ComparisonStatus c = iter.next();
				if(c != ComparisonStatus.EQUAL  ){
					assertTrue("Yay one wasn't equal", true);
				}else if(c == null){
				
					fail("CS was null");
				
				} else {
					System.out.print("Comparison status: " + c);
					if(c == ComparisonStatus.EQUAL){
						continue;
					}
				
				}
				//assertNotNull("Let's find out if this is ")
			}

		}
				
			assertTrue("Class set is not empty, but method map is", classSet.isEmpty());
		
	}
	@Test
	public void testAccessToBundle(){
		JarToClasses jar = new JarToClasses("/Users/Ben/Desktop/bundle.jar");
		ArrayList<Class<?>> classes = jar.classes;
		assertNotNull("classes retrieved", classes);
		
	}
	@Test
	public void testClassesAreAccessedCorrectlyFromBundle(){
		//To check that these classes can be accessed down to the method type level.
		//Checking the same bundle against itse'f, should be equal
		JarToClasses jar = new JarToClasses("/Users/Ben/Desktop/bundle.jar");
		ArrayList<Class<?>> classes = jar.classes;
		if(classes.size() == 0){
			fail("why is there nothing in here");
		}
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>>  methodEqualityMap = MapAnalyser.updateJarAnalysis(jar.classes , jar.classes);
		
		Collection<HashMap<Method, ComparisonStatus>> col = methodEqualityMap.values();
		HashMap[] colIter =  col.toArray(new HashMap[col.size()]);
		Collection<ComparisonStatus> comparisonCol = null;
		
		int equalCount = 0;
		for(HashMap<Method, ComparisonStatus> hash : colIter){
			
			if(hash == null){
				fail("Entry was null");
			}
			comparisonCol = hash.values();
			Iterator<ComparisonStatus> iter = comparisonCol.iterator();
			
			while(iter.hasNext()){
				
				ComparisonStatus c = iter.next();
				if(c != ComparisonStatus.EQUAL){
					fail("WASN'T EQUAL");
				} else {
					//is equal
					equalCount++;
				}

			}
		}
		if(comparisonCol != null){
		assertEquals("Equal count  to collection size, they are all equal", equalCount, comparisonCol.size());
		}
		else {
			//comparisonCol == null, did not get through loop
			fail("Did not get through loop at all");
		}
	
	}
	@Test
	public void testDifferentBundleAccess(){
		JarToClasses jar = new JarToClasses("/Users/Ben/Desktop/bundle.jar");
		ArrayList<Class<?>> classes = jar.classes;
		if(classes.size() == 0){
			fail("why is there nothing in here");
		}
		try{
		JarToClasses jar2 = new JarToClasses("/Users/Ben/Desktop/felixexample.jar");
		} catch(Exception e){
			
			e.printStackTrace();
			fail("Class not found");
		}
		ArrayList<Class<?>> classes2 = jar.classes;
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>>  methodEqualityMap = MapAnalyser.updateJarAnalysis(classes , classes2);
		
		Collection<HashMap<Method, ComparisonStatus>> col = methodEqualityMap.values();
		HashMap[] colIter =  col.toArray(new HashMap[col.size()]);
		Collection<ComparisonStatus> comparisonCol = null;
		
		int equalCount = 0;
		int notEqualCount = 0;
		for(HashMap<Method, ComparisonStatus> hash : colIter){
			
			if(hash == null){
				fail("Entry was null");
			}
			comparisonCol = hash.values();
			Iterator<ComparisonStatus> iter = comparisonCol.iterator();
			
			while(iter.hasNext()){
				
				ComparisonStatus c = iter.next();
				if(c != ComparisonStatus.EQUAL){
					notEqualCount++;
				} else {
					//is equal
					equalCount++;
				}

			}
		}
		if(comparisonCol != null){
			assertNotSame("There were inequal members", 0, notEqualCount);
		}
		else {
			//comparisonCol == null, did not get through loop
			fail("Did not get through loop at all");
		}
	
	
	}
}
