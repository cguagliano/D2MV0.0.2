package fr.epsi.dxf.Graphics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.PrintJob;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.swing.SwingUtilities;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Entities.myArc;
import fr.epsi.dxf.Entities.myBlockReference;
import fr.epsi.dxf.Entities.myCircle;
import fr.epsi.dxf.Entities.myDimension;
import fr.epsi.dxf.Entities.myEllipse;
import fr.epsi.dxf.Entities.myEntity;
import fr.epsi.dxf.Entities.myInsert;
import fr.epsi.dxf.Entities.myLine;
import fr.epsi.dxf.Entities.myPoint;
import fr.epsi.dxf.Entities.myPolyline;
import fr.epsi.dxf.Entities.mySolid;
import fr.epsi.dxf.Entities.myText;
import fr.epsi.dxf.Entities.myTrace;
import fr.epsi.dxf.Entities.myVertex;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myHeader;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myNameGenerator;
import fr.epsi.dxf.Header.myStats;
import fr.epsi.dxf.Header.myTable;

public class myJMenu extends JPopupMenu{

	private static final long serialVersionUID = 6849237026834186660L;
	public myCanvas _mc;
	public static String dialogNAME=DXF_Loader.res.getString("NEW_NAME");

	public static String file_filter;
	public static JMenuItem menuItemNouveau;
	public static JMenuItem menuItemOuvrir;
	public static JMenuItem menuItemEnregistrer;
	public static JMenuItem menuItemEnregistrerSous;
	public static JMenuItem menuItemRenommer;
	public static JMenuItem menuItemExporter;
	public static JMenuItem menuItemImprimer;
	public static JMenuItem menuItemQuitter;
	
	public myJMenu(myCanvas mc){
		super();
		_mc = mc;
	}

