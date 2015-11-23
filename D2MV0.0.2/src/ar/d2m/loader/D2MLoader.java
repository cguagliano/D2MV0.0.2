package ar.d2m.loader;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ar.d2m.graphicalinterface.MillingFeaturesSelection;
import ar.d2m.graphicalinterface.MillingFeaturesSettings;
import fr.epsi.dxf.DXF_Loader;

public class D2MLoader{
	
	private static final long serialVersionUID = 1L;
	public static DXF_Loader DXF;
	public static MillingFeaturesSelection selection;
	public static MillingFeaturesSettings settings;
	public static boolean changeColor=false;
	public static int selected;
	
	public static void main(final String[] args){
		DXF=new DXF_Loader();
		DXF.setVisible(false);
		DXF.toolBar.add(new JButton("Maquina"));
		DXF.frame.setTitle("DXF2Machine - Transformando Dibujos en Codigo Maquina");
		JTabbedPane SetUp= new JTabbedPane();
	//	SetUp.setLayout(new GridLayout(0,2));
		settings= new MillingFeaturesSettings();
		selection=new MillingFeaturesSelection(DXF);
		SetUp.add(selection,"Herramientas");
		SetUp.add(settings,"Configuracion");
		DXF.GCode.setSize(200,200);
		DXF.GCode.add(SetUp);
		DXF.setVisible(true);
	}

}