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
	
	public enum ComparisonStatus { //TODO consider more enums
		NO_METHOD, TYPE_MISMATCH, EQUAL, NOT_EQUAL
	}

	public MapAnalyser(){
		
	}
	/*
	 * @params Strings for the paths of the two jars which will be compared. 
	 * 
	 * This method is the main method for external update analysis. 
	 */
	void updateJarAnalysis(String jar1, String jar2){
		//This method takes the two Jar paths, then creates two map representations of these Jar files.
		//After representations are made, analysis is conducted
		
		//Firstly declaring the two maps to be used for Analysis (Needed?)
		ArrayListMultimap<String, ArrayList<Class<?>>> jar1Map = ArrayListMultimap.create();
		ArrayListMultimap<String, ArrayList<Class<?>>> jar2Map = ArrayListMultimap.create();
		
		JarToClasses jarClasses1 = new JarToClasses(jar1);
		JarToClasses jarClasses2 = new JarToClasses(jar2);
	//	Comparing classes and taking intersection of classes in Jar file
		ArrayList<Class<?>> intersectionOfClasses = new ArrayList<Class<?>>();
		for(int i = 0; i < jarClasses1.classes.size()-1; i++){
			Class<?> class1 = jarClasses1.classes.get(i);
			
			for(int j = 0; j < jarClasses2.classes.size()-1; j++){
				//if(className1.equals(jarClasses2.classes.get(j).getName() )){
				if(isClassEqual(class1, jarClasses2.classes.get(j))){	
					//Class is equal!
					intersectionOfClasses.add(class1);
				} else { //Class not equal
					//TODO implement subtyping, write method which checks what is not equal, if because not exists
					//fail quickly, if type mismatch, then employ subtyping.
				}
			//TODO implement	
			}
		}
		
	}

	boolean isClassEqual(Class<?> class1, Class<?> class2){
		//This method will compare an older version of a bundle class against a newer version.
		//This method will employ Method.equals() for first level comparison 
		//TODO Create a list to hold onto the methods which are not in either class?
		//Simple boolean check, designed to fast fail.
		Method[] methods1 = class1.getDeclaredMethods();
		
		for(Method method : methods1){
			try {
				if(class2.getDeclaredMethod(method.getName(), method.getParameterTypes()) != null){
					Method methodExists = class2.getDeclaredMethod(method.getName(), method.getParameterTypes());
					if(!methodExists.equals(method)){
						//Add to equal method list.
						return false;	
					} else {
						continue;
					}
				} else {
					return false;
				}
			} catch (NoSuchMethodException e) {
				// TODO Add logger
				//In this case the method does not exist in the other class.
				e.printStackTrace();
				return false;
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}//end for
		
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
