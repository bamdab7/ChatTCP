package chattcp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorChat extends JFrame {
    //TODO -> ESTE ES EL PROGRAMA SERVIDOR
    private static final long serialVersionUID = 1L;
    static ServerSocket servidor;
    static final int puerto = 44444; // PUERTO POR EL QUE ESCUCHA
    static int conexiones = 0; // CUENTA DE LAS CONEXIONES
    static int activas = 0; // NUMERO DE CONEXIONES ACTIVAS
    static int maximo = 10; // LIMITE MAXIMO DE CONEXIONES

    static JTextField mensaje = new JTextField("");
    static JTextField mensaje2 = new JTextField("");
    private JScrollPane scrollPanel;
    static JTextArea textarea;
    JButton salir = new JButton("Salir");
    static Socket tabla[] = new Socket[10]; // ALMACENA LOS SOCKETS DE CLIENTES (CONEXIONES)

    //TODO -> DESDE EL CONSTRUCTOR SE PREPARA LA PANTALLA
    public ServidorChat(){
        super("VENTANA DEL SERVIDOR DE CHAT");
        setLayout(null);

        mensaje.setBounds(10,10,400,30);
        add(mensaje);
        mensaje.setEditable(false);

        mensaje2.setBounds(10,348,400,30);
        add(mensaje2);
        mensaje2.setEditable(false);

        textarea = new JTextArea();
        scrollPanel = new JScrollPane(textarea);

        scrollPanel.setBounds(10,50,400,300);
        add(scrollPanel);

        salir.setBounds(420,10,100,30);
        add(salir);

        textarea.setEditable(false);
        salir.addActionListener(this::actionPerformed);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    //TODO-> ACCION CUANDO PULSAMOS EL BOTON SALIR
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == salir){ // SE PULSA AL SALIR
            try{servidor.close(); // SE CIERRA EL SERVIDOR
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Servidor cerrado");
            System.exit(0);
        }
    }

    //TODO-> DESDE EL MAIN SE INICIA EL SERVIDOR Y LAS VARIABLES Y SE PREPARA LA PANTALLA
    public static void main(String[] args) throws IOException {
        servidor = new ServerSocket(puerto);
        System.out.println("Servidor iniciado ...");

        ServidorChat pantalla = new ServidorChat();
        pantalla.setBounds(0,0,540,400);
        pantalla.setVisible(true);

        mensaje.setText("NUMERO DE CONEXIONES ACTUALES: " + 0);

        //TODO-> BUCLE PARA CONTROLAR EL NUMERO DE CONEXIONES, CUANDO SE CONECTA EL CLIENTE, EL SERVIDOR CREA SOCKET
        while (conexiones < maximo){ // SE ADMITEN HASTA 10 CONEXIONES
            Socket s = new Socket();
            try{
                s = servidor.accept(); // ESPERANDO AL CLIENTE
            }catch (IOException e){
                break; // SALIR DEL WHILE
            }
            //TODO-> EL SOCKET CREADO SE ALMACENA EN LA TABLA,CONTAR CONEXIONES E INCREMENTAR LAS ACTIVAS
            tabla[conexiones] = s; // ALMACENAMOS SOCKET
            conexiones++;
            activas++;

            //TODO-> LANZA EL HILO PARA GESTIONAR LOS MENSAJES DE CLIENTE QUE SE ACABA DE CONECTAR
            HiloServidor hilo = new HiloServidor(s);
            hilo.start(); // SE LANZA EL HILO
        } // FIN WHILE

        //TODO-> CUANDO FINALIZA EL BUCLE CERRAR EL SERVIDOR SI NO SE CERRÓ ANTES
        if(!servidor.isClosed()){
            try{
                // SALE CUANDO SE LLEGA AL MAXIMO DE CONEXIONES
                mensaje2.setForeground(Color.red);
                mensaje2.setText("MAXIMO DE CONEXIONES ESTABLECIDAD: " +conexiones);

                servidor.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            System.out.println("Servidor finalizado...");
        }
    }
}
