PRUEBA T�CNICA: CHAT

1.	INTRODUCCI�N
Se nos plantea el problema de realizar una aplicaci�n chat para hablar con otros usuarios conectados al mismo servidor.
Los usuarios deber�n hacer login, escoger un nickname y poder hablar con otros usuarios conectados.

2.	DESARROLLO
Para realizar el chat, vamos a desarrollar por una parte el Servidor, y por otra el Cliente y la interfaz mediante
la cual el usuario podr� comunicarse.
El Servidor ser� desarrollado con una clase ServerSocket de Java. El Servidor utilizar� un puerto dedicado a 
escuchar las posibles conexiones que los clientes le soliciten. Cada vez que un cliente solicita al Servidor
conexi�n, este crea un �Socket� estableciendo as� una conexi�n con el Cliente.

El Servidor est� dedicado principalmente a escuchar peticiones de nuevos clientes y a leer/escribir 
los mensajes que recibe.
El Cliente utiliza la clase Socket para establecer su comunicaci�n con el Servidor y enviar/recibir los mensajes.
El Cliente hereda de Observable, en esta clase se pueden observar dos tipos de elementos, los observadores y los observados. Si un objeto quiere observar a otro se apunta a su lista de observadores para avisarle de que quiere saber cuando cambia su estado para realizar alguna acci�n, por ejemplo mostrar el cambio, y el objeto observado lo que hace es informar a todos los objetos que lo est�n observando para decirles que su estado ha cambiado.

3.	FUNCIONAMIENTO
Hemos determinado que el puerto a través el cuál se comunicará Servidor/Cliente será el 8090,
como en principio es un chat para usar en la maquina local, si ese puerto está ocupado 
en nuestro equipo nos dará error al ejecutar el Servidor, se puede solucionar fácilmente 
cambiando el valor del puerto en la clase ServidorMultiHilos.java, constante con el 
nombre “PORT” por uno que tengamos libre.

Para hacer funcionar el chat es bastante sencillo, tenemos dos opciones:
Si tenemos configurado Ant en nuestra m�quina, debiendo tener configuradas los PATH de Ant y JDK. 
Hemos definido una tarea ant llamada �runChat� que lanzar�a el ServidorMultiHilos en primera instancia, 
seguido de tres ventanas de Chat para que tres usuarios diferentes se conecten al mismo Servidor y puedan 
interactuar entre ellos.
									
									"ant runChat"

O bien desde el IDE de eclipse podemos ejecutarlo manualmente, primero deberemos ejecutar la clase 
ServidorMultiHilos.java, esto hace que el Servidor comience y se quede a la espera de recibir Clientes y 
enviar/recibir mensajes.
Segundo, deberemos ejecutar la clase VentanaChat.java tantas veces como usuarios queramos crear en nuestro chat.
Hemos decidido poner un m�ximo de diez usuarios para el servidor. Esta clase crear� el socket que hemos comentado
antes para realizar la comunicaci�n a trav�s del Servidor.

	-Por cada Cliente deberemos introducir nuestro Nick para ser reconocidos en el chat.
	El Nick no podr� contener el car�cter �@�. Una vez realizado nos aparece una ventana para comenzar a
	hablar con los usuarios conectados.

	-Una vez dentro, si un usuario entra o sale de la sala de chat, ser� notificado a cada uno de los 
	usuarios. Podremos escribir un mensaje p�blico y enviarlo a los usuarios.

	-Para enviar un mensaje privado, deberemos escribir �@nickUsuario� seguido del mensaje y este solo se 
	enviar� a un usuario indicando en la ventana del emisor que es un mensaje privado y a qui�n va dirigido, 
	y en la ventana del receptor nos avisa que es un mensaje privado y de qui�n proviene el mensaje.

	-Para salir del chat deberemos escribir �/exit�


4.	HITOS
Nos han surgido varios hitos a resolver durante el desarrollo del Chat que hemos ido consiguiendo satisfactoriamente, 
vamos a destacar uno de ellos. En un principio se consigui� el funcionamiento del chat Cliente/Servidor 
en el que todos los usuarios se comunicaban con todos, es decir el envi� de mensajes p�blicos. Pero apareci� 
el problema de los mensajes privados, para el cual lo solucionamos creando un Thread por cada Usuario(Cliente) 
que solicita servicio al Servidor. A su vez cada cliente contiene una variable Map<String, Cliente> 
d�nde guardaremos la informaci�n de todos los usuarios conectados. De este modo cuando un usuario quiere 
establecer un mensaje privado, escribiendo @NickUsuario en la ventana seguido del mensaje a enviar, 
obtendremos el cliente que pertenece a la key NickUsuario para que el mensaje sea enviado solo a �l. 
