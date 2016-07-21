package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.io.* ;
import java.net.* ;
//import java.util.* ;
import java.util.StringTokenizer;

public final class WebServer
{
    public static void main(String argv[]) throws Exception
    {
	// Ajustar o numero da porta.
	int port = 6789;
	// Estabelecer o socket de escuta.
	ServerSocket serverSocket =  new ServerSocket(port);
	
	// Processar a requisicao de servico HTTP em um laco infinito.
	while (true)
	    {
		// Escutar requisicao de conexao TCP		
		Socket clientSocket = null;
		clientSocket = serverSocket.accept();
		// Construir um objeto para processar a mensagem de requisicao HTTP.
		HttpRequest request = new HttpRequest(clientSocket);
		
		// Criar um novo thread para processar a requisicao.
		Thread thread = new Thread(request);
		
		// Iniciar o the thread.
		thread.start();
	    }
    }
}

public class HttpRequest implements Runnable {
	
	final static String CRLF = "\r\n";
    Socket socket;
    
    // Construtor
    public HttpRequest (Socket socket) throws Exception 
	{
	    this.socket = socket;
	}
    
    // Implemente o metodo run() da interface Runnable.
    public void run()
    {
	try {
	    processRequest();
	} catch (Exception e)
	    {
		System.out.println(e);
	    }
    }
    
    private void processRequest() throws Exception
    {
    	// Obter uma referencia para os trechos de entrada e saida do socket.
    	InputStream is = socket.getInputStream();
    	DataOutputStream os = new DataOutputStream(socket.getOutputStream());
    	
    	//Ajustar os filtros do trecho de entrada.
    	InputStreamReader isr = new InputStreamReader(is);
    	BufferedReader br = new BufferedReader(isr);
    	
    	// Obter a linha de requisicao da mensagem de requisicao HTTP.
    	String requestLine = br.readLine();
    	
    	//Extrair o nome do arquivo a linha de requisicao.
    	StringTokenizer tokens = new StringTokenizer(requestLine);
    	tokens.nextToken();//pular o metodo, que deve ser "GET"
    	
    	String fileName = tokens.nextToken();
    	//Acrescente um "." de modo que a requisicao do arquivo esteja dentro do diretorio atual.
    	fileName = "." + fileName;
    	
    	//Abrir o arquivo requisitado.
    	FileInputStream fis = null;
    	boolean fileExists = true;
    	try {
    	    fis = new FileInputStream(fileName);		
    	} catch (FileNotFoundException e) {
    	    fileExists = false;
    	}
    	
    	// Construir a mensagem de resposta.
    	String statusLine = null;
    	String contentTypeLine = null;
    	String entityBody = null;
    	if (fileExists) {
    	    statusLine = "HTTP/1.1 200 OK";
    	    contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
    	} else {
    	    statusLine = "HTTP/1.1 404 Not Found";
    	    contentTypeLine = "Content-Type: text/html;" + CRLF;
    	    entityBody = "<HTML>" + "<HEADER><TITLE>Not Found</TITLE></HEAD>" +
    		"<BODY>Not found</BODY></HTML>";
    	}

    	// Enviar a linha do status.
    	os.writeBytes(statusLine);
    	
    	// Enviar a linha do tipo do conteudo.
    	os.writeBytes(contentTypeLine);

    	// Enviar uma linha em branco para indicar o fim das linhas de cabecalho.
    	os.writeBytes(CRLF);
    	
    	// Enviar o corpo da entidade.
    	if (fileExists) {
    	    sendBytes(fis, os);
    	} else {
    	    os.writeBytes(entityBody);
    	}    	
    	
    	// Feche as cadeias e socket.
    	os.close();
    	br.close();
    	socket.close();
    	}
    
    private static void sendBytes(FileInputStream fis, OutputStream os) 
    		throws Exception
    {
    		
    		// Construir um buffer de 1K para comportar os bytes no caminho para o socket.
    		byte[] buffer = new byte[1024];
    		
    		int bytes = 0;
    		
    		// Copiar o arquivo requisitado dentro da cadeia de saida do socket.
    		while((bytes = fis.read(buffer)) != -1) {
    		    os.write(buffer, 0, bytes);
    		}
    }

    private static String contentType(String fileName)
    {
    	if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
    		return "text/html";
    	}
    	
    	if (fileName.endsWith(".gif")) {
    		return "image/gif";
    	}
    	
    	if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
    		return "image/jpeg";
    	}
    	
    	return "application/octet-stream";	  	    
    }
}
