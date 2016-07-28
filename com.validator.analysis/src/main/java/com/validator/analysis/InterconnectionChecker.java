package com.validator.analysis;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Attributes;

import com.validator.analysis.MapAnalyser.ComparisonStatus;

public class InterconnectionChecker {
	
	//Get information from the manifest, to find the classes and packages that are imported.
	public InterconnectionChecker(){
		
	}
	
	public boolean isServiceUsedCorrectly(Class<?> service, int bundleNumber){
		//Minimum check according to Kramer and Magee.
		//This class is used to check if the imported services are following correct usage,
		//including the correct usage of data types.
		//Compare the return and parameter types.
		//COmparing the service interface to the implementing class
		//Get service methods
		Method[] serviceMethods = service.getDeclaredMethods();
		
		//String servicesToBeChecked = manifest.get("Import-Package"); //Assuming this gives qualified class name
		
		//Now, bundle structure in the OSGI Framework
		//"felix-cache" is created in working directory by the framework, which contains a folder for the bundle based on its number
		
		//Automatically checks newest version , may have to check in "data" folder
		File folder = new File("felix-cache/bundle" + bundleNumber + "");
		File[] folderFiles = folder.listFiles();
		String[] versionNumbers = new String[folderFiles.length];
		for(int i = 0; i < folderFiles.length-1; i++){ //TODO complete array implementation
			String fileName = folderFiles[i].getName();
			String versionNumber = fileName.substring("version".length()-1); //Get the version string
			versionNumbers[i] = versionNumber;
		}
		String versionNumber = getLatestVersionNumber(versionNumbers);
		JarToClasses bundle = new JarToClasses("felix-cache/bundle" + bundleNumber +"/data/version"+ versionNumber + "/bundle.jar");
		//Class which is being compared to the service class
		//Need to compare these methods
		Attributes attributes = bundle.attributes;
		String exportPackage = attributes.getValue("Export-Package");
		
		if(exportPackage.contains(exportPackage))
			return false;
		
		for(Class<?> clazz : bundle.classes){
			if(clazz.getName().equals(exportPackage) || clazz.getName().equals(exportPackage+ "Impl") ){ 
				//if the interface or the implementing class
				checkServiceMethods(serviceMethods, clazz.getDeclaredMethods());
			}
		}
		
		return false;
	}
	
	boolean checkServiceMethods(Method[] serviceMethods, Method[] usedMethods){ //Checking to see that the methods are the same
		if(serviceMethods.length > usedMethods.length){
			//interface has to have at least all of it's methods implemented
			return false;
		}
		ArrayList<Method> equalMethods = new ArrayList<Method>(); 
		//arraylist for all the equal methods, should be same size as service methods
		for(Method method : serviceMethods){
			for(Method method2 : usedMethods){
				if(method.equals(method2)){
					equalMethods.add(method2);
				} else if(method.getName().equals(method2.getName())){
					Class<?> returnType = method.getReturnType();
					Class<?> returnType2 = method2.getReturnType();
					Class<?>[] parameterTypes = method.getParameterTypes();
					Class<?>[] parameterTypes2 = method2.getParameterTypes();

					if(returnType.getName().equals(returnType2.getName())){	
						ComparisonStatus paramsStatus =	MapAnalyser.areParamsEqual(parameterTypes, parameterTypes2);
						if(paramsStatus != ComparisonStatus.EQUAL){
							//return false;
							continue;
						} else {
							equalMethods.add(method2);
						}

					} else {
						//return false;
						continue;
					}
				} else {
					continue;
				}
			}
		}


		if(equalMethods.size() >= serviceMethods.length){
			return true;
		} else {
			return false;
		}
	}
	
	String getLatestVersionNumber(String[] versionNumbers){
		//TODO Add in checks for less than 3 digits.
		//versionNumbers contains a few version numbers, this will be accessed as String[]
		ArrayList<String[]> splitVersionStrings = new ArrayList<String[]>();
		for(String str : versionNumbers){
			splitVersionStrings.add(str.split("\\."));
			
		}
		String[] temp = splitVersionStrings.get(0);
		splitVersionStrings.remove(temp);
		for(String[] str : splitVersionStrings){ 
			
			
		 	if(Integer.parseInt(temp[0]) > Integer.parseInt(str[0])){
		 		splitVersionStrings.remove(str);
		 		continue;
		 	} else if(Integer.parseInt(temp[0]) <= Integer.parseInt(str[0])){
		 		if(Integer.parseInt(temp[1]) <= Integer.parseInt(str[1])){
		 			
		 			if(Integer.parseInt(temp[2]) < Integer.parseInt(str[2])){
		 				temp = str;
		 			} else if(Integer.parseInt(temp[2]) > Integer.parseInt(str[2])){
		 				splitVersionStrings.remove(str);
		 			
		 			}
		 			
		 		} else {
		 			splitVersionStrings.remove(str);
		 		}
		 	}
		 	return temp[0] + "." + temp[1] + "." + temp[2];
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
		return"";
	}

}
