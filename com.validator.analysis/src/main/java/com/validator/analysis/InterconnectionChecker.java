package com.validator.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Attributes;

import com.validator.analysis.MapAnalyser.ComparisonStatus;

public class InterconnectionChecker {
	
	//Get information from the manifest, to find the classes and packages that are imported.
	public InterconnectionChecker(){
		
	}
	
	public static String getBundlePathFromNumber(int bundleNumber){
		//This method finds the bundle for checking by going through the felix-cache.
		File folder = new File("./felix-cache/bundle" + bundleNumber);
		File[] folderFiles = folder.listFiles();
		if(folderFiles == null){
			System.out.println("Bundle does not exist with that number");
		}
		ArrayList<String> versionNumbers = new ArrayList<String>();
		
		for(int i = 0; i < folderFiles.length; i++){ //TODO complete array implementation
			
			String fileName = folderFiles[i].getName();
			if(fileName.contains("version")){
			String versionNumber = fileName.substring("version".length()); //Get the version string
			versionNumbers.add(versionNumber);
			}
		}
		String versionNumber = getLatestVersionNumber(versionNumbers); //DEBUGGING
		if(versionNumber == "")
			System.out.println("Error in finding version number");
		
		return "./felix-cache/bundle" + bundleNumber +"/version"+ versionNumber + "/bundle.jar";
		
		
		
	}
	public static ComparisonStatus isServiceUsedCorrectly(ArrayList<Class<?>> services, String path){
		ComparisonStatus isItCorrect = null;
		ArrayList<ComparisonStatus> result = new ArrayList<ComparisonStatus>();
		//ArrayList<Method[]> serviceMethods = new ArrayList<Method[]>();
		//for(Class<?> clazz : services){
		//	serviceMethods.add(clazz.getDeclaredMethods());
		//}
		JarToClasses bundle = new JarToClasses(path);
		ArrayList<Class<?>> classes = bundle.classes;
		//Attributes attributes = bundle.attributes;
		//String exportPackage = attributes.getValue("Export-Package");

		//if(exportPackage.contains(exportPackage))
		//return false;
		for(Class<?> service : services){
			if(service != null){
				for(Class<?> clazz : classes){
					if(clazz != null){
						if(clazz.getName().equals(service.getName()) || clazz.getName().equals(service.getName()+ "Impl") ){ 
						//if the interface or the implementing class
							result.add(checkServiceMethods(service.getDeclaredMethods(), clazz.getDeclaredMethods()));
	
						//} else if(clazz.getName().substring(clazz.getName().lastIndexOf(".")).equals(service.getName().substring(service.getName().lastIndexOf("."))) 
						//		|| clazz.getName().substring(clazz.getName().lastIndexOf(".")).equals(service.getName().substring(service.getName().lastIndexOf("."))+ "Impl")){
						//	result.add(checkServiceMethods(service.getDeclaredMethods(), clazz.getDeclaredMethods()));
						}
					}
				}
			}
		}
		if(result.contains(ComparisonStatus.NO_METHOD)){
			//System.out.println("        Service: );
			return ComparisonStatus.NO_METHOD;
			
		} else if(result.contains(ComparisonStatus.TYPE_MISMATCH)){
			return ComparisonStatus.TYPE_MISMATCH;
		} else if(result.contains(ComparisonStatus.SUB_TYPED)){
			return ComparisonStatus.SUB_TYPED;
		} else { //Only equals
			return ComparisonStatus.EQUAL;
		}



	}
	public static ComparisonStatus isServiceUsedCorrectly(Class<?> service, String path){
		//Path is the path to a bundle.
		ComparisonStatus isItCorrect = null;
		Method[] serviceMethods = service.getDeclaredMethods();
		JarToClasses bundle = new JarToClasses(path);
		Attributes attributes = bundle.attributes;
		//String exportPackage = attributes.getValue("Export-Package");
		
		//if(exportPackage.contains(exportPackage))
			//return false;
		
		for(Class<?> clazz : bundle.classes){
			if(clazz.getName().equals(service.getName()) || clazz.getName().equals(service.getName()+ "Impl") ){ 
				//if the interface or the implementing class
				isItCorrect = checkServiceMethods(serviceMethods, clazz.getDeclaredMethods());
				
			} else if(clazz.getName().substring(clazz.getName().lastIndexOf(".")).equals(service.getName().substring(service.getName().lastIndexOf("."))) 
					|| clazz.getName().substring(clazz.getName().lastIndexOf(".")).equals(service.getName().substring(service.getName().lastIndexOf("."))+ "Impl")){
				isItCorrect = checkServiceMethods(serviceMethods, clazz.getDeclaredMethods());
			}
		}
		if(isItCorrect == null){
			System.out.println("isServiceCorrect() has returned null");
		}
		
		return isItCorrect;
		
	}
	
