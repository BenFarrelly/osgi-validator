package com.validator.analysis.testing;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.validator.analysis.AnalysisStarter;
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
	public void testMethodsAreEqual(){
		//Later change test to use Jar from JarToClasses
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis("/Users/Ben/Desktop/com.acme.prime.upper.api.jar" , "/Users/Ben/Desktop/com.acme.prime.upper.api.jar");
		Collection<HashMap<Method, ComparisonStatus>> col = methodEqualityMap.values();
		Iterator<HashMap<Method, ComparisonStatus>> colIter = col.iterator();
		if(!colIter.hasNext()){
			fail("Iterator does not have next");
		}
		while(colIter.hasNext()){
			HashMap<Method, ComparisonStatus> map = colIter.next();
			Collection<ComparisonStatus> comparisonCol = map.values();
			Iterator<ComparisonStatus> iter = comparisonCol.iterator();
			
			for(ComparisonStatus c = iter.next(); iter.hasNext(); ){
				if(c != ComparisonStatus.EQUAL){
					fail("WASN'T EQUAL");
				}
				
			}
			
		}
		assertTrue("All must be true", true);
	}
	@Test
	public void testMethodsAreNotEqual(){
		//Testing that two different jars do not have the same methods (That my code is sort of correct)
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis("/Users/Ben/Desktop/com.acme.prime.upper.api.jar" , "/Users/Ben/Desktop/aether-api-1.0.2.v20150114.jar");
		Collection<HashMap<Method, ComparisonStatus>> col = methodEqualityMap.values();
		Iterator<HashMap<Method, ComparisonStatus>> colIter = col.iterator();
		if(!colIter.hasNext()){
			fail("Iterator does not have next");
		}
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
		fail("Reached the end without finding an inequality");
	}

}
