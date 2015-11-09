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
import fr.epsi.dxf.Graphics.myLabel;


public class myLayer extends myEntity{

	// consts
	public static final short LAYER_FROZEN      =     1;  /* layer is frozen */
	public static final short LAYER_AUTO_FROZEN =     2;  /* layer automatically frozen in all VIEWPORTS */
	public static final short LAYER_LOCKED      =     4;  /* layer is locked */
	public static final short LAYER_XREF        =     8;  /* layer is from XREF */
	public static final short LAYER_XREF_FOUND  =    16;  /* layer is from known XREF */
	public static final short LAYER_USED        =    32;  /* layer was used */
	public static final short LAYER_INVISIBLE   = 16384;  /* (own:) layer is invisible */

	private static final long serialVersionUID = 1L;
	
	public myTable 		_refTable	;
	public int 			_flag		= 0;
	public String 		_nom 		;
	public Vector<myEntity> _myEnt = new Vector <myEntity>();
	
	public myLayer(String nom, int c, myLineType lineType, int visibility, int flag){
		super(c, null, visibility, lineType, myTable.defaultThickness);		
		_nom	= nom	;
		_flag 	= flag	;
	}
 
	public myLayer(String nom, int c, int flag){
		super(c, null, 0, null, myTable.defaultThickness);
		_nom 	= nom	;
		_flag 	= flag	;
	}
	
	public myLayer(String nom, int c){
		super(c, null, 0, null, myTable.defaultThickness);
		_nom =nom;
	}
	
	public void setVisible(boolean bool){
		isVisible = bool;
		for(int i= 0; i< _myEnt.size(); i++){
			((myEntity) _myEnt.get(i)).setVisible(bool);
		}	
	}

	public static myLayer read(myBufferedReader br, myUnivers u) throws NumberFormatException, IOException {
		String ligne,ligne_tmp, name = "";
		myLayer l = null;
		int f=0, color=0;
		
		while ((ligne=br.readLine().trim()) != null && !(ligne.equals("9")||ligne.equals("0")))
		{
			ligne_tmp = ligne.trim();
			ligne = br.readLine().trim();
			
			if(ligne_tmp.equalsIgnoreCase("2"))
				name = ligne;
			else if(ligne_tmp.equalsIgnoreCase("62")){
				color = Integer.parseInt(ligne);
			}	
			else if(ligne_tmp.equalsIgnoreCase("70")){
				f = Integer.parseInt(ligne);
			}
		}
		
		l = new myLayer(name, color, f);		
		if(color<0)
			l.setVisible(false);
		
		u.currLayer = l;
		return l;
	}

	public void write(FileWriter out) throws IOException {
		out.write("LAYER\n");
		if(!_nom.equalsIgnoreCase("")){
			out.write("2\n");
			out.write(_nom+"\n");			
		}
		out.write("70\n");
		out.write(_flag+"\n");		
		super.writeCommon(out);
		out.write("0\n");		
	}
	
	public String toString(){
		return DXF_Loader.res.getString("myLayer.0") + _nom + "] (" + _myEnt.size()+")";
	}
	
	public DefaultMutableTreeNode getNode() {
		DefaultMutableTreeNode node=null, root = new DefaultMutableTreeNode(this);
		
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.VALUE,_nom)));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.FLAG,String.valueOf(_flag))));
		
		Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));
		
		for(int i= 0; i< _myEnt.size(); i++){
			if((node = _myEnt.get(i).getNode()) != null)
				root.insert(node, root.getChildCount());
		}
		
		return root;		
	}
	
	public void draw(Graphics g) {
		
	}
	
	public myLabel getNewLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l = null;

		if(code.equals(myLabel.VALUE)){
			_nom = newValue.toString();
			l = new myLabel(myLabel.VALUE, newValue.toString());
		}else if(code.equals(myLabel.FLAG)){
			_flag = Integer.parseInt(newValue.toString());
			l = new myLabel(myLabel.FLAG, newValue.toString());
		}else
		{
			l = getCommonLabel(code, newValue);
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

