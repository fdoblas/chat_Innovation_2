package cliente;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author fdoblas*/

public class VentanaChat{
	
	 //Clase que hereda de observable para recibir mensajes del servidor
    public 	static class ChatComunication extends Observable{
        private Socket socket;
        private OutputStream outputStream;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        // Crea el socket y el thread
        public void IniciarSocket(String server, int port) throws IOException{
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            Thread threadRecibido = new Thread(){
                @Override
                public void run(){
                    try{
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null){
                        	notifyObservers(line);
                        }
                    }catch (IOException ex){
                        notifyObservers(ex);
                    }
                }
            };
            threadRecibido.start();
        }


        //enviar texto
        public void send(String text){
            try {
                outputStream.write((text + "\r\n").getBytes());
                outputStream.flush();
            }catch (IOException ex){
                notifyObservers(ex);
            }
        }

        //cerrar el socket
        public void close(){
            try{
                socket.close();
            }catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    //Ventana de CHAT
    @SuppressWarnings("serial")
	public static class ChatFrame extends JFrame implements Observer{

        private JTextArea textArea;
        JTextField inputTextField;
        private JButton sendButton;
        private ChatComunication chatAccess;
        private JTextArea textUsuarios;

        public ChatFrame(ChatComunication chatAccess){
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        //creamos la GUI
        private void buildGUI(){
            textArea = new JTextArea(20, 40);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea), BorderLayout.CENTER);
            
            textUsuarios = new JTextArea(10,15);
            textUsuarios.setEditable(false);
            textUsuarios.setLineWrap(true);
            add(new JScrollPane(textUsuarios), BorderLayout.EAST);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            sendButton = new JButton("Send");
            box.add(inputTextField);
            box.add(sendButton);

            //Action para el boton de enviar
            ActionListener sendListener = new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    String str = inputTextField.getText();
                    if (str != null && str.trim().length() > 0)
                        chatAccess.send(str);
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent e){
                    chatAccess.close();
                }
            });
        }

        //actualizar el area del chat
        public void update(Observable o, Object arg){
            final String finalArg = arg.toString();
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                	if(finalArg.startsWith("#")) {//actualiza el area de usuarios
                		String[] usuarios = finalArg.split("#");
                		StringBuffer texto = new StringBuffer();
                		for(int i=0; i<usuarios.length; i++) {
                			texto.append(usuarios[i]);
                			texto.append("\n");
                		}
                        textUsuarios.setText(texto.toString());;
                		
                	}
                    else {//actualiza el area de chat
                        textArea.append(finalArg.toString());
                        textArea.append("\n");
                    }
                }
            });
        }
    }

    //clase principal
    public static void main(String[] args) throws InterruptedException{
        String server = "localhost";
        int port =servidor.ServidorMultiHilos.PORT;
        ChatComunication access = new ChatComunication();

        ChatFrame frame = new ChatFrame(access);
        frame.setTitle("Chat Innovation " + server + ":" + port);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        try{
            access.IniciarSocket(server,port);
        }catch (IOException ex){
            System.out.println("No se puede conectar a " + server + ":" + port);
            ex.printStackTrace();
            System.exit(0);
        }
    }

}
