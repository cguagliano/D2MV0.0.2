package fr.epsi.dxf;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.Entities.myArc;
import fr.epsi.dxf.Entities.myBlockReference;
import fr.epsi.dxf.Entities.myBufferedReader;
import fr.epsi.dxf.Entities.myCircle;
import fr.epsi.dxf.Entities.myDimension;
import fr.epsi.dxf.Entities.myEllipse;
import fr.epsi.dxf.Entities.myEntity;
import fr.epsi.dxf.Entities.myInsert;
import fr.epsi.dxf.Entities.myLine;
import fr.epsi.dxf.Entities.myLwPolyline;
import fr.epsi.dxf.Entities.myPoint;
import fr.epsi.dxf.Entities.myPolyline;
import fr.epsi.dxf.Entities.mySolid;
import fr.epsi.dxf.Entities.myText;
import fr.epsi.dxf.Entities.myTrace;
import fr.epsi.dxf.Graphics.DXF_Color;
import fr.epsi.dxf.Graphics.myCoord;
import fr.epsi.dxf.Graphics.myHistory;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myHeader;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myNameGenerator;
import fr.epsi.dxf.Header.myTable;

public class myUnivers {

	public static Color 	_bgColor		= Color.BLACK;
	public static boolean	antialiasing	= true;
	
	private Vector<myEntity> _entForUpdate 	= new Vector<myEntity>();
	
	public Rectangle2D.Double 	lastView 	= new Rectangle2D.Double();
	public Vector<myTable> 		_myTables 	= new Vector <myTable>();
	public Vector<myBlock> 		_myBlocks 	= new Vector <myBlock>();

	public myLayer 	currLayer		;
	public myEntity	currEntity		;
	public myBlock	currBlock		;
	public double	currThickness	= 1.0f;
	
	public myHeader _header		;    
	public String 	_filename	;
	public double strayX=0;
	public double strayY=0;
	  

	public myUnivers(myHeader header){
		reset();
		_header = header;
		if (header==null){
			header = new myHeader();
		}       
	}

	public void reset(){
		_filename	= null;
		//_bgColor 	= DXF_Color.getColor(255);

		myNameGenerator.reset();
		
		_myTables.removeAllElements();
		_myBlocks.removeAllElements();
		
		_myTables.addElement(new myTable());
		_myTables.elementAt(0)._myLayers.addElement(new myLayer(myNameGenerator.getLayerName(DXF_Loader.res.getString("myUnivers.0")),DXF_Color.getDefaultColorIndex())); //$NON-NLS-1$
		currLayer=_myTables.elementAt(0)._myLayers.get(0);
	}

	public myBlock findBlock(String nom) {
		myBlock b = null;
		for(int i=0;i<_myBlocks.size(); i++){
			if(_myBlocks.elementAt(i)._name.equals(nom)){
				return _myBlocks.elementAt(i);
			}
		}
		/*
		if(b==null){
			b=new myBlock(0,0,0,nom,null, DXF_Color.getDefaultColorIndex(), null, this);
			_myBlocks.add(b);
		}
		*/
		return b;
	}

	public myLayer findLayer(String nom){
		myLayer l = null;
		for(int i=0;i<_myTables.size(); i++){
			for(int j=0;j<_myTables.elementAt(i)._myLayers.size(); j++){
				if(_myTables.elementAt(i)._myLayers.elementAt(j)._nom.equals(nom)){
					return _myTables.elementAt(i)._myLayers.elementAt(j);
				}
			}
		}
		
		l=new myLayer(nom, DXF_Color.getDefaultColorIndex());
		
		if( _myTables.size() < 1){
			_myTables.add(new myTable());
		}
		
		_myTables.elementAt(0)._myLayers.add(l);
		
		return l;
	}

	public myLineType findLType(String name){
		for(int i=0;i<_myTables.size(); i++){
			for(int j=0;j<_myTables.elementAt(i)._myLineTypes.size(); j++){
				if(_myTables.elementAt(i)._myLineTypes.elementAt(j)._name.equals(name)){
					return _myTables.elementAt(i)._myLineTypes.elementAt(j);
				}
			}
		}
		return null;
	}

	public Vector getLTypes(){
		Vector<myLineType> v = new Vector<myLineType>();
		
		for(int i=0;i<_myTables.size(); i++){
			for(int j=0;j<_myTables.elementAt(i)._myLineTypes.size(); j++){
				v.add(_myTables.elementAt(i)._myLineTypes.elementAt(j));				
			}
		}
		return v;
	}

