package fr.epsi.dxf.Graphics.TreeView;

import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fr.epsi.dxf.Entities.myEntity;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myTable;

class myDropTargetListener implements DropTargetListener {
	myJTree _refTree;

	DefaultMutableTreeNode trans;

	DefaultMutableTreeNode dropTarget;

	public myDropTargetListener(myJTree refTree) {
		_refTree = refTree;
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		trans = (DefaultMutableTreeNode) _refTree.getSelectionPath().getLastPathComponent();
		if (trans.getUserObject() instanceof myEntity) {
			myLog.writeLog("[drag Enter] Entity");
		} else {
			trans = null;
			myLog.writeLog("[drag enter] Refuse : not an entity");
		}
	}

	public void dragOver(DropTargetDragEvent dtde) {
		//myLog.writeLog("drag Over");
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		myLog.writeLog("drop Action Changed");
	}

	public void dragExit(DropTargetEvent dte) {
		myLog.writeLog("drag Exit");
	}

	public void drop(DropTargetDropEvent dtde) {
		DefaultMutableTreeNode parentNodeSource;
		myEntity entity = null;

		if ((trans==null) || (trans.getUserObject() instanceof myLayer) || (trans.getUserObject() instanceof myTable) ){
			myLog.writeLog("[drag enter] Refuse : not an entity");
			return;
		}
		
		if (trans == null) {
			myLog.writeLog("[Drop] returning : no transert, objet null");
			return;
		} else {
			parentNodeSource = (DefaultMutableTreeNode) trans.getParent();
		}

		Point dropLocation = dtde.getLocation();
		TreePath path = _refTree.getPathForLocation(dropLocation.x,dropLocation.y);
		dropTarget = (DefaultMutableTreeNode) path.getLastPathComponent();
		
		if(!(dropTarget.getUserObject() instanceof myLayer)){
			myLog.writeLog("[Drop] returning : the source must be a layer ");
			return;
		}
			
		
		if (dropTarget.isNodeAncestor(trans)) {
			myLog.writeLog("[Drop] returning : Recursivity detected ");
			return;
		} else {

			myLog.writeLog("[Drop] doing stuff ");
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) trans.getParent();

			if (parent == null)
				myLog.writeLog("[Drop] Cannot remove from source : parent is null");
			else
				myLog.writeLog("[Drop] Remove from source : parent = " + parent.getUserObject());

			trans.removeFromParent();
			_refTree.setModel(new DefaultTreeModel(((DefaultMutableTreeNode) ((DefaultTreeModel) _refTree.getModel()).getRoot())));

			if (trans.getUserObject() instanceof myEntity) {
				entity = (myEntity) trans.getUserObject();

				if (parentNodeSource.getUserObject() instanceof myLayer) {
					if (dropTarget.getUserObject() instanceof myLayer) {
						myLog.writeLog("remove to new layer");
						entity._refLayer._myEnt.remove(trans.getUserObject());

						myLog.writeLog("switch layer");
						entity._refLayer = (myLayer) dropTarget.getUserObject();

						myLog.writeLog("add to new layer");
						entity._refLayer._myEnt.add((myEntity) trans.getUserObject());

						dropTarget.add(trans);
						myLog.writeLog("[Drop] stuff done !!");
						_refTree.updateSelection((DefaultMutableTreeNode) trans);
					}
				}
			}
		}
	}
}