package com.validator.analysis;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

//Use .equal comparator on methods, checks params and return type. Although case exists for when they are not, 
//wherein subtyping will be used, so code for breaking down is still needed from mapbuilder.
public class MapAnalyser {
	

	public MapAnalyser(){
		
	}
	
	void updateAnalysis(String className){
		//This method will compare an older version of a bundle class against a newer version.
		ArrayListMultimap<String, List<Type>> firstClass = AnalysisStarter.globalClassMap.get(className);
	}
}
