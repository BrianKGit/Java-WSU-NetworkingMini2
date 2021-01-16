// The code is adapted from the following books:
// 1. An Introduction to Network Programming with Java (2nd Ed.) by Jan Graba, 
//    published by Springer in 2006; and
// 2. Computer Networking: A Top-Down Approach Featuring the Internet (2nd Ed.) 
//    by James Kurose and Keith Ross, published by Addison Wesley in 2002. 

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleTCPServer {

	public static void main(String argv[]) throws Exception
	{
		String clientSentence;
		String lowerCaseSentence;
		
		// Open server socket
		ServerSocket serverSocket = new ServerSocket(6789);

		while(true)
		{
			// Accept connection
			Socket connectionSocket = serverSocket.accept();
			System.out.println("Accepting socket : " + connectionSocket);

			// Set up input and output streams
			Scanner inFromClient = new Scanner(
					connectionSocket.getInputStream());
			PrintWriter outToClient = new PrintWriter(
					connectionSocket.getOutputStream(),true); 

			// Receive request from client
			clientSentence = inFromClient.nextLine();
			System.out.println("Received: " + clientSentence);
			lowerCaseSentence = clientSentence.toLowerCase();
			System.out.println("Converted to: " + lowerCaseSentence);

			// Send reply to client
			outToClient.println(lowerCaseSentence);
			System.out.println("Sent");

			// Close connection
			connectionSocket.close();
		}
	}
}

