package mail;

import java.util.*;
import java.text.*;


public class Message {
	/* Cabecalho e corpo da mensagem. */
    public String Headers;
    public String Body;

    /* Remetente e Destinatario. */
    private String From;
    private String To;
    
    private static final String CRLF = "\r\n";

    /* Cria o objeto mensagem com cabecalho de acordo com a RFC 822 (From, To, Date). */
    public Message(String from, String to, String subject, String text) {
	/* Remove espacos em branco */
	From = from.trim();
	To = to.trim();
	Headers = "From: " + From + CRLF;
	Headers += "To: " + To + CRLF;
	Headers += "Subject: " + subject.trim() + CRLF;

	/* Data e Hora no formato GMT. */
	SimpleDateFormat format = 
	    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
	String dateString = format.format(new Date());
	Headers += "Date: " + dateString + CRLF;
	Body = text;
    }

    /* Acesso ao Remetente e ao Destinatario. */
    public String getFrom() {
	return From;
    }

    public String getTo() {
	return To;
    }

    /* Verifica se o Remetente e o Destinatario possuem o simbolo @ no endereco de email */
    public boolean isValid() {
	int fromat = From.indexOf('@');
	int toat = To.indexOf('@');

	if(fromat < 1 || (From.length() - fromat) <= 1) {
	    System.out.println("Endereco do Remetente esta incorreto");
	    return false;
	}
	if(toat < 1 || (To.length() - toat) <= 1) {
	    System.out.println("Endereco do Destinatario incorreto");
	    return false;
	}
	if(fromat != From.lastIndexOf('@')) {
	    System.out.println("Endereco do Remetente esta incorreto");
	    return false;
	}
	if(toat != To.lastIndexOf('@')) {
	    System.out.println("Endereco do Destinatario incorreto");
	    return false;
	}	
	return true;
    }
    
    /* Imprime a mensagem. */
    public String toString() {
	String res;

	res = Headers + CRLF;
	res += Body;
	return res;
    }
}
