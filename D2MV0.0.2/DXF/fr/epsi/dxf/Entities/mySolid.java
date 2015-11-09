package fr.epsi.dxf.Entities;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
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

public class mySolid extends myEntity{

	private static final long serialVersionUID = 2567756283532200546L;

	public  myPoint 	_p1 = new myPoint();
	public  myPoint 	_p2 = new myPoint();
	public  myPoint 	_p3 = new myPoint();
	public  myPoint 	_p4 = null;
	public GeneralPath g;

	public mySolid(){
		super(-1, null,0, null, myTable.defaultThickness);
		myStats.nbSolid+=1;
	}
			
	public mySolid(myPoint p1, myPoint p2, myPoint p3, myPoint p4,
		double thickness, int c, myLayer l, int visibility, myLineType lineType)
	{
		super(c, l , visibility,lineType, thickness);
		
		_p1 = p1;
		_p2 = p2;
		_p3 = p3;

		if(p4 == null)
			_p4 = p3;
		else
			_p4 = p4;
		
		/*this.g=new GeneralPath();
		this.g.moveTo((float)myCoord.dxfToJava_X(_p1.X()),(float)myCoord.dxfToJava_Y(_p1.Y()));
		this.g.lineTo((float)myCoord.dxfToJava_X(_p2.X()),(float)myCoord.dxfToJava_Y(_p2.Y()));
		this.g.lineTo((float)myCoord.dxfToJava_X(_p3.X()),(float)myCoord.dxfToJava_Y(_p3.Y()));
		if (_p4 != null)
			this.g.lineTo((float)myCoord.dxfToJava_X(_p4.X()),(float)myCoord.dxfToJava_Y(_p4.Y()));
		this.g.closePath();*/
			
	}

	public mySolid(mySolid solid) {
		super(solid._color, solid._refLayer , 0,solid._lineType, solid._thickness);

		_p1 = new myPoint(solid._p1);
		_p2 = new myPoint(solid._p2);
		_p3 = new myPoint(solid._p3);
		_p4 = new myPoint(solid._p4);
	}

	public void draw(Graphics g) {

		super.draw(g);
		
        double x1=myCoord.dxfToJava_X(_p1.X());
        double y1=myCoord.dxfToJava_Y(_p1.Y());
       
        double x2=myCoord.dxfToJava_X(_p2.X());
        double y2=myCoord.dxfToJava_Y(_p2.Y());
       
        double x3=myCoord.dxfToJava_X(_p3.X());
        double y3=myCoord.dxfToJava_Y(_p3.Y());
        
        double x4=0;
        double y4=0;
	
        ((Graphics2D)g).draw(new Line2D.Double(x1, y1, x2, y2));
        ((Graphics2D)g).draw(new Line2D.Double(x2, y2, x3, y3));
        
        if(_p3.equals(_p4) || _p4 == null){
        	((Graphics2D)g).draw(new Line2D.Double(x3, y3, x1, y1));	
        }
        else{
        	x4=myCoord.dxfToJava_X(_p4.X());
            y4=myCoord.dxfToJava_Y(_p4.Y());
    	
        	((Graphics2D)g).draw(new Line2D.Double(x3, y3, x4, y4));
        	((Graphics2D)g).draw(new Line2D.Double(x4, y4, x1, y1));
        }
       /* if (this.g!=null)
        	((Graphics2D)g).draw(this.g);*/
	}

