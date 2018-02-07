package maze;

import java.awt.Image;
import javax.swing.ImageIcon;

//Questa classe rappresenta il "Personaggio" verde all'interno del labirinto
//Esso è rappresentato dall'immagine player.png la quale all'interno della board ha coordinate in x e y
public class Player {
	private double tileX,tileY;
	private Image player;
	public boolean pressOut = false;		//Rappresenta quando con il mouse vado a premere fuori dalla foto
	
	public Player() {
		ImageIcon img= new ImageIcon("risorse/images/player.png");
		player = img.getImage();
		
		tileX = 32;
		tileY = 32;		
		
	}
	
	public Image getPlayer() {
		return player;
	}
	
	//Ritorna la coordinata x
	public double getTileX() {
		return tileX;
	}
	//Ritorna la coordinata y
	public double getTileY() {
		return tileY;
	}
	//Funzione che effettua il movimento del player all'interno del gioco
	public void move(double dx, double dy) {
		tileX += dx;
		tileY += dy;
	}
}
