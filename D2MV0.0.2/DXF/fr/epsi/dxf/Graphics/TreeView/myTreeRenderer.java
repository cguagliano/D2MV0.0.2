package fr.epsi.dxf.Graphics.TreeView;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Entities.myEntity;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myHeader;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myTable;

class myTreeRenderer extends DefaultTreeCellRenderer implements Transferable  {
	private static final long serialVersionUID = 1L;

	public myTreeRenderer(){
		this.setLeafIcon(new ImageIcon(ClassLoader.getSystemResource("images/edit.gif")));
		this.setClosedIcon(new ImageIcon(ClassLoader.getSystemResource("images/FolderClose16.gif")));
		this.setOpenIcon(new ImageIcon(ClassLoader.getSystemResource("images/FolderOpen16.gif")));
	}

	DataFlavor labelFlavor = new DataFlavor(JLabel.class, "label");
	DataFlavor[] supportedFlavors = { labelFlavor };

	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.equals(labelFlavor))
			return true;
		else
			return false;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return this;
	}
	
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) 
	{

		try{
			super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row, hasFocus);

			Object parentObj=null;

			if( value == null){
				return this;
			}
			else{
				if(((DefaultMutableTreeNode) value).getUserObject() instanceof myUnivers){
					setIcon(new ImageIcon(ClassLoader.getSystemResource("images/ico.png")));
				}else if(((DefaultMutableTreeNode) value).getUserObject() instanceof myLabel){
					setToolTipText(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code);
				}
				
				if(((DefaultMutableTreeNode)((DefaultMutableTreeNode) value).getParent()) == null){
					return this;
				}
				else{
					parentObj = ((DefaultMutableTreeNode)((DefaultMutableTreeNode) value).getParent()).getUserObject();
					if(parentObj == null){
						return this;
					}	
				}					
			}	

			// UNIVERS
			if(parentObj instanceof myUnivers){
				if(((DefaultMutableTreeNode) value).getUserObject() instanceof myHeader)
					setIcon(new ImageIcon(ClassLoader.getSystemResource("images/header.gif")));
				
				else if(((DefaultMutableTreeNode) value).getUserObject() instanceof myLabel) {
					if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.RATIO)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/ratio.gif")));
					}
					else if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.MOVE_X)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/left-right.gif")));
					}
					else if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.MOVE_Y)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/up-down.gif")));
					}
					else if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.CUR_LAYER)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/layer-vierge.gif")));
					}
					else if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.CUR_BLOCK)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/block.png")));							
					}
					else if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.RATIOSTEP)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/zoom.gif")));
					}
					else if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.LST_BLOCK)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/blocks.gif")));							
					}
					else if(((myLabel) ((DefaultMutableTreeNode) value).getUserObject())._code.equals(myLabel.LST_TABLE)){
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/tables.gif")));							
					}
					
				}
			}
			// HEADER
			else if(parentObj instanceof myHeader){
				setIcon(new ImageIcon(ClassLoader.getSystemResource(((myHeader)parentObj).getIconName((myLabel) ((DefaultMutableTreeNode) value).getUserObject()))));				
			}
			// TABLE
			else if(parentObj instanceof myTable){
				if(((DefaultMutableTreeNode) value).getUserObject() instanceof myLayer){
					if(leaf)
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/layer-vierge.gif")));
					else
						setIcon(new ImageIcon(ClassLoader.getSystemResource("images/layer.gif")));	
				}
				else if(((DefaultMutableTreeNode) value).getUserObject() instanceof myLineType){
					setIcon(new ImageIcon(ClassLoader.getSystemResource("images/type_ligne.gif")));
				}					
			}
			// OBJ
			else if(parentObj instanceof myEntity && ((DefaultMutableTreeNode) value).getUserObject() instanceof myLabel){
				setIcon(new ImageIcon(ClassLoader.getSystemResource(((myEntity)parentObj).getIconName((myLabel) ((DefaultMutableTreeNode) value).getUserObject()))));
			}
			// LABEL
			else if(parentObj instanceof myLabel ){
				if(((DefaultMutableTreeNode) value).getUserObject() instanceof myTable)
					setIcon(new ImageIcon(ClassLoader.getSystemResource("images/table.gif")));
				else if(((DefaultMutableTreeNode) value).getUserObject() instanceof myBlock)
					setIcon(new ImageIcon(ClassLoader.getSystemResource("images/block.png")));
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		
		return this;
	}
}
