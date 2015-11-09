package fr.epsi.dxf.Entities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myStats;

public class myVertex extends myPoint{

	private static final long serialVersionUID = 1L;
	protected 	double		_bulge			=0;

	public myVertex(double x, double y, double b, int c, myLayer l,myPolyline refPolyline, int visibility) {
		super(x, y, c, l, visibility,1);	
		_bulge = b;
		myStats.nbPoint-=1;
	}

	public myVertex() {
		super(0, 0, -1, null,0,1);	
	}

	public myVertex(myVertex v) {
		this._bulge=v._bulge;
		this._color=v._color;
		this._point=v._point;
		this._refLayer=v._refLayer;
	}

	public myVertex(myVertex orig,boolean bis) {
		super(orig._point.x, orig._point.y, orig._color, orig._refLayer, 0,1);	
		_bulge = orig._bulge;
		myStats.nbPoint-=1;
	}

	public void write(FileWriter out) throws IOException{
		out.write("VERTEX\n");

		super.writeCommon(out);

		out.write("42\n");
		out.write(_bulge+"\n");
		out.write("10\n");
		out.write((_point.getX())+"\n");
		out.write("20\n");
		out.write((_point.getY())+"\n");
		out.write("0\n");
	}

	public static myVertex read(myBufferedReader br, myUnivers univers, myPolyline p) throws IOException {
		String ligne, ligne_temp;
		myLayer l=null;
		int visibility=0, c=-1;
		double x=0, y=0, b=0;
		myLog.writeLog("> new Vertex");

		while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();

			if(ligne_temp.equalsIgnoreCase("8"))
			{
				l = univers.findLayer(ligne);
			}			
			else if(ligne_temp.equalsIgnoreCase("42"))
			{
				b = Double.parseDouble(ligne);
			}			
			else if(ligne_temp.equalsIgnoreCase("10"))
			{
				x = Double.parseDouble(ligne);
			}
			else if(ligne_temp.equalsIgnoreCase("20"))
			{
				y = Double.parseDouble(ligne);
			}
			else if(ligne_temp.equalsIgnoreCase("62"))
			{
				c = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}
			else{
				myLog.writeLog("Unknown :" + ligne_temp);				
			}
		}
		return new myVertex(x, y, b, c, l, p, visibility);
	}

	public String toString(){
		return DXF_Loader.res.getString("myVertex.0") ;
	}

	public DefaultMutableTreeNode getNode() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X, String.valueOf(_point.getX()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y, String.valueOf(_point.getY()))));                            
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.BULGE, String.valueOf(_bulge))));
		
		Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));

		return root;
	}

	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;

		double old = 0.0;

		if(code.equals(myLabel.X)){
			try {
				old = X();
				setX(Double.parseDouble(newValue.toString()));
				l = new myLabel(myLabel.X, newValue.toString());
			} catch (Exception e) {
				setX(old);
				l = new myLabel(myLabel.X, String.valueOf(old));
			}
		}
		else if(code.equals(myLabel.Y)){
			try {
				old = Y();
				setY(Double.parseDouble(newValue.toString()));
				l = new myLabel(myLabel.Y, newValue.toString());
			} catch (Exception e) {
				setY(old);
				l = new myLabel(myLabel.Y, String.valueOf(old));
			}
		}
		else if(code.equals(myLabel.BULGE)){
			try {
				old = _bulge;
				if ((Double.parseDouble(newValue.toString())<=1) && (Double.parseDouble(newValue.toString())>=-1)) {
					_bulge = Double.parseDouble(newValue.toString());
					l = new myLabel(myLabel.BULGE, newValue.toString());
				} else {
					_bulge = old;
					l = new myLabel(myLabel.BULGE, String.valueOf(old));
				}
			} catch (Exception e) {
				_bulge = old;
				l = new myLabel(myLabel.BULGE, String.valueOf(old));
			}			
		}else {
			l = super.getCommonLabel(code, newValue);
		}
		return l;
	}
}
