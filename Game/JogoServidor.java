//Trabalho feito por:
//Manuele S. Christófalo - RA 221026291
//Paulo Henrique Dionysio - RA 221026169


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;



public class JogoServidor{
    
    //1 | INICIALIZAÇÃO DO SERVIDOR
    public static void main(String[] args){
    ServerSocket serverSocket = null;
        try{
        serverSocket = new ServerSocket(1234);
        System.out.println("Servidor iniciado. Aguardando jogadores...");
        } catch(IOException e){
            e.printStackTrace();
        }
        

        try{
            //1.1 - Conexão do jogador 1
            Socket jogador1Socket = serverSocket.accept();
            System.out.println("Jogador 1 conectado.");

            //1.2 - Conexão do jogador 2
            Socket jogador2Socket = serverSocket.accept();
            System.out.println("Jogador 2 conectado.");

            //1.3 - Inicizalização e configuração dos jogadores
            new Servindo(jogador1Socket, jogador2Socket);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}


class Servindo{

    public static final int PARADO=0;
	public static final int ANDANDO_ESQ=1;
    public static final int ANDANDO_DIR=2;
	public static final int ATACANDO=3;
	public static final int DEFENDENDO=4;
	public static final int PULANDO=5;
	public static final int TOMANDO_DANO=6;
    public static int VIDA1 = 500;
    public static int VIDA2 = 500;
    public static boolean GAMEOVER = false;
	int posX1 = 70;
	int posX2 = 950;
    int posY1 = 300;
    int posY2 = 300;
	public static int estado1 = PARADO;
	int estado2 = PARADO;
    int flag;
    int flag1;
    int flag2;
    //posX1 e posX2 serão instanciados logo que o cliente estiver pronto

    DataOutputStream outStream[] = new DataOutputStream[2];
    Socket jogador1Socket;
    Socket jogador2Socket;

    //classe interna para tratar as conexões dos clientes
    class ThreadCliente extends Thread{
        Socket cli;
        int numCliente;

        ThreadCliente(Socket cli, int numCliente){
            this.cli = cli;
            this.numCliente = numCliente;
        }

        public void run(){
            DataInputStream inStream;
            try {
                inStream = new DataInputStream(cli.getInputStream());
                outStream[numCliente] = new DataOutputStream(cli.getOutputStream());
                
                do{
                    int recebeTecla = inStream.readInt();
                    flag = inStream.readInt();
                    System.out.println("Recebeu tecla: " + recebeTecla);
                    //Boolean silverCav = inStream.readBoolean();
                    //Boolean redCav = inStream.readBoolean();
                    //flagFinal=false;
                    flag1=0;
                    flag2=0;
                    jogoLogica(recebeTecla, flag);
                    enviaDados();
                    estado1=PARADO;
                    estado2=PARADO;
                }while(true);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        

        void jogoLogica(int recebeTecla, int flag) {
            if (numCliente == 0) {
                if(flag==1){
                    flag1=1;
                    flag2=1;
                }
                if (recebeTecla == ANDANDO_DIR) {
                    estado1=ANDANDO_DIR;
                    if(!isCollide()) posX1 += 25;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == ANDANDO_ESQ) {
                    posX1 -= 25;
                    estado1=ANDANDO_ESQ;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == ATACANDO) {
                    estado1 = ATACANDO;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == DEFENDENDO) {
                    estado1 = DEFENDENDO;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == PULANDO) {
                    estado1 = PULANDO;
                    colisao(posX1, posX2, estado1, estado2);
                }
            } else if (numCliente == 1) {
                if(flag==1){
                    flag1=1;
                    flag2=1;
                }
                if (recebeTecla == ANDANDO_DIR) {
                    posX2 += 25;
                    estado2=ANDANDO_DIR;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == ANDANDO_ESQ) {
                    if(!isCollide()) posX2 -= 25;
                    estado2=ANDANDO_ESQ;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == ATACANDO) {
                    estado2 = ATACANDO;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == DEFENDENDO) {
                    estado2 = DEFENDENDO;
                    colisao(posX1, posX2, estado1, estado2);
                } else if (recebeTecla == PULANDO) {
                    estado2 = PULANDO;
                    colisao(posX1, posX2, estado1, estado2);
                }
            }
        
        }
        //método para tratar as colisões
        void colisao(int posX1, int posX2, int estado1, int estado2) {
            //colisão com o adversário
            if (isCollide()) {
                if (estado1 == ATACANDO && estado2 != DEFENDENDO) {
                    estado2 = TOMANDO_DANO;
                    flag2=1;
                    flag1=1;
                    VIDA2 -= 50;
                    
                } else if (estado2 == ATACANDO && estado1 != DEFENDENDO) {
                    estado1 = TOMANDO_DANO;
                    flag2=1;
                    flag1=1;
                    VIDA1 -= 50;
                    
                } else if (estado1 == ATACANDO && estado2 == ATACANDO) {
                    estado2 = TOMANDO_DANO;
                    estado1 = TOMANDO_DANO;
                    flag2=1;
                    flag1=1;
                    VIDA1 -= 50;
                    VIDA2 -= 50;
                    
                } 
                if ((VIDA1 <= 0 || VIDA2 <= 0) && (estado1 != TOMANDO_DANO && estado2 != TOMANDO_DANO)) {
                    GAMEOVER = true;
                }
            }
    
            //colisão com a parede
            if (posX1 <= 0) {
                posX1 = 0;
            } else if (posX1 >= 1300) {
                posX1 = 1300;
            }
            if (posX2 <= 0) {
                posX2 = 0;
            } else if (posX2 >= 1300) {
                posX2 = 1300;
            }
        }
        private boolean isCollide() {
            int xDistance = Math.abs(posX1 - posX2);
            return xDistance<=100;
        }
    }

    //construtor da classe Servindo
    Servindo(Socket clientSocket1, Socket clientSocket2) {
        new ThreadCliente(clientSocket1, 0).start();
        new ThreadCliente(clientSocket2, 1).start();

        new Thread() { // thread que atualiza o jogo
            public void run() {
                while (true) {
                    flag1=0;
                    flag2=0;
                    atualizaJogo();
                    enviaDados(); 
                    try{
                        sleep(100);

                    }catch(InterruptedException e){}
                }
            }
        }.start();
    }
    
    
    synchronized void enviaDados() {
        try {
            //se um dos jogares não estiver conectado, não envia nada
            if (outStream[0] == null || outStream[1] == null)
            return; 

            //enviando as informações para os clientes
            outStream[0].writeInt(posX1);
            outStream[0].writeInt(posX2);
            outStream[0].writeInt(posY1);
            outStream[0].writeInt(posY2);
            outStream[0].writeInt(estado1);
            outStream[0].writeInt(estado2);
            outStream[0].writeInt(VIDA1);
            outStream[0].writeInt(VIDA2);
            //outStream[0].writeBoolean(GAMEOVER);
            outStream[0].writeInt(flag2);

            outStream[1].writeInt(posX1);
            outStream[1].writeInt(posX2);
            outStream[1].writeInt(posY1);
            outStream[1].writeInt(posY2);
            outStream[1].writeInt(estado1);
            outStream[1].writeInt(estado2);
            outStream[1].writeInt(VIDA1);
            outStream[1].writeInt(VIDA2);
            //outStream[1].writeBoolean(GAMEOVER);
            outStream[1].writeInt(flag1);

            outStream[0].flush();
            outStream[1].flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     void andaJogador() {
        if (estado1 == ANDANDO_DIR) {
          //estado1 = ANDA1;
          posX1+=25;
        } else if (estado1 == ANDANDO_ESQ) {
          //estado1 = ANDA0;
          posX1-=25;
        }
        if (estado2 == ANDANDO_DIR) {
          //estado2 = ANDA1;
          posX2+=25;
        } else if (estado2 == ANDANDO_ESQ) {
          //estado2 = ANDA0;
          posX2-=25;
        }
    }

    void socaJogador() {
      //~ if (estado == SOCO0) {
        //~ estado = SOCO1;
        //~ des.repaint();
      //~ } else if (estado == SOCO1) {
        //~ estado = SOCO2;
        //~ des.repaint();
      //~ } else if (estado == SOCO2) {
        //~ estado = PARADO;
        //~ des.repaint();
      //~ }
    }

    void atualizaJogo() {
      andaJogador();
      socaJogador();
    }

}