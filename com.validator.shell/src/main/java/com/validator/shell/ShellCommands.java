package com.validator.shell;



import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import com.validator.analysis.*;
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
	//
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
			String bundlePath = InterconnectionChecker.getBundlePathFromNumber(bundleNumber); 

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
			//System.out.println("Making it to the start of the iterating loop, should make it through" + classSize + " times");
			//int i = 0;
			Bundle updatingBundle = bundleContext.getBundle("file:" + path);

			while(classIter.hasNext()){
				//i++;
				tempClass = classIter.next();
				//System.out.println("Making it through this loop, iteration: " + i);
				if(tempClass != null){
					//	System.out.println("Getting into tempClass block for class: " + tempClass);
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
				updatingBundle.update();
				System.out.println("Updated bundle "+ bundleNumber + " to " + path + " successfully!");

				updatingBundle.start();
				System.out.println("Bundle " + bundleNumber+ " has started successfully");
			} else {
				System.out.println("Revise this bundle before updating");
			}
		}catch(BundleException e){
			if(e.getType() == BundleException.RESOLVE_ERROR){
				System.out.println("Resolve error, ensure you have access to all of the relevant packages the bundle is importing");
				System.out.println("Bundle is installed, solve resolver issue before starting");
			} else {
				System.out.println("A Bundle exception because : " + e.getType());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Some random exception :(");
			e.printStackTrace();
		}
	}
	/*public void update(
			@Descriptor("The number of the bundle which you wish to validate")int bundleNumber){//Maybe not relevant...


	}*/
	//
	@Descriptor("Used for checking that a bundle implements a service correctly, give either paths or bundle numbers for the validating bundle and the bundle that contains the service.")
	public void interconnection(
			@Descriptor("The path to the bundle being updated")String path, 
			@Descriptor("The path to the bundle containing the service")String path2){
		JarToClasses bundle = new JarToClasses(path);
		JarToClasses serviceBundle = new JarToClasses(path2);
		ArrayList<Class<?>> serviceClasses = serviceBundle.classes;
		Class<?> service = null;
		Bundle updatingBundle = bundleContext.getBundle("file:"+ path);
		for(Class<?> clazz: serviceClasses){//This only checks one bundle, 
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
			//System.out.println("Passed validation against this service, feel free to update the bundle safely.");
			try {
				updatingBundle.update();
				System.out.println("Updated " + path + " was successful!");
				updatingBundle.start();
				System.out.println("Bundle " + updatingBundle.getBundleId()+ " has started successfully!");
			} catch (BundleException e) {
				if(e.getType() == BundleException.RESOLVE_ERROR){
					System.out.println("Resolve error, ensure you have access to all of the relevant packages the bundle is importing");
					System.out.println("Bundle is installed, solve resolver issue before starting");
				} else {
					System.out.println("A Bundle exception because : " + e.getType());
					e.printStackTrace();

				}
			}
			catch(Exception e){
				System.out.println("Some other exception");
				e.printStackTrace();
			}

		} else {
			System.out.print("Service was not correct in its usage, revise your usage of this service before updating");
		}
	}
	//Complete
	public void interconnection(
			@Descriptor("Bundle number of the bundle that contains the service")int bundleNumber,
			@Descriptor("Path to the bundle that is being checked for validation")String bundlePath){


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
			//Bundle[] bundles = bundleContext.getBundles();
			Bundle updatingBundle = bundleContext.getBundle("file:"+bundlePath);
			//				for(Bundle b : bundles){
			//					if(b.getBundleId() == bundleNumber){
			//						updatingBundle = b;
			//						break;
			//					}
			//				}
			if(updatingBundle != null){
				try {
					updatingBundle.update();

					System.out.println("Bundle " + bundlePath +  " updated");
					updatingBundle.start();
					System.out.println("Bundle " + updatingBundle.getBundleId() + " has started successfully!");
				} catch (BundleException e) {
					if(e.getType() == BundleException.RESOLVE_ERROR){
						System.out.println("Resolve error, ensure you have access to all of the relevant packages the bundle is importing");
						System.out.println("Bundle is installed, solve resolver issue before starting");
					} else {
						System.out.println("A Bundle exception because : " + e.getType());
						e.printStackTrace();
					}
				}


			}


			//			try {
			//				//commandProcessor = getOsgiService(CommandProcessor.class);
			//				ThreadIOImpl t = new ThreadIOImpl();
			//				t.start();
			//				cp = new CommandProcessorImpl(t);
			//				CommandSession csesh = cp.createSession(System.in, System.out, System.err);
			//				csesh.execute("felix:update "+ bundleNumber);
			//			} catch (Exception e) {
			//				// 
			//				e.printStackTrace();
			//			}
		}else if(serviceIsCorrect == ComparisonStatus.SUB_TYPED){
			System.out.println("Passed validation, although the service is using a subtype.");
			Bundle updatingBundle = bundleContext.getBundle("file:"+bundlePath);
			if(updatingBundle != null){
				try {
					updatingBundle.update();
				} catch (BundleException e) {
					if(e.getType() == BundleException.RESOLVE_ERROR){
						System.out.println("Resolve error, ensure you have access to all of the relevant packages the bundle is importing");
						System.out.println("Bundle is installed, solve resolver issue before starting");
					} else {
						System.out.println("A Bundle exception because : " + e.getType());
						e.printStackTrace();
					}
				}
				System.out.println("Bundle " + bundlePath +  " updated");
			}
		} else {
			System.out.println("Service was not correct in usage, revise your usage of this service before updating.");
		}
	}


	@Descriptor("This method checks the interface implementations in this bundle against all the bundle"
			+ " in the framework which export the interface through the export-package header")
	public void totalinterconnection(
			@Descriptor("Path to the bundle in which you wish to check against all other bundles")String path){
		//Need to find the service implementation from the bundle we are checking against
		Bundle checkingBundle = bundleContext.getBundle("file:" + path);
		System.out.println("The path: " + path);
		JarToClasses checkingJar = new JarToClasses(path);
		ArrayList<Class<?>> classesToCheck = new ArrayList<Class<?>>();
		Class<?> activator = null;
		for(Class<?> clazz : checkingJar.classes){
			if(clazz.getName().contains("Impl")){
				classesToCheck.add(clazz);
			} else if(clazz.getName().contains(".Activator")){
				activator = clazz; //By definition there is only one activator per bundle
			}
		}
		System.out.println("We have " + classesToCheck.size() + " classes to look for which is: " + classesToCheck.get(0));
		Class<?>[] interfacesToFind = classesToCheck.get(0).getInterfaces();
		System.out.println("Now we are looking for this interface: " + interfacesToFind[0].getName()); //NEED TO FIND THIS INTERFACE
		List<Class<?>> interfacesList = Arrays.asList(interfacesToFind);
		ArrayList<Class<?>> interfaces = new ArrayList<Class<?>>(interfacesList);
		//Now to decide if in bundle.
		if(isInBundle(interfacesToFind, checkingJar)){
			System.out.println("The only interfaces to find are in this class already");
			System.out.println("");

			ComparisonStatus serviceIsCorrect = InterconnectionChecker.isServiceUsedCorrectly(interfaces, path);
			if(serviceIsCorrect == ComparisonStatus.EQUAL || serviceIsCorrect == ComparisonStatus.SUB_TYPED){
				if(serviceIsCorrect == ComparisonStatus.EQUAL){
					System.out.println("Passed validation against this service, feel free to update the bundle safely");
					if(checkingBundle != null){
						try {
							checkingBundle.update();

							System.out.println("Bundle " + path +  " updated");
							checkingBundle.start();
							System.out.println("Bundle " + checkingBundle.getBundleId() + " has started successfully!");
						} catch (BundleException e) {
							if(e.getType() == BundleException.RESOLVE_ERROR){
								System.out.println("Resolve error, ensure you have access to all of the relevant packages the bundle is importing");
								System.out.println("Bundle is installed, solve resolver issue before starting");
							} else {
								System.out.println("A Bundle exception because : " + e.getType());
								e.printStackTrace();
							}
						}


					}
				} else if(serviceIsCorrect == ComparisonStatus.SUB_TYPED){
					System.out.println("Passed validation, although the service is using a subtype.");

					if(checkingBundle != null){
						try {
							checkingBundle.update();
							System.out.println("Bundle " + path +  " updated");
							checkingBundle.start();
						} catch (BundleException e) {
							if(e.getType() == BundleException.RESOLVE_ERROR){
								System.out.println("Resolve error, ensure you have access to all of the relevant packages the bundle is importing");
								System.out.println("Bundle is installed, solve resolver issue before starting");
							} else {
								System.out.println("A Bundle exception because : " + e.getType());
								e.printStackTrace();
							}
						}
						System.out.println("Bundle " + path +  " updated");
					}
				}
			} else {
				System.out.println("Service was not correct in usage, revise your usage of this service before updating.");
			}
		} else {//When the interface isn't internal
			//Need to find the results of finding the interfaces in external bundles
			//USe service references from the service register, if bundle uses service, check that this is used correctly.
			
			//TODO External checking -- change for checking service refs
			try {
				checkingBundle.update();
			} catch (BundleException e) {
				if(e.getType() == BundleException.RESOLVE_ERROR){ 
					//by doing this we can use the framework to find out if the package exists in the framework
					System.out.println("There is an issue resolving, ensure that the packages under "
							+ "your bundles Import-Package header are in the OSGi framework");
				} else {
					System.out.println("There is a different resolver error");
					e.printStackTrace();
				}
				return; //Just to end this method from running
			}
			ArrayList<String> packageNames = new ArrayList<String>();
			for(Class<?> inter : interfaces){
				packageNames.add(inter.getPackage().getName());
			}

			Bundle[] allBundles = bundleContext.getBundles();
			for(Bundle bun : allBundles){ //Only used for debugging TODO remove for production code
				System.out.println("Bundle: "+bun.getBundleId());
			}
			//Looking now for the packages that are being exported to see if we can find this package.
			//This list contains bundles that contain the packages we are looking for in their Export-Package header
			//meaning that these packages are inside the bundle, meaning we will find the interfaces we are looking for.
			ArrayList<Bundle> bundlesToCheck = new ArrayList<Bundle>(); 
			for(Bundle bund : allBundles){
				String temp = bund.getHeaders().get("Export-Package");
				String[] tempSplit;
				if(temp != null){
					tempSplit = temp.split(",");
					//System.out.println("Made it to tempSplit");
				} else {
					continue;
				}
				for(String x : tempSplit){
					//System.out.println(x + " is being checked");
					if(packageNames.contains(x)){
						System.out.println("Made it through the packages comparison");
						bundlesToCheck.add(bund);
					}
				}
			}
			ArrayList<Boolean> servicesUsedCorrectly = new ArrayList<Boolean>();
			for(int i= 0; i < classesToCheck.size(); i++ ){
				
				 String[] classRoots = classesToCheck.get(i).getName().split("\\.");
				 String classRoot = null;
				 for(int j= 0; j < classRoots.length-1;j++){
					 classRoot += classRoots[i];
				 }
				 String activatorRoot = null;
				 String[] activatorRoots = activator.getName().split("\\.");
				 for(int j= 0; j < activatorRoots.length-1;j++){
					 activatorRoot += activatorRoots[i];
				 }
				if(activatorRoot.equals(classRoot)){
					//If they have the same class root name, then they are from the same package, 
					//the implementing class and the activator that registers the service
					boolean isServiceUsedCorrectly = checkServices(checkingBundle, activator.getName(), classesToCheck.get(i));
					servicesUsedCorrectly.add(isServiceUsedCorrectly);
				}
			}
			if(servicesUsedCorrectly.contains(Boolean.TRUE)){
				System.out.println("Service uses the correct package in implementation and is safe");
				return;//No need to do more checks
			}
			//More checks needed if no trues are returned from servicesUSed correctly.
			System.out.println("Finished getting packages to check, we have " + packageNames.size() + " packages to check, which is "+ packageNames.get(0));
			System.out.println("Finished getting bundles to check, we have " + bundlesToCheck.size() + " bundles to check, which is: " + bundlesToCheck.get(0));
			//Now have the bundles we need to check by comparing the Import and Export package headers.
			//Then search through service package for interfaces - then compare these interfaces to classes checking if they implement them
			//then checking that they are being implemented correctly.
			//TODO -- revise the checking -- write some tests -- ~~ weeeee
			//check that we are finding the interface in these classes
			/*ArrayList<Class<?>> checkingClasses = new ArrayList<Class<?>>();
			ArrayList<Class<?>[]> interfaces = new ArrayList<Class<?>[]>();
			for(Bundle b : bundlesToCheck){
				JarToClasses jar = new JarToClasses(b.getLocation().substring(5));
				for(Class<?> clazz : jar.classes){
					if(clazz.getName().contains("Impl")){ //By the naming convention of implementing a service in OSGi
						checkingClasses.add(clazz);
						interfaces.add(clazz.getInterfaces()); //gets all interfaces implemented by this class.
					}
				}
			}*/
			//Looking for items from interfaces list, and using classesToCheck with this
			ArrayList<ComparisonStatus> results = new ArrayList<ComparisonStatus>();
			int interfacesLength = interfaces.size();
			for(Bundle bund : bundlesToCheck){//TODO MAKE THIS MEET SPEC IN NOTEBOOK
				JarToClasses jar = new JarToClasses(bund.getLocation().substring(5));
				for(Class<?> clazz : jar.classes){
					if(interfaces.contains(clazz)){
						//find if this interface is being implemented correctly.
						Class<?> interf = interfaces.get(interfaces.indexOf(clazz));
						//Now compare clazz against interf
						ComparisonStatus result = InterconnectionChecker.checkServiceMethods(interf.getDeclaredMethods(), clazz.getDeclaredMethods());
						if(result == ComparisonStatus.EQUAL){
							interfacesLength--; //Decrement once found, once all are found we won't need to look any more
							System.out.println(clazz.getName()+" and "+ interf.getName() + " were equal");
							results.add(result);
						} else if(result == ComparisonStatus.SUB_TYPED){
							interfacesLength--;
							System.out.println(clazz.getName()+" and "+ interf.getName() + " were sub typed");
							results.add(result);
						} else {
							//FAILED
							System.out.println(interf.getName() + " failed with a " + result + " sort this out before updating and starting.");
							results.add(result);
						}

						/*						for(Class<?> implClazz :classesToCheck){
							Class<?>[] classesInterfaces = implClazz.getInterfaces();
							for(int i = 0; i < classesInterfaces.length; i++){
								if(interfaces.contains(classesInterfaces[i])){
								//ComparisonStatus result =	InterconnectionChecker.checkServiceMethods()
							}

						}*/
					}
				}
			}
			for(ComparisonStatus c : results){
				if(c == ComparisonStatus.EQUAL || c == ComparisonStatus.SUB_TYPED){
					continue;

				} else {
					System.out.println("Interconnection failed");
					return;
				}
			}

			if(checkingBundle != null){
				try {
					checkingBundle.update();
					System.out.println("Bundle " + path +  " updated");
					checkingBundle.start();
				} catch (BundleException e) {
					if(e.getType() == BundleException.RESOLVE_ERROR){
						System.out.println("Resolve error, ensure you have access to all of the relevant packages the bundle is importing");
						System.out.println("Bundle is installed, solve resolver issue before starting");
					} else {
						System.out.println("A Bundle exception because : " + e.getType());
						e.printStackTrace();
					}
				}
				System.out.println("Bundle " + path +  " updated");
				if(results.contains(ComparisonStatus.SUB_TYPED)){
					System.out.println("Updated and started, however with a subtyped interface");
				} else {
					System.out.println("Updated and started with no issues");
				}

			}
		}
		/*	System.out.println("Finished getting interfaces to check, we have " + interfaces.size() 
			+ "interfaces to check, which is: "+ interfaces.get(0)[0].getName());
			System.out.println("Finished getting classes to check, we have " + classesToCheck.size()
			+ " classes to check, which is: "+ checkingClasses.get(checkingClasses.size()-1));*/

		//Then compare the interfaces that have be obtained, or do that in loop?
		/*ArrayList<ComparisonStatus> results = new ArrayList<ComparisonStatus>();
			HashMap<String, ComparisonStatus> resultMap = new HashMap<String, ComparisonStatus>();
			for(Class<?>[] interf : interfaces){//uses checkServiceMethods
				for(Class<?> singleInterface : interf){
					for(Class<?> checking : classesToCheck){
						if(checking.getName().equals(singleInterface.getName())){
							resultMap.put(checking.getName(),
									InterconnectionChecker.checkServiceMethods(singleInterface.getDeclaredMethods(),
											checking.getDeclaredMethods()));
							System.out.println("Reaching comparison of  " + checking.getName() + " and " + singleInterface.getName());
						}
					}
				}
			}
		 */
		/*	System.out.println("Finished comparisons");
			if(resultMap.keySet().size() == 0){
				System.out.println("Result map is zero");

			}
			else if(resultMap.keySet().size() > 0){
				System.out.println("Result map is greater than 0");
			}
			for(String key :resultMap.keySet()){
				System.out.print(key + " has the comparison status of " + resultMap.get(key));
			}*/
	}

	boolean isInBundle(Class<?>[] checkingInterfaces, JarToClasses jar){

		for(Class<?> clazz : jar.classes){
			for(Class<?> inter : checkingInterfaces){
				if(inter.getName() == clazz.getName() && MapAnalyser.isClassEqual(inter, clazz)){
					return true;
				}
			}
		}

		return false;

	}
	boolean checkServices(Bundle bundle, String clazz, Class<?> implClazz){
		//@params bundle: The bundle which has the implementing class
		//clazz: The class that registers the service
		//implClazz: The class that is implementing the bundle
		//This method is for checking the service usage of the bundle that is being checked
		BundleContext bc = bundle.getBundleContext();
		ServiceReference<?> serviceRef = null;
		//Get the service reference for the service registered by the package that is from the activator of the package
		//e.g. tutorial.example.Activator
		serviceRef =  bc.getServiceReference(clazz);
		
		if(serviceRef.isAssignableTo(bundle, implClazz.getName())){
			//if true then service is implemented correctly
			//isAssignableTo compares the package used to see that the bundle is using the correct package in their implementation
			System.out.println(bundle.getSymbolicName() + " has implemented " + serviceRef.toString() + " correctly in "+ implClazz.getName());
			return true;
		}
		return false;
	}
}
