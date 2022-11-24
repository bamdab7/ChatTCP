package chattcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor extends Thread{
    //TODO-> SE ENCARGA DE RECIBIR Y ENVIAR LOS MENSAJES A LOS CLIENTES DEL CHAT
    DataInputStream flujoEntrada;
    Socket socket = null;

    // CONSTRUCTOR
    //TODO-> RECIBE EL SOCKET CREADO Y CREA EL FLUJO DE ENTRADA, DONDE LEEN LOS MENSAJES QUE EL CLIENTE ENVIA
    public HiloServidor(Socket s) {
        socket = s;
        try{
            // SE CREA EL FLUJO DE ENTRADA
            flujoEntrada = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("ERROR DE E/S");
            throw new RuntimeException(e);
        }
    }
    //TODO-> EN EL METODO RUN SE ENVIAN LOS MENSAJES QUE HAY EN EL CHAT AL CLIENTE PARA QUE LOS VISUALICE EN PANTALLA
    @Override
    public void run() { // ACCIONES A SEGUIR POR CADA HILO DE EJECUCION
        ServidorChat.mensaje.setText("NUMERO DE CONEXIONES ACTUALES: "+ ServidorChat.activas);
        // NADA MAS CONECTARSE EL CLIENTE,SE ENVIAN TODOS LOS MENSAJES
        String texto = ServidorChat.textarea.getText();
        enviarMensajes(texto);

        //TODO-> BUCLE WHILE EN EL QUE SE RECIBE LO QUE EL CLIENTE ESCRIBE POR EL CHAT
        while(true){
            String cadena = "";
            try{
                cadena = flujoEntrada.readUTF(); // LEE LO QUE EL CLIENTE ESCRIBE
                // CUANDO EL CLIENTE FINALIZA ENVIA UN *
                if(cadena.trim().equals("*")){
                    ServidorChat.activas--;
                    ServidorChat.mensaje.setText("NUMERO DE CONEXIONES ACTUALES: " +ServidorChat.activas);
                    break; // SALIR DEL WHILE
                }
                //TODO-> EL TEXTO QUE ESCRIBE EL CLIENTE SE AÑADE AL TEXTAREA DEL SERVIDOR Y ESTE EVNAIRA EL TEXTO A TODOS LOS CLIENTES
                ServidorChat.textarea.append(cadena+ "\n");
                texto = ServidorChat.textarea.getText();
                enviarMensajes(texto); // ENVIO DE TEXTO A TODOS LOS CLIENTES
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void enviarMensajes(String texto) {
        //TODO-> ENVIA EL TEXTO DEL TEXTAREA A TODOS LOS SOCKETS DE LA TABLA, ASI TODOS VEN LA CONVERSACION

        // RECORREMOS TABLA DE SOCKETS PARA ENVIALES LOS MENSAJES
        for( int i = 0; i < ServidorChat.conexiones; i++){
            Socket s1 = ServidorChat.tabla[i]; // OBTENER SOCKETS
            try{
                DataOutputStream flujoSalida = new DataOutputStream(s1.getOutputStream());
                flujoSalida.writeUTF(texto); // ESCRIBIR E EL SOCKET EL TEXTO
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
