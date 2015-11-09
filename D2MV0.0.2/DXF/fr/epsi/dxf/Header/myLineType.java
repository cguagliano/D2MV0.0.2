package fr.epsi.dxf.Header;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.Entities.myBufferedReader;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Graphics.myLog;

public class myLineType {

	private  float 	_motif[] 			= parseTxt("_")	;
	
	public String 	_name 				= DXF_Loader.res.getString("myLineType.0");	// 2
	public String 	_value				= "";					// 3
	public float 	_length 			= 0;					// 40
	public float 	_count 				= 0;					// 73
	public Vector<Float>	_spacing	= new Vector<Float>();	// 49

	//http://java.sun.com/developer/JDCTechTips/2003/tt0520.html
	public myLineType(){
				
	}
	
	public myLineType(String nom, String value, float length, float count, Vector<Float> spacing){
		_name 	= nom	;
		_value	= value	;
		_length = length;
		_count	= count	;
		
		if(spacing != null)
			_spacing= spacing;
		
		_motif = parseDXF();
		myStats.nbLineType+=1;
		
	}

	
	public static myLineType read(myBufferedReader br) throws IOException {
		String ligne, ligne_temp, value="", name="";
		Vector<Float> spacing	= new Vector<Float>();
		float count=0, length=0;
		myLog.writeLog("> new LType");
		
		while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0"))
		{
			ligne_temp = ligne;
			ligne = br.readLine();
			
			if (ligne_temp.equalsIgnoreCase("2")){	
				name=ligne;
			}
			else if (ligne_temp.equalsIgnoreCase("3")){
				value = ligne;
			}
			else if (ligne_temp.equalsIgnoreCase("73")){
				count = Float.parseFloat(ligne);
			}
			else if (ligne_temp.equalsIgnoreCase("40")){
				length = Float.parseFloat(ligne);;
			}
			else if (ligne_temp.equalsIgnoreCase("49")){
				spacing.add(Float.parseFloat(ligne));
			}
			else{
				myLog.writeLog("Unknown :" + ligne_temp + " ("+ ligne +")");				
			}
		}
		
		if(value.equals("") && name.equals(""))
			return null;
		else
			return new myLineType(name, value, length, count, spacing);	
	}

	public String toString() {
		return _value + " (" + _name + ")";
	}
	
	public DefaultMutableTreeNode getNode() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
		
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.VALUE,_name)));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.TYPE_LIGNE,_value)));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.COUNT,String.valueOf(_count))));
		root.add(new DefaultMutableTreeNode(new myLabel(myLabel.LENGTH,String.valueOf(_length))));
		
		for(int i=0; i<_spacing.size(); i++)
			root.add(new DefaultMutableTreeNode(new myLabel(myLabel.SPACING + i,String.valueOf(_spacing.get(i)))));
				
		return root;
	}

	public Object getNewLabel(DefaultMutableTreeNode node, Object newValue) {
		
		
		myLabel l = null;
		String code = ((myLabel)node.getUserObject())._code;
		
		if(code.equals("") || node.getParent() == null || ((DefaultMutableTreeNode) node.getParent()).getUserObject() == null)
			return null;
		
		
		if(code.substring(0, code.length() -1).equals(myLabel.SPACING)){
			_spacing.setElementAt(Float.parseFloat(newValue.toString()), Integer.parseInt(code.substring(code.length() -1, code.length())));
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.TYPE_LIGNE)){
			_value = newValue.toString();
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.VALUE)){
			_name = newValue.toString();
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.LENGTH)){
			_length = Float.parseFloat(newValue.toString());
		}
		else if(((myLabel)node.getUserObject())._code.equals(myLabel.COUNT)){
			_count = Float.parseFloat(newValue.toString());
		}
		
		l = new myLabel(code, newValue.toString());
		
		return l;
	}

	public void write(FileWriter out) throws IOException {
		out.write("LTYPE\n");
		out.write("2\n");
		out.write(_name+"\n");
		out.write("3\n");
		out.write(_value+"\n");
		out.write("73\n");
		out.write(_length+"\n");		
		out.write("40\n");
		out.write(_count+"\n");
		
		for(int i=0; i<_spacing.size(); i++){
			out.write("49\n");
			out.write(_spacing.get(i)+"\n");
		}
			
		out.write("0\n");
	}
	
	public static float[] parseTxt(String s) {
		boolean no=false;
		boolean incr=false;
		s=s.trim();
		if (s.length()%2==1)
			s+=" ";
		char[] strChr = new char[s.length()];
		strChr=s.toCharArray();
		int end=strChr.length-(strChr.length%2);
		
		float[] floatLine = new float[end*2];
		int j=0;
		for (int i=0;i<floatLine.length;i++) {			
			if (strChr[j]==' ') {
				floatLine[i]=0.0f;
				incr=true;
			} else if (strChr[j]=='.')
				floatLine[i]=1.0f;
			else if (strChr[j]=='-')
				floatLine[i]=5.0f;
			else if (strChr[j]=='_') {
				floatLine[i]=10.0f;
				no=true;
			} else
				floatLine[i]=3.0f;
			i++;
			if (no)
				floatLine[i]=0.0f;
			else if (incr)
				floatLine[i]=10.0f;
			else				
				floatLine[i]=3.0f;		
			j++;
			no=false;
			incr=false;
		}		
		if (floatLine.length>0)
			return floatLine;
		else 
			return new float[]{1.0f, 0.0f};
	}
	
	public float[] parseDXF() {
		if (_count!=this._spacing.size())
			return myTable.defautMotif;
		
		float[] ret = new float[(int)_count];
		
		for (int i=0;i<_count;i++){
			ret[i]=((Math.abs(_spacing.elementAt(i))*100)/this._length)/10;
		}	
		
		if(ret.length == 0)
			ret = myTable.defautMotif;
			
		return ret;
	}

	public float[] getMotif() {
		return _motif;
	}

}
