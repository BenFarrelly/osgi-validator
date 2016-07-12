package com.validator.analysis;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
/*
 * This class exists to take a Jar file by with either a JarFile object or a path.
 * @author Benjamin Farrelly
 * 
 * @Parameters JarFile or path to the Jar file
 * @Returns Classes from the Jar file for other classes to analyze via reflection
 * TODO Add loggers
 * TODO Make this code more generic, constructors only accessing Jar, write method to actually get classes
 * 
 */
public class JarToClasses {
	public ArrayList<Class<?>> classes;
	//This boolean is used if only interface checking is selected.
	boolean wantsInterfaces = false;
//	public JarToClasses(JarFile jar){
//		classes = new ArrayList<Class>();
//		Enumeration<JarEntry> entries = jar.entries();
//		for(JarEntry entry = entries.nextElement(); entry != null; entry = entries.nextElement()){
//			if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
//				classes.add(entry);
//			}
//		}
//	}
//	
	
	public JarToClasses(String jar){
		classes = new ArrayList<Class<?>>();
		String className;
		
		try {
			URL[] urls = { new URL("jar:file:" + jar + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);
			
			JarFile file = new JarFile(jar);
			Enumeration<JarEntry> entries = file.entries();
			
			for(JarEntry entry = entries.nextElement(); entry != null && entries.hasMoreElements(); entry = entries.nextElement()){
				if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
					//-6 because of .class
					className = entry.getName().substring(0, entry.getName().length()-6)
							.replace('/', '.');
					
					Class<?> loadingAndAddingToMap = cl.loadClass(className);
					//if interface checking only, confirm class is an interface. 
					//If not using interface checking add regardless
					if((wantsInterfaces && loadingAndAddingToMap.isInterface()) || !wantsInterfaces){
						classes.add(loadingAndAddingToMap);
					}
					
					
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO  More methods for converting Jars?
		MapBuilder.putClassesIntoGlobalMap(classes);
		
		
	}
	
	
	
	
	ArrayList<Class> getInterfaces(ArrayList<Class> classes){
		ArrayList<Class> interfaces = new ArrayList<Class>();
		for(Class c : classes){
			Class possibleInterface;
			if(c.isInterface())  interfaces.add(c);
			
		}
		return interfaces;
	}
	
	
	
	//This code is probably now obsolete
	ArrayList<Class> getClasses(List<String> classNames){
		//This class returns class instances from the class 
		ArrayList<Class> classes = new ArrayList<Class>();
		for(String name : classNames){
			Class possibleClass;
			
			try{
				possibleClass = Class.forName(name);
				classes.add(possibleClass);
			} catch (ClassNotFoundException e){
				//TODO incomplete catch block
				e.printStackTrace();
			}
		}
		
		return classes;
		
	}
}
