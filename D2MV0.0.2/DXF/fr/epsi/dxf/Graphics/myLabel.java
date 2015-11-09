package fr.epsi.dxf.Graphics;

import fr.epsi.dxf.DXF_Loader;

public class myLabel {

	public String _code ="";
	protected Object _value="";
	
	// OBJECTS
	public final static String X 			= DXF_Loader.res.getString("myLabel.2")			;
	public final static String Y 			= DXF_Loader.res.getString("myLabel.3")			;
	public final static String XA 			= DXF_Loader.res.getString("myLabel.4")			;
	public final static String XB 			= DXF_Loader.res.getString("myLabel.5")			;
	public final static String XC 			= DXF_Loader.res.getString("myLabel.6")			;
	public final static String XD 			= DXF_Loader.res.getString("myLabel.7")			;
	public final static String YA 			= DXF_Loader.res.getString("myLabel.8")			;
	public final static String YB			= DXF_Loader.res.getString("myLabel.9")			;
	public final static String YC 			= DXF_Loader.res.getString("myLabel.10")			;
	public final static String YD 			= DXF_Loader.res.getString("myLabel.11")			;
	public final static String ANGLE1 		= DXF_Loader.res.getString("myLabel.12")		;
	public final static String ANGLE2 		= DXF_Loader.res.getString("myLabel.13")		;
	public final static String RADIUS		= DXF_Loader.res.getString("myLabel.14")		;
	public final static String BULGE 		= DXF_Loader.res.getString("myLabel.15")		;
	public final static String COLOR 		= DXF_Loader.res.getString("myLabel.16")		;
	public final static String TYPE_LIGNE 	= DXF_Loader.res.getString("myLabel.17")	;
	public final static String ROTATION 	= DXF_Loader.res.getString("myLabel.18")	;
	public final static String ALIGN		= DXF_Loader.res.getString("myLabel.19")		;
	public final static String HEIGHT 		= DXF_Loader.res.getString("myLabel.20")		;
	public final static String THICKNESS	= DXF_Loader.res.getString("myLabel.21")	;
	public final static String VALUE 		= DXF_Loader.res.getString("myLabel.22")		;
	public final static String FLAG 		= DXF_Loader.res.getString("myLabel.23")		;
	public final static String STYLE		= DXF_Loader.res.getString("myLabel.24")		;
	public final static String VISISBILITY	= DXF_Loader.res.getString("myLabel.25")	;
	public final static String E_RATIO		= DXF_Loader.res.getString("myLabel.26");
	public final static String START		= DXF_Loader.res.getString("myLabel.27")		;
	public final static String END			= DXF_Loader.res.getString("myLabel.28")			;
	public final static String LENGTH		= DXF_Loader.res.getString("myLabel.29")		;
	public final static String COUNT		= DXF_Loader.res.getString("myLabel.30")		;
	public final static String ZOOM_FACTOR	= DXF_Loader.res.getString("myLabel.31")	;

	// HEADER
	public final static String VERSION 		= DXF_Loader.res.getString("myLabel.32")		;
	public final static String FILLMODE 	= DXF_Loader.res.getString("myLabel.33")	;
	public final static String EXTMINX 		= DXF_Loader.res.getString("myLabel.34")		;
	public final static String EXTMINY 		= DXF_Loader.res.getString("myLabel.35")		;
	public final static String EXTMAXX 		= DXF_Loader.res.getString("myLabel.36")		;
	public final static String EXTMAXY 		= DXF_Loader.res.getString("myLabel.37")		;
	public final static String LIMMINX 		= DXF_Loader.res.getString("myLabel.38")		;
	public final static String LIMMINY 		= DXF_Loader.res.getString("myLabel.39")		;
	public final static String LIMMAXX 		= DXF_Loader.res.getString("myLabel.40")		;
	public final static String LIMMAXY		= DXF_Loader.res.getString("myLabel.41")		;
	
	public final static String RATIO 		= DXF_Loader.res.getString("myLabel.42")		;
	public final static String RATIOSTEP 	= DXF_Loader.res.getString("myLabel.43")	;
	public final static String MOVE_X 		= DXF_Loader.res.getString("myLabel.44")	;
	public final static String MOVE_Y 		= DXF_Loader.res.getString("myLabel.45")	;
	
	public final static String LST_TABLE 	= DXF_Loader.res.getString("myLabel.46")	;
	public final static String LST_BLOCK 	= DXF_Loader.res.getString("myLabel.47")	;
	public final static String BLOCK 		= DXF_Loader.res.getString("myLabel.48")		;
	public static final String CUR_LAYER 	= DXF_Loader.res.getString("myLabel.49");
	public static final String CUR_BLOCK 	= DXF_Loader.res.getString("myLabel.50");
	public static final String SPACING 		= DXF_Loader.res.getString("myLabel.51");
	
	public myLabel(String code, String value){
		_code	= code	;
		_value 	= value	;		
	}
	
	public String toString(){
		return _value.toString();		
	}	
}
