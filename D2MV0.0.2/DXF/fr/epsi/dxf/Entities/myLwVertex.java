/*
 * [ 1719398 ] First shot at LWPOLYLINE
 * Peter Hopfgartner - hopfgartner
 *  
 */
package fr.epsi.dxf.Entities;

import java.awt.geom.Point2D;

import fr.epsi.dxf.Header.myStats;

public class myLwVertex extends myPoint{
	double _bulge;

	public myLwVertex(Point2D.Double p, double bulge){
		super(p);
		this._bulge = bulge;
	}
	
	public myLwVertex(double x, double y, double bulge){
		super(new Point2D.Double(x, y));
		this._bulge = bulge;
	}

	public myLwVertex(myLwVertex orig,boolean bis) {
		super(orig._point.x, orig._point.y, orig._color, orig._refLayer, 0,1);	
		_bulge = orig._bulge;
		myStats.nbPoint-=1;
	}

public double getBulge(){
		return _bulge;
	}
}
