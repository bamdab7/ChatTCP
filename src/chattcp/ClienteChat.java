package chattcp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClienteChat extends JFrame implements ActionListener {
    private static final long serialVersion = 1L;
    Socket socket = null;
    // STREAMS
    DataInputStream flujoEntrada; // PARA LEER MENSAJES DE TODOS
    DataOutputStream flujoSalida; // PARA ESCRIBIR SUS MENSAJES

    
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
