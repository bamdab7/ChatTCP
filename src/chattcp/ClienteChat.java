package chattcp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteChat extends JFrame implements ActionListener,Runnable {
    private static final long serialVersion = 1L;
    Socket socket = null;
    // STREAMS
    DataInputStream flujoEntrada; // PARA LEER MENSAJES DE TODOS
    DataOutputStream flujoSalida; // PARA ESCRIBIR SUS MENSAJES

    String nombre;
    static JTextField mensaje = new JTextField();
    private JScrollPane scrollpanel;
    static JTextArea textarea1;
    JButton boton = new JButton("Enviar");
    JButton desconectar = new JButton("Salir");
    boolean repetir = true;

    // TODO-> EL CONSTRUCTOR PREPARA LA PANTALLA
    public ClienteChat(Socket s, String nombre){
        super("CONEXION DEL CLIENTE CHAT: "+ nombre);
        setLayout(null);
        mensaje.setBounds(10,10,400,30);
        add(mensaje);

        textarea1 = new JTextArea();
        scrollpanel = new JScrollPane(textarea1);
        scrollpanel.setBounds(10,50,400,300);
        add(scrollpanel);

        boton.setBounds(420,10,100,30);
        add(boton);

        desconectar.setBounds(420,50,100,30);
        add(desconectar);

        textarea1.setEditable(false);
        boton.addActionListener(this::actionPerformed);
        desconectar.addActionListener(this::actionPerformed);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        socket = s;
        this.nombre = nombre;
        // CREACION DE FLUJOS DE ENTRADA Y SALIDA
        try{
            flujoEntrada = new DataInputStream(socket.getInputStream());
            flujoSalida = new DataOutputStream(socket.getOutputStream());

            String texto =  " > Entra en el chat... " + nombre;
            flujoSalida.writeUTF(texto); // ESCRIBE EL MENSAJE DE ENTRADA
        } catch (IOException e) {
            System.out.println("ERROR DEL E/S");
            e.printStackTrace();
            System.exit(0);
        }
    }

    //TODO-> CUANDO SE PULSA, SE ENVIA AL FLUJO DE SALIDA EL MENSAJE QUE SE ESCRIBIO
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == boton){ //SE PULSA BOTON ENVIAR
            String texto = nombre + ">" + mensaje.getText();
            try{
                mensaje.setText(""); //LIMPIAMOS AREA DEL MENSAJE
                flujoSalida.writeUTF(texto);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if(e.getSource() == desconectar){ //SE PULSA EL BOTON SALIR
            String texto = "> Abandona el chat... " +nombre;
            try{
                flujoSalida.writeUTF(texto);
                flujoSalida.writeUTF("*");
                repetir = false; // PARA SAIR DEL BUCLE
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //TODO-> EL CLIENTE LEE LO QUE EL HILO MANDA PARA MOSTRARLO
    // SE REALIZA MIENTRAS EL USUARIO NO PULSE SALIR

    public void run(){
        String texto = " ";
        while(repetir){
            try{
                texto = flujoEntrada.readUTF(); //LEER MENSAJE
                textarea1.setText(texto);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "IMPOSIBLE CONECTAR CON EL SERVIDOR \n" +
                        e.getMessage(), "<<MENSAJE DE ERROR:2>>",
                        JOptionPane.ERROR_MESSAGE);
                repetir = false; //FINALIZA EL BUCLE
            }
        }
        try{
            socket.close(); //CERRAR EL SOCKET
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO-> SE PIDE EL NOMBRE DE USUARIO, CONECTA AL SERVIDOR Y SE CREA OBJETO, SE MUESTRA
    //POR PANTALLA Y EJECUTAMOS EJECUTAR
    public static void main(String[] args) {
        int puerto = 44444;
        String nombre = JOptionPane.showInputDialog("Introduce tu nombre: ");
        Socket s = null;
        try{
            // CLIENTE Y SERVIDOR SE EJECUTAN EN LA MAQUINA
            s = new Socket("localhost", puerto);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "IMPOSIBLE CONECTAR CON EL SERVIDOR\n" + e.getMessage(),
                            "<<MENSAJE DE ERRROR:1>>", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if(!nombre.trim().equals("")){ //ESCRIBIR ALGO
            ClienteChat cliente = new ClienteChat(s, nombre);
            cliente.setBounds(0,0,540,400);
            cliente.setVisible(true);
            new Thread(cliente).start();
        }else{
            System.out.println("El nombre esta vacio...");
        }

    }
}
