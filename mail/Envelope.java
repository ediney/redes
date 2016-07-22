package mail;


import java.io.*;
import java.net.*;
import java.util.*;

public class Envelope {
	/* Remetente (From-header) */
    public String Sender;

    /* Destinatario (To-header) */
    public String Recipient;

    /* Target MX-host */
    public String DestHost;
    public InetAddress DestAddr;

    /* Messagem */
    public Message Message;

    /* Cria o envelope. */
    public Envelope(Message message, String localServer) throws UnknownHostException {
	/* Obtem o Remetente e o Destinatario. */
	Sender = message.getFrom();
	Recipient = message.getTo();

	/* Obtem a mensagem e certifica as linhas para o envio da mensagem. */
	Message = escapeMessage(message);

	/* Pega o nome do mailserver local e mapeia */
	DestHost = localServer;
	try {
	    DestAddr = InetAddress.getByName(DestHost);
	} catch (UnknownHostException e) {
	    System.out.println("Unknown host: " + DestHost);
	    System.out.println(e);
	    throw e;
	}
	return;
    }

    /* Verifica a duplicacao no inicio de linha. */
    private Message escapeMessage(Message message) {
	String escapedBody = "";
	String token;
	StringTokenizer parser = new StringTokenizer(message.Body, "\n", true);

	while(parser.hasMoreTokens()) {
	    token = parser.nextToken();
	    if(token.startsWith(".")) {
		token = "." + token;
	    }
	    escapedBody += token;
	}
	message.Body = escapedBody;
	return message;
    }

    /* Imprime o envelope em caso de debug. */
    public String toString() {
	String res = "Remetente: " + Sender + '\n';
	res += "Destinatario: " + Recipient + '\n';
	res += "MX-host: " + DestHost + ", address: " + DestAddr + '\n';
	res += "Messagem:" + '\n';
	res += Message.toString();
	
	return res;
    }

}
