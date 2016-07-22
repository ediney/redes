package udp_pinger;

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Servidor para processar as requisicoes de Ping sobre UDP.
 */

public class PingServer 
{
	private static final double LOSS_RATE = 0.3;
	private static final int AVERAGE_DELAY = 100;  // milliseconds
	
	public static void main(String[] args) throws Exception
	{
		// Obter o argumento da linha de comando.
		if (args.length != 1) {
			System.out.println("Required arguments: port");
			return;
		}
		
		int port = Integer.parseInt(args[0]);
		
		// Criar um gerador de numeros aleatorios para uso em simulacao
		// de perda de pacotes e atrasos na rede.
		Random random = new Random();
		
		// Criar um socket de datagrama para receber e enviar pacotes UDP
		// atraves da porta especificada na linha de comando.
		DatagramSocket socket = new DatagramSocket(port);
		
		// Loop de processamento.
		while (true) {
			// Criar um pacote de datagrama para comportar o pacote UDP de chegada.
			DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
			// Bloquear ate que o hospedeiro receba o pacote UDP.
			socket.receive(request);
			
			// Imprimir os dados recebidos.
			printData(request);
			
			// Decidir se responde, ou simula perda de pacotes.
			if (random.nextDouble() < LOSS_RATE) {
				System.out.println("Reply not sent.");
				continue;
			}
			
			// Simular o atraso da rede.
			Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));
			
			// Enviar resposta.
			InetAddress clientHost = request.getAddress();
			int clientPort = request.getPort();
			byte[] buf = request.getData();
			DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
			socket.send(reply);
			
			System.out.println("Reply sent.");
		}
	 }
	
	/*
	 * Imprimir o dado de Ping para o trecho de saida padrao.
	 */
	
	private static void printData(DatagramPacket request) throws Exception
	{
		// Obter referencias para a ordem de pacotes de bytes.
		byte[] buf = request.getData();
		
		// Envolver os bytes numa cadeia de entrada vetor de bytes, de
		// modo que voce possa ler os dados como uma cadeia de bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		
		// Envolver a cadeia de saida do vetor de bytes num leitor de
		// cadeia de entrada, de modo que voce possa ler os dados como
		// uma cadeia de caracteres.
		InputStreamReader isr = new InputStreamReader(bais);
		
		// Envolver o leitor de cadeia de entrada num leitor com
		// armazenagem, de modo que voce possa ler os dados de caracteres
		// linha a linha. (A linha e uma sequencia de caracteres
		// terminados por alguma combinacao de \r e \n.)
		BufferedReader br = new BufferedReader(isr);
		
		// O dado da mensagem esta contido numa unica linha, entao leia esta linha.
		String line = br.readLine();
		
		// Imprimir o endereco do hospedeiro e o dado recebido dele.
		System.out.println(
				"Received from " + 
		         request.getAddress().getHostAddress() +
		         ": " +
		         new String(line));
		}
}
