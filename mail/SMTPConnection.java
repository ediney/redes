package mail;

import java.net.*;
import java.io.*;
import java.util.*;

/**

 * Abre uma conexão SMTP para o servidor de correio e envia um e-mail.

 *

 */
public class SMTPConnection {
	/* O socket para o servidor */
	private Socket connection;
	
	/* Trechos para leitura e escrita no socket */
	private BufferedReader fromServer;
	private DataOutputStream toServer;
	
	private static final int SMTP_PORT = 25;
	private static final String CRLF = "\r\n";
	
	/* Estamos conectados? Usamos close() para determinar o que fazer.*/
	private boolean isConnected = false;
	
	/* Crie um objeto SMTPConnection.
	 * Crie os sockets e os trechos associados.
	 * Inicie a conexão SMTP.
	 */
	public SMTPConnection(Envelope envelope)throws IOException{
		
		//connection = /* Preencher */;
		connection = new Socket(envelope.DestAddr, 25);
		
		//InputStream is = socket.getInputStream();
		//DataOutputStream os = new DataOutputStream();
		//InputStreamReader isr = new InputStreamReader(is);
		fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		toServer = new DataOutputStream(connection.getOutputStream());
		//fromServer = new BufferedReader(new InputStreamReader(System.in));
		//toServer = new DataOutputStream(System.out);
		
		/* Ler uma linha do servidor e verificar se o código de resposta é
		 * 220. Se não for, lance uma IOException. */
		String reply = fromServer.readLine();
		int replyCode = parseReply(reply);
		if (replyCode != 220){
			throw new IOException();
			}
		
		/* SMTP handshake. Precisamos do nome da máquina local.
		 * Envie o comando handskhake do SMTP apropriado. */
		
		String localhost = "192.168.25.1";
		sendCommand("HELLO", 250);
		isConnected = true;
	}
	
	/* Envie a mensagem. Escreva os comandos SMTP corretos na ordem correta.
	 *  Não verifique de erros, apenas lance-os ao chamador. */
	public void send(Envelope envelope)throws IOException{
		
		/* Envie todos os comandos necessários para enviar a mensagem.
	       * Chame o sendCommand() para fazer o trabalho sujo. 
	       * Não apanhe a exceção lançada pelo sendCommand().*/

	    //sendCommand("HELO", 250);
		sendCommand("MAIL FROM:<" + envelope.Sender + ">", 250);
		sendCommand("RCPT TO:<" + envelope.Recipient + ">", 250);
		sendCommand("DATA", 354);
		sendCommand(envelope.Message + CRLF + ".", 250);
		//sendCommand("QUIT", 221);		
	}
	
	/* Feche a conexão. Primeiro, termine no nível SMTP, então feche o socket. */
	public void close(){
		isConnected = false;
		try {
			sendCommand("QUIT", 221);
			if (connection != null){
				connection.close();
			}
		}catch (IOException e) {
			System.out.println("Unable to lose connection: " + e);
			isConnected = true;
			}
	}
	
	/* Envie um comando SMTP ao servidor. 
	 * Cheque se o código de resposta está de acordo com o RFC 821. */
	private void sendCommand(String command, int rc) throws IOException {
		/* Escrever o comando do servidor e ler a resposta do servidor. */
		toServer.writeBytes(command + CRLF);
		String reply = fromServer.readLine();
		int replyCode = parseReply(reply);
		if (replyCode != rc)
		    {
			
			throw new IOException("Expected rc=" + rc + " got " + replyCode);
		    }

	    }	 

	   /* Cheque se o código de resposta do servidor é o mesmo do parâmetro rc.
	    *  Se não, envie um IOException. */
	   /* Analise a linha de resposta do servidor. Retorne o código de resposta. */
		private int	parseReply(String reply) {
			System.out.println("reply " + reply);
			String[] vals = reply.split(" ");
			if (vals.length == 0){
				System.out.println("Invalid reply :" + reply);
				return 500;
		    }
		int replyCode = Integer.parseInt(vals[0]);
		return replyCode;
	}	 

	/* Destructor. Fecha a conexão se algo de ruim acontecer. */
	protected void finalize() throws Throwable {
		if(isConnected){
			close();
			}
		super.finalize();
	}
}