	private JMenuItem getEntityMenuItem(final String val, final Object userObject, final boolean bInserted){

		JMenuItem menuItem = new JMenuItem(val);
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int idx=0;
				DefaultMutableTreeNode newNode, currentNode  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				Object parentObj = ((DefaultMutableTreeNode)currentNode.getParent()).getUserObject();				
				myEntity o=null;
				myLayer lay=null;

				if(bInserted){
					if(parentObj instanceof myLayer){
						lay = (myLayer) parentObj;
					}
				}else{
					if(currentNode.getUserObject() instanceof myLayer)
						lay =(myLayer) currentNode.getUserObject();
				}
				
				if(val.equalsIgnoreCase("ARC")){
					o = new myArc();
				} else if(val.equalsIgnoreCase("CIRCLE")){
					o = new myCircle();
				} else if(val.equalsIgnoreCase("DIMENSION")){
					o = new myDimension();      
				} else if(val.equalsIgnoreCase("ELLIPSE")){
					o = new myEllipse();      
				} else if(val.equalsIgnoreCase("INSERT")){
					o = new myInsert(); 
				} else if(val.equals("LINE")){
					o = new myLine(); 
				} else if(val.equalsIgnoreCase("POINT")){
					o = new myPoint();
				} else if(val.equalsIgnoreCase("POLYLINE")){
					o = new myPolyline();
				} else if(val.equalsIgnoreCase("SOLID")){
					o = new mySolid();
				} else if(val.equalsIgnoreCase("TEXT")){
					o = new myText();
				} else if(val.equalsIgnoreCase("TRACE")){
					o = new myTrace();      
				} else {
					myLog.writeLog("Unkown entity : " + val);
				}

				o._refLayer = lay;

				if(currentNode.getUserObject() instanceof myLayer){
					((myLayer)currentNode.getUserObject())._myEnt.add(o);

				}else if(currentNode.getUserObject() instanceof myBlock){
					((myBlock)currentNode.getUserObject())._myEnt.add(o);

				}else if(bInserted && currentNode.getUserObject() instanceof myEntity){

					if(parentObj instanceof myLayer){
						idx =((myLayer) parentObj)._myEnt.indexOf(currentNode.getUserObject());
						((myLayer)parentObj)._myEnt.insertElementAt(o, idx);
						idx+=4;
					}else if(parentObj instanceof myBlock){
						idx =((myBlock) parentObj)._myEnt.indexOf(currentNode.getUserObject());
						((myBlock) parentObj)._myEnt.insertElementAt(o, idx);
						idx+=5;
					}
				}

				newNode = o.getNode();
				if(newNode!=null){
					if(bInserted){
						((DefaultMutableTreeNode) currentNode.getParent()).insert(newNode,idx);
						currentNode = (DefaultMutableTreeNode) currentNode.getParent();
					}else{
						currentNode.add(newNode);
					}
				}
				_mc._dxf.tree.updateSelection(currentNode);				
			}
		}
		);		
		return menuItem;
	}

	private JMenu getNewEntityMenuItem(final Object userObject){
		return getNewEntityMenuItem(userObject, false);
	}

	private JMenu getNewEntityMenuItem(final Object userObject, final boolean bInserted){	

		JMenu menu;
		if(bInserted)
			menu = new JMenu(DXF_Loader.res.getString("myJMenu.0"));
		else
			menu = new JMenu(DXF_Loader.res.getString("myJMenu.1"));

		menu.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/shape.jpg")));
		menu.add(getEntityMenuItem("POINT", userObject, bInserted));
		menu.add(getEntityMenuItem("LINE", userObject, bInserted));
		menu.add(getEntityMenuItem("POLYLINE", userObject, bInserted));
		menu.add(getEntityMenuItem("SOLID", userObject, bInserted));
		menu.add(getEntityMenuItem("ARC", userObject, bInserted));
		menu.add(getEntityMenuItem("CIRCLE", userObject, bInserted));
		menu.add(getEntityMenuItem("ELLIPSE", userObject, bInserted));
		menu.add(getEntityMenuItem("INSERT", userObject, bInserted));
		menu.add(getEntityMenuItem("DIMENSION", userObject, bInserted));
		menu.add(getEntityMenuItem("TEXT", userObject, bInserted));
		menu.add(getEntityMenuItem("TRACE", userObject, bInserted));

		return menu;
	}

	private JMenuItem getVisibilityMenuItem(final Object userObject){

		JMenuItem menuItem;		
		if(((myEntity) userObject).isVisible){
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.2"));
			menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/notvisible.gif")));

		}else{
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.3"));
			menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/visible.gif")));				
		}

		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				DefaultMutableTreeNode  tmp, node  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				boolean val = ((myEntity) userObject).isVisible;
				if(userObject instanceof myLayer){
					((myLayer) userObject).setVisible(!val);
					node.setUserObject(userObject);
				}
				else if(userObject instanceof myEntity){
					((myEntity) userObject).setVisible(!val);
					node.setUserObject(userObject);
					node.removeAllChildren();

					tmp = (((myEntity) userObject).getNode());
					int size = tmp.getChildCount();
					for(int i =0; i<size; i++)
						node.add((MutableTreeNode) tmp.getChildAt(0));
				}
				_mc._dxf.tree.updateSelection(node);
			}
		}); 
		return menuItem;
	}

	@SuppressWarnings("unchecked")
	private JMenuItem getLineTypeMenuItem(){

		JMenuItem menuItem=null;
		JMenu menu = new JMenu(DXF_Loader.res.getString("myJMenu.4"));
		menu.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/select.jpg")));

		MouseListener ml = new MouseAdapter(){
			public void mouseReleased(final MouseEvent e) {
				DefaultMutableTreeNode node  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				if(((DefaultMutableTreeNode) node.getParent()).getUserObject() instanceof myEntity){
					((myEntity) ((DefaultMutableTreeNode) node.getParent()).getUserObject())._lineType = _mc._dxf._u.findLType(((JMenuItem) e.getSource()).getToolTipText());
					((myLabel )((DefaultMutableTreeNode) node).getUserObject())._value = ((JMenuItem) e.getSource()).getToolTipText();
					((DefaultTreeModel)  _mc._dxf.tree.getModel()).nodeStructureChanged(node);
					_mc._dxf.tree.updateSelection(node);
				}
			}
		};

		Vector<myLineType> lt = _mc._dxf._u.getLTypes();
		for(int i = 0; i< lt.size(); i++){
			menuItem = new JMenuItem(lt.get(i).toString().substring(6,lt.get(i).toString().length()));
			menuItem.setToolTipText(lt.get(i)._name); 
			menuItem.addMouseListener(ml);		
			menu.add(menuItem);
		};		

		return menu;
	}	

	private JMenuItem getColorMenuItem(){

		JMenuItem menuItem=null;
		JMenu menu = new JMenu(DXF_Loader.res.getString("myJMenu.5"));
		menu.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/select.jpg")));
		
		for(int i=0; i< fr.epsi.dxf.Graphics.DXF_Color.ColorMap.length; i++){
			menuItem = new JMenuItem();
			menuItem.setToolTipText(String.valueOf(i));
			menuItem.setPreferredSize(new Dimension(100,4));
			menuItem.setBackground(DXF_Color.ColorMap[i]);
			menuItem.addMouseListener(new MouseAdapter() {
				public void mouseReleased(final MouseEvent e) {

					DefaultMutableTreeNode node  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
					if(((DefaultMutableTreeNode) node.getParent()).getUserObject() instanceof myEntity){
						((myEntity) ((DefaultMutableTreeNode) node.getParent()).getUserObject())._color = Integer.parseInt(((JMenuItem) e.getSource()).getToolTipText());												
						((myLabel) ((DefaultMutableTreeNode) node).getUserObject())._value =((JMenuItem) e.getSource()).getToolTipText();
						((DefaultTreeModel)  _mc._dxf.tree.getModel()).nodeStructureChanged(node);
						_mc._dxf.tree.updateSelection(node);
						myJColorChooser.colLayer.setBackground(DXF_Color.getColor(((myEntity) ((DefaultMutableTreeNode) node.getParent()).getUserObject())._color));
					}
				}
			});
			menu.add(menuItem);		
		}

		return menu;
	}

	private JMenuItem getActivationMenuItem(final Object userObject){

		
		if(_mc._dxf._u.currLayer == userObject){
			return null;
		}else{
			JMenuItem menuItem;		
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.6"));
			menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/green.png")));
			
			menuItem.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if(((JMenuItem) e.getSource()).getText().equals(DXF_Loader.res.getString("myJMenu.6"))){
						_mc._dxf._u.currLayer = (myLayer) userObject;
						_mc._dxf.tree.updateEnv();
						myJColorChooser.colLayer.setBackground(DXF_Color.getColor(_mc._dxf._u.currLayer._color));
					}
				}
			});
			return menuItem;
		}
	}

	private JMenuItem getCurrentBlockMenuItem(final Object userObject){

		if(_mc._dxf._u.currBlock == userObject){
			return null;
		}else{
			JMenuItem menuItem;		
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.6"));
			menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/green.png")));
			menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if(((JMenuItem) e.getSource()).getText().equals(DXF_Loader.res.getString("myJMenu.6"))){
					_mc._dxf._u.currBlock = (myBlock) userObject;
					_mc._dxf.tree.updateEnv();
				}
			}});
			return menuItem;
		}		
	}
	
	private JMenuItem getDeleteMenuItem(final Object userObject){

		JMenuItem menuItem;	
		menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.7"));
		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/cross.gif")));
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				DefaultMutableTreeNode  node  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent(),
				parent= (DefaultMutableTreeNode) node.getParent();

				// LAYER
				if(node.getUserObject() instanceof myLayer){
					if(((myTable)parent.getUserObject())._myLayers.remove(node.getUserObject())){
						parent.setUserObject(((DefaultMutableTreeNode) node.getParent()).getUserObject());
						node.removeFromParent();	
						((DefaultTreeModel)_mc._dxf.tree.getModel()).nodeStructureChanged(parent);
						_mc._dxf.tree.updateSelection(parent);
						myStats.nbLayer-=1;
					}
					else{
						//TOTDO -> LOG ?
						//System.err.println("[NOK] Suppression du layer impossible");
					}

				}
				// TABLE
				else if(node.getUserObject() instanceof myTable){
					myTable t = ((myTable) node.getUserObject());

					if(_mc._dxf._u._myTables.remove(t))  {      						
						parent.setUserObject(new myLabel(myLabel.LST_TABLE,_mc._dxf._u.ToStringTables()));
						node.removeFromParent();	
						_mc._dxf.tree.updateSelection(parent);
					}else {
						//TODO -> LOG ?
						//System.err.println("[NOK] Suppression de la table impossible");
					}
				}
				// BLOCK
				else if(node.getUserObject() instanceof myBlock){
					myBlock b = ((myBlock) node.getUserObject());

					for(int i = 0; i< b._myEnt.size(); i++){
						b._myEnt.elementAt(i)._refLayer._myEnt.remove(b._myEnt.elementAt(i));
					}
					b._myEnt.removeAllElements();
					if(_mc._dxf._u._myBlocks.remove(b)){
						parent.setUserObject(new myLabel(myLabel.LST_BLOCK,_mc._dxf._u.ToStringBlocks()));
						node.removeFromParent();	
						_mc._dxf.tree.updateSelection(parent);
					}else {
//						TODO -> LOG ?
						//System.err.println("[NOK] Suppression du block impossible");
					}
				}
				// VERTEX
				else if(node.getUserObject() instanceof myVertex){
					if(((myPolyline)parent.getUserObject())._myVertex.remove(userObject)) {       						
						node.removeFromParent();
						((DefaultTreeModel)_mc._dxf.tree.getModel()).nodeStructureChanged(parent);
						_mc._dxf.tree.updateSelection(parent);						
					}else{
//						TODO -> LOG ?
						//System.err.println("[NOK] Suppression de l'objet impossible");
					}
				}
				// OBJECT
				else if(node.getUserObject() instanceof myEntity){
					myStats.decrease((myEntity) node.getUserObject());
					
					if(((myEntity) userObject)._refLayer._myEnt.remove(userObject)) {       						
						parent.setUserObject(parent.getUserObject());
						node.removeFromParent();
						((DefaultTreeModel)_mc._dxf.tree.getModel()).nodeStructureChanged(parent);
						_mc._dxf.tree.updateSelection(parent);						
					}else{
//						TODO -> LOG ?
						//System.err.println("[NOK] Suppression de l'objet impossible");
					}
				}else{
//					TODO -> LOG ?
					//System.err.println("[NOK] Op�ration inconnue");
				}
				_mc._dxf.updateStats();
			}
		});
		
		return menuItem;
	}

	public JMenuItem getNewLayerMenuItem(final Object userObject){
		JMenuItem menuItem = null;

		if(userObject instanceof myTable){
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.8"));
		}
		else{
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.9"));
		}

		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				DefaultMutableTreeNode  node = null;

				if(userObject instanceof myTable){
					myLayer l = new myLayer(myNameGenerator.getLayerName(DXF_Loader.res.getString("myJMenu.10")), DXF_Color.getColor(myJColorChooser.col.getBackground()),  0);
					((myTable) userObject)._myLayers.add(l);
					_mc._dxf._u.currLayer = l;					
					myJColorChooser.colLayer.setBackground(DXF_Color.getColor(_mc._dxf._u.currLayer._color));
					node = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
					node.insert(l.getNode(), node.getChildCount());

				}else if(userObject instanceof myLayer){
					node = (DefaultMutableTreeNode)  ((DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent()).getParent();
					int idx = ((myTable) node.getUserObject())._myLayers.indexOf(userObject);

					myLayer l = new myLayer(myNameGenerator.getLayerName(DXF_Loader.res.getString("myJMenu.10")), DXF_Color.getColor(myUnivers._bgColor),  0);
					((myTable) node.getUserObject())._myLayers.insertElementAt(l, idx);
					_mc._dxf._u.currLayer = l;		
					myJColorChooser.colLayer.setBackground(DXF_Color.getColor(_mc._dxf._u.currLayer._color));
					node.insert(l.getNode(), idx);
				}

				_mc._dxf.tree.updateSelection(node);
			}
		});		

		return menuItem;	
	}

	public void defineOption(final Object userObject)
	{
		Object obj;
		JMenu menu=null;
		this.removeAll();

		// Univers
		if(userObject instanceof myUnivers){
			Vector<JMenuItem> vm = myJMenu.getFileMenuItems(_mc, true);
			for(int i = 0; i< vm.size(); i++){
				if(vm.elementAt(i).getText().equals("Separateur"))
					this.addSeparator();
				else
					this.add(vm.elementAt(i));
			}
		}
		// LAYER
		if(userObject instanceof myLayer){
			menu = new JMenu(DXF_Loader.res.getString("myJMenu.11"));
			menu.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
			menu.add(getNewLayerMenuItem(userObject));
			menu.add(getNewEntityMenuItem(userObject));
			this.add(menu);
			
			obj = getActivationMenuItem(userObject);
			if(obj != null)
				this.add((JMenuItem)obj);
			
			this.add(getVisibilityMenuItem(userObject));
			this.add(getDeleteMenuItem(userObject));
		}
		// BLOCK
		else if(userObject instanceof myBlock){
			menu = new JMenu(DXF_Loader.res.getString("myJMenu.11"));
			menu.add(getNewBlockMenuItem(userObject));
			menu.add(getNewEntityMenuItem(userObject));
			this.add(menu);
			obj = getCurrentBlockMenuItem(userObject);
			if(obj != null)
				this.add((JMenuItem)obj);
			
			this.add(getDeleteMenuItem(userObject));
			this.add(getCaptureMenuItem(userObject));
		}
		// TABLE
		else if(userObject instanceof myTable){
			menu = new JMenu(DXF_Loader.res.getString("myJMenu.11"));
			menu.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
			menu.add(getNewLayerMenuItem(userObject));
			menu.add(getTableMenuItem(userObject));
			//menu.add(getLTypeMenuItem(userObject));
			this.add(menu);
			this.add(getDeleteMenuItem(userObject));			
		}
		// POLYLINE
		else if(userObject instanceof myPolyline || userObject instanceof myVertex){
			this.add(getDeleteMenuItem(userObject));
			this.add(getVertexMenuItem(userObject));
		}
		// OBJ
		else if(userObject instanceof myEntity){
			this.add(getNewEntityMenuItem(userObject, true));
			this.add(getVisibilityMenuItem(userObject));
			this.add(getDeleteMenuItem(userObject));			
		}
		// LABEL
		else if(userObject instanceof myLabel){
			
			if(((myLabel) userObject)._code.equalsIgnoreCase(myLabel.COLOR)){
				this.add(getColorMenuItem());
				
				DefaultMutableTreeNode node  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				
				if (!(((DefaultMutableTreeNode)node.getParent()).getUserObject() instanceof myLayer)) {
					JMenuItem item = new JMenuItem(DXF_Loader.res.getString("myJMenu.12"));
					item.addMouseListener(new MouseAdapter() 
					{
						public void mouseReleased(MouseEvent e) {
							DefaultMutableTreeNode node  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
							if(((DefaultMutableTreeNode) node.getParent()).getUserObject() instanceof myEntity){
								((myEntity) ((DefaultMutableTreeNode) node.getParent()).getUserObject())._color = 255;												
								((myLabel) ((DefaultMutableTreeNode) node).getUserObject())._value ="255";
								((DefaultTreeModel)  _mc._dxf.tree.getModel()).nodeStructureChanged(node);
								_mc._dxf.tree.updateSelection(node);
							}
						}
					}
					);
					this.add(item);	
				}
				
				
				
			}else if(((myLabel) userObject)._code.equalsIgnoreCase(myLabel.LST_BLOCK)){
				this.add(getNewBlockMenuItem(userObject));				
			}else if(((myLabel) userObject)._code.equalsIgnoreCase(myLabel.LST_TABLE)){
				this.add(getTableMenuItem(userObject));				
			}else if(((myLabel) userObject)._code.equalsIgnoreCase(myLabel.BLOCK)){
				this.add(getBlockMenuItem());
			}else if(((myLabel) userObject)._code.equalsIgnoreCase(myLabel.TYPE_LIGNE)){
				this.add(getLineTypeMenuItem());
			}else if(((myLabel) userObject)._code.equalsIgnoreCase(myLabel.CUR_BLOCK)){
				this.add(getBlockMenuItem());
			}
		}
		this.add(getRefreshItem());
	}

	private Component getCaptureMenuItem(Object userObject) {

		JMenuItem menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.13"));
		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/photo.png")));
		menuItem.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e) {
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				myBlock 	b 	= (myBlock) currentNode.getUserObject();
				myEntity 	obj	= null;
				
				if(b._refUnivers == null){
					b._refUnivers = _mc._dxf._u;					
				}
				
				for (int i=0;i<_mc._dxf._u._myTables.size();i++) {
					for (int j=0;j<_mc._dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
						for (int k=0;k<((myLayer)((myTable)_mc._dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.size();k++) {
							if(((myLayer)((myTable)_mc._dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.elementAt(k).isVisible){                    
								obj = ((myLayer)((myTable)_mc._dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.elementAt(k);
								if(obj instanceof myBlockReference){
									((myBlockReference)obj)._refBlock 	= b;
									((myBlockReference)obj)._blockName	= b._name;
								}	
								b._myEnt.add(obj);
								((myLayer)((myTable)_mc._dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.removeElementAt(k);
								k=k-1;
							}
						}
					}
				}
				_mc._dxf.tree.createNodes();
			}
		});

		return menuItem;
	}

	private Component getRefreshItem() {
		JMenuItem menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.14"));
		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/refresh.png")));
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				DefaultMutableTreeNode tmp=null, currentNode = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();;

				if(currentNode.getUserObject() instanceof myUnivers){
					tmp = new DefaultMutableTreeNode();
					tmp = (((myUnivers) currentNode.getUserObject()).getNode());
				}
				else if(currentNode.getUserObject() instanceof myLayer){
					tmp = (((myLayer) currentNode.getUserObject()).getNode());
				}
				else if(currentNode.getUserObject() instanceof myHeader){
					tmp = (((myHeader) currentNode.getUserObject()).getNode());
				}
				else if(currentNode.getUserObject() instanceof myBlock){
					tmp = (((myBlock) currentNode.getUserObject()).getNode());
				}
				else if(currentNode.getUserObject() instanceof myLayer){
					tmp = (((myLayer) currentNode.getUserObject()).getNode());
				}
				else if(currentNode.getUserObject() instanceof myLabel){
					if(((myLabel)currentNode.getUserObject())._code.equalsIgnoreCase(myLabel.LST_TABLE)){
						tmp = _mc._dxf._u.getMenuTables();
					}else if(((myLabel)currentNode.getUserObject())._code.equalsIgnoreCase(myLabel.LST_BLOCK)){
						tmp = _mc._dxf._u.getMenuBlocks();
					}						
				}
				else if(currentNode.getUserObject() instanceof myEntity){
					tmp = (((myEntity) currentNode.getUserObject()).getNode());
				}

				if(tmp!=null){
					currentNode.removeAllChildren();
					int size = tmp.getChildCount();
					for(int i =0; i<size; i++)
						currentNode.add((MutableTreeNode) tmp.getChildAt(0));

					_mc._dxf.tree.updateSelection(currentNode);
				}						
			}
		});		

		return menuItem;
	}

	private Component getVertexMenuItem(final Object userObject) {
		JMenuItem menuItem = null;

		if(userObject instanceof myPolyline){
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.15"));
		}
		else{
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.16"));
		}

		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				DefaultMutableTreeNode  newNode = null,currentNode = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();;
				myPolyline 	p 	= null;
				myVertex   	v 	= new myVertex(); 
				int 		idx	= 0;

				if(currentNode.getUserObject() instanceof myPolyline){
					p = (myPolyline) currentNode.getUserObject();
					idx = p._myVertex.size();
				}else if(currentNode.getUserObject() instanceof myVertex){
					p = (myPolyline) ((DefaultMutableTreeNode)currentNode.getParent()).getUserObject();
					idx = p._myVertex.indexOf(currentNode.getUserObject());					

					currentNode = (DefaultMutableTreeNode) currentNode.getParent();
				}

				p._myVertex.insertElementAt(v, idx);
				idx+=4;

				newNode= v.getNode();
				if(newNode != null){
					currentNode.insert(newNode, idx);
				}

				_mc._dxf.tree.updateSelection(currentNode);			
			}
		});		

		return menuItem;
	}

	private Component getBlockMenuItem() {
		JMenu menu= new JMenu(DXF_Loader.res.getString("myJMenu.17"));
		menu.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/select.jpg")));
		JMenuItem item;

		for(int i=0; i<_mc._dxf._u._myBlocks.size(); i++){
			item = new JMenuItem(_mc._dxf._u._myBlocks.get(i)._name);		
			menu.add(item);
			item.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					
					DefaultMutableTreeNode node  = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
					Object obj = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
					
					if(obj instanceof myBlockReference){
						_mc._dxf._u.changeBlock((myBlockReference) obj, ((JMenuItem) e.getSource()).getText());
						((DefaultMutableTreeNode) node).setUserObject(new myLabel(myLabel.BLOCK,((JMenuItem) e.getSource()).getText()));
						_mc._dxf.tree.updateSelection(node);
					}
					else if(obj instanceof myUnivers){
						_mc._dxf._u.currBlock = _mc._dxf._u.findBlock(((JMenuItem) e.getSource()).getText());
						_mc._dxf.tree.updateEnv();
					}
				}
			}
			);
		}
		return menu;
	}
