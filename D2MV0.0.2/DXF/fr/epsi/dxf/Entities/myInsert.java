package fr.epsi.dxf.Entities;


import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myStats;

public class myInsert extends fr.epsi.dxf.Entities.myBlockReference{

	private static final long serialVersionUID = -8331159359392060196L;
	public myPoint 			_point			= new myPoint();
	
	public myInsert(double x, double y, String nomBlock, myBlock refBlock, myLayer l, int visibility, int c, myLineType lineType) {
		super(c, l, visibility, lineType, nomBlock, refBlock);
		_point 		= new myPoint(x, y, c, null, visibility,1);
		
		myStats.nbInsert+=1;
	}
	public myInsert(myPoint p, String nomBlock, myBlock refBlock, myLayer l, int visibility,myLineType lineType) {
		super(-1, l, visibility, lineType, nomBlock, refBlock);
		_point 		= p;
		
		myStats.nbInsert+=1;
	}

	public myInsert() {
		super(-1, null, 0, null, "", null);
		
		myStats.nbInsert+=1;
	}

	public void draw(Graphics g) {	
		if(_refBlock == null || _refBlock._myEnt == null)
			return;
		
		try {
			for(int i=0; i<_refBlock._myEnt.size(); i++){
				if(_refBlock._myEnt.elementAt(i).isVisible){
					_refBlock._myEnt.elementAt(i).draw(g);
				}
			}
		} catch ( StackOverflowError e) { 
			myLog.writeLog(" [Stack Error] Pile pleine !"); 
		}
	}

	public static myInsert read(myBufferedReader br, myUnivers univers) throws IOException {
		String ligne="", ligne_temp="", nomBlock="";
		myInsert 	m = null;
		myLayer  	l = null;
		double x=0, y=0;
		int visibility=0, c=-1;
		myBlock refBlock = null;
		myLineType lineType = null;
		
		myLog.writeLog("> new Insert");
		while((ligne=br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();

			if (ligne_temp.equalsIgnoreCase("8"))
			{	
				l = univers.findLayer(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("2"))
			{
				nomBlock = ligne;
				refBlock = univers.findBlock(nomBlock);
			}
			else if (ligne_temp.equalsIgnoreCase("10"))
			{
				x = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("20"))
			{
				y = Double.parseDouble(ligne);	
			}else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}else if (ligne_temp.equalsIgnoreCase("6"))
			{
				lineType = univers.findLType(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("62"))
			{
				c = Integer.parseInt(ligne);
			}		
		}

		m = new myInsert(x, y, nomBlock, refBlock, l, visibility, c, lineType);

		if((refBlock == null)||(refBlock != null && !refBlock._name.equalsIgnoreCase(nomBlock))){
			univers.addRefBlockForUpdate(m);
		}

		return m;
	}

	public void write(FileWriter out) throws IOException{
		out.write("INSERT\n");
		super.writeCommon(out);
		out.write("10\n");
		out.write(_point.X()+"\n");
		out.write("20\n");
		out.write(_point.Y()+"\n");
		out.write("0\n");

	}	
	
	public String toString(){
		return DXF_Loader.res.getString("myInsert.0") + _blockName + ")";
	}

	public DefaultMutableTreeNode getNode(){
		DefaultMutableTreeNode root = null;
		
		root = new DefaultMutableTreeNode(this);
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X,String.valueOf(_point.X()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y,String.valueOf(_point.Y()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.BLOCK,_blockName)));

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
		else if(code.equals(myLabel.BLOCK)){		
			if(_refBlock._refUnivers != null && _refBlock._refUnivers != null){
				_refBlock._refUnivers.changeBlock(this, newValue.toString());
			}
			l = new myLabel(myLabel.BLOCK, newValue.toString());	
		}else {
			l = super.getCommonLabel(code, newValue);
		}
		return l;
	}

	public double getMinX(double min) {
		if (_point.X()<min)
			return _point.X();			
		return min;
	}

	public double getMaxX(double max) {
		if ((_point.X())>max)
			return _point.X();			
		return max;
	}

	public double getMinY(double min) {
		if (_point.Y()<min)
			return _point.Y();			
		return min;
	}

	public double getMaxY(double max) {
		if ((_point.Y())>max)
			return _point.Y();			
		return max;
	}
}
