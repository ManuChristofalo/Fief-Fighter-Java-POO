//Trabalho feito por:
//Manuele S. Christófalo - RA 221026291
//Paulo Henrique Dionysio - RA 221026169

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.*;

import javax.imageio.ImageIO;



public class JogoCliente extends JFrame implements Runnable{
    private static final String SERVER_IP="localhost";
    private static final int PORT=1234;
    private static final int WIDTH=1300;
    private static final int HEIGHT=700;
    private BackgroundPanel backgroundPanel;
    static JogoCliente tela;
    
    public static boolean defendendo = false;
    public static boolean atacando = false;
    public static boolean andandoDir = false;
    public static boolean andandoEsq = false;
    public static boolean pulando = false;

    public static final int PARADO=0;
	public static final int ANDANDO_ESQ=1;
    public static final int ANDANDO_DIR=2;
	public static final int ATACANDO=3;
	public static final int DEFENDENDO=4;
	public static final int PULANDO=5;
	public static final int TOMANDO_DANO=6;
    public static int atual;
    public int velocidade;

    static int flag;
    static int flag2;

    private JogoCliente.BarraVida prataVida;
	private BarraVida vermelhoVida;

    private Sor_Silver cavPrata;
	private Lord_Bordeaux cavVermelho;

    public static int VIDA1=500;
    public static int VIDA2=500;
    public static boolean GAMEOVER = false;
	static int posX1 = 70;
	static int posX2 = 950;
    static int posY1 = 300;
    static int posY2 = 300;
	static int estado1 = PARADO;
	static int estado2 = PARADO;
    static int imgEstado1;
	static int imgEstado2;

    Socket socket;
    DataOutputStream outStream;
    DataInputStream inStream;
    int acao;
    
    Thread thread;


    //1 | CONFIGURAÇÃO DA JANELA
    public JogoCliente(){
        //1.1- Base
        setTitle("Fief Fighter - Jogador");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);

        acaoExec();

        //1.2- Background
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.add(prataVida);
        backgroundPanel.add(vermelhoVida);
        backgroundPanel.setBounds(0, 0, WIDTH, HEIGHT); 
        getContentPane().add(backgroundPanel);

        //1.3- Barras de Vida
        prataVida = new BarraVida();
        prataVida.BarraDeVida(50, "Sor Silver");
        vermelhoVida = new BarraVida();
        vermelhoVida.BarraDeVida(1300/2+150, "Lorde Bordeaux");

        //1.4- Outras configurações
        setLocationRelativeTo(null);
        setVisible(true);
        this.setFocusable(true);

