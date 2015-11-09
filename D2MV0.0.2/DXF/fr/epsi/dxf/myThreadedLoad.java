package fr.epsi.dxf;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileReader;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import fr.epsi.dxf.Entities.myBufferedReader;
import fr.epsi.dxf.Graphics.myHistory;
import fr.epsi.dxf.Graphics.myLog;
import fr.epsi.dxf.Header.myHeader;

public class myThreadedLoad implements Runnable {

	private DXF_Loader _loader;
	private File Fichier;
	public  static JProgressBar mybar = new JProgressBar(0,100);
	public static JDialog j;
	
	public myThreadedLoad(DXF_Loader loader, File fichier) {
		_loader=loader;
		Fichier=fichier;
		j = new JDialog();
		//j.setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/dxf.jpg")).getImage());
		j.setTitle(DXF_Loader.res.getString("WAIT"));
	   	j.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - j.getWidth(), (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - j.getHeight());
	   	JPanel myDialContent = new JPanel();
		mybar.setStringPainted(true);
		j.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		Box mybox = Box.createVerticalBox();
		//JLabel myLabel = new JLabel("blablal");
		//mybox.add(myLabel);
		mybox.add(mybar);
		myDialContent.add(mybox);		

		_loader.info.setText(DXF_Loader.res.getString("WAIT"));	
		_loader.newDXF();
		
		j.getContentPane().add(myDialContent);
		j.pack();
		j.setVisible(true);
		(new Thread(this)).start();
	}
	
	public void run() {
		
		myBufferedReader 	br 	= null;
		String ligne;
		try {
			
			br = new myBufferedReader(new FileReader(Fichier.getAbsolutePath()));
			mybar.setMaximum((int) Fichier.length()/6);
		}
		catch(Exception e){
			myLog.writeLog(DXF_Loader.res.getString("NOK_OPEN"));
			_loader.info.setText(DXF_Loader.res.getString("IMP_OPEN"));
		}
		_loader.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		j.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try{
			while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("EOF")){
				ligne = ligne.toUpperCase();
				if(ligne.equals("HEADER")){
					_loader._u._myTables.removeAllElements();
					_loader._u._myBlocks.removeAllElements();

					_loader._u._header = myHeader.read(br, _loader._u);
					if (_loader._u._header == null) {
						br.close();
						throw new Exception("Header corrompu");
					}
					else if(_loader._u._header._EXTMAX==null || _loader._u._header._EXTMIN==null){
						_loader._u._header = new myHeader();
					}
				} else if(ligne.equals("TABLES")){
					_loader._u.readTables(br);
				} else if(ligne.equals("BLOCKS")){
					_loader._u.readBlocks(br);
				} else if(ligne.equals("ENTITIES")){
					_loader._u.readEntities(br);
				}				
			}
			br.close();
						
			_loader._u._filename = Fichier.getName();

			DXF_Loader._mc._dxf._u.updateRefBlock();
			DXF_Loader._mc._dxf.updateLineTypeCombo();
			_loader.tree.createNodes();
			
			mybar.setValue(mybar.getMaximum());
			
		} catch(Exception e){
			e.printStackTrace();
			myLog.writeLog(DXF_Loader.res.getString("ERR_LOAD"));
			_loader.info.setText(DXF_Loader.res.getString("IMP_LOAD"));
		}		
		_loader.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		_loader.cadrer();
		myHistory.resetHistory();
		_loader.info.setText(DXF_Loader.res.getString("READY"));
		j.setVisible(false);

		myLog.writeLog(" >------ END" + Fichier.getName() + " ------<");
	}

}
