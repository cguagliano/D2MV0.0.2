package ar.d2m.loader;

import java.awt.Canvas;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ar.d2m.graphicalinterface.MillingFeaturesSelection;
import ar.d2m.graphicalinterface.MillingGraphicalInterface;
import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.Entities.myArc;
import fr.epsi.dxf.Entities.myBlockReference;
import fr.epsi.dxf.Entities.myCircle;
import fr.epsi.dxf.Entities.myEntity;
import fr.epsi.dxf.Entities.myLine;
import fr.epsi.dxf.Entities.myPoint;
import fr.epsi.dxf.Entities.myPolyline;
import fr.epsi.dxf.Entities.mySolid;
import fr.epsi.dxf.Entities.myText;
import fr.epsi.dxf.Graphics.DXF_Color;
import fr.epsi.dxf.Graphics.myCoord;
import fr.epsi.dxf.Graphics.myJColorChooser;

public class D2MLoader {
	
	public static DXF_Loader DXF;
	public static MillingFeaturesSelection selection;
	public static boolean changeColor=false;
	public static int selected;
	
	public static void main(final String[] args){
		DXF=new DXF_Loader();
		DXF.setVisible(false);
		DXF.toolBar.add(new JButton("Maquina"));
		DXF.frame.setTitle("DXF2Machine - Transformando Dibujos en Codigo Maquina");
		JTabbedPane SetUp= new JTabbedPane();
		//SetUp.setLayout(new GridLayout(1,1));
		JPanel settings= new JPanel();
		selection=new MillingFeaturesSelection(DXF);
		SetUp.add(selection,"Herramientas");
		SetUp.add(settings,"Configuracion");
		DXF.GCode.setSize(200,200);
		DXF.GCode.add(SetUp);
		DXF.setVisible(true);
	}
}





	
	