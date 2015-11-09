package fr.epsi.dxf.Entities;



import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
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

public class myCircle extends myEntity{

	private static final long serialVersionUID = 1L;
	private Ellipse2D.Double _e	= new Ellipse2D.Double();
	public 	myPoint _point 		= new myPoint();
    public 	double 	_radius 	= 0;
    
    public myCircle(myPoint p, double r, myLineType lineType, int c, myLayer l,int visibility, double thickness) {
		super(c, l, visibility, lineType, thickness);	
		_point	= p;
		_radius	= r;
		
		myStats.nbCercle+=1;
	}
	
    public myCircle() {
		super(0, null, 0, null, myTable.defaultThickness);
		
		myStats.nbCercle+=1;
	}
	
	public myCircle(myCircle orig) {
		super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
		_point=new myPoint(orig._point);
		_radius=orig._radius;
		
	}

	public void draw(Graphics g) {
		 double x=myCoord.dxfToJava_X(_point.X());
		 double y=myCoord.dxfToJava_Y(_point.Y());

		 super.draw(g);
		 	 
		 _e.setFrameFromCenter(x,y,
        		(x-(_radius*myCoord.Ratio)), (y-(_radius*myCoord.Ratio))
        );
        /*=new Ellipse2D.Double(myCoord.dxfToJava_X(_point.X()-_radius),
        		myCoord.dxfToJava_Y(_point.Y()+_radius), 
				(_radius*2*myCoord.Ratio), 
				(_radius*2*myCoord.Ratio)
        ); */
       ((Graphics2D)g).draw(_e);
	}
public static myCircle read(myBufferedReader br, myUnivers univers) throws NumberFormatException, IOException {
		
		String ligne, ligne_temp;
		int visibility = 0, color=0;
		double x=0, y=0, r=0, thickness=1;
		myLayer l = null;
		myLineType lineType = null;
		
		myLog.writeLog("> new myCircle");
		while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();
			
			if (ligne_temp.equalsIgnoreCase("8"))
			{	
				l = univers.findLayer(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("62"))
			{
				color = Integer.parseInt(ligne);
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
			else if (ligne_temp.equalsIgnoreCase("39"))
			{
				thickness = Double.parseDouble(ligne);
			}else if (ligne_temp.equalsIgnoreCase("20"))
			{
				y = Double.parseDouble(ligne);
			} else{
				myLog.writeLog("Unknown :" + ligne_temp + "("+ ligne +")");				
			}
		}
		return new myCircle(new myPoint(x, y, color, l, visibility,1), r, lineType,  color, l, visibility, thickness);
	}
	
	public void write(FileWriter out) throws IOException{
		out.write("CIRCLE\n");
		super.writeCommon(out);
		out.write("40\n");
		out.write((_radius)+"\n");
		out.write("10\n");
		out.write(_point.X()+"\n");
		out.write("20\n");
		out.write(_point.Y()+"\n");
		out.write("39\n");
		out.write(_thickness+"\n");
		out.write("0\n");
	}
	public String toString(){
		return DXF_Loader.res.getString("myCircle.0") ;
	}
	
	public DefaultMutableTreeNode getNode()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X, String.valueOf(_point.X()))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y , String.valueOf(_point.Y()))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.RADIUS , String.valueOf(_radius))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.THICKNESS , String.valueOf(_thickness))));
        
        Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));
        
		return root;
	}
	
	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
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

	public Ellipse2D.Double getSelectedEntity() {
		return _e;
	}

	public void translate(double x, double y) {
		this._point._point.x-=myCoord.getTransalation(x);
		this._point._point.y+=myCoord.getTransalation(y);
	}

	public double getMinX(double min) {
		if ((_point.X()-_radius)<min)
			return _point.X()-_radius;			
		return min;
	}

	public double getMaxX(double max) {
		if ((_point.X()+_radius)>max)
			return _point.X()+_radius;			
		return max;
	}

	public double getMinY(double min) {
		if ((_point.Y()-_radius)<min)
			return _point.Y()-_radius;			
		return min;
	}

	public double getMaxY(double max) {
		if ((_point.Y()+_radius)>max)
			return _point.Y()+_radius;			
		return max;
	}
	public static Ellipse2D.Double getSmallerGraphicEntity(Ellipse2D.Double orig) {
		Ellipse2D.Double ret = new Ellipse2D.Double();
		ret.setFrameFromCenter(orig.getCenterX(), orig.getCenterY(), orig.getMaxX()-10, orig.getMaxY()-10);
		return ret;		
	}
	
}
