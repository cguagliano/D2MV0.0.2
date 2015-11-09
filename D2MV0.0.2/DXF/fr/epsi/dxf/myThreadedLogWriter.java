package fr.epsi.dxf;

import java.awt.Dimension;
import java.awt.TextArea;

public class myThreadedLogWriter extends TextArea implements Runnable {

	private static final long serialVersionUID = 1L;
	private Thread  	_thread	;
	private String strBuff;
	
	public myThreadedLogWriter() {
		super();
		setEditable(false);
		setMinimumSize(new Dimension(25,25));
		
		_thread = new Thread(this);
		_thread.setPriority(Thread.MIN_PRIORITY);
		_thread.start();
	}
	
	public void writeLog(String s) {
		strBuff=s;
	}
	
	public void run() {
		while(true){
			if (strBuff != null) {
				DXF_Loader.logText.append(strBuff);
				strBuff=null;
			}
		}
	}
}
