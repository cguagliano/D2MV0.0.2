package fr.epsi.dxf.Graphics.FileDrag;

public class FileDropEvent extends java.util.EventObject
{
	private static final long serialVersionUID = 1L;
	private java.io.File[] files;

    public FileDropEvent( java.io.File[] files, Object source )
    {   super( source );
        this.files = files;
    }  

    public java.io.File[] getFiles()
    {   return files;
    }  
}  