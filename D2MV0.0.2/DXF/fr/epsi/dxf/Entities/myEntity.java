package fr.epsi.dxf.Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Graphics.DXF_Color;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myTable;


public abstract class myEntity {
	
	private static final long serialVersionUID = 1L;
		
	public myLineType	_lineType	;
	public int	 		_color		;
	public myLayer 		_refLayer	;
	public double 		_thickness	;
	
	public boolean 		isVisible = true;
	public boolean 		selected  = false;
	public boolean 		changing  = false;
	
	public abstract myLabel getNewLabel(String code, Object newValue) throws NumberFormatException;	
	public abstract DefaultMutableTreeNode getNode();	
	public abstract void write(FileWriter out) throws IOException;
	public abstract Object getSelectedEntity();
	
	public abstract double getMinX(double min);	
	public abstract double getMaxX(double min);	
	public abstract double getMinY(double min);	
	public abstract double getMaxY(double min);
	public abstract void translate(double x, double y);
	
	public BasicStroke _stroke;

	
	public myEntity(int c, myLayer l, int visibility, myLineType lineType, double thickness) {
		_lineType	= lineType;
		_refLayer	= l;
		_color=c;
		_thickness=thickness;
		
		if(visibility == 0)
			isVisible = true;
		else
			isVisible = false;
		
		if(_lineType != null)
			_stroke = new BasicStroke((float)_thickness, myTable.CAP, myTable.JOIN, 10.0f,myLineType.parseTxt(_lineType._value),0.0f);
		else
			_stroke = new BasicStroke((float)_thickness, myTable.CAP, myTable.JOIN, 10.0f,myTable.defautMotif,0.0f);
			
		DXF_Loader._mc._dxf.updateStats();
	}

	public myLabel getCommonLabel(String code, Object newValue) throws NumberFormatException{
		myLabel l =null;
		if(code.equals(myLabel.COLOR)){
			_color = Integer.parseInt(newValue.toString());
			l = new myLabel(myLabel.COLOR, newValue.toString());
		}
		else if(code.equals(myLabel.VISISBILITY)){
			if(Integer.parseInt(newValue.toString()) == 0)
				isVisible = true;
			else
				isVisible = false;
			l = new myLabel(myLabel.VISISBILITY, newValue.toString());
		}
		else if(code.equals(myLabel.TYPE_LIGNE)){
			//_lineType = myLineType.getStrokeID(newValue.toString());//find
			l = new myLabel(myLabel.TYPE_LIGNE, newValue.toString());
		}
		return l;
	}
	
	public void draw(Graphics g){
		
		BasicStroke _lineStroke = null	;
		int c = _color;
		
		if (this.selected)
        	c = DXF_Color.getColor(DXF_Color.getSelectingColor());
		else if (this.changing)
        	c = DXF_Color.getColor(DXF_Color.getChangingColor());
	    else{
	    	if(!(this instanceof myBlockReference) && !(this instanceof myLayer)){
	    		if ((c<0) || (_color == 255 && _refLayer != null))
		    		if(_refLayer == null)
		    			c=DXF_Color.getDefaultColorIndex();
		    		else	
		    			c=_refLayer._color;
	    	}    	
	    }
		
		if(c<0)
			c= DXF_Color.getDefaultColorIndex();
		
		// Gestion de la couleur en fonction de la couleur de fond d'�cran
		try {
			if (DXF_Color.getColor(c).equals(myUnivers._bgColor)) {
				if (myUnivers._bgColor.equals(Color.WHITE))
					g.setColor(Color.BLACK);
				else if (myUnivers._bgColor.equals(Color.BLACK))		
					g.setColor(Color.WHITE);
			} else{
				g.setColor(DXF_Color.getColor(c));
			}
		} catch (NullPointerException e) {
			g.setColor(DXF_Color.getColor(c));
		}
		
		if (DXF_Loader.checkLineType) {
			if(_lineType != null && _lineType._name.equalsIgnoreCase("BYLAYER")){
				if(_refLayer != null){
					_lineStroke = _refLayer._stroke;
				}
			}
			else {
				_lineStroke= _stroke;
			}
			if(_lineStroke == null)
				_lineStroke = myTable.defaultStroke;
			
			if(_lineStroke != null) {
				if ( ((Graphics2D) g).getStroke()!=_lineStroke)
					((Graphics2D) g).setStroke(_lineStroke);
			}
		}
		
	}
	
