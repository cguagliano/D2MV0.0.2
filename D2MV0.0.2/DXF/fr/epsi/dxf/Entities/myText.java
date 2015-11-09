package fr.epsi.dxf.Entities;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
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

public class myText extends myEntity{

	private static final long serialVersionUID = 438369023987897247L;

	public myPoint 	_point		= new myPoint(); // 10 ,20
	protected String 	_value 		= ""; // 1
	protected double 	_height		= 0	; // 40
	protected double 	_rotation 	= 0	; // 50
	protected int	 	_align		= 0	; // 72
	protected String 	_style		= ""; // 7
	protected double 	_angle		= 0 ; // 51
	protected double 	_zoomfactor	= 1 ; // 41
	protected Rectangle2D.Double _r = new Rectangle2D.Double();

	public myText(double x, double y,String value, double rotation, double thickness, double height, int align, String style,int c, myLayer l, double angle, double zoomFactor, int visibility, myLineType lineType){
		super(c, l, visibility, lineType, thickness);
		_point = new myPoint(x, y, c, l,visibility,thickness);
		_value 		= value		; 
		_rotation 	= rotation	; 
		_height		= height	; 
		_align		= align		; 
		_style		= style		;
		_angle		= angle		;
		_zoomfactor	= zoomFactor;
		myStats.nbText+=1;
	}

	public myText(){
		super(-1, null, 0, null, myTable.defaultThickness);		
	}

	public myText(myText text) {
		super(text._color, text._refLayer, 0, text._lineType, text._thickness);
		_point = new myPoint(text._point.X(), text._point.Y(), text._color, text._refLayer,0,text._thickness);
		_value 		= text._value		; 
		_rotation 	= text._rotation	; 
		_height		= text._height	; 
		_align		= text._align		; 
		_style		= text._style		;
		_angle		= text._angle		;
		_zoomfactor	= text._zoomfactor;
	}

	public void setVal(String s) {
		_value=s;
	}

	public String getVal() {
		return _value;
	}
	
	public void appendVal(char c) {
		_value+=c;
	}

	public void delChar() {
		_value = _value.substring(0, _value.length()-1);
	}
	
