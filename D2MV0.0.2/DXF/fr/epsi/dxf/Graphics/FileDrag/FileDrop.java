package fr.epsi.dxf.Graphics.FileDrag;

public class FileDrop
{
   private transient javax.swing.border.Border normalBorder;
   private transient java.awt.dnd.DropTargetListener dropListener;
        
    private static java.awt.Color defaultBorderColor = new java.awt.Color( 0f, 0f, 1f, 0.25f );
   
    public FileDrop(
    final java.awt.Component c,
    final Listener listener )
    {   this( null, 
              c,  
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), // Drag border
              true, 
              listener );
    }  
    
    public FileDrop(
    final java.awt.Component c,
    final boolean recursive,
    final Listener listener )
    {   this( null, 
              c, 
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), // Drag border
              recursive,
              listener );
    }
    
    public FileDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final Listener listener )
    {   this( out, 
              c, 
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), 
              false,
              listener );
    } 
    
    public FileDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final boolean recursive,
    final Listener listener)
    {   this( out,
              c,
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), // Drag border
              recursive,
              listener );
    } 

    public FileDrop(
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final Listener listener) 
    {   this(
            null,
            c,
            dragBorder,
            false,
            listener);
    } 
   
    public FileDrop(
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final boolean recursive,
    final Listener listener) 
    {   this(
            null,
            c,
            dragBorder,
            recursive,
            listener );
    }
 
    public FileDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final Listener listener) 
    {   this(
            out, 
            c, 
            dragBorder,
            false,
            listener );
    } 
    
    public FileDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final boolean recursive,
    final Listener listener) 
    {   
        dropListener = new java.awt.dnd.DropTargetListener()
        {	public void dragEnter( java.awt.dnd.DropTargetDragEvent evt )
            {	      
                if( isDragOk( out, evt ) )
                {
                    if( c instanceof javax.swing.JComponent )
                    {   javax.swing.JComponent jc = (javax.swing.JComponent) c;
                        normalBorder = jc.getBorder();
                        jc.setBorder( dragBorder );
                    } 
                    evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY );
                } 
                else 
                {
                    evt.rejectDrag();
                } 
            } 

            public void dragOver( java.awt.dnd.DropTargetDragEvent evt ) { }   // end dragOver

            public void drop( java.awt.dnd.DropTargetDropEvent evt ) {
                try
                { 
                    java.awt.datatransfer.Transferable tr = evt.getTransferable();

                    if (tr.isDataFlavorSupported (java.awt.datatransfer.DataFlavor.javaFileListFlavor))
                    {
                        evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY );

                        java.util.List<?> fileList = (java.util.List) 
                            tr.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                        //java.util.Iterator iterator = fileList.iterator();

                        java.io.File[] filesTemp = new java.io.File[ fileList.size() ];
                        fileList.toArray( filesTemp );
                        final java.io.File[] files = filesTemp;

                        if( listener != null )
                            listener.filesDropped( files );

                        evt.getDropTargetContext().dropComplete(true);
                    }   
                    else 
                    {   
                    	evt.rejectDrop();
                    }   
                } 
                catch ( java.io.IOException io) 
                {   
                    evt.rejectDrop();
                }   
                catch (java.awt.datatransfer.UnsupportedFlavorException ufe) 
                {   
                	ufe.printStackTrace( out );
                    evt.rejectDrop();
                }   
                finally
                {
                    if( c instanceof javax.swing.JComponent )
                    {   javax.swing.JComponent jc = (javax.swing.JComponent) c;
                        jc.setBorder( normalBorder );
                    }   
                }   
            }  

            public void dragExit( java.awt.dnd.DropTargetEvent evt ) 
            {   
            	if( c instanceof javax.swing.JComponent )
                {   javax.swing.JComponent jc = (javax.swing.JComponent) c;
                    jc.setBorder( normalBorder );
                }  
            }  

            public void dropActionChanged( java.awt.dnd.DropTargetDragEvent evt ) 
            {   
            	if( isDragOk( out, evt ) )
                {   
            		evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY );
                }   
                else 
                {   evt.rejectDrag();
                }  
            }  
        }; 

        makeDropTarget( out, c, recursive );
    }  
    
    private void makeDropTarget( final java.io.PrintStream out, final java.awt.Component c, boolean recursive )
    {
        final java.awt.dnd.DropTarget dt = new java.awt.dnd.DropTarget();
        try
        {   dt.addDropTargetListener( dropListener );
        } 
        catch( java.util.TooManyListenersException e ) { }

        c.addHierarchyListener( new java.awt.event.HierarchyListener()
        {   public void hierarchyChanged( java.awt.event.HierarchyEvent evt )
            {   
                java.awt.Component parent = c.getParent();
                if( parent == null ) 
                	c.setDropTarget( null );
                else
                	new java.awt.dnd.DropTarget(c, dropListener);
            }
        });
        if( c.getParent() != null )
            new java.awt.dnd.DropTarget(c, dropListener);
        
        if( recursive && (c instanceof java.awt.Container ) )
        {   
            java.awt.Container cont = (java.awt.Container) c;
            
            java.awt.Component[] comps = cont.getComponents();
            
            for( int i = 0; i < comps.length; i++ )
                makeDropTarget( out, comps[i], recursive );
        }   
    }  

    private boolean isDragOk( final java.io.PrintStream out, final java.awt.dnd.DropTargetDragEvent evt )
    {   boolean ok = false;
        
        java.awt.datatransfer.DataFlavor[] flavors = evt.getCurrentDataFlavors();
        
        int i = 0;
        while( !ok && i < flavors.length )
        {   
            if( flavors[i].equals( java.awt.datatransfer.DataFlavor.javaFileListFlavor ) )
                ok = true;            
            i++;
        }          
        return ok;
    }  
    
    public static boolean remove( java.awt.Component c) {
    	return remove( null, c, true );
    }  

    public static boolean remove( java.io.PrintStream out, java.awt.Component c, boolean recursive )
    {   
        c.setDropTarget( null );
        if( recursive && ( c instanceof java.awt.Container ) )
        {   java.awt.Component[] comps = ((java.awt.Container)c).getComponents();
            for( int i = 0; i < comps.length; i++ )
                remove( out, comps[i], recursive );
            return true;
        }   
        else return false;
    } 
    
    public interface Listener
    {   
        public abstract void filesDropped( java.io.File[] files );
    }      
} 