	public static ComparisonStatus isServiceUsedCorrectly(Class<?> service, int bundleNumber){
		//Minimum check according to Kramer and Magee.
		//This class is used to check if the imported services are following correct usage,
		//including the correct usage of data types.
		//Compare the return and parameter types.
		//COmparing the service interface to the implementing class
		//Get service methods
		Method[] serviceMethods = service.getDeclaredMethods();
		ComparisonStatus isItCorrect = null;
		//String servicesToBeChecked = manifest.get("Import-Package"); //Assuming this gives qualified class name
		
		//Now, bundle structure in the OSGI Framework
		//"felix-cache" is created in working directory by the framework, which contains a folder for the bundle based on its number
		
		//Automatically checks newest version , may have to check in "data" folder
	
		File folder = new File("./felix-cache/bundle" + bundleNumber); //will throw exception
		
		//File folder = new File(".");
		File[] folderFiles = folder.listFiles();
		if(folderFiles == null){
			System.out.println("No bundle found in the felix-cache");
		}
		ArrayList<String> versionNumbers = new ArrayList<String>();
		for(int i = 0; i < folderFiles.length; i++){ //TODO complete array implementation
			String fileName = folderFiles[i].getName();
			if(fileName.contains("version")){
			String versionNumber = fileName.substring("version".length()); //Get the version string
			versionNumbers.add(versionNumber);
			}
		}
		String versionNumber = getLatestVersionNumber(versionNumbers); //DEBUGGING
		if(versionNumber == "")
			System.out.println("PLS NO");
		JarToClasses bundle = new JarToClasses("./felix-cache/bundle" + bundleNumber +"/version"+ versionNumber + "/bundle.jar");
		//Class which is being compared to the service class
		//Need to compare these methods
		Attributes attributes = bundle.attributes;
		String exportPackage = attributes.getValue("Export-Package");
		
		//if(exportPackage.contains(exportPackage))
			//return false;
		
		for(Class<?> clazz : bundle.classes){
			if(clazz.getName().equals(service.getName()) || clazz.getName().equals(service.getName()+ "Impl") ){ 
				//if the interface or the implementing class
				isItCorrect = checkServiceMethods(serviceMethods, clazz.getDeclaredMethods());
				
			}
		}
		
		return isItCorrect;
	}
	