	public void readTables(myBufferedReader br) throws IOException {
		String ligne;
		myTable table= null;
		while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("ENDSEC")) {
			if(ligne.toUpperCase().trim().equals("TABLE")) {
				table = new myTable(br, this);
				_myTables.add(table);
			}
		}
	}

	public void readBlocks(myBufferedReader br) throws IOException {
		String ligne;
		myBlock b = null;

		while ((ligne = br.readLine()) != null && !ligne.trim().equalsIgnoreCase("ENDSEC")){
			ligne=ligne.trim();
			if(ligne.equalsIgnoreCase("BLOCK")) {
				b = myBlock.read(br, this);
				_myBlocks.add(b);
			}
		}
	}

	public void readEntities(myBufferedReader br) throws IOException {
		String ligne;
		while ((ligne = br.readLine()) != null && !ligne.equals("ENDSEC")){
			addEntity(br, ligne , true);
		}
	}

	public myEntity addEntity(myBufferedReader br, String Element, boolean addToLayer) throws IOException {
		myEntity o = null;
		Element = Element.trim();
		
		if(Element.equals("0"))
			Element = br.readLine();
		
		if(Element.equals("LINE")){
			o = myLine.read(br, this); 
		} else if(Element.equals("ARC")){
			o = myArc.read(br, this);
		} else if(Element.equals("CIRCLE")){
			o = myCircle.read(br, this);
		} else if(Element.equals("POLYLINE")){
			o = myPolyline.read(br, this);
		} else if(Element.equals("LWPOLYLINE")){
			o = myLwPolyline.read(br, this);
		} else if(Element.equals("POINT")){
			o = myPoint.read(br, this);
		} else if(Element.equals("SOLID")){
			o = mySolid.read(br, this);
		} else if(Element.equals("TEXT")){
			o = myText.read(br, this);
		} else if(Element.equals("MTEXT")){
			o = myText.read(br, this);
		} else if(Element.equals("INSERT")){
			o = myInsert.read(br, this); 
		} else if(Element.equals("DIMENSION")){
			o = myDimension.read(br, this);      
		} else if(Element.equals("TRACE")){
			o = myTrace.read(br, this);      
		} else if(Element.equals("ELLIPSE")){
			o = myEllipse.read(br, this);      
		} else if(Element.equals("ATTDEF") || Element.equals("ENDBLK")){
			o = new myPoint(); 
			addToLayer = false;
		} else {
			myLog.writeLog(DXF_Loader.res.getString("myUnivers.87") + Element);
		}

		if(o!=null && o._refLayer!=null && addToLayer)
			o._refLayer._myEnt.addElement(o);

		return o;	
	}

	public void writeTables(FileWriter out) throws IOException {
		out.write("SECTION\n");
		out.write("2\n");
		out.write("TABLES\n");
		out.write("0\n");
		for (int i=0;i<_myTables.size();i++) {
			out.write("TABLE\n");
			out.write("2\n");
			for (int j=0;j<_myTables.elementAt(i)._myLineTypes.size();j++) {
				_myTables.elementAt(i)._myLineTypes.elementAt(j).write(out);
			}
			for (int j=0;j<_myTables.elementAt(i)._myLayers.size();j++) {
				_myTables.elementAt(i)._myLayers.elementAt(j).write(out);
			}
			out.write("ENDTAB\n");
			out.write("0\n");
		}
		out.write("ENDSEC\n");
		out.write("0\n");
	}

	public void writeHeader(FileWriter out) throws IOException, NullPointerException {
		boolean bElt = false;
		out.write("0\n");
		out.write("SECTION\n");
		out.write("2\n");
		out.write("HEADER\n");
		out.write("9\n");

		if(!_header._ACADVER.equalsIgnoreCase("")){
			bElt = true;
			out.write("$ACADVER\n");
			out.write("1\n");
			out.write(_header._ACADVER+"\n");
		}

		if(_header._EXTMIN!=null){
			if(bElt)
				out.write("9\n");
			else
				bElt = true;
			out.write("$EXTMIN\n");
			out.write("10\n");
			out.write(_header._EXTMIN.X()+"\n");
			out.write("20\n");
			out.write(_header._EXTMIN.Y()+"\n");               
		}
		if(_header._EXTMAX!=null){
			if(bElt)
				out.write("9\n");
			else
				bElt = true;
			out.write("$EXTMAX\n");
			out.write("10\n");
			out.write(_header._EXTMAX.X()+"\n");
			out.write("20\n");
			out.write(_header._EXTMAX.Y()+"\n");
		}
		if(_header._LIMMIN!=null){
			if(bElt)
				out.write("9\n");
			else
				bElt = true;
			out.write("$LIMMIN\n");
			out.write("10\n");
			out.write(_header._LIMMIN.X()+"\n");
			out.write("20\n");
			out.write(_header._LIMMIN.Y()+"\n");
		}
		if(_header._LIMMAX!=null){
			if(bElt)
				out.write("9\n");
			else
				bElt = true;
			out.write("$LIMMAX\n");
			out.write("10\n");
			out.write(_header._LIMMAX.X()+"\n");
			out.write("20\n");
			out.write(_header._LIMMAX.Y()+"\n");
		}

		out.write("0\n");
		out.write("ENDSEC\n");
		out.write("0\n");
	}

	public void writeBlocks(FileWriter out) throws IOException {
		out.write("SECTION\n");
		out.write("2\n");
		out.write("BLOCKS\n");
		out.write("0\n");
		for (int i=0;i<_myBlocks.size();i++) {
			_myBlocks.elementAt(i).write(out);
		}
		out.write("ENDSEC\n");
		out.write("0\n");
	}
	public void writeEntities(FileWriter out) throws IOException {
		out.write("SECTION\n");
		out.write("2\n");
		out.write("ENTITIES\n");
		out.write("0\n");
		for (int i=0;i<_myTables.size();i++) {
			for (int j=0;j<_myTables.elementAt(i)._myLayers.size();j++) {
				for (int k=0;k<((myLayer)((myTable)_myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.size();k++) {
					_myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k).write(out);
				}
			}
		}
		out.write("ENDSEC\n");
		out.write("0\n");
	}

	public DefaultMutableTreeNode getNode(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.RATIO,String.valueOf(myCoord.Ratio))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.MOVE_X,String.valueOf(myCoord.decalageX))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.MOVE_Y,String.valueOf(myCoord.decalageY))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.RATIOSTEP,String.valueOf(myCoord.ratioStep))));
		
		if(currLayer ==null)
			root.add(new DefaultMutableTreeNode(new myLabel(myLabel.CUR_LAYER,DXF_Loader.res.getString("myUnivers.80"))));
		else
			root.add(new DefaultMutableTreeNode(new myLabel(myLabel.CUR_LAYER,currLayer._nom)));
		
		if(currBlock ==null)
			root.add(new DefaultMutableTreeNode(new myLabel(myLabel.CUR_BLOCK,DXF_Loader.res.getString("myUnivers.81"))));
		else
			root.add(new DefaultMutableTreeNode(new myLabel(myLabel.CUR_BLOCK,currBlock._name)));
		
		
		root.add(_header.getNode());
		root.add(getMenuTables());
		root.add(getMenuBlocks());

		return root;
	}

	public DefaultMutableTreeNode getMenuTables(){

		DefaultMutableTreeNode root = null, node = null;

		root = new DefaultMutableTreeNode(new myLabel(myLabel.LST_TABLE, ToStringTables()));
		for(int i= 0; i< _myTables.size(); i++){
			node = _myTables.get(i).getNode();
			root.insert(node, root.getChildCount());
		}

		return root;
	}
	public String ToStringBlocks(){
		return _myBlocks.size() + DXF_Loader.res.getString("myUnivers.82");
	}

	public String ToStringTables(){
		return _myTables.size() + DXF_Loader.res.getString("myUnivers.83");
	}

	public DefaultMutableTreeNode getMenuBlocks(){

		DefaultMutableTreeNode root = null, node = null;

		root = new DefaultMutableTreeNode(new myLabel(myLabel.LST_BLOCK, ToStringBlocks()));

		for(int i= 0; i< _myBlocks.size(); i++){
			node = _myBlocks.get(i).getNode();
			root.insert(node, root.getChildCount());
		}

		return root;
	}
	public String toString(){
		if (_filename == null)
			return DXF_Loader.res.getString("fileNotSaved");
		File f = new File(_filename);
		String theFile = f.getName();
		int idx = theFile.toLowerCase().lastIndexOf(".dxf");
		if(idx > 0)    			
			return theFile.substring(0, idx);
		else
			return theFile;
	}

	public Object getNewLabel(DefaultMutableTreeNode node, Object newValue) {
		myLabel l = null;

		if(node.getParent() == null || ((DefaultMutableTreeNode) node.getParent()).getUserObject() == null)
			return null;

		if(((myLabel)node.getUserObject())._code.equals(myLabel.RATIO)){
			myCoord.Ratio = Double.parseDouble(newValue.toString());
			if (myCoord.Ratio<=0) {
				myCoord.Ratio=0.0001;
			}
			l = myCoord.getLabelRatio();
			myHistory.saveHistory(true);
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.MOVE_X)){
			myCoord.decalageX = Double.parseDouble(newValue.toString());
			l = myCoord.getLabelX();
			myHistory.saveHistory(true);
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.MOVE_Y)){
			myCoord.decalageY = -Double.parseDouble(newValue.toString())+0;
			l = myCoord.getLabelX();
			myHistory.saveHistory(true);
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.RATIOSTEP)){
			myCoord.ratioStep = Double.parseDouble(newValue.toString());
			if (myCoord.ratioStep<=0)
				myCoord.ratioStep=0.5;
			l = new myLabel(myLabel.RATIOSTEP, newValue.toString());
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.CUR_LAYER)){
			currLayer = findLayer(newValue.toString());
			if(currLayer == null){
				myLayer lay = new myLayer(DXF_Loader.res.getString("myUnivers.86"),DXF_Color.getDefaultColorIndex());
				currLayer = lay;
				_myTables.get(0)._myLayers.add(lay);
			}
			l = new myLabel(myLabel.CUR_LAYER, currLayer._nom);
		}
		return l;
	}

	public void addRefBlockForUpdate(myEntity obj){
		_entForUpdate.addElement(obj);
	}
	
	public void updateRefBlock(){
		myEntity obj = null;
		for (int i=0; i<_entForUpdate.size();i++){
			obj = _entForUpdate.get(i); 
			if(obj instanceof myBlockReference){
				changeBlock((myBlockReference) obj, ((myBlockReference) obj)._blockName);
			}
		}			
		_entForUpdate.removeAllElements();
	}
	
	public void changeBlock(myBlockReference obj, String nom) {
		myBlock b;
		if(obj == null)
			return;
		
		if(obj._refBlock == null){
			b = findBlock(nom);
			if (b!=null) {
				if(nom.equalsIgnoreCase(b._name)){
					obj._blockName = nom;
					obj._refBlock  = b;
				}
			}
		}
		else{
			if(!nom.equalsIgnoreCase(obj._refBlock._name)){
				b =  findBlock(nom);
				if(nom.equalsIgnoreCase(b._name)){
					obj._blockName = nom;
					obj._refBlock  = b;
				}
			}
		}
	}


	public double getMinX() {
		double min=Double.MAX_VALUE;
		for (int i=0;i<_myTables.size();i++) {
			for (int j=0;j<_myTables.elementAt(i)._myLayers.size();j++) {
				for (int k=0;k<_myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
					min = _myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k).getMinX(min);
				}
			}
		}
		if (min==Double.MAX_VALUE)
			return 0;
		else
			return min;		
	}
	
	public double getMaxX() {
		double max=Double.MIN_VALUE;
		for (int i=0;i<_myTables.size();i++) {
			for (int j=0;j<_myTables.elementAt(i)._myLayers.size();j++) {
				for (int k=0;k<_myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
					max = _myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k).getMaxX(max);
				}
			}
		}
		if (max==Double.MIN_VALUE)
			return 0;
		else
			return max;		
	}
	
	public double getMinY() {
		double min=Double.MAX_VALUE;
		for (int i=0;i<_myTables.size();i++) {
			for (int j=0;j<_myTables.elementAt(i)._myLayers.size();j++) {
				for (int k=0;k<_myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
					min = _myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k).getMinY(min);
				}
			}
		}
		if (min==Double.MAX_VALUE)
			return 0;
		else
		return min;		
	}
	
	public double getMaxY() {
		double max=Double.MIN_VALUE;
		for (int i=0;i<_myTables.size();i++) {
			for (int j=0;j<_myTables.elementAt(i)._myLayers.size();j++) {
				for (int k=0;k<_myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
					max = _myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k).getMaxY(max);
				}
			}
		}
		if (max==Double.MIN_VALUE)
			return 0;
		else
			return max;		
	}

	public double getXspan() {	
		double min=getMinX();
		double ret = getMaxX()-min;
		this.lastView.setRect(min, -1, ret, -1);
		if (ret==0)
			return DXF_Loader._mc.getWidth()-10;
		else
			return ret;
				
	}

	public double getYspan() {	
		double min=getMinY();	
		double ret = getMaxY()-min;
		this.lastView.setRect(lastView.getX(), min, lastView.getWidth(), ret);
		if (ret==0)
			return DXF_Loader._mc.getHeight()-10;
		else
			return ret;
	}

	public double getMaxSpan() {
		double xSpan=getXspan();
		double ySpan=getYspan();
		if (xSpan>ySpan) {
			strayX=5;
			strayY=150;
			return xSpan;
		} else {
			strayX=150;
			strayY=5;
			return ySpan;
		}
	}
	
	
}