/*
	private JMenuItem getLTypeMenuItem(final Object userObject) {
		JMenuItem menuItem = null;	

		menuItem = new JMenuItem("new LType");
		
		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				myLineType lt = new myLineType();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				node.setUserObject(new myLabel(myLabel.LST_TABLE, _mc._dxf._u.ToStringTables()));
				node.insert(lt.getNode(), ((myTable) userObject)._myLineTypes.size());
				((myTable) userObject)._myLineTypes.add(lt);
				_mc._dxf.tree.updateSelection(node);
			}
		});
		return menuItem;
	}
*/
	private JMenuItem getTableMenuItem(final Object userObject) {
		JMenuItem menuItem = null;	

		if(userObject instanceof myLabel){
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.18"));
		}
		else if(userObject instanceof myTable){
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.18"));
		}

		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int idx = 0;
				myTable t = new myTable();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				if(userObject instanceof myLabel){
					idx = node.getChildCount();
					_mc._dxf._u._myTables.add(t);
				}
				else if(userObject instanceof myTable){
					idx = _mc._dxf._u._myTables.indexOf(node.getUserObject());
					_mc._dxf._u._myTables.insertElementAt(t, idx);
					node = (DefaultMutableTreeNode) node.getParent();
				}
				node.setUserObject(new myLabel(myLabel.LST_TABLE, _mc._dxf._u.ToStringTables()));
				node.insert(t.getNode(), idx);
				_mc._dxf.tree.updateSelection(node);
			}
		});
		return menuItem;
	}

	private Component getNewBlockMenuItem(final Object userObject) {
		JMenuItem menuItem = null;	

		if(userObject instanceof myLabel){
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.19"));
		}
		else if(userObject instanceof myBlock){
			menuItem = new JMenuItem(DXF_Loader.res.getString("myJMenu.19"));
		}
		if(menuItem == null)
			return null;

		menuItem.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Add16.gif")));
		menuItem.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int idx = 0;
				myBlock b = new myBlock(0, 0, 0, myNameGenerator.getBlockName(DXF_Loader.res.getString("myJMenu.20")), null, DXF_Color.getDefaultColorIndex(), null, _mc._dxf._u);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) _mc._dxf.tree.getSelectionPath().getLastPathComponent();
				if(userObject instanceof myLabel){
					idx = node.getChildCount();
					_mc._dxf._u._myBlocks.add(b);
				}
				else if(userObject instanceof myBlock){
					idx = _mc._dxf._u._myBlocks.indexOf(node.getUserObject());
					_mc._dxf._u._myBlocks.insertElementAt(b, idx);
					node = (DefaultMutableTreeNode) node.getParent();
				}

				node.setUserObject(new myLabel(myLabel.LST_BLOCK, _mc._dxf._u.ToStringBlocks()));
				node.insert(b.getNode(), idx);
				_mc._dxf._u.currBlock = b;
				
				_mc._dxf.tree.updateSelection((DefaultMutableTreeNode) node.getParent());
				_mc._dxf.tree.updateEnv();
				
			}
		});
		return menuItem;
	}

	public void show(Component component, int x, int y, Object userObject) {
		defineOption(userObject);
		this.show(component,x, y);		
	}

	public static Vector<JMenuItem> getFileMenuItems(final myCanvas mc, boolean tree){
		Vector<JMenuItem> vm = new Vector<JMenuItem>();

		if(mc == null)
			return null;

		menuItemNouveau = new JMenuItem(DXF_Loader.res.getString("menuItemNouveau"));
		menuItemNouveau.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/layer-vierge.gif")));
		menuItemNouveau.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuItemNouveau.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						mc._dxf.newDXF();
						mc._dxf.newDXF(); //...
					}
				}
		);
		vm.add(menuItemNouveau);

		menuItemOuvrir = new JMenuItem(DXF_Loader.res.getString("menuItemOuvrir"));
		menuItemOuvrir.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/open.png")));
		menuItemOuvrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

		menuItemOuvrir.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser chooser = new JFileChooser();
						doIntlFileChooser(chooser);

						chooser.setDialogTitle(DXF_Loader.res.getString("menuItemOuvrirFichier"));
						chooser.setApproveButtonText(DXF_Loader.res.getString("openButtonText"));
						chooser.setApproveButtonToolTipText(DXF_Loader.res.getString("openButtonToolTipText"));
						
						file_filter = DXF_Loader.res.getString("FILE_FILTER");
						chooser.addChoosableFileFilter(new myFilter(new String[]{"dxf"},file_filter));
						chooser.setAcceptAllFileFilterUsed(false);
						chooser.setCurrentDirectory(new File(mc._dxf.lastOpenDXF));
														
						int retour = chooser.showOpenDialog(mc._dxf.getParent());
						if(retour == JFileChooser.APPROVE_OPTION) {
							try {
								mc._dxf.load(chooser.getSelectedFile());
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						mc._dxf.lastOpenDXF=chooser.getCurrentDirectory().getAbsolutePath();
						mc._dxf.tree.createNodes();
					}
				}
		);
		vm.add(menuItemOuvrir);

		menuItemEnregistrer = new JMenuItem(DXF_Loader.res.getString("menuItemEnregistrer"));
		menuItemEnregistrer.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/floppy.jpg")));
		menuItemEnregistrer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuItemEnregistrer.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						mc._dxf.treeMenu.saveDXF();
					}
				}
		);
		vm.add(menuItemEnregistrer);
		
		menuItemEnregistrerSous = new JMenuItem(DXF_Loader.res.getString("menuItemEnregistrerSous"));
		menuItemEnregistrerSous.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/floppy.jpg")));
		menuItemEnregistrerSous.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						mc._dxf.treeMenu.saveAsDXF();
					}
				}
		);
		vm.add(menuItemEnregistrerSous);
		
		menuItemRenommer = new JMenuItem(DXF_Loader.res.getString("menuItemRenommer"));
		menuItemRenommer.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/rename.png")));
		menuItemRenommer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuItemRenommer.addActionListener(
				new ActionListener(){ //TODO fix...
					public void actionPerformed(ActionEvent arg0) {
											
						String nom = "";
						while(nom.equals("")){
						    UIManager.put("OptionPane.cancelButtonText", DXF_Loader.res.getString("cancelButtonText"));
						    UIManager.put("OptionPane.okButtonText", DXF_Loader.res.getString("menuItemRenommerButton"));
							nom = JOptionPane.showInputDialog(DXF_Loader.res.getString("newFileName"), mc._dxf.lastOpenDXF + File.separator + mc._dxf._u._filename);
						}		
						if (!nom.substring(nom.length()-4, nom.length()).equalsIgnoreCase(".dxf"))		
							nom+=".dxf";

						File f = new File(mc._dxf.lastOpenDXF + File.separator + mc._dxf._u._filename);
						if(f.renameTo(new File(nom))){
							mc._dxf._u._filename=f.getName();
							mc._dxf.tree.updateEnv();
						}
						else
						{
							JOptionPane.showMessageDialog(null, DXF_Loader.res.getString("myJMenu.21"),DXF_Loader.res.getString("myJMenu.22"), JOptionPane.ERROR_MESSAGE );
						}

					}
				}
		);
		menuItemRenommer.setEnabled(false);
		vm.add(menuItemRenommer);

		menuItemExporter = new JMenuItem(DXF_Loader.res.getString("menuItemExporter"));
		menuItemExporter.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/photo.png")));
		menuItemExporter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		menuItemExporter.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						mc._dxf.treeMenu.exportAs();
					}
				}
		);
		vm.add(menuItemExporter);