	public void draw(Graphics g) {

		_r.setFrame(_point._point.x, _point._point.y, _value.length()*3, _height);
	
		Font f;
		Graphics2D g2 = (Graphics2D) g;
		
		Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		map.put(TextAttribute.FONT,Font.PLAIN);
		map.put(TextAttribute.WEIGHT,getWeight(_zoomfactor));
		map.put(TextAttribute.WIDTH,getWidth(_zoomfactor));
		map.put(TextAttribute.SIZE,_height*myCoord.Ratio);
		map.put(TextAttribute.JUSTIFICATION,_align);
		map.put(TextAttribute.POSTURE,Math.tan(Math.PI*_angle/180.0));

		switch(_align){
		case 0:
			map.put(TextAttribute.JUSTIFICATION,1.0);
			break;
		case 1:
			map.put(TextAttribute.JUSTIFICATION,0.5);
			break;
		default:
			map.put(TextAttribute.JUSTIFICATION,0.0);
		}

		try {
			f = new Font(map);
			g2.setFont(f);
		} catch (Exception e) { }		

		super.draw(g2);
		AffineTransform aT = g2.getTransform();
	   	AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(-_rotation), myCoord.dxfToJava_X(_point.X()), myCoord.dxfToJava_Y(_point.Y()));
	   	g2.transform(rotate);
	   	g2.drawString(
				_value+" ", 
				(float) myCoord.dxfToJava_X(_point.X()),
				(float) myCoord.dxfToJava_Y(_point.Y())
		);
	   	g2.setTransform(aT);
	}

	private double getWeight(double zoomfactor) {
		
		double value=TextAttribute.WEIGHT_REGULAR;
		
		if(zoomfactor <= 0.5){
			value=TextAttribute.WEIGHT_EXTRA_LIGHT;
		}else if(zoomfactor <= 0.75){
			value=TextAttribute.WEIGHT_LIGHT ;
		}else if(zoomfactor <= 0.875){
			value=TextAttribute.WEIGHT_DEMILIGHT;
		}else if(zoomfactor <= 1){
			value=TextAttribute.WEIGHT_REGULAR ;
		}else if(zoomfactor <= 1.25){
			value=TextAttribute.WEIGHT_SEMIBOLD ;
		}else if(zoomfactor <= 1.5){
			value=TextAttribute.WEIGHT_MEDIUM ;
		}else if(zoomfactor <= 1.75){
			value=TextAttribute.WEIGHT_DEMIBOLD ;
		}else if(zoomfactor <= 2.0){
			value=TextAttribute.WEIGHT_BOLD  ;
		}else if(zoomfactor <= 2.25){
			value=TextAttribute.WEIGHT_HEAVY  ;
		}else if(zoomfactor <= 2.50){
			value=TextAttribute.WEIGHT_EXTRABOLD ;
		}else {
			value=TextAttribute.WEIGHT_ULTRABOLD ;
		}
		
		return value;
	}
	private double getWidth(double zoomfactor) {
		
		double value=TextAttribute.WIDTH_REGULAR;
		
		if(zoomfactor <= 0.75){
			value=TextAttribute.WIDTH_CONDENSED;
		}else if(zoomfactor <= 0.875){
			value=TextAttribute.WIDTH_SEMI_CONDENSED ;
		}else if(zoomfactor <= 1.0){
			value=TextAttribute.WIDTH_REGULAR;
		}else if(zoomfactor <= 1.25){
			value=TextAttribute.WIDTH_SEMI_EXTENDED;
		}else {
			value=TextAttribute.WIDTH_EXTENDED ;
		}
		
		return value;
	}


	public static myText read(myBufferedReader br, myUnivers univers) throws IOException {
		myLayer l = null;
		String 	ligne		="", 
				ligne_temp	="",
				value		="",
				style		="STANDARD"
		;
		int visibility=0,align=0, c=-1;
		myLineType lineType = null;
		double 	
			x=0, 
			y=0			,
			angle=0		, 
			rotation=0	,
			zoomfactor=1,
			thickness	= myTable.defaultThickness,
			height=0				
		;	

		myLog.writeLog(">> myText");
		while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();

			if (ligne_temp.equalsIgnoreCase("10"))
			{	
				x = Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("20"))
			{	
				y = Double.parseDouble(ligne);				
			}
			else if (ligne_temp.equalsIgnoreCase("1"))
			{	
				value = ligne;
			}
			else if (ligne_temp.equalsIgnoreCase("50"))
			{	
				rotation=Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("39"))
			{	
				thickness=(float)Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("40"))
			{	
				height=Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("51"))
			{	
				angle=Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("41"))
			{	
				zoomfactor=Double.parseDouble(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("72"))
			{	
				align=Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("8"))
			{	
				l = univers.findLayer(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("62"))
			{
				c = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("7"))
			{
				style = ligne;
			}
			else if (ligne_temp.equalsIgnoreCase("60"))
			{
				visibility = Integer.parseInt(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("6"))
			{
				lineType = univers.findLType(ligne);
			}
			else{
				myLog.writeLog("Unknown :" + ligne_temp + "("+ ligne +")");				
			}
		}		
		return new myText(x, y, value, rotation, thickness, height,align, style, c, l, angle, zoomfactor, visibility,lineType);
	}
	public void write(FileWriter out) throws IOException{
		out.write("TEXT\n");
		super.writeCommon(out);
		out.write("10\n");
		out.write(_point.X()+"\n");
		out.write("20\n");
		out.write(_point.Y()+"\n");
		out.write("1\n");
		out.write(_value+"\n");
		out.write("50\n");
		out.write(_rotation+"\n");
		out.write("39\n");
		out.write(_thickness+"\n");
		out.write("40\n");
		out.write(_height+"\n");
		out.write("72\n");
		out.write(_align+"\n");
		out.write("41\n");
		out.write(_zoomfactor+"\n");
		out.write("51\n");
		out.write(_angle+"\n");
		out.write("7\n");
		out.write(_style+"\n");
		out.write("0\n");
	}
	public String toString(){
		return DXF_Loader.res.getString("myText.48") + _value + DXF_Loader.res.getString("myText.49");
	}

	public DefaultMutableTreeNode getNode()
	{
		DefaultMutableTreeNode root= new DefaultMutableTreeNode(this);
		
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.VALUE, _value)));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.X, String.valueOf(_point.X()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.Y, String.valueOf(_point.Y()))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.ALIGN, String.valueOf(_align))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.HEIGHT, String.valueOf(_height))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.ROTATION, String.valueOf(_rotation))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.STYLE, String.valueOf(_style))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.THICKNESS, String.valueOf(_thickness))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.ANGLE1, String.valueOf(_angle))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.ZOOM_FACTOR, String.valueOf(_zoomfactor))));
		
		Vector<DefaultMutableTreeNode> v = super.getCommonNode();
		for(int i=0; i< v.size(); i++)
			root.add(v.get(i));

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
		else if(code.equals(myLabel.THICKNESS)){
			_thickness = (float)Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.THICKNESS, newValue.toString());
		}
		else if(code.equals(myLabel.ALIGN)){
			_align = Integer.parseInt(newValue.toString());
			l = new myLabel(myLabel.ALIGN, newValue.toString());
		}
		else if(code.equals(myLabel.HEIGHT)){
			_height =Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.HEIGHT, newValue.toString());
		}
		else if(code.equals(myLabel.ROTATION)){
			_rotation = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.ROTATION, newValue.toString());
		}
		else if(code.equals(myLabel.STYLE)){
			_style = newValue.toString();
			l = new myLabel(myLabel.STYLE, newValue.toString());
		}
		else if(code.equals(myLabel.VALUE)){
			_value = newValue.toString();
			l = new myLabel(myLabel.VALUE, newValue.toString());
		}
		else if(code.equals(myLabel.ANGLE1)){
			_angle = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.ANGLE1, newValue.toString());
		}else if(code.equals(myLabel.ZOOM_FACTOR)){
			_zoomfactor = Double.parseDouble(newValue.toString());
			l = new myLabel(myLabel.ZOOM_FACTOR, newValue.toString());
		}else {
			l = super.getCommonLabel(code, newValue);
		}
		return l;			
	}

	public double getMinX(double min) {
		if ((_point.X())<min)
			return _point.X();			
		return min;
	}

	public double getMaxX(double max) {
		if ((_point.X())>max)
			return _point.X();			
		return max;
	}
	
	public double getMinY(double min) {
		if ((_point.Y())<min)
			return _point.Y();			
		return min;
	}

	public double getMaxY(double max) {
		if ((_point.Y())>max)
			return _point.Y();			
		return max;
	}

	@Override
	public Rectangle2D.Double getSelectedEntity() {
		_r.setFrame(myCoord.dxfToJava_X(_point.X()),myCoord.dxfToJava_Y(_point.Y()), _value.length()*3, _height);
		return _r;
	}

	public void translate(double x, double y) {
		this._point._point.x-=myCoord.getTransalation(x);
		this._point._point.y+=myCoord.getTransalation(y);
	}
}