	public static ComparisonStatus checkServiceMethods(Method[] serviceMethods, Method[] usedMethods){ //Checking to see that the methods are the same
		if(serviceMethods.length > usedMethods.length){
			//interface has to have at least all of it's methods implemented
			System.out.println("Going into the first statement");
			return ComparisonStatus.NOT_EQUAL;
		}
		
		//ArrayList<Method> equalMethods = new ArrayList<Method>(); 
		//arraylist for all the equal methods, should be same size as service methods

		for(Method method : serviceMethods){
		//	System.out.println("Making it to i = " + i++);
			for(Method method2 : usedMethods){
			//	System.out.println("Making it to j = " + j++);
				if(method.equals(method2)){
					System.out.println(method.getName() +  " is equal");
					return ComparisonStatus.EQUAL;
					//equalMethods.add(method2);
					
				} else if(method.getName().equals(method2.getName())){
				//	System.out.println("Making it to the start of the methodname == methodname 2 block");
					Class<?> returnType = method.getReturnType();
					Class<?> returnType2 = method2.getReturnType();
					Class<?>[] parameterTypes = method.getParameterTypes();
					Class<?>[] parameterTypes2 = method2.getParameterTypes();

					if(returnType.getName().equals(returnType2.getName())){	
						//System.out.println("return type name equals return type 2");
						ComparisonStatus paramsStatus =	MapAnalyser.areParamsEqual(parameterTypes, parameterTypes2, returnType, returnType2);
						if(paramsStatus != ComparisonStatus.EQUAL){
							//return false;
							paramsStatus = MapAnalyser.typeMismatchChecking(parameterTypes, parameterTypes2, returnType, returnType2);
							if(paramsStatus == ComparisonStatus.SUB_TYPED){
								System.out.println("Method: " +method.getName() +  " is sub typed");
								return ComparisonStatus.SUB_TYPED;
							}
							else if(paramsStatus == ComparisonStatus.TYPE_MISMATCH){
								System.out.println("Method: " +method.getName() +  " has a type mismatch");
								return ComparisonStatus.TYPE_MISMATCH;
							}
							
						} else {
							System.out.println("Method: " +method.getName() +  " is equal");
							return ComparisonStatus.EQUAL;
							//equalMethods.add(method2);
						}

					} else {
				//		System.out.println("Making it to the start of the else block");
						//Return types were not the same.
						if(returnType.isAssignableFrom(returnType2) || returnType2.isAssignableFrom(returnType)){
							//Where the types are subtypes
							System.out.println("Method: " +method.getName() +  " is sub typed");
							return ComparisonStatus.SUB_TYPED;
						}
						//the return types have different names, because they are different
						System.out.println("Method: " +method.getName() +  " has a type mismatch");
						return ComparisonStatus.TYPE_MISMATCH;
							
					}
				} else {
					continue;
				}
			}
		}


			//When the interface isn't used in this class
		System.out.println("Missing method in this class");
			return ComparisonStatus.NO_METHOD;
		
		
	}
	
	static String getLatestVersionNumber(ArrayList<String> versionNumbers){
		//TODO Add in checks for less than 3 digits.
		//versionNumbers contains a few version numbers, this will be accessed as String[]
		ArrayList<String[]> splitVersionStrings = new ArrayList<String[]>();
		if(versionNumbers.size() == 1){
			return versionNumbers.get(0);
		}
		for(String str : versionNumbers){
			splitVersionStrings.add(str.split("\\."));
			
		}
		String[] temp = splitVersionStrings.get(0);
		splitVersionStrings.remove(temp);
		for(String[] str : splitVersionStrings){ 
			
			
		 	if(Integer.parseInt(temp[0]) > Integer.parseInt(str[0])){
		 		splitVersionStrings.remove(str);
		 		continue;
		 	} else if(Integer.parseInt(temp[0]) < Integer.parseInt(str[0])){
		 		temp = str;
		 		continue;
		 		
		 	} else {
		 		if(Integer.parseInt(temp[1]) < Integer.parseInt(str[1])){
		 			temp = str;
		 			continue;
		 			
		 		}else if(Integer.parseInt(temp[1]) > Integer.parseInt(str[1])){
		 			splitVersionStrings.remove(str);
		 			
		 		} else {
		 			
		 			if(Integer.parseInt(temp[2]) < Integer.parseInt(str[2])){
		 				temp = str;
		 			} else if(Integer.parseInt(temp[2]) > Integer.parseInt(str[2])){
		 				splitVersionStrings.remove(str);
		 			
		 			} else {
		 				
		 			}
		 		}
		 	}
		 	
		}
		
//		int[] firstVersionNumber = new int[versionNumbers.length];
//		for(int i = 0; i< versionNumbers.length-1; i++){
//			firstVersionNumber[i] = Integer.parseInt(versionNumbers[i]); //Get the first version number 
//		}
//		int temp = firstVersionNumber[0];
//		HashMap<Integer, Integer> similarVersions = new HashMap<Integer, Integer>();
//		for(int i = 1; i<versionNumbers.length-1;i++){
//			if(firstVersionNumber[i] > temp){
//				temp = firstVersionNumber[i];
//			} else if(firstVersionNumber[i] == temp){
//				similarVersions.put(i, firstVersionNumber[i]);
//			} else {
//				//temp > firstVersionNumber[i]
//				continue;
//			}
//		
//		}
//		
//		//compare the first version number
//		if()
		if(temp.length == 3){
		return temp[0] + "." + temp[1] + "." + temp[2];
		} else if(temp.length == 2){
			return temp[0] + "." + temp[1]; 
		} else{
			return temp[0];
		}
	}

}