/*		menuItemImprimer = new JMenuItem(DXF_Loader.res.getString("menuItemImprimer"));
		menuItemImprimer.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/print.png")));
		menuItemImprimer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menuItemImprimer.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						PrintJob pjob;
						pjob= mc.getToolkit().getPrintJob(mc._dxf.frame,mc._dxf._u._filename, null);
						if (pjob != null) {
							Graphics pg = pjob.getGraphics();

							if (pg != null) {
								mc.printAll(pg);
								pg.dispose();
							}
							pjob.end();
						}
					}

				}
		);
		vm.add(menuItemImprimer);*/
		if(!tree){
			vm.add(new JMenuItem("Separateur"));

			menuItemQuitter = new JMenuItem(DXF_Loader.res.getString("menuItemQuitter"));
			menuItemQuitter.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/quit.png")));
			menuItemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
			menuItemQuitter.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent arg0) {
							System.exit(0);						
						}					
					}
			);
			vm.add(menuItemQuitter);

		}

		return vm;
	}

	public static void doIntlFileChooser(JFileChooser jfc) {	    
		UIManager.put("FileChooser.lookInLabelText", DXF_Loader.res.getString("lookInLabelText"));
		UIManager.put("FileChooser.fileNameLabelText", DXF_Loader.res.getString("fileNameLabelText"));
		UIManager.put("FileChooser.filesOfTypeLabelText", DXF_Loader.res.getString("filesOfTypeLabelText"));
		UIManager.put("FileChooser.cancelButtonText", DXF_Loader.res.getString("cancelButtonText"));
		UIManager.put("FileChooser.cancelButtonToolTipText", DXF_Loader.res.getString("cancelButtonToolTipText"));
		UIManager.put("FileChooser.openButtonText", DXF_Loader.res.getString("openButtonText"));
		UIManager.put("FileChooser.openButtonToolTipText", DXF_Loader.res.getString("openButtonToolTipText"));
		UIManager.put("FileChooser.upFolderToolTipText", DXF_Loader.res.getString("upFolderToolTipText"));
		UIManager.put("FileChooser.homeFolderToolTipText", DXF_Loader.res.getString("homeFolderToolTipText"));
		UIManager.put("FileChooser.newFolderToolTipText", DXF_Loader.res.getString("newFolderToolTipText"));
		UIManager.put("FileChooser.listViewButtonToolTipTextlist", DXF_Loader.res.getString("listViewButtonToolTipTextlist"));
		UIManager.put("FileChooser.detailsViewButtonToolTipText", DXF_Loader.res.getString("detailsViewButtonToolTipText"));
		UIManager.put("FileChooser.saveButtonText", DXF_Loader.res.getString("saveButtonText"));
		UIManager.put("FileChooser.saveButtonToolTipText", DXF_Loader.res.getString("saveButtonToolTipText"));
		UIManager.put("FileChooser.updateButtonText", DXF_Loader.res.getString("updateButtonText"));
		UIManager.put("FileChooser.helpButtonText", DXF_Loader.res.getString("helpButtonText"));
		UIManager.put("FileChooser.helpButtonToolTipText", DXF_Loader.res.getString("helpButtonToolTipText"));
		UIManager.put("FileChooser.updateButtonToolTipText", DXF_Loader.res.getString("updateButtonToolTipText"));
		SwingUtilities.updateComponentTreeUI(jfc);
	}

	public void saveDXF(){
		
		saveAsDXF();
		
		/*if (_mc._dxf._u._filename == null) {
			saveAsDXF();
		} 
		else if(_mc == null){
			JOptionPane.showMessageDialog(new Frame(), DXF_Loader.res.getString("myJMenu.23"),DXF_Loader.res.getString("myJMenu.24"), JOptionPane.WARNING_MESSAGE);
		} 
		else {		
			try {
				_mc._dxf.write(_mc._dxf.lastOpenDXF.concat(_mc._dxf.lastOpenFile));				
				_mc._dxf._u.currLayer = _mc._dxf._u._myTables.elementAt(0)._myLayers.elementAt(0);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			_mc._dxf.tree.updateEnv();
		}*/
	}

	public void saveAsDXF(){
		
		if(_mc == null){
			JOptionPane.showMessageDialog(new Frame(), DXF_Loader.res.getString("myJMenu.23"),"Attention", JOptionPane.WARNING_MESSAGE);
		} 
		else {
		
			String absFile="";
			JFileChooser chooser = new JFileChooser();
			doIntlFileChooser(chooser);
	
			chooser.setDialogTitle(DXF_Loader.res.getString("menuItemSaveFichier"));
			chooser.setApproveButtonText(DXF_Loader.res.getString("saveButtonText"));
			chooser.setApproveButtonToolTipText(DXF_Loader.res.getString("saveButtonToolTipText"));
	
			chooser.addChoosableFileFilter(new myFilter(new String[]{"dxf"},DXF_Loader.res.getString("FILE_FILTER")));
			chooser.setCurrentDirectory(new File(_mc._dxf.lastSaveDXF));
			chooser.setAcceptAllFileFilterUsed(false);
			
			int retour=0;
			boolean go=false;
	
			while (!go) {
				chooser.setSelectedFile(new File(absFile));
				retour = chooser.showOpenDialog(getParent());
				if (retour == JFileChooser.CANCEL_OPTION) { //Bouton annuler
					_mc._dxf.lastSaveDXF=chooser.getCurrentDirectory().getAbsolutePath();
					return;
				}
				
				absFile = chooser.getSelectedFile().getAbsolutePath();
				_mc._dxf.lastSaveAsImg=absFile;
				if (!absFile.substring(absFile.length()-4, absFile.length()).equalsIgnoreCase(".dxf"))		
					absFile+=".dxf";

			    UIManager.put("OptionPane.noButtonText", DXF_Loader.res.getString("N"));
			    UIManager.put("OptionPane.yesButtonText", DXF_Loader.res.getString("Y"));
				if ( (new File(absFile)).getAbsoluteFile().exists() ) {
					int n = JOptionPane.showConfirmDialog(
							_mc._dxf.frame, DXF_Loader.res.getString("ecraseFichier"),
							DXF_Loader.res.getString("existeFichier"),
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						go=true;
					} else {
						go=false;
					} 
				} else {
					go=true;
				}
			}
			if (go) {
				try {
					_mc._dxf._u._filename = absFile;
					_mc._dxf.write(absFile);				
					_mc._dxf._u.currLayer = _mc._dxf._u._myTables.elementAt(0)._myLayers.elementAt(0);
					menuItemRenommer.setEnabled(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	

	public static String[] unique(String[] strings) {
        Set<String> set = new HashSet<String>();
        for (int i=0; i<strings.length; i++) {
            String name = strings[i].toLowerCase();
            set.add(name);
        }
        return (String[])set.toArray(new String[0]);
    }
	
	public void exportAs(){
		String absFile="", extType="";
		File f=null;
		JFileChooser chooser = new JFileChooser();
		doIntlFileChooser(chooser);

		chooser.setDialogTitle(DXF_Loader.res.getString("menuItemExporter"));
		chooser.setApproveButtonText(DXF_Loader.res.getString("menuItemButExport"));
		chooser.setApproveButtonToolTipText(DXF_Loader.res.getString("menuItemButExport"));
		
		String[] writerFormat = ImageIO.getWriterFormatNames();
		writerFormat = unique(writerFormat);

		DXF_Loader.locale = new Locale("en", "US");
	    Locale.setDefault(DXF_Loader.locale);
	    
		for(int i=0; i< writerFormat.length; i++){
			chooser.addChoosableFileFilter(new myFilter(new String[]{writerFormat[i]},"Fichiers " + writerFormat[i].toUpperCase() + " (*."+ writerFormat[i] +")"));
		}
				
		if(!_mc._dxf.lastSaveAsImg.equals("")){
			chooser.setCurrentDirectory(new File(_mc._dxf.lastSaveAsImg));
		}
		int retour=0, n=0;
		boolean go=false;

		while (!go) {
			chooser.setSelectedFile(new File(absFile));
			retour = chooser.showOpenDialog(getParent());

			if (retour == JFileChooser.CANCEL_OPTION) { 
				return;
			}
			absFile = chooser.getSelectedFile().getAbsolutePath();
			extType = ((myFilter) chooser.getFileFilter()).lesSuffixes[0];
			_mc._dxf.lastSaveAsImg=absFile;

			if (!absFile.substring(absFile.length()-4, absFile.length()).equalsIgnoreCase("."+extType))		
				absFile+="."+extType;

			f = new File(absFile);
			if(f.exists()){
				go=false;

			    UIManager.put("OptionPane.noButtonText", DXF_Loader.res.getString("N"));
			    UIManager.put("OptionPane.yesButtonText", DXF_Loader.res.getString("Y"));
				n = JOptionPane.showConfirmDialog(
						_mc._dxf.frame, DXF_Loader.res.getString("ecraseFichier"),
						DXF_Loader.res.getString("existeFichier"),
						JOptionPane.YES_NO_OPTION);
				
				if (n == JOptionPane.YES_OPTION) {
					f = new File(absFile);
					go=true;
				} else {
					f = new File(absFile);
				}
			}
			else{
				go=true;
			}
		}
		if (go) {
			try {
				Rectangle r = _mc.getBounds();
				Image image = _mc.createImage(r.width, r.height);
				_mc.paint(image.getGraphics());
				
				if(!ImageIO.write((RenderedImage)image, extType, f)){
					f.delete();
					JOptionPane.showMessageDialog(_mc._dxf.frame, "[" + extType + "] : "+DXF_Loader.res.getString("unknownType"),"Erreur",JOptionPane.ERROR_MESSAGE);

				}
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}
	
}
