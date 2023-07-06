import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Base.Sprite;

public class Sor_Silver{
	private BufferedImage andando[]=new BufferedImage[10];
	private BufferedImage tomouDano[]=new BufferedImage[2];
	private BufferedImage parado[]=new BufferedImage[1];
	private BufferedImage ataque[]=new BufferedImage[5];
	private BufferedImage defesa[]=new BufferedImage[1];
	private BufferedImage pulando[]=new BufferedImage[11];


	public static final int PARADO=0;
	public static final int ANDANDO_ESQ=1;
    public static final int ANDANDO_DIR=2;
	public static final int ATACANDO=3;
	public static final int DEFENDENDO=4;
	public static final int PULANDO=5;
	public static final int TOMANDO_DANO=6;
	
	int atual;
	private BufferedImage imgAnda;
	private BufferedImage imgParado;
	private BufferedImage imgPulo;
	private BufferedImage imgDefesa;
	private BufferedImage imgDano;
	private BufferedImage imgAtaque;
	private int numAcao;

	public Sor_Silver() throws IOException{
		atual=JogoCliente.estado1;

		imgAnda=ImageIO.read(Sor_Silver.class.getResource("andando1.gif"));
		imgParado=ImageIO.read(Sor_Silver.class.getResource("parado1.gif"));
		imgPulo=ImageIO.read(Sor_Silver.class.getResource("pulando1.gif"));
		imgDefesa=ImageIO.read(Sor_Silver.class.getResource("defesa1.gif"));
		imgDano=ImageIO.read(Sor_Silver.class.getResource("dano1.gif"));
		imgAtaque=ImageIO.read(Sor_Silver.class.getResource("attack1.gif"));
		
		imagemAndando();
		imagemParado();
		imagemAtaca();
		imagemTomouDano();
		imagemPula();
		imagemDefendendo();
	}
	

	private void imagemDefendendo(){
		defesa[0]=imgDefesa.getSubimage(0, 0, 80, 86);
	}
	
	private void imagemAndando(){
		andando[0]=imgAnda.getSubimage(0, 0, 80, 86);
		andando[1]=imgAnda.getSubimage(0, 0, 80, 86);
		andando[2]=imgAnda.getSubimage(80, 0, 80, 86);
		andando[3]=imgAnda.getSubimage(80, 0, 80, 86);
		andando[4]=imgAnda.getSubimage(160, 0, 80, 86);
		andando[5]=imgAnda.getSubimage(160, 0, 80, 86);
		andando[6]=imgAnda.getSubimage(240, 0, 60, 86);
		andando[7]=imgAnda.getSubimage(240, 0, 60, 86);
		andando[8]=imgAnda.getSubimage(310, 0, 57, 86);
		andando[9]=imgAnda.getSubimage(310, 0, 57, 86);
	}
	
	// coloquei imagemParado() public para poder acessar no JogoServidor
	private void imagemParado(){
		parado[0]=imgParado.getSubimage(0, 0, 80, 86);
	}
	
	private void imagemAtaca(){
		ataque[0]=imgAtaque.getSubimage(0, 0, 84, 86);
		ataque[1]=imgAtaque.getSubimage(86, 0, 84, 86);
		ataque[2]=imgAtaque.getSubimage(172, 0, 84, 86);
		ataque[3]=imgAtaque.getSubimage(258, 0, 84, 86);
		ataque[4]=imgAtaque.getSubimage(344, 0, 84, 86);
	}

	private void imagemPula(){
		pulando[0]=imgPulo.getSubimage(10, 0, 80, 86);
		pulando[1]=imgPulo.getSubimage(10, 0, 80, 86);
		pulando[2]=imgPulo.getSubimage(90, 0, 80, 86);
		pulando[3]=imgPulo.getSubimage(90, 0, 80, 86);
		pulando[4]=imgPulo.getSubimage(90, 0, 80, 86);
		pulando[5]=imgPulo.getSubimage(90, 0, 80, 86);
		pulando[6]=imgPulo.getSubimage(90, 0, 80, 86);
		pulando[7]=imgPulo.getSubimage(90, 0, 80, 86);
		pulando[8]=imgPulo.getSubimage(160, 0, 80, 86);
		pulando[9]=imgPulo.getSubimage(160, 0, 80, 86);
		pulando[10]=imgPulo.getSubimage(160, 0, 80, 86);
	}
	
	private void imagemTomouDano(){
		tomouDano[0]=imgDano.getSubimage(0, 0, 80, 86);
		tomouDano[1]=imgDano.getSubimage(60, 0, 80, 86);
	}
	
	private BufferedImage tomaDano(){
		if(numAcao>=1){
			numAcao=0;
			JogoCliente.flag2=1;
			JogoCliente.imgEstado1=PARADO;
		}
		BufferedImage img=tomouDano[numAcao];
		numAcao++;
		return img;
	}
	
	private BufferedImage anda(){
		if(numAcao>8){
			numAcao=0;
		}
		BufferedImage img=andando[numAcao];
		numAcao++;
		return img;
	}

	private BufferedImage pula(){
		if(numAcao>10){
			numAcao=0;
			JogoCliente.flag2=1;
			JogoCliente.imgEstado1=PARADO;
		}
		BufferedImage img=pulando[numAcao];
		numAcao++;
		return img;
	}
	
	private BufferedImage ataque(){
		if(numAcao>4){
			numAcao=0;
			JogoCliente.flag2=1;
			JogoCliente.imgEstado1=PARADO;
		}
		BufferedImage img=ataque[numAcao];
		numAcao++;
		return img;
	}
	
	private BufferedImage paradao(){
		BufferedImage img=parado[0];
		return img;
	}

	private BufferedImage defesa(){
		BufferedImage img=defesa[0];
		return img;
	}
	
	public BufferedImage Imagem(int acao){
		//JogoCliente.flag2=0;
		if(acao==ANDANDO_ESQ || acao==ANDANDO_DIR) return anda();
		else if(acao==ATACANDO) return ataque();
		else if(acao==DEFENDENDO) return defesa();
		else if(acao==TOMANDO_DANO) return tomaDano();
		else if(acao==PULANDO) return pula();
		return paradao();	
	}
}