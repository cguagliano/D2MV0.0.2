package fr.epsi.dxf;

import javax.swing.JProgressBar;

public class myThreadedRam implements Runnable {

	private DXF_Loader 	_loader	;
	private Thread  	_thread	;
	private long 		_totMem	;
	private int 		_used	;
	
	public myThreadedRam(DXF_Loader loader) {
		
		_loader=loader;
		_loader.memoryProgress = new JProgressBar(0, 1024);
		_loader.memoryProgress.setToolTipText(DXF_Loader.res.getString("myThreadedRam.0"));
		
		_thread = new Thread(this);
		_thread.setPriority(Thread.MIN_PRIORITY);
		_thread.start();
	}
	
	public void run() {
		while(true){
			if (Runtime.getRuntime().totalMemory() != _totMem) {
                     _totMem = Runtime.getRuntime().totalMemory();
                     _loader.memoryProgress.setMaximum((int) _totMem / 1024);
                 }

                 _used = (int) (_totMem - Runtime.getRuntime().freeMemory()) / 1024;
                 _loader.memoryProgress.setValue(_used);
                 _loader.memoryProgress.setToolTipText(DXF_Loader.res.getString("myThreadedRam.1") + _used + "/" + (_totMem / 1024) + DXF_Loader.res.getString("myThreadedRam.2"));
                 _loader.ram.setText("  "+((_used/1024))+" / "+(((_totMem/1024)/1024)+1)+DXF_Loader.res.getString("myThreadedRam.3"));
                 _loader.ram.repaint();

                 try {
                    Thread.sleep(2000);
                 } catch (Exception e) {
                	 e.printStackTrace();
                 }
				
		}
	}
}
