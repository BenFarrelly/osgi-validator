package com.validator.shell;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.gogo.runtime.threadio.ThreadIOImpl;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import com.validator.analysis.*;
import com.validator.analysis.InterconnectionChecker;
import com.validator.analysis.MapAnalyser.ComparisonStatus;

public class ShellCommands {
	//This class is a bundle which gives a customised version of the Gogo shell giving the extra commands in validation.
	private final BundleContext bundleContext;
	private ConfigurationAdmin configAdmin;
	
	CommandProcessorImpl cp;
	public ShellCommands(BundleContext bundleContext){
		
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
		try{
			
		JarToClasses jar = new JarToClasses(path);
		
		ArrayList<Class<?>> classes = jar.classes;
		String bundlePath = InterconnectionChecker.getBundlePathFromNumber(bundleNumber); //TODO check if this is actually bening used
		//TODO take the code from the tests
		JarToClasses jar2 = new JarToClasses(bundlePath);
		ArrayList<Class<?>> classes2 = jar2.classes;
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = MapAnalyser.updateJarAnalysis(classes, classes2);
		//Now to give a response regarding the results
		Set<Class<?>> classSet = methodEqualityMap.keySet();
		Iterator<Class<?>> classIter = classSet.iterator();
		Class<?> tempClass = null;
		HashMap<Method, ComparisonStatus> tempMap;
		int classSize = classSet.size();
		int tempClassSize = 0;
		System.out.println("Making it to the start of the iterating loop, should make it through" + classSize + " times");
		int i = 0;
		while(classIter.hasNext()){
			i++;
			tempClass = classIter.next();
			System.out.println("Making it through this loop, iteration: " + i);
			if(tempClass != null){
				System.out.println("Getting into tempClass block for class: " + tempClass);
			//assertNotNull("Class isn't null", tempClass);
			tempMap = methodEqualityMap.get(tempClass);
			//This is going to get ugly
			
				if(tempMap.containsValue(ComparisonStatus.NO_METHOD)){
					//ComparisonStatus classStatus = tempMap.get(tempClass);
					System.out.println(tempClass.getName() + " has a missing method, solve this before updating bundle");
				
				}else if(tempMap.containsValue(ComparisonStatus.TYPE_MISMATCH)){
					System.out.println(tempClass.getName() + " has a type mismatch solve this before updating bundle");
				
				}else if(tempMap.containsValue(ComparisonStatus.SUB_TYPED)){
					tempClassSize++;
					System.out.println(tempClass.getName() + " has a subtyped method, bundle is fine, just letting you know!");
				}else if(tempMap.containsValue(ComparisonStatus.NOT_EQUAL)){
					System.out.println(tempClass.getName() + " has a non-equal method solve this before updating bundle");
				} else if (tempMap.containsValue(ComparisonStatus.EQUAL)){
						tempClassSize++;
						System.out.println("ComparisonStatus = EQUAL");
				} else {
					System.out.println("Did not have a comparison status for some reason");
				}
			
			}
		}
		if(tempClassSize == classSize){
			System.out.println("The bundle has been validated and can be updated!");
		} else {
			System.out.println("Revise this bundle before updating");
		}
		}catch(Exception e){
			System.out.println("That path returned a FileNotFoundException, please revise the path you provided");
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
		for(Class<?> clazz: serviceClasses){//This only checks one bundle, TODO consider adding implementation for more than one service
			if(clazz.isInterface()){
				service = clazz;
				break;
			}
		}
		ComparisonStatus serviceIsCorrect = null;
		if(service!= null){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(service, path); // need to make new implementation that takes a path
		}
		if(serviceIsCorrect == ComparisonStatus.EQUAL || serviceIsCorrect == ComparisonStatus.SUB_TYPED){
			System.out.println("Passed validation against this service, feel free to update the bundle safely.");
		} else {
			System.out.print("Service was not correct in its usage, revise your usage of this service before updating");
		}
	}
	
	public void interconnection(
			@Descriptor("Bundle number of the bundle that contains the service")int bundleNumber,
			@Descriptor("Path to the bundle that is being checked for validation")String bundlePath) throws BundleException{
		
		
		String path = InterconnectionChecker.getBundlePathFromNumber(bundleNumber);
		//JarToClasses serviceBundle = new JarToClasses(path);
		JarToClasses bundle = new JarToClasses(path);
		ArrayList<Class<?>> serviceClasses = bundle.classes;
		ArrayList<Class<?>> services = new ArrayList<Class<?>>();
		for(Class<?> clazz: serviceClasses){
			if(clazz.isInterface()){
				services.add(clazz);
				
			}
		}
		ComparisonStatus serviceIsCorrect = null;
		if(services.size() != 0){
			serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(services, bundlePath); // need to make new implementation that takes a path
		}
		System.out.println("ComparisonStatus of this interface is: " + serviceIsCorrect);
		if(serviceIsCorrect == ComparisonStatus.EQUAL){
			System.out.println("Passed validation against this service, feel free to update the bundle safely");
			Bundle[] bundles = bundleContext.getBundles();
			Bundle updatingBundle = null;
				for(Bundle b : bundles){
					if(b.getBundleId() == bundleNumber){
						updatingBundle = b;
						break;
					}
				}
			if(updatingBundle != null){
				updatingBundle.update();
				System.out.println("Bundle" + bundleNumber + "updated");
			}
			
//			try {
//				//commandProcessor = getOsgiService(CommandProcessor.class);
//				ThreadIOImpl t = new ThreadIOImpl();
//				t.start();
//				cp = new CommandProcessorImpl(t);
//				CommandSession csesh = cp.createSession(System.in, System.out, System.err);
//				csesh.execute("felix:update "+ bundleNumber);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}else if(serviceIsCorrect == ComparisonStatus.SUB_TYPED){
			System.out.println("Passed validation, although the service is using a subtype.");
		} else {
			System.out.println("Service was not correct in usage, revise your usage of this service before updating.");
		}
	}
	
}
