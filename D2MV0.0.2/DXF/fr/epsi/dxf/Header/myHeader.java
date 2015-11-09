package fr.epsi.dxf.Header;

import java.awt.geom.Point2D;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Entities.myBufferedReader;
import fr.epsi.dxf.Entities.myPoint;
import fr.epsi.dxf.Graphics.myCoord;
import fr.epsi.dxf.Graphics.myLabel;

public class myHeader {

	public myPoint 	_LIMMIN		;
	public myPoint	_LIMMAX		;
	public myPoint	_EXTMIN		;
	public myPoint 	_EXTMAX		;
	public int		_FILLMODE	;
	public String	_ACADVER	;
    
	public myHeader(){
            _LIMMIN 		= new myPoint(new Point2D.Double(0,0))		;
            _LIMMAX 		= new myPoint(new Point2D.Double(100,100))	;
            _EXTMIN 		= new myPoint(new Point2D.Double(100,100))	;
            _EXTMAX 		= new myPoint(new Point2D.Double(50,50))	;
            _FILLMODE 		= 0			;
            _ACADVER		= "AC1006"	;
	}
	
	public myHeader(myPoint limmin,myPoint limmax, myPoint extmin, myPoint extmax, int fillmode, String version){
    	_LIMMIN 		= limmin	;
        _LIMMAX 		= limmax	;
        _EXTMIN 		= extmin	;
        _EXTMAX 		= extmax	;
        _FILLMODE 		= fillmode	;
        _ACADVER		= version	;
	}
	
