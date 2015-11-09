package fr.epsi.dxf.Entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
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

public class myLine extends myEntity {
	
	private static final long serialVersionUID = 1L;
	public 	myPoint 	_a = new myPoint();
	public 	myPoint 	_b = new myPoint();
	private Line2D.Double _l=new Line2D.Double();
	
	public myLine (myPoint a, myPoint b, int c, myLayer l, myLineType lineType, double thickness, int visibility) { 
		super(c, l, visibility, lineType, thickness);
		_a=a;
		_b=b;
		
		myStats.nbLine+=1;
	}

	public myLine () { 
		super(-1, null, 0, null, myTable.defaultThickness);
		myStats.nbLine+=1;
	}
	
	public myLine(myLine original) {
		super(original._color, original._refLayer, 0, original._lineType, original._thickness);
		_a=new myPoint(original._a);
		_b=new myPoint(original._b);
		
		myStats.nbLine+=1;
	}

	public void draw(Graphics g) {
		
        _l.setLine(myCoord.dxfToJava_X(_a.X()),
        		myCoord.dxfToJava_Y(_a.Y()),
        		myCoord.dxfToJava_X(_b.X()),
        		myCoord.dxfToJava_Y(_b.Y())
        );
        super.draw(g);
       ((Graphics2D)g).draw(_l);
	}
	
	public Line2D.Double getSelectedEntity() {
		return _l;
	}

	public static myLine read(myBufferedReader br, myUnivers univers) throws IOException {
		String ligne="", ligne_temp="";
		myLayer l	= null;
		double x1=0, y1=0, x2=0, y2=0,thickness=0;
		myLineType lineType = null;
		int visibility=0,  c=-1;
		myLog.writeLog("> new myLine");
		
		while ((ligne = br.readLine()) != null && !ligne.trim().equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();
			
			if (ligne_temp.equalsIgnoreCase("10"))
			{	
				x1 = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("20"))
			{	
				y1 = Double.parseDouble(ligne);				
			}
			else if (ligne_temp.equalsIgnoreCase("11"))
			{	
				x2 = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("21"))
			{	
				y2 = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("8"))
			{	
				l = univers.findLayer(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("62"))
			{
				c =Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("6"))
			{
				lineType = univers.findLType(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("39"))
			{
				thickness = Double.parseDouble(ligne);;
			}
			else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}
			else{
				myLog.writeLog("Unknown :" + ligne_temp + "("+ ligne +")");				
			}			
		}		
		return new myLine(new myPoint(x1, y1, c, l ,visibility,1),
				new myPoint(x2, y2, c, l, visibility,1),
				c,
				l,
				lineType,
				thickness,
				visibility
		);
	}
	
	public void write(FileWriter out) throws IOException{		
		out.write("LINE\n");
		out.write("10\n");
		out.write(_a.X()+"\n");
		out.write("20\n");
		out.write(_a.Y()+"\n");
		out.write("11\n");
		out.write(_b.X()+"\n");
		out.write("21\n");
		out.write(_b.Y()+"\n");
		out.write("6\n");
		out.write(_lineType+"\n");
		out.write("39\n");
		out.write(_thickness+"\n");
		super.writeCommon(out);
		out.write("0\n");
	}

	public String toString(){
		return DXF_Loader.res.getString("myLine.0");
	}
	
	public DefaultMutableTreeNode getNode(){
		
		DefaultMutableTreeNode root= new DefaultMutableTreeNode(this);
		
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.XA , String.valueOf(this._a.X()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.YA,String.valueOf(this._a.Y()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.XB,String.valueOf(this._b.X()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.YB,String.valueOf(this._b.Y()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.THICKNESS,String.valueOf(this._thickness))));
		
		Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));
		
		return root;
	}
	
	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;

		if(code.equals(myLabel.XA)){
			_a.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.XA, newValue.toString());
		}
		else if(code.equals(myLabel.YA)){
			_a.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.YA, newValue.toString());
		}
		else if(code.equals(myLabel.XB)){
			_b.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.XB, newValue.toString());
		}
		else if(code.equals(myLabel.YB)){
			_b.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.YB, newValue.toString());
		}
		else if(code.equals(myLabel.THICKNESS)){
			_thickness = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.THICKNESS, newValue.toString());
		}
		else {
			l = super.getCommonLabel(code, newValue);
		}
		return l;
	}

	public void translate(double x, double y) {
		this._a._point.x-=myCoord.getTransalation(x);
		this._a._point.y+=myCoord.getTransalation(y);
		this._b._point.x-=myCoord.getTransalation(x);
		this._b._point.y+=myCoord.getTransalation(y);
	}

	public double getMinX(double min) {
		if (_a.X()<min)
			min = _a.X();	
		if (_b.X()<min)
			min = _b.X();	
		return min;
	}

	public double getMaxX(double max) {
		if (_a.X()>max)
			max = _a.X();	
		if (_b.X()>max)
			max = _b.X();	
		return max;
	}

	public double getMinY(double min) {
		if (_a.Y()<min)
			min = _a.Y();	
		if (_b.Y()<min)
			min = _b.Y();	
		return min;
	}

	public double getMaxY(double max) {
		if (_a.Y()>max)
			max = _a.Y();	
		if (_b.Y()>max)
			max = _b.Y();	
		
		
		return max;
	}
	
	protected void finalize() throws Throwable {
	    myStats.nbLine-=1;	    
	    super.finalize();
	}
	
}
