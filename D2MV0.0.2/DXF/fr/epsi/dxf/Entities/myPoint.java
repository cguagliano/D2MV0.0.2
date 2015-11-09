package fr.epsi.dxf.Entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Graphics.myCoord;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myStats;
import fr.epsi.dxf.Header.myTable;

public class myPoint extends myEntity {
	
	private static final long serialVersionUID = 1L;
	
	private 	Line2D.Double 	_l 			= new Line2D.Double();
	public 	Point2D.Double 	_point 		= new Point2D.Double(0,0);	
	
	public myPoint(Point2D.Double p, int c, myLayer l, int visibility,float thickness) {
		super(c, l, visibility, null, thickness);
		if(p == null)
			p = new Point2D.Double(0,0);
		
		_point 		= p;
		_thickness 	= thickness;
		
		myStats.nbPoint+=1;
	}
	
	public myPoint(Point2D.Double p) {
		super(-1, null, 0, null, myTable.defaultThickness);
		if(p == null)
			p = new Point2D.Double(0,0);
		_point 		= p;
	}
	
	public myPoint() {
		super(-1, null, 0, null, myTable.defaultThickness);
	}
		
	public myPoint(double x, double y, int c, myLayer l,int  visibility,double thickness) {
		super(c, l, visibility, null, myTable.defaultThickness);
		_point = new Point2D.Double(x,y);
		myStats.nbPoint+=1;
	}
	
	public myPoint(myPoint _a) {
		super(_a._color, _a._refLayer, 0, null, myTable.defaultThickness);
		_point = new Point2D.Double(_a.X(),_a.Y());
		myStats.nbPoint+=1;
	}
	public void setX(double x) {
		_point.x=x;
	}
	
	public void setY(double y) {
		_point.y=y;
	}
	
	public double X() {
		return _point.getX();
	}
	
	public double Y() {
		return _point.getY();
	}
	
	public void draw(Graphics g) { 
		_l.setLine(myCoord.dxfToJava_X(X()),
    			myCoord.dxfToJava_Y(Y()),
    			myCoord.dxfToJava_X(X()),
    			myCoord.dxfToJava_Y(Y()));
		super.draw(g);
    	((Graphics2D)g).draw(_l);	
	}

	public void write(FileWriter out) throws IOException{
		out.write("POINT\n");
		out.write("10\n");
		out.write(_point.getX()+"\n");
		out.write("20\n");
		out.write(_point.getY()+"\n");
		out.write("39\n");
		out.write(_thickness+"\n");
		super.writeCommon(out);
		out.write("0\n");
	}	
	
	public static myPoint read(myBufferedReader br, myUnivers univers) throws NumberFormatException, IOException
	{
            String ligne,ligne_temp;
            myLayer l	= null;
            int visibility=0, color=-1;
            double x=0,y=0, thickness=0;
            
            myLog.writeLog("> new myPoint");
            while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
                ligne_temp = ligne;
                ligne = br.readLine();
                
                if (ligne_temp.equalsIgnoreCase("8")) {
                    l = univers.findLayer(ligne);
                } else if(ligne_temp.equalsIgnoreCase("10")){
                    x = Double.parseDouble(ligne);
                } else if(ligne_temp.equalsIgnoreCase("20")){
                    y = Double.parseDouble(ligne);
                } else if(ligne_temp.equalsIgnoreCase("62")){
                    color = Integer.parseInt(ligne);
                }else if (ligne_temp.equalsIgnoreCase("60")){
    				visibility = Integer.parseInt(ligne);
                }else if (ligne_temp.equalsIgnoreCase("39")){
    				thickness = Double.parseDouble(ligne);
    			}
    			 else{
                    myLog.writeLog("Unknown :" + ligne_temp + "("+ ligne +")");
                }
            }
            return new myPoint(x,y,color,l, visibility, thickness);
    }
	
	public String toString(){
		return DXF_Loader.res.getString("myPoint.0") ;
	}
	
	public DefaultMutableTreeNode getNode(){
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X , String.valueOf(_point.getX()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y , String.valueOf(_point.getY()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.THICKNESS , String.valueOf(_thickness))));
	       
        Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));
        
		return root;
	}
	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;
	
		if(code.equals(myLabel.X)){
			setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.X, newValue.toString());
		}
		else if(code.equals(myLabel.Y)){
			setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.Y, newValue.toString());
		}else if(code.equals(myLabel.THICKNESS)){
			_thickness = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.THICKNESS, newValue.toString());
		}else {
			l = super.getCommonLabel(code, newValue);
		}
		return l;
	}

	public Rectangle2D.Double getSelectedEntity() {
		return new Rectangle2D.Double(myCoord.dxfToJava_X(X()),myCoord.dxfToJava_Y(Y()),2,2);
	}
	
	public void translate(double x, double y) {
		this._point.x-=myCoord.getTransalation(x);
		this._point.y+=myCoord.getTransalation(y);
	}

	public double getMinX(double min) {
		if ((_point.getX())<min)
			return _point.getX();			
		return min;
	}

	public double getMaxX(double max) {
		if ((_point.getX())>max)
			return _point.getX();			
		return max;
	}

	public double getMinY(double min) {
		if ((_point.getY())<min)
			return _point.getY();			
		return min;
	}

	public double getMaxY(double max) {
		if ((_point.getY())>max)
			return _point.getY();			
		return max;
	}
	
	public boolean isHigherThan(myPoint p) {
		if(p.X() > X() || p.Y() > Y()){
			return false;
		}
		else{
			return true;
		}
	}
	public boolean isLowerThan(myPoint p) {
		if(p.X() < X() || p.Y() < Y()){
			return false;
		}
		else{
			return true;
		}
	}
}