package servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;

/**
 * @author fdoblas*/

public class Cliente extends Thread{
	private String nickName = "";
	private BufferedReader inputStream = null;
	private PrintStream printStream = null;
	private Socket socketCliente = null;
	private final Map<String,Cliente> threads;
	
	
	public Cliente(Socket socketCliente, Map<String, Cliente> threads){
		this.socketCliente = socketCliente;
		this.threads = threads;
	}	
	
	public void run(){
		
		try{
			//creamos la entrada/salida para el cliente
			inputStream = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
		    printStream = new PrintStream(socketCliente.getOutputStream());
			String name;
			
			while(true){
				printStream.println("Introduce tu nick: ");
				name = inputStream.readLine().trim();
				if(isNicknameCorrecto(name, printStream)){
					threads.put(name, this);
					break;
				}
			}
				
				//bienvenida del usuario
			printStream.println("Bienvenido " + name + " a nuestro chat.\nPara salir escribe /exit en la linea de entrada.");
			synchronized (this){
				nickName = "@" + name;
				refrescaUsuarios();
				for (Iterator<Cliente> itCliente = threads.values().iterator(); itCliente.hasNext();){
					Cliente cli = itCliente.next();
					if(cli != this) {
						cli.printStream.println("El usuario " + name + " ha entrado en la sala de chat !!!");
					}
			    }
			}
			
			
			//comienza la conversacion
			while (true) {
		        String line = inputStream.readLine();
		        if (line == null || line.startsWith("/exit")) {
		          break;
		        }
		        //El mensaje es enviado a un usuario privado
		        if(line.startsWith("@")){
		          String[] words = line.split("\\s", 2);
		          if (words.length > 1 && words[1] != null) {
		            words[1] = words[1].trim();
		            if (!words[1].isEmpty()){
		              synchronized (this){
		            	 Cliente cliDest = threads.get(words[0].substring(1));
		                 cliDest.printStream.println("<PrivateMSG: " + name + "> " + words[1]);
		                 printStream.println("<PrivateMSG: " + cliDest.nickName + "> " + words[1]);
		              }
		            }
		          }
		        }else{//el mensaje es publico
		        	synchronized (this) {
						for (Iterator<Cliente> itCliente = threads.values().iterator(); itCliente.hasNext();){
							Cliente cli = itCliente.next();
							cli.printStream.println("<" + name + "> " + line);
						}
		        	}
		        }
		    }
			
			synchronized (this) {//el usuario abandona la sala
				for (Iterator<Cliente> itCliente = threads.values().iterator(); itCliente.hasNext();){
					Cliente cli = itCliente.next();
					cli.printStream.println("El usuario " + name  + " ha abandonado la sala!");
		        }
		    }
		    
			printStream.println("Hasta luego "+ name);
			
			//reseteamos los Clientes
			synchronized (this) {
				threads.remove(name);
				refrescaUsuarios();
		    }
			
		
			inputStream.close();
		    printStream.close();
		    socketCliente.close();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void refrescaUsuarios() {
		StringBuffer usuarios = new StringBuffer();
		for (Iterator<Cliente> itCliente = threads.values().iterator(); itCliente.hasNext();){
			Cliente cli = itCliente.next();
			usuarios.append("#"+cli.nickName);
		}
		
		for (Iterator<Cliente> itCliente = threads.values().iterator(); itCliente.hasNext();){
			Cliente cli = itCliente.next();
			cli.printStream.println(usuarios.toString());
	    }
	}
	
	private boolean isNicknameCorrecto(String nick, PrintStream  pr){
		boolean correcto = false;
		if (nick.indexOf('@') == -1) {
	        correcto = true;
	    }else{
	    	pr.println("No puede contener el caracter @.");
	    }
		return correcto;
	}
}
