package com.validator.analysis;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

//Use .equal comparator on methods, checks params and return type. Although case exists for when they are not, 
//wherein subtyping will be used, so code for breaking down is still needed from mapbuilder.
public class MapAnalyser {
	
	//NO_METHOD: NoSuchMethodException has been returned when trying to use getDeclaredMethod
	//TYPE_MISMATCH: When method is found but with different parameter or return types
	//EQUAL: The method is Equal in name, parameter and return types.
	//NOT_EQUAL: Method.equals() returns false. Leads to type checking. Method names are the same, but param/return type is not equal.
	public enum ComparisonStatus { //TODO consider more enums
		NO_METHOD, TYPE_MISMATCH, EQUAL, NOT_EQUAL, SUB_TYPED
	}
	

	public MapAnalyser(){
		
	}
	/*
	 * @params Strings for the paths of the two jars which will be compared. 
	 * @return methodEqualityMap, which allows for further subtyping to be employed from methods which have "SUB_TYPE" enum.
	 * This method is the main method for external update analysis. 
	 */
	public static HashMap<Class<?>, HashMap<Method, ComparisonStatus>> updateJarAnalysis(String jar1, String jar2){
		//This method takes the two Jar paths, then creates two map representations of these Jar files.
		//After representations are made, analysis is conducted
		
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = new HashMap<Class<?>, HashMap<Method, ComparisonStatus>>();
		JarToClasses jarClasses1 = new JarToClasses(jar1);
		JarToClasses jarClasses2 = new JarToClasses(jar2);
		
	//	Comparing classes and taking intersection of classes in Jar file
		ArrayList<Class<?>> intersectionOfClasses = new ArrayList<Class<?>>();
		for(int i = 0; i < jarClasses1.classes.size()-1; i++){
			HashMap<Method, ComparisonStatus> methodComparison;
			Class<?> class1 = jarClasses1.classes.get(i);
			
			for(int j = 0; j < jarClasses2.classes.size()-1; j++){
				Class<?> class2 = jarClasses2.classes.get(j);
				//if(className1.equals(jarClasses2.classes.get(j).getName() )){
				if(isClassEqual(class1, jarClasses2.classes.get(j))){	
					//Class is equal!
					intersectionOfClasses.add(class1);
				} else if(class1.getName().equals(class2.getName()) ){ //Class not equal, but names are the same
					//TODO implement subtyping, write method which checks what is not equal, if because not exists
					//fail quickly, if type mismatch, then employ subtyping.
					methodComparison = methodComparator(class1, class2);
					methodEqualityMap.put(class1, methodComparison);
				}
			//TODO implement	
			}
		}
		return methodEqualityMap;
	}

	
	public static HashMap<Class<?>, HashMap<Method, ComparisonStatus>> updateJarAnalysis(ArrayList<Class<?>> classes1, ArrayList<Class<?>> classes2){
		//This method takes the two Jar paths, then creates two map representations of these Jar files.
		//After representations are made, analysis is conducted
		//TODO FIX THIS AFTER FIXING METHODS
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = new HashMap<Class<?>, HashMap<Method, ComparisonStatus>>();
		ArrayList<Class<?>> analyzedClasses = new ArrayList<Class<?>>();
		ArrayList<Class<?>> equalClasses = new ArrayList<Class<?>>();
	//	Comparing classes and taking intersection of classes in Jar file
		//ArrayList<Class<?>> intersectionOfClasses = new ArrayList<Class<?>>();
		for(Iterator<Class<?>> it = classes1.iterator(); it.hasNext();){
			Class<?> clazz = it.next();
			System.out.println("------------- We're analysing "+ clazz + " now --------------");
			HashMap<Method, ComparisonStatus> methodComparison;
			
			if(analyzedClasses.contains(clazz))
				continue;
			for(Iterator<Class<?>> it2 = classes2.iterator(); it2.hasNext();){
				Class<?> clazz2 = it2.next();
				if(analyzedClasses.contains(clazz) || analyzedClasses.contains(clazz2))
					continue;
				//if(className1.equals(jarClasses2.classes.get(j).getName() )){
				if(isClassEqual(clazz, clazz2)){	
					//Class is equal!
					//intersectionOfClasses.add(class1);
					methodComparison = methodComparator(clazz, clazz2);
					methodEqualityMap.put(clazz, methodComparison);
					analyzedClasses.add(clazz);
					equalClasses.add(clazz);
					it.remove();
					break;
				} else if(clazz.getName().equals(clazz2.getName()) ){ //Class not equal, but names are the same
					//TODO implement subtyping, write method which checks what is not equal, if because not exists
					//fail quickly, if type mismatch, then employ subtyping.
					methodComparison = methodComparator(clazz, clazz2);
					methodEqualityMap.put(clazz, methodComparison);
					analyzedClasses.add(clazz);
					break;
				} else if(clazz.getName().substring(clazz.getName().lastIndexOf("."))
						.equals(clazz2.getName().substring(clazz2.getName().lastIndexOf(".")))){
					methodComparison = methodComparator(clazz, clazz2);
					methodEqualityMap.put(clazz,  methodComparison);
					analyzedClasses.add(clazz);
					break;
				}	
			}
		}
		return methodEqualityMap;
	}
	private static HashMap<Method, ComparisonStatus> methodComparator(Class<?> class1, Class<?> class2) {
		//Hashmap used, which overwrites when "putting" to the same key. Need to check this works.
		
		//1st Double loop, find all the equals. All those that are left in the iterator need to be categorized.
		
		
		HashMap<Method, ComparisonStatus> methodComparison = new HashMap<Method, ComparisonStatus>();
		ArrayList<Method> initMethods = new ArrayList<Method>(Arrays.asList(class1.getDeclaredMethods()));
		ArrayList<Method> initMethods2 = new ArrayList<Method>(Arrays.asList(class2.getDeclaredMethods()));
		//Remove all $ classes
		ArrayList<Method> methods = new ArrayList<Method>();
		ArrayList<Method> methods2 = new ArrayList<Method>();
		for(Method method : initMethods){

			if(method.getName().contains("$")){
				String temp = method.getName().substring(method.getName().lastIndexOf('$')+1);
				int tempInt = 0;
				try{
					tempInt = Integer.parseInt(temp);	
				}catch (NumberFormatException e){
					//Not an int
					methods.add(method);
				}
			}else{

				methods.add(method);


			}

		}
		for(Method method : initMethods2){
			if(method.getName().contains("$")){
				String temp = method.getName().substring(method.getName().lastIndexOf('$')+1);
				int tempInt = 0;
				try{
					tempInt = Integer.parseInt(temp);	
				}catch (NumberFormatException e){
					//Not an int
					methods2.add(method);
				}
			}else{

				methods2.add(method);


			}

		}
		int originalMethodSize = methods.size();
		int originalMethods2Size = methods2.size();
		 //This list is to allow us to find the methods which are NO_METHOD 
		//TODO find a better way for finding no method.
		ArrayList<Method> analyzedMethods = new ArrayList<Method>();
		ArrayList<Method> equalMethods = new ArrayList<Method>();
		for(Iterator<Method> it = methods.iterator(); it.hasNext();){
			Method method = it.next();
			if(analyzedMethods.contains(method)) {
				it.remove();
				continue;
			}
			try {
				for(Iterator<Method> it2 =  methods2.iterator(); it2.hasNext();){
					Method method2 = it2.next();
					
					//if(method2.getName().equals(method.getName())){
					//Method method2 = class2.getDeclaredMethod(method.getName(), method.getParameterTypes());
					if(analyzedMethods.contains(method2)){
						it2.remove();
						continue; //Already analyzed, no need to go through and re-analyze
					}

					if(method2.equals(method)) {
						methodComparison.put(method, ComparisonStatus.EQUAL);
						analyzedMethods.add(method);
						System.out.println("                  Method: " + method.getName()+ " was equal.");
						equalMethods.add(method);
						it.remove();
						it2.remove();
						break;
					} else if(method2.getName().equals(method.getName())){
							//if method is the same, but data types are different
							//Compare data types
							//Type returnType =
							Class<?> returnType1 = method.getReturnType();
							Class<?> returnType2 = method2.getReturnType();
							Class<?>[] parameters = method.getParameterTypes();
							Class<?>[] parameters2 = method2.getParameterTypes();

							if(returnType1.getName() == returnType2.getName()){ 
								if(areParamsEqual(parameters, parameters2, returnType1, returnType2) == ComparisonStatus.EQUAL){
									methodComparison.put(method, ComparisonStatus.EQUAL);
									System.out.println("                  Method: " + method.getName()+ " was equal.");
									equalMethods.add(method);
									analyzedMethods.add(method);
									it.remove();
									it2.remove();
									break;
							//	} else if(areParamsEqual(parameters, parameters2) == ComparisonStatus.SUB_TYPED){
								//	methodComparison.put(method, ComparisonStatus.SUB_TYPED);
									//System.out.println("                  Method: " + method.getName()+ " was sub typed.");
								}
//							} else if((returnType1.getTypeName() == returnType2.getTypeName()) && 
//									areParamsEqual(parameters, parameters2) == ComparisonStatus.NOT_EQUAL){
//								if(parameters.length != parameters2.length){ //Return types are the same, parameters are not the same length, not equal
//									methodComparison.put(method, ComparisonStatus.NOT_EQUAL);
//									System.out.println("                  Method: " + method.getName()+ " was not equal.");
//								} else if(parameters.length == parameters2.length){//length is the same but received a Not_equal so must be type mismatch
//									methodComparison.put(method, ComparisonStatus.TYPE_MISMATCH);
//									System.out.println("                  Method: " + method.getName()+ " had a type mismatch.");
//								}
//								methodComparison.put(method, ComparisonStatus.NOT_EQUAL);
//							} else if(returnType1.getTypeName() != returnType2.getTypeName()){
//								//If return types are different but params are the same -> type mismatch
//								if(areParamsEqual(parameters, parameters2) == ComparisonStatus.EQUAL && !returnType1.isAssignableFrom(returnType2)){
//									methodComparison.put(method, ComparisonStatus.TYPE_MISMATCH);
//									System.out.println("                  Method: " + method.getName()+ " had a type mismatch.");
//								} else if(parameters.length != parameters2.length){
//									methodComparison.put(method, ComparisonStatus.NOT_EQUAL);
//									System.out.println("                  Method: " + method.getName()+ " was not equal.");
//
//								}
//							}
						
							}
					} else {
						//Method doesn't exist...
						//methodComparison.put(method, ComparisonStatus.NO_METHOD);
					}
					
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
	
		}
		for(Iterator<Method> it = methods.iterator(); it.hasNext();){
			Method method = it.next();
			if(equalMethods.contains(method)){
				it.remove();
				continue;
			}
			for(Iterator<Method> it2 = methods2.iterator(); it2.hasNext();){
				Method method2 = it2.next();
				//This is where other comparisons have to occur
				if(typeMismatchChecking(method.getParameterTypes(), method2.getParameterTypes(), method.getReturnType(), method2.getReturnType()) == ComparisonStatus.SUB_TYPED){
					System.out.println("                  Method: " + method.getName()+ " was sub typed.");
					methodComparison.put(method, ComparisonStatus.SUB_TYPED);
					if(!analyzedMethods.contains(method)){
						analyzedMethods.add(method);
					}
				} else if(typeMismatchChecking(method.getParameterTypes(), method2.getParameterTypes(), method.getReturnType(), method2.getReturnType()) == ComparisonStatus.TYPE_MISMATCH){
					System.out.println("                  Method: " + method.getName()+ " had a type mismatch.");
					methodComparison.put(method, ComparisonStatus.TYPE_MISMATCH);
					if(!analyzedMethods.contains(method)){
						analyzedMethods.add(method);
					}
				}
			}
		}
		
		//-----This part will not work now with the way I'm changing the algorithm TODO: implement newer version.
		/*if(methods.size() != methods2.size()){
			
			if(methods.size() > methods2.size()){
				for(Method method : methods2){ 
					if(!analyzedMethods.contains(method)){
						analyzedMethods.add(method);
					
					}
				}
				for(Method method : methods){
					if(!analyzedMethods.contains(method.getName())){
						methodComparison.put(method, ComparisonStatus.NO_METHOD);
						System.out.println("                  Method: " + method.getName()+ " does not exist in this class");
					}
				}
			} else { //methods2.length is greater
				for(Method method : methods){ 
					if(!analyzedMethods.contains(method)){
						analyzedMethods.add(method);
					
					}
				}
				for(Method method : methods2){
					if(!analyzedMethods.contains(method.getName())){
						methodComparison.put(method, ComparisonStatus.NO_METHOD);
						System.out.println("                  Method: " + method.getName()+ " does not exist in this class");
					}
				}
			}
		}
		else if(analyzedMethods.size() != methods.size()){
			for(Method method : methods){ 
				if(method.getName().contains("$")){
					continue;
				
				}else if(!analyzedMethods.contains(method.getName())){
					methodComparison.put(method, ComparisonStatus.NO_METHOD);
					System.out.println("                  Method: " + method.getName()+ " does not exist in this class");
				}
			}
		} else if(analyzedMethods.size() != methods2.size()){
			for(Method method : methods2){ 
				if(method.getName().contains("$")){
					continue;
				}
				else if(!analyzedMethods.contains(method.getName())){
					methodComparison.put(method, ComparisonStatus.NO_METHOD);
					System.out.println("                  Method: " + method.getName()+ " does not exist in this class");
				}
			}
		}*/
		if(analyzedMethods.size() < originalMethodSize || analyzedMethods.size() < originalMethods2Size){
			ArrayList<Method> missingMethods = isThereAMissingMethod(methods, methods2, analyzedMethods);
			if(missingMethods.size() > 0){
				for(Method missMethod : missingMethods){
					analyzedMethods.add(missMethod);
					methodComparison.put(missMethod, ComparisonStatus.NO_METHOD);
				}
			}
		}
		
		
		return methodComparison;
	}
	
	static ArrayList<Method> isThereAMissingMethod(ArrayList<Method> methods, ArrayList<Method> methods2, ArrayList<Method> analyzedMethods){
		//Check to find if there is a missing method.
		//First check if there is a difference in length, then if there's a method which isn't analysed.
		ArrayList<Method> missingMethods = new ArrayList<Method>();
		for(Method method : methods){
			if(!analyzedMethods.contains(method)){ //Maybe consider doing this against methods2 instead
				missingMethods.add(method);
				System.out.println(" ------- " + method.getName() + "is missing...");
			}
		}
		for(Method method : methods2){
			if(!analyzedMethods.contains(method)){ //Maybe consider doing this against methods2 instead
				missingMethods.add(method);
				System.out.println(" ------- " + method.getName() + "is missing...");
			}
		}
		
		return missingMethods;
	}
	
	static ComparisonStatus areParamsEqual(Class<?>[] params1, Class<?>[] params2, Class<?> returnType1, Class<?> returnType2){
		//Change to make full comparison occur here TODO .
		ArrayList<String> equalTypes = new ArrayList<String>();
		ArrayList<Class<?>> inequalTypes = new ArrayList();
		
		
		if(params1.length == params2.length){
			int correctTypeCount = 0;
			//boolean[][] typeMatrix = new boolean[params1.length][params2.length];
			for(int i = 0; i < params1.length; i++){
				for(int j = 0; j < params2.length; j++){
					if(params1[i].getName().equals(params2[j].getName())){
						correctTypeCount++;
						equalTypes.add(params2[j].getName());
					} else {
						continue;
					}
				}
			}
			if((correctTypeCount == params1.length) && (correctTypeCount ==params2.length)){
				return ComparisonStatus.EQUAL;
			} else  {
				//Type mismatch? need method for type mismatch. Need to check sub typing.
				//Call type mismatch checking, check equalTypes
				
				return null;
						//typeMismatchChecking(params1, params2, equalTypes);
			}
			
		} else {
			return ComparisonStatus.NOT_EQUAL;
		}	
	}
	
	static ComparisonStatus typeMismatchChecking(Class<?>[] params1, Class<?>[] params2, Class<?> returnType1, Class<?> returnType2){
		//This method is checking if the "type mismatch" can be recovered by subtyping. As in, is the class recoverable.
		//Will only receive a filtered list.
		if(returnType1 != returnType2){
		//Return types are not equal, find out if they are sub-typable or a total mismatch.
			if(returnType1.isAssignableFrom(returnType2) || returnType2.isAssignableFrom(returnType1)){
				return ComparisonStatus.SUB_TYPED;
			} else { //Non-assignable, probably mismatch
				return ComparisonStatus.TYPE_MISMATCH;
			}
		}else{ //Return types are equal, inequality is in the params
			
		
			for(Class<?> type : params1){
			
				//Find out if subtype-able
				for(Class<?> type2 : params2){
					if(type.equals(type2)){
						continue; //Is equal, not the one we're looking for.
					
					}
					
					else if(type != type2){
						
					
						if(type.isAssignableFrom(type2) || type2.isAssignableFrom(type)){
						//if class is super class or superinterface (isAssignable from)
						//Checks for a identity conversion or widening reference, if true it is sub typeable.
							return ComparisonStatus.SUB_TYPED;
						} else {
							return ComparisonStatus.TYPE_MISMATCH;
						}
					}
				}
			
			
		}
		//If it somehow makes it this far...
		return ComparisonStatus.TYPE_MISMATCH;
		}
	}
	
	public static boolean isClassEqual(Class<?> class1, Class<?> class2){
		//This method will compare an older version of a bundle class against a newer version.
		//This method will employ Method.equals() for first level comparison 
		//TODO Create a list to hold onto the methods which are not in either class?
		//Simple boolean check, designed to fast fail.
		Method[] methods1 = class1.getDeclaredMethods();
		try {
			for(Method method : methods1){
				
				if(class2.getDeclaredMethod(method.getName(), method.getParameterTypes()) != null){
					Method methodExists = class2.getDeclaredMethod(method.getName(), method.getParameterTypes());
					if(!methodExists.equals(method)){
						//Add to equal method list.
						return false;	
					} else { //NOT_EQUAL
						continue;
					}
				} else {
					return false;
				}


			}//end for
		} catch (NoSuchMethodException e) {
			// TODO Add logger
			//In this case the method does not exist in the other class.
			//e.printStackTrace();
			
			return false;

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			System.out.println("Start of errors");
			e.printStackTrace();
			
		}
		//if it makes it all the way through, it must be true
		return true;	
	}

	double classSimilarityRating(Class<?> class1, Class<?> class2){
		//This class returns a simple double from (Number of equal methods)/(Number of methods)
		Method[] methods = class1.getDeclaredMethods();
		double numberOfEqualMethods = 0.0;
		for(Method method : methods){
			
			try {
				if(class2.getDeclaredMethod(method.getName(), method.getParameterTypes()) != null){
					Method methodExists = class2.getDeclaredMethod(method.getName(), method.getParameterTypes());
				
					if(methodExists.equals(method)) 
						numberOfEqualMethods++;
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (double)numberOfEqualMethods/(double)methods.length;
	}
}
