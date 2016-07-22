package mail;

import java.io.*;
import java.net.*;

public class EmailSender {
	static BufferedReader fromServer;
    static DataOutputStream toServer;
    private static final String CRLF = "\r\n";
    
	public static void main(String[] args) throws Exception {
		// Estabelecer uma conexão TCP com o servidor de correio.
		Socket socket = new Socket("mail.com", 25);
		
		// Criar um BufferedReader para ler a linha atual.
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		fromServer = br;
		
		// Ler os cumprimentos do servidor.
		String response = br.readLine();
		System.out.println(response);
		if (!response.startsWith("220")) {
			throw new Exception("220 reply not received from server.");
		}
		
		// Pegar uma referência para o trecho de saída do socket.
		OutputStream os = socket.getOutputStream();
		toServer = new DataOutputStream(os);
		
		// Enviar o comando HELO e pegar a resposta do servidor.
		String command = "Helo Alice\r\n";
		System.out.println(command);
		os.write(command.getBytes("US-ASCII"));
		response = br.readLine();
		System.out.println(response);
		if (!response.startsWith("250")) {
			throw new Exception("250 reply not received from server.");
			}
		
		// Enviar o comando MAIL FROM.
		sendCommand("MAIL FROM:<" + "edineylopes@gmail.com" + ">", 250);
		
		// Enviar o comando RECP TO.
		sendCommand("RECPT TO:<" + "edineylopes@gmail.com" + ">", 250);
		
		// Enviar o comando DATA.
		sendCommand("DATA", 354);
		
		// Enviar o dados da mensagem.
		sendCommand("SUBJECT:EmailSender\n\nHi, keep calm and call Batman" + CRLF + ".", 250);
		
		// Terminar com uma linha de um único período.
		
		
		// Enviar o comando QUIT.
		sendCommand("QUIT", 221);
		socket.close();		
		}
		
	/* Envia um comando SMTP para o Servidor e confere se esta de acordo com a RFC 821. */
	private static void sendCommand(String command, int rc) throws IOException {
		/* Continuar... */
		toServer.writeBytes(command + CRLF);
		String reply = fromServer.readLine();
		int replyCode = parseReply(reply);
		if (replyCode != rc){
			throw new IOException("Expected rc=" + rc + " got " + replyCode);
			}
		}
	
	/* Analisa a resposta do Servidor e retorna o código de resposta. */
	private static int parseReply(String reply) {
		/* Continuar... */
		System.out.println("reply " + reply);
		String[] vals = reply.split(" ");
		if (vals.length == 0){
			System.out.println("Invalid reply :" + reply);
			return 500;
	    }
		int replyCode = Integer.parseInt(vals[0]);
		return replyCode;
	}
}
