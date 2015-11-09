package fr.epsi.dxf.Header;


import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Entities.myBufferedReader;
import fr.epsi.dxf.Entities.myEntity;
import fr.epsi.dxf.Entities.myPoint;
import fr.epsi.dxf.Graphics.DXF_Color;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Graphics.myLog;

public class myBlock extends myEntity{

	private static final long serialVersionUID = -2195994318962049800L;

	public Vector<myEntity> _myEnt 	= new Vector<myEntity>();
	public myPoint 			_point 	= new myPoint();
	public String			_name	;
	public int				_flag	;
	public myUnivers			_refUnivers;

	public myBlock(double x, double y, int flag, String name, Vector<myEntity> ent, int c, myLayer l, myUnivers univers) {
		super(c, l, 0, null, myTable.defaultThickness);
		_point 		= new myPoint(x, y, c, l, 0,1);
		_name 		= name;
		_flag 		= flag;
		_refUnivers = univers;
		
		if(ent == null)
			ent		= new Vector<myEntity>();
		
		_myEnt		= ent;
	}

	public myBlock(myUnivers univers) {
		super(-1, null, 0,null, myTable.defaultThickness);
		_name = myNameGenerator.getBlockName(DXF_Loader.res.getString("myBlock.0"));
		_refUnivers = univers;
		_myEnt= new Vector<myEntity>();
	}

	public static myBlock read(myBufferedReader br, myUnivers univers) throws IOException {
		myEntity obj=null;
		Vector<myEntity> myEnt = new Vector<myEntity>();
		String ligne="", ligne_temp="", name="";
		double x=0, y=0;
		int flag=0;
		myLayer l =null;
		myLog.writeLog("> new Block");
		while((ligne=br.readLine()) != null)
		{
			ligne_temp = ligne;
			ligne = br.readLine();

			if (ligne_temp.equalsIgnoreCase("8"))
			{	
				l = univers.findLayer(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("2"))
			{
				name=ligne;
			}
			else if (ligne_temp.equalsIgnoreCase("70"))
			{
				flag = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("10"))
			{
				x = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("20"))
			{
				y = Double.parseDouble(ligne);	
			}
			else if (ligne_temp.equalsIgnoreCase("0"))
			{
				// Ajout des �l�ments du block
				while((obj=univers.addEntity(br, ligne, false)) != null){
					myEnt.add(obj);
					ligne=br.readLine();
				}					
				break;
			}
			else{
				myLog.writeLog("Unknown :" + ligne_temp + "("+ ligne +")");		
			}
		}
		return new myBlock(x, y, flag, name, myEnt, DXF_Color.getDefaultColorIndex(), l, univers);
	}

	public void write(FileWriter out) throws IOException{
		out.write("BLOCK\n");

		out.write("8\n");
		if(_refLayer ==null)
			out.write("0");
		else
			out.write(_refLayer._nom);

		out.write("\n2\n");
		out.write(_name+"\n");
		out.write("70\n");
		out.write(_flag+"\n");
		out.write("10\n");
		out.write(_point.X()+"\n");
		out.write("20\n");
		out.write(_point.Y()+"\n");
		out.write("0\n");

		for(int i=0; i<_myEnt.size();i++){
			_myEnt.elementAt(i).write(out);
		}

		out.write("ENDBLK\n");
		out.write("0\n");
	}	
	public String toString(){
		return _name + " (" + _myEnt.size() +")";
	}
	public DefaultMutableTreeNode getNode(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.VALUE,_name)));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X,String.valueOf(_point.X()))));
		root.add( new DefaultMutableTreeNode(new myLabel(myLabel.Y,String.valueOf(_point.Y()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.FLAG,String.valueOf(_flag))));

		for(int i= 0; i< _myEnt.size(); i++){
			root.add( _myEnt.get(i).getNode());
		}

		return root;
	}

	public void draw(Graphics g) {

	}

	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;

		if(code.equals(myLabel.COLOR)){
			_color = Integer.parseInt(newValue.toString());
			l = new myLabel(myLabel.COLOR, newValue.toString());
		}
		else if(code.equals(myLabel.X)){
			_point.setX(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.X, newValue.toString());
		}
		else if(code.equals(myLabel.Y)){
			_point.setY(Double.parseDouble(newValue.toString()));
			l = new myLabel(myLabel.Y, newValue.toString());
		}
		else if(code.equals(myLabel.FLAG)){
			_flag = Integer.parseInt(newValue.toString());
			l = new myLabel(myLabel.FLAG, newValue.toString());
		}
		else if(code.equals(myLabel.VALUE)){
			_name = newValue.toString();
			l = new myLabel(myLabel.VALUE, newValue.toString());
		}
		return l;
	}

	public double getMinX(double min) {
		for (int k=0;k<_myEnt.size();k++) {
			min = _myEnt.elementAt(k).getMinX(min);
		}		
		return min;
	}

	public double getMaxX(double max) {
		for (int k=0;k<_myEnt.size();k++) {
			max = _myEnt.elementAt(k).getMaxX(max);
		}		
		return max;
	}

	public double getMinY(double min) {
		for (int k=0;k<_myEnt.size();k++) {
			min = _myEnt.elementAt(k).getMinY(min);
		}		
		return min;
	}

	public double getMaxY(double max) {
		for (int k=0;k<_myEnt.size();k++) {
			max = _myEnt.elementAt(k).getMaxY(max);
		}		
		return max;
	}

	@Override
	public Object getSelectedEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void translate(double x, double y) {
		// TODO Auto-generated method stub		
	}
}
