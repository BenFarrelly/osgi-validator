package com.validator.shell;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import com.validator.analysis.*;
import com.validator.analysis.MapAnalyser.ComparisonStatus;

public class ShellCommands {
	//This class is a bundle which gives a customised version of the Gogo shell giving the extra commands in validation.
	private final BundleContext bundleContext;
	private ConfigurationAdmin configAdmin;
	
	ShellCommands(BundleContext bundleContext){
		
		this.bundleContext = bundleContext;
		
		//getConfigAdmin();
	}
	@Descriptor("Validate a bundle for updating.")
	public void update(
			@Descriptor("The bundle number that is the 'old' bundle")int bundleNumber, 
			@Descriptor("The path to the new bundle")String path){
		//This method is used to validate that a bundle can indeed update safely - in that nomethod and typemismatch exceptions will not occur. 
		//These are typically unchecked by osgi.
		
		//Firstly the classes are taken from the Bundle
		JarToClasses jar = new JarToClasses(path);
		ArrayList<Class<?>> classes = jar.classes;
		String bundlePath = InterconnectionChecker.getBundlePathFromNumber(bundleNumber);
		//TODO take the code from the tests
		
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(classes, classes);
		//Now to give a response regarding the results
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		int classSize = classSet.size();
		int tempClassSize = 0;
		while(classIter.hasNext()){
			
			tempClass = classIter.next();
			if(tempClass != null){
			//assertNotNull("Class isn't null", tempClass);
			tempMap = methodEqualityMap.get(tempClass);
				if(!tempMap.containsValue(ComparisonStatus.EQUAL)){
					ComparisonStatus classStatus = tempMap.get(tempClass);
					System.out.println(tempClass.getName() + " has a " + classStatus + " ");
				
				} else if (tempMap.containsValue(ComparisonStatus.EQUAL)){
						tempClassSize++;
				}
			
			}
		}
		if(tempClassSize == classSize){
			System.out.println("The bundle has been validated and can be updated!");
		}
	}
	public void update(
			@Descriptor("The number of the bundle which you wish to validate")int bundleNumber){//Maybe not relevant...
		
		
	}
	@Descriptor("Used for checking that a bundle implements a service correctly, give either paths or bundle numbers for the validating bundle and the bundle that contains the service.")
	public void interconnection(
			@Descriptor("The path to the bundle being updated")String path, 
			@Descriptor("The path to the bundle containing the service")String path2){
		JarToClasses bundle = new JarToClasses(path);
		JarToClasses serviceBundle = new JarToClasses(path2);
		ArrayList<Class<?>> serviceClasses = serviceBundle.classes;
		Class<?> service = null;
		for(Class<?> clazz: serviceClasses){
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		boolean serviceIsCorrect;
		if(service!= null){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, 0); // need to make new implementation that takes a path
		}
	}
	public void interconnection(int bundeNumber, int bundleNumber){
		
	}
	
}