	public Vector<DefaultMutableTreeNode> getCommonNode(){
		Vector<DefaultMutableTreeNode> v = new Vector<DefaultMutableTreeNode>();
		
		v.add(new DefaultMutableTreeNode(new myLabel(myLabel.COLOR,String.valueOf(this._color))));
		
		if(isVisible)
			v.add(new DefaultMutableTreeNode(new myLabel(myLabel.VISISBILITY,String.valueOf(0))));
		else
			v.add(new DefaultMutableTreeNode(new myLabel(myLabel.VISISBILITY,String.valueOf(1))));
			
		if(_lineType != null){
			v.add(new DefaultMutableTreeNode(new myLabel(myLabel.TYPE_LIGNE,_lineType._name)));
		}
		return v;
	}
		
	public void writeCommon(FileWriter out) throws IOException{
		if(_color > 0){
			out.write("62\n");
			out.write(_color+"\n");
		}
		if(_refLayer != null){
			out.write("8\n");
			out.write(_refLayer._nom + "\n");
		}
		if(_lineType != null && !(this instanceof myBlock) && !(this instanceof myPoint)){
			out.write("6\n");
			out.write(_lineType._name + "\n");	
		}
		
		if(!(this instanceof myLayer) || !(this instanceof myBlock)){
			out.write("60\n");
			if(isVisible)
				out.write("0\n");
			else
				out.write("1\n");
		}
	}
	
	public void setVisible(boolean bool){
		isVisible = bool;		
	}

	public String getIconName(myLabel Value){		
		String nomImage = "spacer.gif";		
		if      (Value._code.equalsIgnoreCase(myLabel.X)){			nomImage = "x.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.Y)){			nomImage = "y.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.XA)){			nomImage = "xa.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.YA)){			nomImage = "ya.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.XB)){			nomImage = "xb.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.YB)){			nomImage = "yb.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.XC)){			nomImage = "x.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.YC)){			nomImage = "y.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.XD)){			nomImage = "x.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.YD)){			nomImage = "y.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.COLOR)){		nomImage = "color.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.RADIUS)){		nomImage = "radius.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.BULGE)){		nomImage = "bezier.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.TYPE_LIGNE)){	nomImage = "type_ligne.gif"	;
		}else if(Value._code.equalsIgnoreCase(myLabel.ANGLE1)){		nomImage = "angle.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.ANGLE2)){		nomImage = "angle.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.ROTATION)){	nomImage = "rotation.gif"	;
		}else if(Value._code.equalsIgnoreCase(myLabel.ALIGN)){		nomImage = "align.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.HEIGHT)){		nomImage = "height.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.THICKNESS)){	nomImage = "thickness.png"	;
		}else if(Value._code.equalsIgnoreCase(myLabel.VALUE)){		nomImage = "value.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.FLAG)){		nomImage = "flag.gif"		;
		}else if(Value._code.equalsIgnoreCase(myLabel.STYLE)){		nomImage = "style.gif"		;		
		}else if(Value._code.equalsIgnoreCase(myLabel.VISISBILITY)){nomImage = "visible.gif"	;
		}else if(Value._code.equalsIgnoreCase(myLabel.ZOOM_FACTOR)){nomImage = "zoom.gif"	;
		}else if(Value._code.equalsIgnoreCase(myLabel.BLOCK)){		nomImage = "block.png"		;}
		return ("images/" + nomImage);		
	}
	
	public void setSelected(boolean s) {
		this.selected=s;
	}

	public void setChanging(boolean b) {
		this.changing=b;
	}
	
}
