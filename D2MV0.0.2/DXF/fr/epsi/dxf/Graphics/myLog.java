package fr.epsi.dxf.Graphics;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myThreadedLogWriter;

public class myLog {

	public  String  value;
	private static boolean _activ=false;
	
	public myLog(){ }
	
	public static void writeLog(String line){
		if(_activ)
			((myThreadedLogWriter) DXF_Loader.logText).writeLog(line+"\n");
	}
	
	public static void writeLog(int line){
		writeLog(String.valueOf(line));
	}
	
	public static void writeLog(double line){
		writeLog(String.valueOf(line));
	}

	public static void setActiv(boolean b) {
		_activ = b;		
	}
}
