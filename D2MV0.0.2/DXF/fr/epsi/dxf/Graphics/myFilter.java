package fr.epsi.dxf.Graphics;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;



public class myFilter extends FileFilter implements FilenameFilter{
	
	String []lesSuffixes;
	String  laDescription;
	
	public myFilter(String []lesSuffixes, String laDescription){		
		this.lesSuffixes = lesSuffixes;
		this.laDescription = laDescription;
	}
	
	boolean appartient( String suffixe ){		
		for( int i = 0; i<lesSuffixes.length; ++i){
			if(suffixe.equals(lesSuffixes[i])) return true;
		}
		return false;
	}
	
	
	public boolean accept(File f) {		
		if (f.isDirectory()) {
			return true;
		}		
		String suffixe = null;
		String s = f.getName();
		
		int i = s.lastIndexOf('.');
		
		if (i > 0 &&  i < s.length() - 1) {
			suffixe = s.substring(i+1).toLowerCase();
		}		
		return suffixe != null && appartient(suffixe);
	}
	
	public String getDescription() {		
		return laDescription;		
	}

	public boolean accept(File f, String nom) {		
		if(nom.lastIndexOf(".") >= 0 && nom.substring(nom.lastIndexOf("."), nom.length()).equals("png")){
				return true;
		}	
		else{
			return false;	
		}		
	}
	
}