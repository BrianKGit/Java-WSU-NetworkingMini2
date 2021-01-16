// The code is adapted from the following books:
// 1. An Introduction to Network Programming with Java (2nd Ed.) by Jan Graba, 
//    published by Springer in 2006; and
// 2. Computer Networking: A Top-Down Approach Featuring the Internet (2nd Ed.) 
//    by James Kurose and Keith Ross, published by Addison Wesley in 2002. 

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleTCPClient {

	/**
	 * @param args
	 */

	public static void main(String argv[]) throws Exception
	{
		String sentence;
		String modifiedSentence;

		// Create a stream socket and connects it to port number on the named server
		// In our code, the client socket connects to 6789 on localhost 
		Socket clientSocket = new Socket("localhost", 6789);

		// Set up input and output streams
		Scanner inFromServer = new Scanner(
				clientSocket.getInputStream());
		PrintWriter outToServer = new PrintWriter(
					clientSocket.getOutputStream(), true);

		// Obtain input from user
		Scanner inFromUser = new Scanner(System.in);
		System.out.print("Enter your sentence: ");
		sentence = inFromUser.nextLine();
		
		// Send request to server
		outToServer.println(sentence);
		
		// Receive reply from server
		modifiedSentence = inFromServer.nextLine();
		System.out.println("FROM SERVER: " + modifiedSentence);

		// Close connection
		clientSocket.close();
	}
}