        try {
            socket = new Socket("localhost", 1234);
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class BarraVida{
        private String playerName;
        protected int x;
        protected int y;
        protected int w;
        protected int h;

        public void BarraDeVida(int x , String playerName){
            this.x=x;
            y=50;
            h=45;
            
            this.playerName=playerName;
        }
        
        public void printBox(Graphics pen){
            pen.setColor(Color.RED);
            pen.fillRect(x-5, y, 480, h);
            pen.setColor(Color.GREEN);
            if(playerName=="Sor Silver"){
                if(VIDA1>=20) pen.fillRect(x-5, y, VIDA1-20, h);
                else pen.fillRect(x-5, y, 0, h);
            }
            else{
                if(VIDA2>=20) pen.fillRect(x-5, y, VIDA2-20, h);
                else pen.fillRect(x-5, y, 0, h);
            }
            pen.setColor(Color.WHITE);
            pen.setFont(new Font("times", Font.BOLD, 30));
            pen.drawString(playerName, x, y+h+30);
        }
    }

    //1.3- Configuração do plano de fundo
    private class BackgroundPanel extends JPanel {
        BufferedImage imageBg;
        
        
        public BackgroundPanel() {
            loadBackgroundImage();
            setFocusable(true);
        }

        public void add(JogoCliente.BarraVida vida) {
        }

        private void loadBackgroundImage() {
            try {
                cavPrata = new Sor_Silver();
                cavVermelho = new Lord_Bordeaux();
                imageBg = ImageIO.read(getClass().getResource("bg.png"));
                System.out.println("Background carregado!");
            } catch (IOException ex) {
                System.out.println("Falha ao carregar imagem de background...");
                System.exit(0);
            }
        }

        protected void paintComponent(Graphics pen) {
            super.paintComponent(pen);
            pen.drawImage(imageBg, 0, 0, 1300, 700, null);
            prataVida.printBox(pen);
            vermelhoVida.printBox(pen);
            pen.drawImage(cavPrata.Imagem(imgEstado1), posX1, posY1, 230, 250, null);
            pen.drawImage(cavVermelho.Imagem(imgEstado2), posX2, posY2, 230, 250, null);
            if(VIDA1<=0 || VIDA2<=0){
                pen.setColor(Color.RED);
                pen.setFont(new Font("times",Font.BOLD, 50));
                pen.drawString("Game Over", 1350/2-145, 300);
            }
        }

    }
    



    //2 | INTERAÇÃO COM O SERVIDOR (ENVIO DAS AÇÕES)
    @Override
    public void run(){
        try{
            //3.1- Comunicação i/o com o servidor
           // acaoExec();
            System.out.println("Thread rodando!");


            //3.2- Execução principal
            while(true){
                //3.2.1- Leitura da ação

                flag2=0;
                posX1=inStream.readInt();
                posX2=inStream.readInt();
                posY1=inStream.readInt();
                posY2=inStream.readInt();
                estado1=inStream.readInt();
                estado2=inStream.readInt();
                VIDA1=inStream.readInt();
                VIDA2=inStream.readInt();
                //GAMEOVER=inStream.readBoolean();
                flag2=inStream.readInt();

                //3.2.4- Atualiza a interface GUI
                if(flag2==1){
                    imgEstado1=estado1;
                    imgEstado2=estado2;
                }
                flag2=0;

                atualizaGUI();
                atual=PARADO;
                if(VIDA1<=0 || VIDA2<=0){
                    thread.sleep(2000);
                    System.exit(1);
                }
            }

        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Thread não rodou!");
            //JogoServidor.removerJogador(this); //Jogador desconectado
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }



    //3 | FUNÇÕES DE LEITURA DAS AÇÕES
    public void acaoExec(){
        flag=0;
		this.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_SHIFT){
                    atual=PARADO;
                    flag=1; 
                }
                else if(e.getKeyCode()==KeyEvent.VK_D){
                    atual=PARADO;
                    flag=1;
                }
                else if(e.getKeyCode()==KeyEvent.VK_A){
                    atual=PARADO;
                    flag=1;
                }
                try {
                    outStream.writeInt(atual);
                    outStream.writeInt(flag);
                    outStream.flush();
                    //flag=false;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
			}
			
			@Override
			public void keyPressed(KeyEvent e){
                flag=0;
				if(e.getKeyCode()==KeyEvent.VK_A){
                    atual=ANDANDO_ESQ;
                    flag=1;
                }
				
				else if(e.getKeyCode()==KeyEvent.VK_D){
                    atual=ANDANDO_DIR;
                    flag=1;
                }
				
				else if(e.getKeyCode()==KeyEvent.VK_SHIFT){
                    atual=DEFENDENDO;
                    flag=1;
                }
				
				else if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    atual=ATACANDO;
                    flag=1;
                }

				else if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    atual=PULANDO;
                    flag=1;
                }

                System.out.println("Tecla pressionada " + e.getKeyChar());
                try {
                    outStream.writeInt(atual);
                    outStream.writeInt(flag);
                    outStream.flush();
                    flag=0;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
			}
		});
	}

    private void atualizaGUI(){
        tela.repaint();
        backgroundPanel.repaint();
    }

    //5 | MAIN
    public static void main(String[] args){
        System.out.println("Iniciando a tela...");
        new TelaDeSplash();
        tela=new JogoCliente();
    }

}