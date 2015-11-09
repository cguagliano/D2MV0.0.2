package fr.epsi.dxf.Entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Graphics.myCoord;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myStats;

public class myDimension extends myBlockReference {

	private static final long serialVersionUID = -6592513174745850863L;
	
	public double 	_angle		= 0						;//50
	public String 	_dimension	= "<>"					;//1
	public myPoint   _point_WCS	= new myPoint()			;//10,20
	
	public myDimension(double a, String dim, double x, double y, myBlock refBlock, String nomBlock, myLayer l, int visibility, int c, myLineType lineType) {
		super(c, l, visibility, null, nomBlock, refBlock);	
		_angle 		= a;
		_dimension 	= dim;
		_point_WCS 	= new myPoint(x, y, c, null, visibility,1);
		myStats.nbDimension+=1;
	}
	
	public myDimension() {
		super(-1, null, 0, null, "", null);
		myStats.nbDimension+=1;
	}
	
	public void draw(Graphics g) {
		//Rectangle2D.Double rect = getRectBound();
		if ((_refBlock!=null) && (_refBlock._myEnt!=null) ){// && (rect!=null)) {

	        try {
				Graphics2D g2 = (Graphics2D)g;
		        AffineTransform aT = g2.getTransform();
			   	
			   	AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(-_angle), myCoord.dxfToJava_X(_point_WCS.X()), myCoord.dxfToJava_Y(_point_WCS.Y()));
			   	g2.transform(rotate);
				for(int i=0; i<_refBlock._myEnt.size(); i++){
					_refBlock._myEnt.elementAt(i).draw(g2);
				}					
			g2.setTransform(aT);
	        } catch (Exception e) { }
		}
	}
	
	public static myDimension read(myBufferedReader br, myUnivers univers) throws IOException {
		String ligne="", ligne_temp="", dimension="", nomBlock="";
		myDimension d = null;
		myLayer l = null;
		myBlock refBlock = null;
		double angle=0,x=0, y=0;
		int visibility=0, c=-1;
		myLineType lineType=null;
		
		myLog.writeLog("> new myDimension");
		while((ligne=br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();
			
			if (ligne_temp.equalsIgnoreCase("8")){	
				l = univers.findLayer(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("1")){
				dimension = ligne;
			}
			else if (ligne_temp.equalsIgnoreCase("50")){
				angle = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("2")){
				nomBlock = ligne;
				refBlock = univers.findBlock(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("6"))
			{
				lineType = univers.findLType(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("10")){
				x = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("20")){
				y = Double.parseDouble(ligne);	
			}
			else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("62"))
			{
				c =Integer.parseInt(ligne);
			}						
		}
		d = new myDimension(angle, dimension,x, y, refBlock, nomBlock, l, visibility,c, lineType);
		
		if((refBlock == null)||(refBlock != null && !refBlock._name.equalsIgnoreCase(nomBlock))){
			univers.addRefBlockForUpdate(d);
		}		
		return d;
	}
	
	public void write(FileWriter out) throws IOException{
		out.write("DIMENSION\n");
		out.write("2\n");
		out.write(_blockName+"\n");
		out.write("10\n");
		out.write(_point_WCS.X()+"\n");
		out.write("20\n");
		out.write(_point_WCS.Y()+"\n");
		out.write("50\n");
		out.write(_angle+"\n");
		super.writeCommon(out);
		out.write("0\n");
	}	
	public String toString(){
		return DXF_Loader.res.getString("myDimension.0") + _blockName + ")";
	}
	public DefaultMutableTreeNode getNode(){
		DefaultMutableTreeNode root = null;
		
		root = new DefaultMutableTreeNode(this);
		
		root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.X,String.valueOf(_point_WCS.X()))), root.getChildCount());
		root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.Y,String.valueOf(_point_WCS.Y()))), root.getChildCount());
		root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.ANGLE1,String.valueOf(_angle))), root.getChildCount());
		root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.BLOCK,_blockName)), root.getChildCount());
	       
        Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));
        
		return root;
	}

	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;

		if(code.equals(myLabel.X)){
			_point_WCS.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.X, newValue.toString());
		}
		else if(code.equals(myLabel.Y)){
			_point_WCS.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.Y, newValue.toString());
		}
		else if(code.equals(myLabel.ANGLE1)){
			_angle = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.ANGLE1, newValue.toString());
		}
		else if(code.equals(myLabel.BLOCK)){
			if(_refBlock._refUnivers != null){
				_refBlock._refUnivers.changeBlock(this, newValue.toString());
			}
			l = new myLabel(myLabel.BLOCK, newValue.toString());						
		}else {
			l = super.getCommonLabel(code, newValue);
		}
		
		return l;
	}

	public double getMinX(double min) {
		if ((_point_WCS.X())<min)
			return _point_WCS.X();			
		return min;
	}


	public double getMaxX(double max) {
		if ((_point_WCS.X())>max)
			return _point_WCS.X();			
		return max;
	}

	public double getMinY(double min) {
		if ((_point_WCS.Y())<min)
			return _point_WCS.Y();			
		return min;
	}

	public double getMaxY(double max) {
		if ((_point_WCS.Y())>max)
			return _point_WCS.Y();			
		return max;
	}
	
	/*public Rectangle2D.Double getRectBound() {
		double minX=Double.MAX_VALUE;
		double maxX=Double.MIN_VALUE;
		double minY=Double.MAX_VALUE;
		double maxY=Double.MIN_VALUE;
		if (_refBlock==null)
			return null;
		for (int k=0;k<_refBlock._myEnt.size();k++) {
			minX = _refBlock._myEnt.elementAt(k).getMinX(minX);
			maxX = _refBlock._myEnt.elementAt(k).getMaxX(maxX);
			minY = _refBlock._myEnt.elementAt(k).getMinX(minY);
			maxY = _refBlock._myEnt.elementAt(k).getMaxX(maxY);
		}
		if (minX==Double.MAX_VALUE)
			minX=0;
		if (minY==Double.MAX_VALUE)
			minY=0;
		if (maxX==Double.MIN_VALUE)
			maxX=0;
		if (maxY==Double.MIN_VALUE)
			maxY=0;
		
		return new Rectangle2D.Double(minX,minY,maxX-minX,maxY-minY);
	}*/
}
