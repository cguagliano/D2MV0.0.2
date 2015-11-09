package fr.epsi.dxf;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.Graphics.myCanvas;
import fr.epsi.dxf.Graphics.myCoord;
import fr.epsi.dxf.Graphics.myHistory;
import fr.epsi.dxf.Graphics.myJColorChooser;
import fr.epsi.dxf.Graphics.myJMenu;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Graphics.myToolBar;
import fr.epsi.dxf.Graphics.FileDrag.FileDrop;
import fr.epsi.dxf.Graphics.TreeView.myJTree;
import fr.epsi.dxf.Header.myHeader;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myStats;
import fr.epsi.dxf.Header.myTable;

public class DXF_Loader extends JPanel implements ActionListener{
	public static ResourceBundle res = ResourceBundle.getBundle("myDXF.i18n.Ressources_EN");
	
	public static final long serialVersionUID = 1L;
	public static JTabbedPane tabPane;
	
	public myToolBar _typeOutil;
	public JComboBox _comboLineType;
	public myJColorChooser _jcc;
	public JSplitPane _sp;
	public JScrollPane _dxfScrollPane;
	public JScrollPane _toolScrollPane;
	public myJTree tree;
	public myUnivers _u;
	public myJMenu treeMenu;
	public JFrame frame;
	public String lastOpenDXF="";
	public String lastSaveDXF="";
	public String lastSaveAsImg="";
	protected boolean init=false;

	public static DefaultMutableTreeNode defaultLayer;
	public static JTextField[] textFields;
	public static JButton back;
	public static JButton fwd;
	public static TextArea logText ;
	public static myCanvas _mc;

	public static boolean checkLineType=false;

	public static Locale locale;

	public Label txtBlocks 		= new Label();
	public Label txtTables 		= new Label();
	public Label txtLayers		= new Label();
	public Label txtLine 		= new Label();
	public Label txtPoint	 	= new Label();
	public Label txtPolyline 	= new Label();
	public Label txtLwPolyline 	= new Label();
	public Label txtArc 		= new Label();
	public Label txtCircle 		= new Label();
	public Label txtDimension	= new Label();
	public Label txtSolid 		= new Label();
	public Label txtTrace 		= new Label();
	public Label txtEllipse		= new Label();
	public Label txtMText		= new Label();
	public Label txtText		= new Label();	
	public Label txtLineType	= new Label();
	public Label txtEntities	= new Label();	
	
	public Checkbox	chkWriteLog	;
	public JLabel 	info;
	public JLabel 	clipB;
	public JLabel 	sel;
	public JLabel 	coordXY; 
	public JProgressBar memoryProgress;
	public JLabel ram;
	public boolean proximitySelection=false;
	public final String defClipTxtA=res.getString("defClipTxtA");
	public final String defSelTxtA=res.getString("defSelTxtA");
	public final String txtB="                                                      ";

	public Checkbox proximity;
	private Checkbox chk;
	private Checkbox ltype;
	private JLabel lbl_ltype;
	private JLabel lbl_thick;

	private Label lbl_mem;

	private Label i18n;

	private JMenuItem menuItemHelp;

	private JMenuItem menuItemAbout;

	public JMenu menu;
	public JMenu menuAide;

	public JButton jbClearLogs;

