package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fdoblas*/
public class ServidorMultiHilos{
	
	private static ServerSocket serverSocket = null;
	private static Socket socketCliente = null;
	
	private static final int MAX = 10;
	private static final Map<String,Cliente> threads = new HashMap<String,Cliente>(MAX);
	public static final int PORT = 8090;//se establece el puerto de comunicación, si está ocupado debemos cambiarlo por otro
	
	public static void main(String[] args) throws IOException{
		
		/*
		 * establecemos la conexion del Servidor en el puerto 9087,
		 *  podriamos modificarlo para elegir qué puerto deseamos conectarnos*/
		
		try{
			serverSocket = new ServerSocket(PORT);
			Logger.getLogger(ServidorMultiHilos.class.getName()).log(Level.INFO, "Servidor a la espera de conexiones.");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//creamos un socket por cada CLiente y lo metemos en un thread
		while (true) {
			socketCliente = serverSocket.accept();
		    Cliente nuevoCli = new Cliente(socketCliente, threads);
		    nuevoCli.start();
			Logger.getLogger(ServidorMultiHilos.class.getName()).log(Level.INFO, "Cliente con la IP " + serverSocket.getInetAddress().getHostName() + " conectado.");
        }
	}


}
