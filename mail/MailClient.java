package mail;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class MailClient extends Frame {
    /* Componentes da Interface. */
    private Button btSend = new Button("Send");
    private Button btClear = new Button("Clear");
    private Button btQuit = new Button("Quit");
    private Label serverLabel = new Label("Local mailserver:");
    private TextField serverField = new TextField("", 40);
    private Label fromLabel = new Label("From:");
    private TextField fromField = new TextField("", 40);
    private Label toLabel = new Label("To:"); 
    private TextField toField = new TextField("", 40);
    private Label subjectLabel = new Label("Subject:");
    private TextField subjectField = new TextField("", 40);
    private Label messageLabel = new Label("Message:");
    private TextArea messageText = new TextArea(10, 40);

    /**
     * Cria uma nova janela MailClient com campos a serem preenchidos
     * com todas as informacoes necessarias para o envio de um email
     * (Remetente, Destinatario, Assunto e corpo da mensagem).
     */
    public MailClient() {
	super("Java Mailclient");
	
	/* Cria as telas para o preenchimento dos campos.
	 * Para um visual mais agradavel, cria uma tela extra,
	 * para a geracao das demais telas subsequentes. */
	Panel serverPanel = new Panel(new BorderLayout());
	Panel fromPanel = new Panel(new BorderLayout());
	Panel toPanel = new Panel(new BorderLayout());
	Panel subjectPanel = new Panel(new BorderLayout());
	Panel messagePanel = new Panel(new BorderLayout());
	serverPanel.add(serverLabel, BorderLayout.WEST);
	serverPanel.add(serverField, BorderLayout.CENTER);
	fromPanel.add(fromLabel, BorderLayout.WEST);
	fromPanel.add(fromField, BorderLayout.CENTER);
	toPanel.add(toLabel, BorderLayout.WEST);
	toPanel.add(toField, BorderLayout.CENTER);
	subjectPanel.add(subjectLabel, BorderLayout.WEST);
	subjectPanel.add(subjectField, BorderLayout.CENTER);
	messagePanel.add(messageLabel, BorderLayout.NORTH);	
	messagePanel.add(messageText, BorderLayout.CENTER);
	Panel fieldPanel = new Panel(new GridLayout(0, 1));
	fieldPanel.add(serverPanel);
	fieldPanel.add(fromPanel);
	fieldPanel.add(toPanel);
	fieldPanel.add(subjectPanel);

	/* Cria o painel e botoes */
	Panel buttonPanel = new Panel(new GridLayout(1, 0));
	btSend.addActionListener(new SendListener());
	btClear.addActionListener(new ClearListener());
	btQuit.addActionListener(new QuitListener());
	buttonPanel.add(btSend);
	buttonPanel.add(btClear);
	buttonPanel.add(btQuit);
	
	/* Add, pack e show. */
	add(fieldPanel, BorderLayout.NORTH);
	add(messagePanel, BorderLayout.CENTER);
	add(buttonPanel, BorderLayout.SOUTH);
	pack();
	show();
    }

    static public void main(String argv[]) {
	new MailClient();
    }

    /* Caracteristicas do botao enviar. */
    class SendListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    System.out.println("Sending mail");
	    
	    /* Verifica o mailserver local*/
	    if ((serverField.getText()).equals("")) {
		System.out.println("Need name of local mailserver!");
		return;
	    }

	    /* Verifica Remetente e Destinatario. */
	    if((fromField.getText()).equals("")) {
		System.out.println("Informe o Remetente!");
		return;
	    }
	    if((toField.getText()).equals("")) {
		System.out.println("Informe o Destinatario!");
		return;
	    }

	    /* Cria a mensagem */
	    Message mailMessage = new Message(fromField.getText(), 
					      toField.getText(), 
					      subjectField.getText(), 
					      messageText.getText());

	    /* Verifica se a mensagem e valida e se remetente e destinatario estao corretos. */
	    if(!mailMessage.isValid()) {
		return;
	    }

	    /* Cria o envelope, abre a conexao e tenta enviar a mensagem. */
	    Envelope envelope = null;
	    try {
		envelope = new Envelope(mailMessage, serverField.getText());
	    } catch (UnknownHostException e) {
		// Se houver erros, informa o que ocorreu 
		return;
	    }
	    try {
	    	SMTPConnection connection = new SMTPConnection(envelope);
	    	connection.send(envelope);
	    	connection.close();
	    } catch (IOException error) {
	    	System.out.println("Erro ao enviar mensagem: " + error);
	    	return;
	    }
	    System.out.println("Email enviado com sucesso!");
	}
    }

    /* Limpa os campos da GUI. */
    class ClearListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    System.out.println("Limpando campos");
	    fromField.setText("");
	    toField.setText("");
	    subjectField.setText("");
	    messageText.setText("");
	}
    }

    /* Finaliza */
    class QuitListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    System.exit(0);
	}
    }
}