	public String lastOpenFile;

	
	public DXF_Loader() {
		super();
		//JPopupMenu ppmRightMenu = new JPopupMenu();
		//ppmRightMenu.addSeparator();
		//ppmRightMenu.add(new JMenuItem("Properties"));
		
		this.frame = new JFrame("DXF");
		this.frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/dxf.jpg")).getImage());
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setContentPane(this.createContentPane());

		new  FileDrop( this.frame, new FileDrop.Listener() {
			public void  filesDropped( java.io.File[] files ) {				 
				try {
					if (files[files.length-1].getName().toLowerCase().lastIndexOf(".dxf")!=-1) {
						load(files[files.length-1].getAbsoluteFile());
						lastOpenDXF=files[files.length-1].getAbsoluteFile().getParent();
												
						//-->File prefs_myMDXF = new File(ClassLoader.getSystemResource("myDXF/prefs.myDXF").getFile());
//						-->String replaced = prefs_myMDXF.toString().replace("%20", " ");
//						-->BufferedWriter out = new BufferedWriter(new FileWriter(replaced));
//						-->out.write(lastOpenDXF);
//						-->out.close();
					}
				} catch (Exception e) { e.printStackTrace(); }
			}  
		});
		this.frame.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {}
			public void componentMoved(ComponentEvent arg0) {}
			public void componentResized(ComponentEvent arg0) {
				if (DXF_Loader.this.init)
					DXF_Loader.this.cadrer();
			}				
			public void componentShown(ComponentEvent arg0) {}			
		});

		Dimension minimumSize = new Dimension(400, 300);
		
		
		tabPane					= new JTabbedPane();
		
		JPanel globalPanel 		= new JPanel(new GridLayout(0,1));
		JPanel toolPanel 		= new JPanel(new GridLayout(0,1));
		JPanel optionPanel		= new JPanel(new GridLayout(0,2));
		JPanel linePanel		= new JPanel(new GridLayout(0,1));
		JPanel statsPanel		= new JPanel(new GridLayout(0,2));
		JPanel logsPanel		= new JPanel(new GridLayout(0,2));
		
		// STATS  
		statsPanel.add(new Label(res.getString("DXF_Loader.10")));		statsPanel.add(this.txtBlocks);
		statsPanel.add(new Label(res.getString("DXF_Loader.11")));		statsPanel.add(this.txtTables);
		statsPanel.add(new Label(res.getString("DXF_Loader.12")));			statsPanel.add(this.txtLayers);
		statsPanel.add(new Label(res.getString("DXF_Loader.13")));			statsPanel.add(this.txtLineType);
		statsPanel.add(new Label(" "));				statsPanel.add(new Label(" "));
		statsPanel.add(new Label(res.getString("DXF_Loader.16")));	statsPanel.add(this.txtEntities);
		statsPanel.add(new Label(" "));				statsPanel.add(new Label(" "));
		statsPanel.add(new Label(res.getString("DXF_Loader.19")));			statsPanel.add(this.txtPoint);
		statsPanel.add(new Label(res.getString("DXF_Loader.20")));			statsPanel.add(this.txtLine);
		statsPanel.add(new Label(res.getString("DXF_Loader.21")));		statsPanel.add(this.txtPolyline);
		statsPanel.add(new Label(res.getString("DXF_Loader.22")));	statsPanel.add(this.txtLwPolyline);
		statsPanel.add(new Label(res.getString("DXF_Loader.23")));			statsPanel.add(this.txtArc);
		statsPanel.add(new Label(res.getString("DXF_Loader.24")));		statsPanel.add(this.txtCircle);
		statsPanel.add(new Label(res.getString("DXF_Loader.25")));		statsPanel.add(this.txtDimension);
		statsPanel.add(new Label(res.getString("DXF_Loader.26")));			statsPanel.add(this.txtSolid);
		statsPanel.add(new Label(res.getString("DXF_Loader.27")));			statsPanel.add(this.txtTrace);
		statsPanel.add(new Label(res.getString("DXF_Loader.28")));			statsPanel.add(this.txtMText);
		statsPanel.add(new Label(res.getString("DXF_Loader.29")));			statsPanel.add(this.txtText);
		statsPanel.add(new Label(res.getString("DXF_Loader.30")));		statsPanel.add(this.txtEllipse);
			
		// OPTION

		proximity = new Checkbox(res.getString("CHK_PROXIMITY"));	
		proximity.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent item) {
				 int status = item.getStateChange();
				 if (status == ItemEvent.SELECTED)
					DXF_Loader.this.proximitySelection = true;
				else
					DXF_Loader.this.proximitySelection = false;  
			}
			
		});
		
		chk = new Checkbox(res.getString("DXF_Loader.32"));	
		chk.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent item) {
				 int status = item.getStateChange();
				 if (status == ItemEvent.SELECTED)
					myUnivers.antialiasing = true;
				else
					myUnivers.antialiasing = false;  
			}
			
		});
		
		ltype = new Checkbox(res.getString("CHK_LINE_STYLE"));	
		ltype.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent item) {
				 int status = item.getStateChange();
				 if (status == ItemEvent.SELECTED)
					checkLineType = true;
				else {
			    	 checkLineType = false;
					 _mc.big.setStroke(myTable.defaultStroke);
				 }
			}
			
		});
		i18n = new Label(res.getString(res.getString("DXF_Loader.9")));
		optionPanel.add(i18n);
		String[] listeLang = {res.getString("DXF_Loader.35"),res.getString("DXF_Loader.36"),res.getString("DXF_Loader.37")};
		JComboBox comboLang = new JComboBox(listeLang);
		comboLang.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int sel=((JComboBox)e.getSource()).getSelectedIndex();
				switch (sel) {
					case 0 : res = ResourceBundle.getBundle("myDXF.i18n.Ressources_EN");
							locale = new Locale("en", "US");
						    Locale.setDefault(locale);
						break;
					case 1 : res = ResourceBundle.getBundle("myDXF.i18n.Ressources_FR");
							locale = new Locale("fr", "FR");
						    Locale.setDefault(locale);
						break;
					case 2 : res = ResourceBundle.getBundle("myDXF.i18n.Ressources_ES");
							locale = new Locale("es", "ES");
						    Locale.setDefault(locale);
						break;
					default : res = ResourceBundle.getBundle("myDXF.i18n.Ressources_EN");
							locale = new Locale("en", "US");
							Locale.setDefault(locale);
						break;
				}
				doInternational();
				
			}
		});
		optionPanel.add(comboLang);

		optionPanel.add(proximity);
		optionPanel.add(new Label(" "));
		
		optionPanel.add(ltype);
		lbl_mem = new Label(res.getString("LBL_MEM"));
		optionPanel.add(lbl_mem);
		
		optionPanel.add(new Label(" "));		
		optionPanel.add(new Label(" "));
        
		String[] listeCAP = {res.getString("DXF_Loader.54"), res.getString("DXF_Loader.55"), res.getString("DXF_Loader.56")};
		JComboBox comboCAP = new JComboBox(listeCAP);
		comboCAP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        if(cb.getSelectedIndex() == 0)
					myTable.CAP=BasicStroke.CAP_ROUND;
				else  if(cb.getSelectedIndex() == 1)
					myTable.CAP=BasicStroke.CAP_BUTT;
				else  if(cb.getSelectedIndex() == 2)
					myTable.CAP=BasicStroke.CAP_SQUARE;

			}
		});
		optionPanel.add(new Label(res.getString("DXF_Loader.57")));
		optionPanel.add(comboCAP);
		
		String[] listeJOIN = {res.getString("DXF_Loader.58"), res.getString("DXF_Loader.59"), res.getString("DXF_Loader.60")};
		JComboBox comboJOIN = new JComboBox(listeJOIN);
		comboJOIN.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				if(cb.getSelectedIndex() == 0)
					myTable.JOIN=BasicStroke.JOIN_ROUND;
				else  if(cb.getSelectedIndex() == 1)
					myTable.JOIN=BasicStroke.JOIN_MITER;
				else  if(cb.getSelectedIndex() == 2)
					myTable.JOIN=BasicStroke.JOIN_BEVEL;
			}
		});
		optionPanel.add(new Label(res.getString("DXF_Loader.61")));
		optionPanel.add(comboJOIN);
		
		String[] listeBGColor = {res.getString("DXF_Loader.62"), res.getString("DXF_Loader.63")};
		JComboBox comboBGColor = new JComboBox(listeBGColor);
		comboBGColor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        if(cb.getSelectedIndex() == 0)
					myUnivers._bgColor=Color.BLACK;
				else
					myUnivers._bgColor=Color.WHITE;
			}
		});
        optionPanel.add(new Label(res.getString("DXF_Loader.64")));
        optionPanel.add(comboBGColor);
		
        // TOOLS
		this._typeOutil = new myToolBar();
		
		// MOVE TOOLS
		JPanel movePanel = new JPanel(new GridLayout(1,10));
		movePanel.setMinimumSize(new Dimension(350,30));
		movePanel.setPreferredSize(new Dimension(350,30));
		JToolBar moveBar = new JToolBar(res.getString("DXF_Loader.8"));
		
		JButton zoomp = new JButton();
		zoomp.setActionCommand("zoom+");
		zoomp.addActionListener(this);
		zoomp.setToolTipText(res.getString("DXF_Loader.67"));
		zoomp.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/zoomin.gif")));
		movePanel.add(zoomp,BorderLayout.SOUTH);

		JButton zoomm = new JButton();
		zoomm.setActionCommand("zoom-");
		zoomm.setToolTipText(res.getString("DXF_Loader.70"));
		zoomm.addActionListener(this);
		zoomm.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/zoomout.gif")));
		movePanel.add(zoomm,BorderLayout.SOUTH);

		back = new JButton();
		back.setActionCommand("back");
		back.setToolTipText(res.getString("DXF_Loader.73"));
		back.addActionListener(this);
		back.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/undo.gif")));
		back.setEnabled(false);
		movePanel.add(back,BorderLayout.SOUTH);

		fwd = new JButton();
		fwd.setActionCommand("fwd");
		fwd.setToolTipText(res.getString("DXF_Loader.76"));
		fwd.addActionListener(this);
		fwd.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/redo.gif")));
		fwd.setEnabled(false);
		movePanel.add(fwd);

		JButton left = new JButton("");
		left.setActionCommand("left");
		left.setToolTipText(res.getString("DXF_Loader.80"));
		left.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/gauche.gif")));
		left.addActionListener(this);
		movePanel.add(left);

		JButton right = new JButton("");
		right.setActionCommand("right");
		right.setToolTipText(res.getString("DXF_Loader.84"));
		right.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/droite.gif")));
		right.addActionListener(this);
		movePanel.add(right);

		JButton up = new JButton();
		up.setActionCommand("up");
		up.setToolTipText(res.getString("DXF_Loader.87"));
		up.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/haut.gif")));
		up.addActionListener(this);
		movePanel.add(up);
		
		JButton down = new JButton("");
		down.setActionCommand("down");
		down.setToolTipText(res.getString("DXF_Loader.91"));
		down.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/bas.gif")));
		down.addActionListener(this);
		movePanel.add(down);

		JButton centrer = new JButton("");
		centrer.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/cadrer.jpg")));
		centrer.setActionCommand("cadrer");
		centrer.setToolTipText(res.getString("DXF_Loader.96"));
		centrer.addActionListener(this);
		movePanel.add(centrer);
		
		JButton btnResetSize = new JButton("");
		btnResetSize.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/reset.gif")));
		btnResetSize.addActionListener(this);
		btnResetSize.setToolTipText(res.getString("DXF_Loader.99"));
		btnResetSize.setActionCommand("reset_size");
		movePanel.add(btnResetSize);
		
		movePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		moveBar.add(movePanel);
		
		// TYPE LINE
		JToolBar lineBar = new JToolBar(res.getString("DXF_Loader.101"));
		
		this._comboLineType = new JComboBox();
		this._comboLineType.setAutoscrolls(true);
		
		// THICKNESS
		SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 256, 1);  
		JSpinner spinnerThickness = new JSpinner(spinnerModel);
		spinnerThickness.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent event) {
				JSpinner source = (JSpinner)event.getSource();
				 DXF_Loader.this._u.currThickness = Integer.parseInt(source.getValue().toString());
			}
			
		});
		lbl_ltype = new JLabel(res.getString("LBL_CURR_LTYPE"));
		linePanel.add(lbl_ltype);
		linePanel.add(this._comboLineType);
		lbl_thick = new JLabel(res.getString("LBL_CURR_THICK"));
		linePanel.add(lbl_thick);
		linePanel.add(spinnerThickness);

		lineBar.add(linePanel);
		
		// LOG
		logsPanel.setLayout(new BorderLayout());
		
		logText = new myThreadedLogWriter();
		
		JPanel iLoveJavaGUI = new JPanel();
		
		this.chkWriteLog = new Checkbox(res.getString("CHK_LOG"));
		this.chkWriteLog.setState(false);
		this.chkWriteLog.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent item) {
				 int status = item.getStateChange();
				 if (status == ItemEvent.SELECTED)
					myLog.setActiv(true);
				else
					myLog.setActiv(false);  
			}
		});
		
		iLoveJavaGUI.add(this.chkWriteLog, BorderLayout.WEST);

		JLabel jl = new JLabel("                          ");
		iLoveJavaGUI.add(jl,BorderLayout.CENTER);
		
		jbClearLogs = new JButton(res.getString("CLR_LOG"));
		jbClearLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logText.setText("");
			}			
		});
		iLoveJavaGUI.add(jbClearLogs,BorderLayout.EAST);
		
		logsPanel.add(iLoveJavaGUI, BorderLayout.NORTH);
		logsPanel.add(logText, BorderLayout.CENTER);
		
				
		// COLOR
		this._jcc = new myJColorChooser();
		this._jcc.setBorder(BorderFactory.createTitledBorder(res.getString("DXF_Loader.105")));
		JToolBar colorBar = new JToolBar(res.getString("DXF_Loader.106"));		
		colorBar.add(this._jcc);

		// TREEVIEW
		JToolBar treeBar = new JToolBar(res.getString("DXF_Loader.107"));

		// DO NOT MOVE THIS LINE
		_mc = new myCanvas(this);	
		this.tree = new myJTree(_mc);
		// La survie du monde en d�pend ! 
		
		
		JScrollPane treeView = new JScrollPane(this.tree);
		treeBar.add(treeView);

		minimumSize=new Dimension(400, 500);
		treeBar.setMinimumSize(minimumSize);
		treeBar.setPreferredSize(minimumSize);

		treeView.setPreferredSize(new Dimension(100, 100));

		treeBar.setMinimumSize(new Dimension(50, 50));
		treeBar.setPreferredSize(new Dimension(50, 50));
		// MISE EN FORME

		toolPanel.add(this._typeOutil);
		toolPanel.add(moveBar);
		toolPanel.add(lineBar);
		
		tabPane.add(res.getString("TP_TOOLS")	, toolPanel);
		tabPane.add(res.getString("TP_LOGS")	, logsPanel);
		tabPane.add(res.getString("TP_OPTIONS")	, optionPanel);
		tabPane.add(res.getString("TP_STATS")	, statsPanel);
		
		globalPanel.add(tabPane);
		globalPanel.add(colorBar);
		globalPanel.add(treeBar);
		this.add(_mc);	
	
		
		this.tree._refCanva = _mc;	
		this.treeMenu = new myJMenu(_mc);
		this._toolScrollPane = new JScrollPane(globalPanel);

		this._sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,globalPanel,_mc);
		this._sp.setOneTouchExpandable(true);
		this._sp.setDividerLocation(378);
		this._sp.setPreferredSize(new Dimension((int) (globalPanel.getPreferredSize().getWidth()+816),802));


		JPanel _infoBar = new JPanel(new BorderLayout());	
		_infoBar.setSize(1, 50);
		
		JPanel memoryUsage = new JPanel(new BorderLayout());		
		this.ram = new JLabel(res.getString("DXF_Loader.112"));
		this.ram.setOpaque(true);
		this.ram.setBackground(Color.DARK_GRAY);
		this.ram.setForeground(Color.WHITE);		
		
		memoryUsage.add(this.ram,BorderLayout.EAST);
		new myThreadedRam(this);
		memoryUsage.add(this.memoryProgress,BorderLayout.CENTER);		
		memoryUsage.setPreferredSize(new Dimension(384, 10));		
		_infoBar.add(memoryUsage, BorderLayout.WEST);
		

		JPanel _labels = new JPanel(new BorderLayout());	
		_labels.setSize(1, 50);	
		
		this.coordXY = new JLabel();
		this.coordXY.setOpaque(true);
		this.coordXY.setBackground(Color.BLACK);
		this.coordXY.setForeground(Color.WHITE);
		_infoBar.add(this.coordXY,BorderLayout.CENTER);		

		this.clipB = new JLabel(defClipTxtA+"0"+txtB);
		this.clipB.setOpaque(true);
		this.clipB.setBackground(Color.BLACK);
		this.clipB.setForeground(Color.WHITE);		
		_labels.add(this.clipB,BorderLayout.CENTER);
		
		this.sel = new JLabel(defSelTxtA+"0"+txtB);
		this.sel.setOpaque(true);
		this.sel.setBackground(Color.BLACK);
		this.sel.setForeground(Color.WHITE);		
		_labels.add(this.sel,BorderLayout.WEST);
		
		this.info = new JLabel(DXF_Loader.res.getString("READY"));
		this.info.setOpaque(true);
		this.info.setBackground(Color.BLACK);
		this.info.setForeground(Color.WHITE);		
		_labels.add(this.info,BorderLayout.EAST);


		_infoBar.add(_labels,BorderLayout.EAST);
		
		_infoBar.setOpaque(true);
		_infoBar.setBackground(Color.BLACK);
		_infoBar.setForeground(Color.WHITE);

		
		this.frame.setLocation(10,10);
		this.frame.setJMenuBar(this.createMenuBar());
		this.frame.getContentPane().add(this._sp,BorderLayout.NORTH);
		this.frame.getContentPane().add(_infoBar,BorderLayout.SOUTH);
		this.frame.pack();
		this.frame.setVisible(true);
		
		this._u = new myUnivers(new myHeader());
		this.updateLineTypeCombo();	
		this.tree.createNodes();
	}
	
	public void doInternational() {
		proximity.setLabel(res.getString("CHK_PROXIMITY"));
		ltype.setLabel(res.getString("CHK_LINE_STYLE"));
		tabPane.setTitleAt(0, res.getString("TP_TOOLS"));
		tabPane.setTitleAt(1, res.getString("TP_LOGS"));
		tabPane.setTitleAt(2, res.getString("TP_OPTIONS"));
		tabPane.setTitleAt(3, res.getString("TP_STATS"));
		lbl_ltype.setText(res.getString("LBL_CURR_LTYPE"));
		lbl_thick.setText(res.getString("LBL_CURR_THICK"));
		chkWriteLog.setLabel(res.getString("CHK_LOG"));
		jbClearLogs.setText(res.getString("CLR_LOG"));
		lbl_mem.setText(res.getString("LBL_MEM"));
		sel.setText(res.getString("defSelTxtA")+_mc.vectClickOn.size()+txtB);
		clipB.setText(res.getString("defClipTxtA")+_mc.clipBoard.size()+txtB);
		i18n.setText(res.getString("INTL"));
		myJMenu.file_filter=res.getString("FILE_FILTER");
		myJMenu.dialogNAME=DXF_Loader.res.getString("NEW_NAME");
		info.setText(res.getString("READY"));	
		menuItemHelp.setText(res.getString("menuItemHelp"));
		menuItemAbout.setText(res.getString("menuItemAbout"));

		menu.setText(DXF_Loader.res.getString("menuItemFichier"));
		
		myJMenu.menuItemNouveau.setText(DXF_Loader.res.getString("menuItemNouveau"));
		myJMenu.menuItemOuvrir.setText(DXF_Loader.res.getString("menuItemOuvrir"));
		myJMenu.menuItemEnregistrer.setText(DXF_Loader.res.getString("menuItemEnregistrer"));
		myJMenu.menuItemEnregistrerSous.setText(DXF_Loader.res.getString("menuItemEnregistrerSous"));
		myJMenu.menuItemRenommer.setText(DXF_Loader.res.getString("menuItemRenommer"));
		myJMenu.menuItemExporter.setText(DXF_Loader.res.getString("menuItemExporter"));
		myJMenu.menuItemImprimer.setText(DXF_Loader.res.getString("menuItemImprimer"));
		myJMenu.menuItemQuitter.setText(DXF_Loader.res.getString("menuItemQuitter"));
	}

	public void updateLineTypeCombo(){
		Vector lt = this._u.getLTypes();
		this._comboLineType.removeAllItems();
		
		for(int i = 0; i< lt.size(); i++)
			this._comboLineType.addItem(((myLineType) lt.get(i)));		
	}
	
	public void updateStats(){
		if (this._u != null) {
			this.txtBlocks.setText(String.valueOf(this._u._myBlocks.size()));
			this.txtTables.setText(String.valueOf(this._u._myTables.size()));
		}
		else{
			this.txtBlocks.setText(String.valueOf("0"));
			this.txtTables.setText(String.valueOf("0"));
		}
		this.txtLayers.setText(String.valueOf(myStats.nbLayer));
		this.txtLineType.setText(String.valueOf(myStats.nbLineType));
		this.txtPoint.setText(String.valueOf(myStats.nbPoint));
		this.txtLine.setText(String.valueOf(myStats.nbLine));
		this.txtPolyline.setText(String.valueOf(myStats.nbPolyline));
		this.txtLwPolyline.setText(String.valueOf(myStats.nbLwPolyline));
		this.txtArc.setText(String.valueOf(myStats.nbArc));
		this.txtCircle.setText(String.valueOf(myStats.nbCercle));
		this.txtDimension.setText(String.valueOf(myStats.nbDimension));
		this.txtSolid.setText(String.valueOf(myStats.nbSolid));
		this.txtTrace.setText(String.valueOf(myStats.nbTrace));
		this.txtMText.setText(String.valueOf(myStats.nbMText));
		this.txtText.setText(String.valueOf(myStats.nbMText));
		this.txtEllipse.setText(String.valueOf(myStats.nbEllipse));
		
		this.txtEntities.setText(String.valueOf(
				myStats.nbLine		+
				myStats.nbPoint 	+
				myStats.nbPolyline 	+
				myStats.nbLwPolyline+
				myStats.nbArc 		+
				myStats.nbCercle 	+
				myStats.nbDimension +
				myStats.nbSolid 	+
				myStats.nbTrace 	+
				myStats.nbMText 	+
				myStats.nbEllipse				
		));	
	}
	
	
	public JMenuBar createMenuBar() {

		//JMenuItem 	menuItem = null;
		JMenuBar 	menuBar  = new JMenuBar();
		Vector<JMenuItem> vm = myJMenu.getFileMenuItems(_mc, false);

		menu = new JMenu(res.getString("menuItemFichier"));
		menu.setMnemonic(KeyEvent.VK_A);

		for(int i = 0; i< vm.size(); i++){
			if(vm.elementAt(i).getText().equals("Separateur")) {
				menu.addSeparator();
			} else {
				menu.add(vm.elementAt(i));
			}
		}
		menuBar.add(menu);
		
		menuAide  = new JMenu(res.getString("DXF_Loader.147"));
		
		menuItemHelp = new JMenuItem(res.getString("menuItemHelp"));
		menuItemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menuItemHelp.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/aide.png")));
		menuItemHelp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					//	TODO --> 2
					
			    	//URL url;
			    		// Construct the URL
			    		//url= this.getClass().getResource("/Aide/Manuel_Utilisateur.htm");
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + res.getString("URL_HELP"));
					//URL u = DXF_Loader.class.getResource("DXF_Loader.class");
					//String path = (new File(new File(u.toString()).getParent()).getParent()) + File.separator +  "Aide" + File.separator + "Manuel_Utilisateur.htm";
					//ClassLoader.getSystemResource("Aide/Manuel_Utilisateur.htm");
					
				} catch (IOException e) {
					myLog.writeLog(e.getMessage());
				}
			}			
		});
		menuAide.add(menuItemHelp);
		
		menuAide.addSeparator();		
		menuItemAbout = new JMenuItem(res.getString("menuItemAbout"));
		menuItemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menuItemAbout.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/award.png")));
		menuItemAbout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {			  
				JFrame frame = new JFrame(res.getString("ABOUT_VERS"));				
				frame.setLocation(200, 200);
				frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/award.png")).getImage());
				frame.setMinimumSize(new Dimension(400, 150));
				frame.getContentPane().setLayout(new GridLayout(0, 1));
				JLabel labelImage = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/about.jpg")));
				frame.getContentPane().add(labelImage);
				//frame.getContentPane().add(new JLabel("Authors :"));
				//frame.getContentPane().add(new Label(" -> 3d, Nova & Marcus"));
				//frame.getContentPane().add(new JLabel("Version :"));
				//frame.getContentPane().add(new Label("Release Candidate 1"));
				frame.pack();	
				frame.setVisible (true);
			}
		});
		menuAide.add(menuItemAbout);		
		menuBar.add(menuAide);
		
		return menuBar;
	}

	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		JPanel menuPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		menuPane.setVisible(true);
		contentPane.add(menuPane);
		return contentPane;
	}

	public void Center(Point double1){
		if (double1.getX()<(myCoord.Max/2)){
			if (double1.getY()<(myCoord.Max/2)){
				myCoord.decalageX+=((myCoord.Max/2)-double1.getX());
				myCoord.decalageY+=((myCoord.Max/2)-double1.getY());
			}else if (double1.getY()>(myCoord.Max/2)){
				myCoord.decalageX+=((myCoord.Max/2)-double1.getX());
				myCoord.decalageY-=(double1.getY()-(myCoord.Max/2));
			}
		}else if (double1.getY()<(myCoord.Max/2)){
			myCoord.decalageX-=(double1.getX()-(myCoord.Max/2));
			myCoord.decalageY+=((myCoord.Max/2)-double1.getY());
		}else if (double1.getY()>(myCoord.Max/2)){
			myCoord.decalageX-=(double1.getX()-(myCoord.Max/2));
			myCoord.decalageY-=(double1.getY()-(myCoord.Max/2));
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand()=="zoom+") {
			myCoord.Ratio+=myCoord.ratioStep;
			myHistory.saveHistory(true);			
		} else if (e.getActionCommand()=="zoom-") {
			if ((myCoord.Ratio-myCoord.ratioStep)<1) {
				myCoord.ratioStep/=2;
				myCoord.Ratio-=myCoord.ratioStep;			
			} else
				myCoord.Ratio-=myCoord.ratioStep;			
			myHistory.saveHistory(true);
			
		} else if (e.getActionCommand()=="back")
			myHistory.backToThePast();
		else if (e.getActionCommand()=="fwd")
			myHistory.backToTheFuture();
		else if (e.getActionCommand()=="left") {
			myCoord.decalageX-=10;
			myHistory.saveHistory(true);
		} else if (e.getActionCommand()=="right") {
			myCoord.decalageX+=10;
			myHistory.saveHistory(true);
		} else if (e.getActionCommand()=="up") {
			myCoord.decalageY-=10;
			myHistory.saveHistory(true);
		} else if (e.getActionCommand()=="down") {
			myCoord.decalageY+=10;
			myHistory.saveHistory(true);
		} else if (e.getActionCommand()=="cadrer")
			this.cadrer();
		else if (e.getActionCommand()=="reset_size") {
			myCoord.reset();
			myHistory.saveHistory(true);
		}
		this._u._header.setLIM(_mc.getWidth(),_mc.getHeight());
		
		this.tree.updateEnv();
		_mc.repaint();		
	}

	private static void createAndShowGUI() {
		new DXF_Loader();		
	}


	public void newDXF() {

		if(_mc != null) {
			_mc._dxf.tree.createNodes();
			myCoord.reset();
			myHistory.resetHistory();
			myStats.reset();
			_mc._dxf.tree.updateEnv();

			_mc.selecting=false;
			_mc.moving=false;
			_mc.zooming=false;
			_mc.drawingCircle=false;
			_mc.drawingPolyLineStart=false;
			_mc.drawingPolyLineEnd=false;
			_mc.drawingLwPolyLineStart=false;
			_mc.drawingLwPolyLineEnd=false;
			_mc.drawingArc=false;
			_mc.drawingArcAngleStart=false;
			_mc.drawingArcAngleEnd=false;
			_mc.drawingEllipse=false;
			_mc.drawingTrace=false;
			_mc.drawingTxt=false;
			_mc.drawingSolid=false;
		}
		
		if (this._u != null)
			this._u.reset();
		
		
	}

	public void write(String nomFichier) throws Exception {
		FileWriter 	out = null;
		File 		f 	= null;

		try {
			f 	= new File(nomFichier);
			out = new FileWriter(f);

			if(this._u._header==null)
				this._u._header=new myHeader();

			this._u.writeHeader(out);
			this._u.writeTables(out);
			this._u.writeBlocks(out);
			this._u.writeEntities(out);

			out.write("EOF\n");
			out.close();
		} catch(IOException e) {
			myLog.writeLog(res.getString("DXF_Loader.168") + e.getMessage());
		}
		myLog.writeLog(res.getString("DXF_Loader.169") );
		
	}

	public void load(File Fichier) throws IOException, Exception {
		myLog.writeLog(" <------ START " + Fichier.getName() + " ------>");
		new myThreadedLoad(this,Fichier);
	}

	public void cadrer() {
		myCoord.Max=this._u.getMaxSpan();
		int w=DXF_Loader._mc.getWidth()-10;
		int h=DXF_Loader._mc.getHeight()-10;
		
		if (w<h)
			myCoord.Ratio=(w)/myCoord.Max;
		else
			myCoord.Ratio=(h)/myCoord.Max;
		myCoord.decalageX-=myCoord.dxfToJava_X(_u.lastView.getMinX())-myCoord.javaToDXF_X(0)-_u.strayX;
		myCoord.decalageY-=myCoord.dxfToJava_Y(_u.lastView.getMaxY())-_u.strayY;
		
		myHistory.saveHistory(true);
		this.tree.updateEnv();
	}

	public static void main(final String[] args) {
	    
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JPopupMenu.setDefaultLightWeightPopupEnabled(false);
				createAndShowGUI();
				if ((args.length!=0) && (args[0].toLowerCase().lastIndexOf(".dxf")!=-1)) {
					File f = new File(args[0]);
					if (f.exists() && f.canRead()) {
						try {
							DXF_Loader._mc._dxf.load(f);
						} catch (Exception e) { e.printStackTrace(); };
					}
				}
			}
		});

	}
}
