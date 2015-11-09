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

public class myEllipse extends myEntity{
	
	private static final long serialVersionUID = 5630853252028026450L;
	
	public  myPoint _centre 	= new myPoint();
	public  myPoint _point 	= new myPoint();
	public  double _ratio 	= 0;
	public  double _start 	= 0;
	public  double _end 		= 0;
    
    private Arc2D.Double _e=new Arc2D.Double();
	
    public myEllipse(myPoint centre, myPoint p, double r, double s, double e, int c, myLayer l,int visibility, myLineType typeLine) {
		super(c, l, visibility, typeLine, myTable.defaultThickness);	
		_centre	= centre;
		_point	= p;
		_ratio	= r;
		_end	= e;
		_start 	= s;	
	    _e.setArcType(Arc2D.OPEN);
	    
	    myStats.nbEllipse+=1;
	}
	
    public myEllipse() {
		super(-1, null, 0, null, myTable.defaultThickness);
		
		myStats.nbEllipse+=1;
	}
	
	public void draw(Graphics g) {
	   double x=myCoord.dxfToJava_X(_centre.X());
	   double y=myCoord.dxfToJava_Y(_centre.Y());
	   double height=2*(myCoord.dxfToJava_Y(_centre.Y())-x);
	   double width=height*_ratio;
	   
	   super.draw(g);
	  
	   _e.setFrameFromCenter(x,y,
       		x+width/2, y+height/2
       	);
       _e.setAngleExtent(_end-_start);
       _e.setAngleStart(_start);
		 
	   /*
        _e=new Arc2D.Double(myCoord.dxfToJava_X(_centre.X()-_ratio),
        		myCoord.dxfToJava_Y(_centre.Y()+_ratio), 
				(_ratio*2*myCoord.Ratio), 
				(_ratio*2*myCoord.Ratio), 0, 360, Arc2D.OPEN 
        ); */
       //Graphics2D g2 = (Graphics2D)g;
        //AffineTransform aT = g2.getTransform();
	   	//AffineTransform rotate45 = AffineTransform.getRotateInstance(Math.PI/4.0,0.0,0.0);
	   //	g2.transform(rotate45);
       ((Graphics2D)g).draw(_e);
	   	//g2.setTransform(aT);
	}
public static myEllipse read(myBufferedReader br, myUnivers univers) throws NumberFormatException, IOException {
		
		String ligne, ligne_temp;
		int visibility = 0, c=0;
		double x=0, y=0,  x1=0, y1=0, r=0, s=0, e=0;
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
			else if (ligne_temp.equalsIgnoreCase("6"))
			{
				lineType = univers.findLType(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("62"))
			{
				c = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("40"))
			{
				r = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("41"))
			{
				s = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("42"))
			{
				e = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("10"))
			{
				x = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("20"))
			{
				y = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("11"))
			{
				x1 = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("21"))
			{
				y1 = Double.parseDouble(ligne);
			} else{
				myLog.writeLog("Unknown :" + ligne_temp + "("+ ligne +")");				
			}
		}
		return new myEllipse(
							new myPoint(x, y, c, l, visibility,1),
							new myPoint(x1, y1, c, l, visibility,1), 
							r, s, e, c, l, visibility,lineType
					);
	}
	
	public void write(FileWriter out) throws IOException{
		out.write("ELLIPSE\n");
		super.writeCommon(out);
		out.write("10\n");
		out.write(_centre.X()+"\n");
		out.write("20\n");
		out.write(_centre.Y()+"\n");
		out.write("11\n");
		out.write(_point.X()+"\n");
		out.write("21\n");
		out.write(_point.Y()+"\n");
		out.write("40\n");
		out.write((_ratio)+"\n");
		out.write("41\n");
		out.write((Math.toRadians(_start))+"\n");
		out.write("42\n");
		out.write(Math.toRadians((_end))+"\n");
		out.write("0\n");
	}
	public String toString(){
		return DXF_Loader.res.getString("myEllipse.0") ;
	}
	
	public DefaultMutableTreeNode getNode()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
		
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X, String.valueOf(_centre.X()))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y , String.valueOf(_centre.Y()))));
       root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X, String.valueOf(_point.X()))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y , String.valueOf(_point.Y()))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.E_RATIO, String.valueOf(_ratio))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.START, String.valueOf(_start))));
        root.add(new DefaultMutableTreeNode(new myLabel(myLabel.END, String.valueOf(_end))));
        
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
			_ratio = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.RADIUS, newValue.toString());
		}
		else if(code.equals(myLabel.E_RATIO)){
			_ratio = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.E_RATIO, newValue.toString());
		}
		else if(code.equals(myLabel.START)){
			_start = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.START, newValue.toString());
		}
		else if(code.equals(myLabel.END)){
			_end = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.END, newValue.toString());
		}
		else {
			l = super.getCommonLabel(code, newValue);
		}
		return l;
	}

	public Arc2D.Double getSelectedEntity() {
		return _e;
	}

	public void translate(double x, double y) {
		this._point._point.x-=myCoord.javaToDXF_X(x)+myCoord.decalageX;
		this._point._point.y-=myCoord.javaToDXF_X(y)+myCoord.decalageY;
	}

	public double getMinX(double min) {
		if ((_point.X())<min)
			return _point.X();			
		return min;
	}

	public double getMaxX(double max) {
		if ((_point.X())>max)
			return _point.X();			
		return max;
	}
	
	public double getMinY(double min) {
		if ((_point.Y())<min)
			return _point.Y();			
		return min;
	}

	public double getMaxY(double max) {
		if ((_point.Y())>max)
			return _point.Y();			
		return max;
	}
}
