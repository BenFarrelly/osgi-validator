package com.validator.analysis;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.lang.reflect.Member;
import java.lang.Class;
//Likely no longer needed
/*
 * @author Benjamin Farrelly
 * 
 * @param A Class which will be reflected
 * @return A HashMap which contains Method or interface names and data types
 * 
 * A class for creating and populating a Map with the method names, parameter types and return type.
 * Every map that gets made, gets added to the global class map which will make analysis easier.
 * 
 */
public class MapBuilder {
	ArrayListMultimap<String, List<Type>> typeMap;
	
	public MapBuilder(Class bundleClass){
		
		
	}
	public MapBuilder(String className){
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	Method[] getMethodList(Class bundleClass){
		Method[] classMethods = bundleClass.getDeclaredMethods();
		
		return classMethods;
	}
	
	ArrayListMultimap<String, List<Type>> makeAnalysisMap(Method[] methods ){
		//Creates map containing method name and parameter and return types
		typeMap = ArrayListMultimap.create();
		List<Type> parameters = new ArrayList<Type>();
		//Build list in loop then add
		for(int i=0; i < methods.length;i++ ){
			
			Type returnTypes = methods[i].getGenericReturnType();
			
			for(Type type : methods[i].getGenericParameterTypes()){
				parameters.add(type);
			}
			
			parameters.add(returnTypes);
			typeMap.put(methods[i].getName(), parameters );
			
			
		}
		//Add to global map then return. TODO find a more elegant way than accessing through methods[0]
		//Make its own method
		
		AnalysisStarter.globalClassMap.put(methods[0].getDeclaringClass(),typeMap);
		return typeMap;
		
	}
	//TODO find a way to discern between new and old class. Maybe refer to files' mod time.
	//Lazy implementation slightly change name of one, by adding "(1)"
	String changedClassNameIfAlreadyExists(String className, Method method){
		
		if(AnalysisStarter.globalClassMap.containsKey(className)) {
			Class classPuttingInMap = method.getDeclaringClass();
			Class[] keySetArray = (Class[]) AnalysisStarter.globalClassMap.keySet().toArray();
			Class classAlreadyInMap;
			
			for(Class key : keySetArray){ 
				if(key.equals(classPuttingInMap)) classAlreadyInMap = key;
				break;
			}
			
			
		}
		return "";
	}
	
	static void putClassesIntoGlobalMap(ArrayList<Class<?>> list){
		//This method is for converting a list of classes into correctly being added to the global map.
		//Global map, HashMap<Class, ArrayListMultimap<String, List<Type>>
		
		for(Class<?> c : list){
			ArrayListMultimap<String, List<Type>> map = ArrayListMultimap.create();
			Method[] methods = c.getDeclaredMethods();
			for(Method method : methods){
				
				List<Type> types = new ArrayList<Type>();
				Type[] params = method.getGenericParameterTypes();
				
				for(Type t : params) { types.add(t); }
				
				types.add(method.getGenericReturnType());
				map.put(method.getName(), types);
			}
			AnalysisStarter.globalClassMap.put(c, map);
		}
		
		
	}//end method
}
