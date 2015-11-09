package fr.epsi.dxf.Header;

import fr.epsi.dxf.Entities.*;

public class myStats {

	public static int nbPoint		=0;
	public static int nbLine		=0;
	public static int nbArc			=0;
	public static int nbCercle		=0;
	public static int nbEllipse		=0;
	public static int nbPolyline	=0;
	public static int nbLwPolyline	=0;
	public static int nbLayer		=0;
	public static int nbInsert		=0;
	public static int nbDimension	=0;
	public static int nbSolid		=0;
	public static int nbTrace		=0;
	public static int nbText		=0;
	public static int nbMText		=0;
	public static int nbLineType 	=0;
		
	public static void reset(){
		nbPoint		=0;
		nbLine		=0;
		nbArc		=0;
		nbCercle	=0;
		nbEllipse	=0;
		nbPolyline	=0;
		nbLwPolyline=0;
		nbLayer		=0;
		nbInsert	=0;
		nbDimension	=0;
		nbSolid		=0;
		nbTrace		=0;
		nbText		=0;
		nbMText		=0;	
	}
	
	public static void decrease(myEntity ent){
		
		if(ent instanceof myPoint){
			nbPoint-=1;
		}else if(ent instanceof myLine){
			nbLine-=1;
		}else if(ent instanceof myArc){
			nbArc-=1;
		}else if(ent instanceof myCircle){
			nbCercle-=1;
		}else if(ent instanceof myPolyline){
			nbPolyline-=1;
		}else if(ent instanceof myLwPolyline){
			nbLwPolyline-=1;
		}else if(ent instanceof myEllipse){
			nbEllipse-=1;
		}else if(ent instanceof myText){
			nbText-=1;
		}else if(ent instanceof myTrace){
			nbTrace-=1;
		}else if(ent instanceof mySolid){
			nbSolid-=1;
		}else if(ent instanceof myDimension){
			nbDimension-=1;
		}else if(ent instanceof myInsert){
			nbInsert-=1;
		}
	}
}
