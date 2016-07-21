package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.* ;
import java.net.* ;
//import java.util.* ;

public final class WebServer {
	public static void main(String argv[]) throws Exception {
		// Ajustar o numero da porta.
		int port = 6789;
		// Estabelecer o socket de escuta.
		ServerSocket serverSocket =  new ServerSocket(port);
		
		// Processar a requisicao de servico HTTP em um laco infinito.
		while (true) {
			// Escutar requisicao de conexao TCP
			Socket clientSocket = null;
			clientSocket = serverSocket.accept();
			// Construir um objeto para processar a mensagem de requisicao HTTP.
			HttpRequest request = new HttpRequest(clientSocket);
			
			//Criar um novo thread para processar a requisicao.
			Thread thread = new Thread(request);
			
			// Iniciar o the thread.
			thread.start();
		}
	}
}

final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";
    Socket socket;
    
    // Construtor
    public HttpRequest(Socket socket) throws Exception {
    	this.socket = socket;
	}
    
    // Implemente o metodo run() da interface Runnable.
    public void run() {
    	try {
    			processRequest();
    		} catch (Exception e) {
    			System.out.println(e);
    		}
    }
    
    private void processRequest() throws Exception {
    	// Obter uma referencia para os trechos de entrada e saida do socket.
    	InputStream is = socket.getInputStream();
    	DataOutputStream os = new DataOutputStream(socket.getOutputStream());
    	
    	//Ajustar os filtros do trecho de entrada.
    	InputStreamReader isr = new InputStreamReader(is);
    	BufferedReader br = new BufferedReader(isr);
    	
    	// Obter a linha de requisicao da mensagem de requisicao HTTP.
    	String requestLine = br.readLine();
    	
    	// Exibir a linha de requisicao.
    	System.out.println();
    	System.out.println(requestLine);
    	
    	// Obter e exibir as linhas de cabecalho.
    	String headerLine = null;
    	while ((headerLine = br.readLine()).length() != 0) {
    		System.out.println(headerLine);
    	}
    	
    	//Feche as cadeias e socket.
    	os.close();
    	br.close();
    	socket.close();
    }
}

