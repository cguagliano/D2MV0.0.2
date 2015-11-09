package fr.epsi.dxf.Entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import fr.epsi.dxf.myThreadedLoad;

public class myBufferedReader extends BufferedReader {

	public myBufferedReader(Reader r) {
		super(r);
	}

	public String readLine() throws IOException{
		String value;
		myThreadedLoad.mybar.setValue(myThreadedLoad.mybar.getValue()+1);
		
		value = super.readLine();
		
		if(value != null){
			value=value.trim();
		}
		return value;
	}
}
