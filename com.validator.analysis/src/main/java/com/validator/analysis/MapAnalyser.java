package com.validator.analysis;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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
		//Firstly declaring the two maps to be used for Analysis (Needed?)
		ArrayListMultimap<String, ArrayList<Class<?>>> jar1Map = ArrayListMultimap.create();
		ArrayListMultimap<String, ArrayList<Class<?>>> jar2Map = ArrayListMultimap.create();
		
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
		
		HashMap<Class<?>, HashMap<Method, ComparisonStatus>> methodEqualityMap = new HashMap<Class<?>, HashMap<Method, ComparisonStatus>>();
		ArrayList<Class<?>> analyzedClasses = new ArrayList<Class<?>>();
	//	Comparing classes and taking intersection of classes in Jar file
		//ArrayList<Class<?>> intersectionOfClasses = new ArrayList<Class<?>>();
		for(int i = 0; i < classes1.size(); i++){
			HashMap<Method, ComparisonStatus> methodComparison;
			Class<?> class1 = classes1.get(i);
			if(analyzedClasses.contains(class1))
				continue;
			for(int j = 0; j < classes2.size(); j++){
				Class<?> class2 = classes2.get(j);
				if(analyzedClasses.contains(class1) || analyzedClasses.contains(class2))
					continue;
				//if(className1.equals(jarClasses2.classes.get(j).getName() )){
				if(isClassEqual(class1, classes2.get(j))){	
					//Class is equal!
					//intersectionOfClasses.add(class1);
					methodComparison = methodComparator(class1, class2);
					methodEqualityMap.put(class1, methodComparison);
					analyzedClasses.add(class1);
				} else if(class1.getName().equals(class2.getName()) ){ //Class not equal, but names are the same
					//TODO implement subtyping, write method which checks what is not equal, if because not exists
					//fail quickly, if type mismatch, then employ subtyping.
					methodComparison = methodComparator(class1, class2);
					methodEqualityMap.put(class1, methodComparison);
					analyzedClasses.add(class1);
				} else if(class1.getName().substring(class1.getName().lastIndexOf("."))
						.equals(class2.getName().substring(class2.getName().lastIndexOf(".")))){
					methodComparison = methodComparator(class1, class2);
					methodEqualityMap.put(class1,  methodComparison);
					analyzedClasses.add(class1);
				}
			//TODO implement	
			}
		}
		return methodEqualityMap;
	}
	private static HashMap<Method, ComparisonStatus> methodComparator(Class<?> class1, Class<?> class2) {
		//Hashmap used, which overwrites when "putting" to the same key. Need to check this works.
		HashMap<Method, ComparisonStatus> methodComparison = new HashMap<Method, ComparisonStatus>();
		Method[] methods = class1.getDeclaredMethods();
		Method[] methods2 = class2.getDeclaredMethods();
		 //This list is to allow us to find the methods which are NO_METHOD 
		//TODO find a better way for finding no method.
		ArrayList<String> analyzedMethods = new ArrayList<String>();
		for(Method method : methods){
			if(method.getName().contains("$") || analyzedMethods.contains(method)) 
				continue;
			
			try {
				for(Method method2 : methods2){ 
					//if(method2.getName().equals(method.getName())){
					//Method method2 = class2.getDeclaredMethod(method.getName(), method.getParameterTypes());
					if(method2.getName().contains("$") || analyzedMethods.contains(method2))
						continue; //Already analyzed, no need to go through and re-analyze

					if(method2.equals(method)) {
						methodComparison.put(method, ComparisonStatus.EQUAL);
						analyzedMethods.add(method2.getName());
						
					} else if(method2.getName().equals(method.getName())){
							//if method is the same, but data types are different
							//Compare data types
							//Type returnType =
							Class<?> returnType1 = method.getReturnType();
							Class<?> returnType2 = method2.getReturnType();
							Class<?>[] parameters = method.getParameterTypes();
							Class<?>[] parameters2 = method2.getParameterTypes();

							if(returnType1.getName() == returnType2.getName()){ 
								if(areParamsEqual(parameters, parameters2) == ComparisonStatus.EQUAL){
									methodComparison.put(method, ComparisonStatus.EQUAL);
								} else if(areParamsEqual(parameters, parameters2) == ComparisonStatus.SUB_TYPED){
									methodComparison.put(method, ComparisonStatus.SUB_TYPED);
								}
							} else if((returnType1.getTypeName() == returnType2.getTypeName()) && 
									areParamsEqual(parameters, parameters2) == ComparisonStatus.NOT_EQUAL){
								if(parameters.length != parameters2.length){ //Return types are the same, parameters are not the same length, not equal
									methodComparison.put(method, ComparisonStatus.NOT_EQUAL);
								} else if(parameters.length == parameters2.length){//length is the same but received a Not_equal so must be type mismatch
									methodComparison.put(method, ComparisonStatus.TYPE_MISMATCH);
								}
								methodComparison.put(method, ComparisonStatus.NOT_EQUAL);
							} else if(returnType1.getTypeName() != returnType2.getTypeName()){
								//If return types are different but params are the same -> type mismatch
								if(areParamsEqual(parameters, parameters2) == ComparisonStatus.EQUAL && !returnType1.isAssignableFrom(returnType2)){
									methodComparison.put(method, ComparisonStatus.TYPE_MISMATCH);
								} else if(parameters.length != parameters2.length){
									methodComparison.put(method, ComparisonStatus.NOT_EQUAL);

								}
							}
							analyzedMethods.add(method2.getName());
						
					//} else {
						//Method doesn't exist...
						//methodComparison.put(method, ComparisonStatus.NO_METHOD);
					}
					
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
	
		}
		if(methods.length != methods2.length){
			
			if(methods.length > methods2.length){
				for(Method method : methods2){ 
					if(!analyzedMethods.contains(method.getName())){
						analyzedMethods.add(method.getName());
					
					}
				}
				for(Method method : methods){
					if(!analyzedMethods.contains(method.getName())){
						methodComparison.put(method, ComparisonStatus.NO_METHOD);
					}
				}
			} else { //methods2.length is greater
				for(Method method : methods){ 
					if(!analyzedMethods.contains(method.getName())){
						analyzedMethods.add(method.getName());
					
					}
				}
				for(Method method : methods2){
					if(!analyzedMethods.contains(method.getName())){
						methodComparison.put(method, ComparisonStatus.NO_METHOD);
					}
				}
			}
		}
		else if(analyzedMethods.size() != methods.length){
			for(Method method : methods){ 
				if(method.getName().contains("$")){
					continue;
				
				}else if(!analyzedMethods.contains(method.getName())){
					methodComparison.put(method, ComparisonStatus.NO_METHOD);
					
				}
			}
		} else if(analyzedMethods.size() != methods2.length){
			for(Method method : methods2){ 
				if(method.getName().contains("$")){
					continue;
				}
				else if(!analyzedMethods.contains(method.getName())){
					methodComparison.put(method, ComparisonStatus.NO_METHOD);
					
				}
			}
		}
		return methodComparison;
	}
	Method findNoMethod(Method[] methods, Method[] methods2){
		ArrayList<Method> methodList = new ArrayList<Method>();
		ArrayList<Method> methodList2 = new ArrayList<Method>();
		for(Method method : methods){ methodList.add(method); }
		for(Method method : methods2){ methodList2.add(method); }
		return null;
	}
	
	static ComparisonStatus areParamsEqual(Class<?>[] params1, Class<?>[] params2){
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
				
				return typeMismatchChecking(params1, params2, equalTypes);
			}
			
		} else {
			return ComparisonStatus.NOT_EQUAL;
		}	
	}
	
	static ComparisonStatus typeMismatchChecking(Class<?>[] params1, Class<?>[] params2, ArrayList<String> equalTypes){
		//This method is checking if the "type mismatch" can be recovered by subtyping. As in, is the class recoverable.
		for(Class<?> type : params1){
			if(equalTypes.contains(type)){
				//If in list, already checked -- TODO consider not sending equal fields in the first place
				continue;
			} else {
				//Find out if subtype-able
				for(Class<?> type2 : params2){
					if(type.isAssignableFrom(type2) || type2.isAssignableFrom(type)){
						//if class is super class or superinterface (isAssignable from)
						//Checks for a identity conversion or widening reference, if true it is sub typeable.
						return ComparisonStatus.SUB_TYPED;
					} 
				}
			}
			
		}
		return ComparisonStatus.TYPE_MISMATCH;
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
