package fr.epsi.dxf.Entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
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
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myStats;
import fr.epsi.dxf.Header.myTable;

public class myArc extends myEntity {

	private static final long serialVersionUID = 1L;
	public myPoint 	_point 		= new myPoint();
    public double 	_radius 	= 0;    
	protected double 	_angle1 	= 0;
	protected double 	_angle2 	= 0;
	protected double    _2angle2    =0;
	 
    private Arc2D.Double _a = new Arc2D.Double();
     
    public myArc(double a1, double a2,myPoint p, double r, myLineType lineType, int c, myLayer l,int visibility, double thickness)  {
    	super(c, l, visibility,lineType, thickness);	
		_point		= p;
		_radius		= r;
		_angle1 	= a1;
		_angle2 	= a2;
		_thickness	= thickness;
		
		myStats.nbArc+=1;
	}
	
    public myArc()  {
		super(-1, null, 0,null, myTable.defaultThickness);
		_point		= new myPoint();
		_radius		= 0;
		
		myStats.nbArc+=1;
	}
	
	public myArc(myArc orig) {
		super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
		_point=new myPoint(orig._point);
		_radius=orig._radius;
		_angle1=orig._angle1;
		_angle2=orig._angle2;
	}

	public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; 
       
        super.draw(g);
		if (_angle2 < _angle1) {
			_2angle2 = _angle2 + 360 - _angle1;
		} else {
			_2angle2 = _angle2 - _angle1;
		}
		_a.setArcByCenter(myCoord.dxfToJava_X(_point.X()),
				myCoord.dxfToJava_Y(_point.Y()), _radius * myCoord.Ratio,
				(_angle1), _2angle2, Arc2D.OPEN);

		g2d.draw(_a);
	}
	
	public static myArc read(myBufferedReader br, myUnivers univers) throws NumberFormatException, IOException {
		
		String ligne, ligne_temp="";
		double x=0, y=0, r=0, a1=0, a2=0,thickness=0;
		int visibility=0, c	= 0;
		myLineType lineType=null;
		myLayer l = null;
		myLog.writeLog("> new myArc");
		
		while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();
			
			if (ligne_temp.equalsIgnoreCase("8"))
			{	
				l = univers.findLayer(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("62"))
			{
				c = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("6"))
			{
				lineType = univers.findLType(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("40"))
			{
				r = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("10"))
			{
				x = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("20"))
			{
				y = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("50"))
			{
				a1 = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("51"))
			{
				a2 = Double.parseDouble(ligne);
			}else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}else if (ligne_temp.equalsIgnoreCase("39"))
			{
				thickness = Double.parseDouble(ligne);
			}
		}
		return new myArc(a1, a2, new myPoint(x, y, c, null,visibility,1), r, lineType,  c, l,visibility, thickness);
	}
	
	public void write(FileWriter out) throws IOException{
		out.write("ARC\n");
		super.writeCommon(out);
		out.write("10\n");
		out.write(_point.X()+"\n");
		out.write("20\n");
		out.write(_point.Y()+"\n");
		out.write("40\n");
		out.write(_radius+"\n");
		out.write("50\n");
		out.write(_angle1+"\n");
		out.write("51\n");
		out.write(_angle2+"\n");
		out.write("0\n");
	}
	public String toString(){
		return DXF_Loader.res.getString("myArc.0") ;
	}
	
	public DefaultMutableTreeNode getNode()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X, String.valueOf(_point.X()))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y, String.valueOf(_point.Y()))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.ANGLE1, String.valueOf(_angle1))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.ANGLE2, String.valueOf(_angle2))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.RADIUS, String.valueOf(_radius))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.THICKNESS , String.valueOf(_thickness))));
        
        Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));
        
		return root;
	}
	
	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;

		if(code.equals(myLabel.ANGLE1)){
			_angle1 = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.ANGLE1, newValue.toString());
		}
		else if(code.equals(myLabel.ANGLE2)){
			_angle2 = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.ANGLE2, newValue.toString());
		}
		else {
			l = getNewArcLabel(code, newValue);
		}
				
		return l;
	}


	public myLabel getNewArcLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;

		if(code.equals(myLabel.X)){
			_point.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.X, newValue.toString());
		}
		else if(code.equals(myLabel.Y)){
			_point.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.Y, newValue.toString());
		}
		else if(code.equals(myLabel.RADIUS)){
			_radius = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.RADIUS, newValue.toString());
		}else if(code.equals(myLabel.THICKNESS)){
			_thickness = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.THICKNESS, newValue.toString());
		}else {
			l = super.getCommonLabel(code, newValue);
		}
		
		return l;
	}
	
	public Arc2D.Double getSelectedEntity() {
		return _a;
	}
	
	public void translate(double x, double y) {
		this._point._point.x-=myCoord.getTransalation(x);
		this._point._point.y+=myCoord.getTransalation(y);
	}

	public double getMinX(double min) {
		if ((_point.X()-_radius)<min)
			return _point.X();			
		return min;
	}

	public double getMaxX(double max) {
		if ((_point.X()+_radius)>max)
			return _point.X();			
		return max;
	}

	public double getMinY(double min) {
		if ((_point.Y()-_radius)<min)
			return _point.Y();			
		return min;
	}

	public double getMaxY(double max) {
		if ((_point.Y()+_radius)>max)
			return _point.Y();			
		return max;
	}
}
