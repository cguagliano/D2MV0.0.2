package fr.epsi.dxf.Graphics;

import fr.epsi.dxf.DXF_Loader;


public class myCoord {

	public static final int SE = 1;
	public static final int NE = 2;
	public static final int NO = 3;
	public static final int SO = 4;
	public static double ratioStep=0.5;
	public static double Ratio=1.0;	
	public static double Max;
	public static double decalageX=0;
	public static double decalageY=0;
	
	static void resetRatio() {
		Ratio=(double)DXF_Loader._mc.getWidth()/Max;
		myHistory.saveHistory(true);
	}
	
	public static double javaToDXF_X(double java_x) {
		return (java_x-decalageX)/Ratio;
	}
	
	public static double javaToDXF_Y(double java_y) {		
		return (DXF_Loader._mc.getHeight()-java_y+decalageY)/Ratio;
	}

	public static double dxfToJava_X(double java_x) {
		return (java_x*Ratio+decalageX);
	}
	
	public static double dxfToJava_Y(double java_y) {		
		return DXF_Loader._mc.getHeight()-(java_y*Ratio)+decalageY;
	}

	public static myLabel getLabelRatio() {
		return new myLabel(myLabel.RATIO,String.valueOf(myCoord.Ratio));
	}
	
	public static myLabel getLabelY() {
		return new myLabel(myLabel.MOVE_Y,String.valueOf(-myCoord.decalageY+0));
	}
	
	public static myLabel getLabelX() {
		return new myLabel(myLabel.MOVE_X,String.valueOf(myCoord.decalageX));
	}
	
	public static void reset() {
		myCoord.Ratio=1.0;
		myCoord.decalageX=0.0;
		myCoord.decalageY=0.0;	
		ratioStep=0.5;
	}

	public static double getTransalation(double xy) {
		return xy/myCoord.Ratio;
	}
}
