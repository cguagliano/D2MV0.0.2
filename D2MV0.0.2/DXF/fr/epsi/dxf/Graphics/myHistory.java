package fr.epsi.dxf.Graphics;

import java.util.Vector;

import fr.epsi.dxf.DXF_Loader;


public class myHistory {
	
	static int now=0;
	static Vector<Double> zoomRatio=new Vector<Double>();
	static Vector<Double> xStray=new Vector<Double>();
	static Vector<Double> yStray=new Vector<Double>();
	
	public static void resetHistory() {
		now=0;
		zoomRatio=null;zoomRatio=new Vector<Double>();
		xStray=null;xStray=new Vector<Double>();
		yStray=null;yStray=new Vector<Double>();
		saveHistory(false);
		DXF_Loader.fwd.setEnabled(false);
		DXF_Loader.back.setEnabled(false);
	}
	
	public static void saveHistory(boolean b) {
		if (now!=zoomRatio.size()) { //On est pas au bout 
			int size=zoomRatio.size()-1;
			now++;
			for (int i=size;i>=now;i--) {
				zoomRatio.removeElementAt(i);
				xStray.removeElementAt(i);
				yStray.removeElementAt(i);
			}
			zoomRatio.insertElementAt(myCoord.Ratio,now);
			xStray.insertElementAt(myCoord.decalageX,now);
			yStray.insertElementAt(myCoord.decalageY,now);
		} else {
			zoomRatio.insertElementAt(myCoord.Ratio,now);
			xStray.insertElementAt(myCoord.decalageX,now);
			yStray.insertElementAt(myCoord.decalageY,now);
			now++;
		}
		DXF_Loader.back.setEnabled(true);
		DXF_Loader.fwd.setEnabled(false);		
		if (b)
			DXF_Loader._mc._dxf.tree.updateSelection();		
	}

	public static void backToThePast() {
		if (now==zoomRatio.size())
			now-=2;
		else
			now--;
		if (now < 0)
			now=0;
		myCoord.Ratio=zoomRatio.elementAt(now);
		myCoord.decalageX=xStray.elementAt(now);
		myCoord.decalageY=yStray.elementAt(now);		
		if (now==0)
			DXF_Loader.back.setEnabled(false);
		DXF_Loader.fwd.setEnabled(true);
		DXF_Loader._mc._dxf.tree.updateEnv();
	}
	
	public static void backToTheFuture() {
		now++;
		if (now>=zoomRatio.size()) {
			now=zoomRatio.size()-1;
			DXF_Loader.fwd.setEnabled(false);
		} else {
			myCoord.Ratio=zoomRatio.elementAt(now);
			myCoord.decalageX=xStray.elementAt(now);
			myCoord.decalageY=yStray.elementAt(now);
			if (now==zoomRatio.size()-1)
				DXF_Loader.fwd.setEnabled(false);
		}
		DXF_Loader.back.setEnabled(true);
		DXF_Loader._mc._dxf.tree.updateEnv();
	}
}