	public static myEntity read(myBufferedReader br, myUnivers univers) throws IOException {
		double p1_x=0, p2_x=0, p3_x=0, p4_x=0, p1_y=0, p2_y=0, p3_y=0, p4_y=0;
		double thickness = 0;
		int visibility=0, c=-1;
		String ligne="", ligne_tmp="";
		myLayer l = null;
		myLineType lineType = null;
		
		myLog.writeLog(">> mySolid");
		while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_tmp = ligne;
			ligne = br.readLine();

			if (ligne_tmp.equalsIgnoreCase("10")){	
				p1_x = Double.parseDouble(ligne);
			}
			else if (ligne_tmp.equalsIgnoreCase("11")){	
				p2_x = Double.parseDouble(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("12")){	
				p3_x = Double.parseDouble(ligne);
			}
			else if (ligne_tmp.equalsIgnoreCase("13")){	
				p4_x = Double.parseDouble(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("20")){	
				p1_y = Double.parseDouble(ligne);
			}
			else if (ligne_tmp.equalsIgnoreCase("21")){	
				p2_y = Double.parseDouble(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("22")){	
				p3_y = Double.parseDouble(ligne);
			}
			else if (ligne_tmp.equalsIgnoreCase("23")){	
				p4_y = Double.parseDouble(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("39")){	
				thickness = Double.parseDouble(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("8")){	
				l = univers.findLayer(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("62")){	
				c =Integer.parseInt(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("6")){	
				lineType = univers.findLType(ligne);				
			}
			else if (ligne_tmp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}
		}	
		
		return new mySolid(
				new myPoint(p1_x, p1_y, c, null, visibility,1),
				new myPoint(p2_x, p2_y, c, null, visibility,1),
				new myPoint(p3_x, p3_y, c, null, visibility,1),
				new myPoint(p4_x, p4_y, c, null, visibility,1),
				thickness,
				c,
				l, 
				visibility,
				lineType
		);
	}

	public void write(FileWriter out) throws IOException{
		out.write(this.toString()+"\n");
		out.write("10\n");
		out.write(_p1.X()+"\n");
		out.write("20\n");
		out.write(_p1.Y()+"\n");
		out.write("11\n");
		out.write(_p2.X()+"\n");
		out.write("21\n");
		out.write(_p2.Y()+"\n");
		out.write("12\n");
		out.write(_p3.X()+"\n");
		out.write("22\n");
		out.write(_p3.Y()+"\n");
		out.write("13\n");
		out.write(_p4.X()+"\n");
		out.write("23\n");
		out.write(_p4.Y()+"\n");
		out.write("39\n");
		out.write(_thickness+"\n");
		super.writeCommon(out);
		out.write("0\n");
	}
	public String toString(){
		return DXF_Loader.res.getString("mySolid.0");
	}
	public DefaultMutableTreeNode getNode()
	{
		double x=0, y=0;
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.XA, String.valueOf(_p1.X()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.YA, String.valueOf(_p1.Y()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.XB, String.valueOf(_p2.X()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.YB, String.valueOf(_p2.Y()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.XC, String.valueOf(_p3.X()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.YC, String.valueOf(_p3.Y()))));

		if(_p4!=null){
			x=_p4.X();
			y=_p4.Y();
		}
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.XD, String.valueOf(x))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.YD, String.valueOf(y))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.THICKNESS, String.valueOf(_thickness))));
		
		Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));
		
		return root;
	}

	
	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException {
		myLabel l=null;
		
		if(code.equals(myLabel.XA)){
			_p1.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.XA, newValue.toString());
		}
		else if(code.equals(myLabel.YA)){
			_p1.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.YA, newValue.toString());
		}
		else if(code.equals(myLabel.XB)){
			_p2.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.XB, newValue.toString());
		}
		else if(code.equals(myLabel.YB)){
			_p2.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.YB, newValue.toString());
		}
		else if(code.equals(myLabel.XC)){
			_p3.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.XC, newValue.toString());
		}
		else if(code.equals(myLabel.YC)){
			_p3.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.YC, newValue.toString());
		}
		else if(code.equals(myLabel.XD)){
			if(_p4 == null)
				_p4 = new myPoint();
			
			_p4.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.XD, newValue.toString());
		}
		else if(code.equals(myLabel.YD)){
			if(_p4 == null)
				_p4 = new myPoint();
			
			_p4.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.YD, newValue.toString());
		}
		else if(code.equals(myLabel.THICKNESS)){
			_thickness = Integer.parseInt(newValue.toString());
			l = new myLabel(myLabel.THICKNESS, newValue.toString());
		}else {
			l = super.getCommonLabel(code, newValue);
		}
		return l;
	}

	public double getMinX(double min) {
		if (_p1.X()<min)
			min = _p1.X();	
		if (_p2.X()<min)
			min = _p2.X();	
		if (_p3.X()<min)
			min = _p3.X();	
		if (_p4.X()<min)
			min = _p4.X();	
		return min;
	}

	public double getMaxX(double max) {
		if (_p1.X()>max)
			max = _p1.X();	
		if (_p2.X()>max)
			max = _p2.X();	
		if (_p3.X()>max)
			max = _p3.X();	
		if (_p4.X()>max)
			max = _p4.X();	
		return max;
	}

	public double getMinY(double min) {
		if (_p1.Y()<min)
			min = _p1.Y();	
		if (_p2.Y()<min)
			min = _p2.Y();	
		if (_p3.Y()<min)
			min = _p3.Y();	
		if (_p4.Y()<min)
			min = _p4.Y();	
		return min;
	}

	public double getMaxY(double max) {
		if (_p1.Y()>max)
			max = _p1.Y();	
		if (_p2.Y()>max)
			max = _p2.Y();	
		if (_p3.Y()>max)
			max = _p3.Y();	
		if (_p4.Y()>max)
			max = _p4.Y();	
		return max;
	}

	public void translate(double x, double y) {
		_p1._point.x-=myCoord.getTransalation(x);
		_p1._point.y+=myCoord.getTransalation(y);

		_p2._point.x-=myCoord.getTransalation(x);
		_p2._point.y+=myCoord.getTransalation(y);

		_p3._point.x-=myCoord.getTransalation(x);
		_p3._point.y+=myCoord.getTransalation(y);

		if (_p4 != _p3) {
			_p4._point.x-=myCoord.getTransalation(x);
			_p4._point.y+=myCoord.getTransalation(y);
		}
		
	/*	this.g=new GeneralPath();
		this.g.moveTo((float)myCoord.dxfToJava_X(_p1.X()),(float)myCoord.dxfToJava_Y(_p1.Y()));
		this.g.lineTo((float)myCoord.dxfToJava_X(_p2.X()),(float)myCoord.dxfToJava_Y(_p2.Y()));
		this.g.lineTo((float)myCoord.dxfToJava_X(_p3.X()),(float)myCoord.dxfToJava_Y(_p3.Y()));
		if (_p4 != null)
			this.g.lineTo((float)myCoord.dxfToJava_X(_p4.X()),(float)myCoord.dxfToJava_Y(_p4.Y()));
		this.g.closePath();*/
		
	}

	@Override
	public GeneralPath getSelectedEntity() {
		this.g=new GeneralPath();
		this.g.moveTo((float)myCoord.dxfToJava_X(_p1.X()),(float)myCoord.dxfToJava_Y(_p1.Y()));
		this.g.lineTo((float)myCoord.dxfToJava_X(_p2.X()),(float)myCoord.dxfToJava_Y(_p2.Y()));
		this.g.lineTo((float)myCoord.dxfToJava_X(_p3.X()),(float)myCoord.dxfToJava_Y(_p3.Y()));
		if (_p4 != null)
			this.g.lineTo((float)myCoord.dxfToJava_X(_p4.X()),(float)myCoord.dxfToJava_Y(_p4.Y()));
		this.g.closePath();
		return this.g;
	}
}