	public static myHeader read(myBufferedReader br, myUnivers univers) throws IOException{
		String ligne, version="AC1006";
		double x=0, y=0;
		Point2D.Double limmin = null;
		Point2D.Double limmax = null;
		Point2D.Double extmin = null;
		Point2D.Double extmax = null;
        int	 fillmode = 0 ;
        
        while ((ligne=br.readLine()) != null && !ligne.equals("0"))
        {
        	ligne = br.readLine();
        	if(ligne.equals("0")){
        		break;
        	}
        	else if(ligne.equalsIgnoreCase("$LIMMIN"))
        	{
        		x=0;
        		y=0;
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("10")){
        			ligne = br.readLine();
            		x = Double.parseDouble(ligne);
                }
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("20")){
        			ligne = br.readLine();
            		y = Double.parseDouble(ligne);
                }
                 
                limmin = new Point2D.Double(x, y);
        	}
        	else if(ligne.equalsIgnoreCase("$LIMMAX"))
        	{
        		x=0;
        		y=0;
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("10")){
        			ligne = br.readLine();
            		x = Double.parseDouble(ligne);
                }
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("20")){
        			ligne = br.readLine();
            		y = Double.parseDouble(ligne);
                }
                 
                limmax = new Point2D.Double(x, y);
        	}
        	else if(ligne.equalsIgnoreCase("$EXTMIN"))
        	{
        		x=0;
        		y=0;
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("10")){
        			ligne = br.readLine();
            		x = Double.parseDouble(ligne);
                }
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("20")){
        			ligne = br.readLine();
            		y = Double.parseDouble(ligne);
                }
                 
                extmin = new Point2D.Double(x, y);
        	}
        	else if(ligne.equalsIgnoreCase("$EXTMAX"))
        	{
        		x=0;
        		y=0;
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("10")){
        			ligne = br.readLine();
            		x = Double.parseDouble(ligne);
                }
        		ligne = br.readLine();
        		if(ligne.equalsIgnoreCase("20")){
        			ligne = br.readLine();
            		y = Double.parseDouble(ligne);
                }
                 
                extmax = new Point2D.Double(x, y);
        	}
        	else if(ligne.equalsIgnoreCase("$ACADVER"))
        	{
        		ligne=br.readLine();
        		if(ligne.equalsIgnoreCase("1")){
        			ligne=br.readLine();
        			version = ligne;
        		}
        	}
        	else if(ligne.equalsIgnoreCase("$FILLMODE"))
        	{
        		ligne=br.readLine();
        		if(ligne.equalsIgnoreCase("70")){
        			ligne=br.readLine();
        			if(!ligne.equalsIgnoreCase("0")){
        				fillmode=Integer.parseInt(ligne);
        			}
        		}
        	}
        }
        
        return new myHeader(new myPoint(limmin, -1, null,1,1), 
        					new myPoint(limmax, -1, null,1,1), 
        					new myPoint(extmin, -1, null,1,1), 
        					new myPoint(extmax, -1, null,1,1), 
        					fillmode, version);
	}

	public  String toString(){
		return DXF_Loader.res.getString("myHeader.0");
	}
	
	public DefaultMutableTreeNode getNode() {
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
				
		root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.VERSION,String.valueOf(_ACADVER))), root.getChildCount());
		root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.FILLMODE,String.valueOf(_FILLMODE))), root.getChildCount());
		
		if(_EXTMIN != null){
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.EXTMINX,String.valueOf(_EXTMIN.X()))), root.getChildCount());
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.EXTMINY,String.valueOf(_EXTMIN.Y()))), root.getChildCount());
		}
		if(_EXTMAX != null){
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.EXTMAXX,String.valueOf(_EXTMAX.X()))), root.getChildCount());
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.EXTMAXY,String.valueOf(_EXTMAX.Y()))), root.getChildCount());
		}
		if(_LIMMIN != null){
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.LIMMINX,String.valueOf(_LIMMIN.X()))), root.getChildCount());
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.LIMMINY,String.valueOf(_LIMMIN.Y()))), root.getChildCount());
		}
		if(_LIMMAX != null){
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.LIMMAXX,String.valueOf(_LIMMAX.X()))), root.getChildCount());
			root.insert(new DefaultMutableTreeNode(new myLabel(myLabel.LIMMAXY,String.valueOf(_LIMMAX.Y()))), root.getChildCount());
		}
		return root;
	}

	public String getIconName(myLabel Value) {
		
		String nomImage = "edit.gif";
		
		if(Value._code.equalsIgnoreCase(myLabel.VERSION)){					nomImage = "version.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.FILLMODE)){			nomImage = "fillmode.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.E_RATIO)){			nomImage = "ratio.gif"				;
		/*
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMINX)){			nomImage = "extminx.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMINX)){			nomImage = "extminx.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMINY)){			nomImage = "extminy.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMAXX)){			nomImage = "extmaxx.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMAXY)){			nomImage = "extmaxy.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMINX)){			nomImage = "limminx.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMINY)){			nomImage = "limminy.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMAXX)){			nomImage = "limmaxx.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMAXY)){			nomImage = "limmaxy.gif"			;}
		*/
		}else if(Value._code.equalsIgnoreCase(myLabel.FILLMODE)){			nomImage = "fillmode.gif"	;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMINX)){			nomImage = "y.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMINX)){			nomImage = "x.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMINY)){			nomImage = "y.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMAXX)){			nomImage = "x.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.EXTMAXY)){			nomImage = "y.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMINX)){			nomImage = "x.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMINY)){			nomImage = "y.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMAXX)){			nomImage = "x.gif"			;
		}else if(Value._code.equalsIgnoreCase(myLabel.LIMMAXY)){			nomImage = "y.gif"			;}
	
		return ("images/" + nomImage);
	}

	public Object getNewLabel(DefaultMutableTreeNode node, Object newValue) {
		myLabel l = null;
		String code = ((myLabel)node.getUserObject())._code;
		
		if(code.equals("") || node.getParent() == null || ((DefaultMutableTreeNode) node.getParent()).getUserObject() == null)
			return null;
		
		if(code.equals(myLabel.VERSION)){
			_ACADVER = newValue.toString();
			code = myLabel.VERSION;
		}
		else if(code.equals(myLabel.EXTMINX)){
			_EXTMIN.setX(Double.parseDouble(newValue.toString()));
			code = myLabel.EXTMINX;
		}
		else if(code.equals(myLabel.EXTMINY)){
			_EXTMIN.setY(Double.parseDouble(newValue.toString()));
			code = myLabel.EXTMINY;
		}
		else if(code.equals(myLabel.EXTMAXX)){
			_EXTMAX.setX(Double.parseDouble(newValue.toString()));
			code = myLabel.EXTMAXX;
		}
		else if(code.equals(myLabel.EXTMAXY)){
			_EXTMAX.setY(Double.parseDouble(newValue.toString()));
			code = myLabel.EXTMAXY;
		}
		else if(code.equals(myLabel.LIMMINX)){
			_LIMMIN.setX(Double.parseDouble(newValue.toString()));
			code = myLabel.LIMMINX;
		}
		else if(code.equals(myLabel.LIMMINY)){
			_LIMMIN.setY(Double.parseDouble(newValue.toString()));
			code = myLabel.LIMMINY;
		}
		else if(code.equals(myLabel.LIMMAXX)){
			_LIMMAX.setX(Double.parseDouble(newValue.toString()));
			code = myLabel.LIMMAXX;
		}
		else if(code.equals(myLabel.LIMMAXY)){
			_LIMMAX.setY(Double.parseDouble(newValue.toString()));
			code = myLabel.LIMMAXY;
		}
		
		
		l = new myLabel(code, newValue.toString());
		
		return l;
	}

	public void setLIM(double d, double e) {
		
		_LIMMIN.setX(myCoord.javaToDXF_X(0));
		_LIMMIN.setY(myCoord.javaToDXF_Y(0));
		
		_LIMMAX.setX(myCoord.javaToDXF_X(d));
		_LIMMAX.setY(myCoord.javaToDXF_X(e));
		
		if(_LIMMAX.X() > _EXTMAX.X()){
			_EXTMAX.setX(_LIMMAX.X());			
		}
		if(_LIMMAX.Y() > _EXTMAX.Y()){
			_EXTMAX.setY(_LIMMAX.Y());
		}
		
		if(_LIMMIN.X() < _EXTMIN.X()){
			_EXTMIN.setX(_LIMMIN.X());			
		}
		if(_LIMMIN.Y() < _EXTMIN.Y()){
			_EXTMIN.setY(_LIMMIN.Y());
		}		
	}
}
