package com.validator.analysis;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
//TODO Convert maps to Multimaps and start writing analysis
//Need a way of storing String, HashMap<String, List<Type>> so class and all methods with types can be accessed.
//HashMap implementation chosen as Multimap implementation would not solve this issue. Class name checked and creation/modification
//time checked for which is the newer class, where a string like "New" or "Old" can be added. 
public class AnalysisStarter {
	public static HashMap<Class, ArrayListMultimap<String, List<Type>>> globalClassMap = new HashMap<Class, ArrayListMultimap<String, List<Type>>>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
