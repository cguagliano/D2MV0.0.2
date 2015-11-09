package fr.epsi.dxf.Header;

import java.awt.BasicStroke;
import java.io.IOException;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Entities.myBufferedReader;
import fr.epsi.dxf.Graphics.myLabel;

public class myTable {	
	public 	static final double			defaultThickness	= 1.0f;
	public  static final float 			defautMotif[] 		= {1.0f, 0.0f}	;
	private static final float 			zoomDash[] 			= myLineType.parseTxt("_")	;
	public 	static int 	CAP				= BasicStroke.CAP_ROUND;
	public 	static int 	JOIN			= BasicStroke.JOIN_ROUND;
	public 	static final BasicStroke 	defaultStroke 		= new BasicStroke((float)defaultThickness,CAP,JOIN,10.0f, defautMotif, 	0.0f);
	public 	static final BasicStroke 	zoomStroke 			= new BasicStroke((float)defaultThickness,CAP,JOIN,10.0f, zoomDash, 	0.0f);

	public Vector<myLayer> 		_myLayers 		= new Vector<myLayer>()	;
	public Vector<myLineType> 	_myLineTypes 	= new Vector<myLineType>();
	
	public myTable() {
		//_myLayers.add(new myLayer("default", DXF_Color.getDefaultColorIndex()));
	}
	

	public myTable(myBufferedReader br, myUnivers u) throws IOException {
		String ligne;
		Object obj;
		
        while ((ligne = br.readLine().trim()) != null && !ligne.equals("ENDTAB")) {
            if (ligne.toUpperCase().trim().equalsIgnoreCase("LAYER")){
            	obj =myLayer.read(br, u);
            
            	if(obj != null)
            		_myLayers.addElement((myLayer) obj);
            	
            }
            else if(ligne.equalsIgnoreCase("LTYPE"))
            {
            	obj =myLineType.read(br);
            	
            	if(obj != null){
            		_myLineTypes.addElement((myLineType) obj);
            	
            	}
    		}	
        }        
	}	
	
	public String toString(){
		return DXF_Loader.res.getString("myTable.0") + _myLayers.size() + " - " + _myLineTypes.size() + ")";
	}
	
	public DefaultMutableTreeNode getNode(){
		DefaultMutableTreeNode root = null, node = null;
		
		root = new DefaultMutableTreeNode(this);
		
		for(int i= 0; i< _myLineTypes.size(); i++){
			node = _myLineTypes.get(i).getNode();
			root.insert(node, root.getChildCount());
		}

		for(int i= 0; i< _myLayers.size(); i++){
			node = _myLayers.get(i).getNode();
			root.insert(node, root.getChildCount());
		}
		
		return root;
	}

	public Icon getIcon(myLabel label) {		
		return null;
	}	
}
