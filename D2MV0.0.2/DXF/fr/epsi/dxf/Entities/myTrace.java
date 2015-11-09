package fr.epsi.dxf.Entities;


import java.io.IOException;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myStats;


public class myTrace extends mySolid {

	private static final long serialVersionUID = -442565242161470198L;	
	
	public myTrace(){
		super();	
	}
			
	public myTrace(myPoint p1, myPoint p2, myPoint p3, myPoint p4,double thickness, int c, myLayer l, int visibility, myLineType lineType) 
	{
		super(p1, p2, p3, p4, thickness, c, l, visibility,lineType);
		myStats.nbTrace+=1;
		myStats.nbSolid+=1;
	}
	
	public myTrace(myTrace trace) {
		super(trace._p1, trace._p2, trace._p3, trace._p4, trace._thickness, trace._color, trace._refLayer, 0,trace._lineType);
	}

	public static myEntity read(myBufferedReader br, myUnivers univers) throws IOException {
		int visibility =0;
		mySolid s = (mySolid) mySolid.read(br, univers);
		if(!s.isVisible)
			visibility=1;
			
		return new myTrace(s._p1, s._p2, s._p3, s._p4, s._thickness, s._color, s._refLayer, visibility, s._lineType);
	}
	
	public String toString(){
		return DXF_Loader.res.getString("myTrace.0");
	}
}
