import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

// The code is adapted from the following books:
// 1. An Introduction to Network Programming with Java (2nd Ed.) by Jan Graba, 
//    published by Springer in 2006; and
// 2. Computer Networking: A Top-Down Approach Featuring the Internet (2nd Ed.) 
//    by James Kurose and Keith Ross, published by Addison Wesley in 2002. 

/* Author: 		Brian Klein
 * Class: 		CS 413 Advanced Networking and Telecommunication
 * Professor: 	Chi-Cheng Lin
 * Date: 		April 30, 2020
 * Description: Purpose of this program is to set up a TCP connection to the local host 
 * 				and transfer a text file.
 * 				If the text file is not found then an appropriate HTML 404 response 
 * 				file should be sent to the client to inform them of the error.
 * 				Tested with Chrome and Firefox browsers.
 * Requires:	alice.txt
 * 				404response.html
 * 				favicon.ico
 */
public class SimpleWebServer {

	public static void main(String argv[]) throws Exception
	{
		FileInputStream fis;
		OutputStream os;
		
		// Open server socket
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(6789);

		while(true)
		{
			// Accept connection
			Socket connectionSocket = serverSocket.accept();

			// Set up input and output streams
			Scanner inFromClient = new Scanner(
					connectionSocket.getInputStream());
			PrintWriter outToClient = new PrintWriter(
					connectionSocket.getOutputStream(),true); 

			/* InputStreamReader -> BufferedReader
			 * BufferedReader reads a line into String line
			 * line is used for printing and is parsed to fine the file name
			 */
			InputStreamReader inputStreamReader = new InputStreamReader(connectionSocket.getInputStream()); 
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader); 
			String line = bufferedReader.readLine(); 
			String fileName = null;
			
			System.out.println("*** Http messages received ***");
			// Print the HTTP GET request header to the console
			while (!line.isEmpty()) { 
				/* Parse the header to get the file name
				 * Find the line that starts with GET
				 * Grab the index of the first open space after GET + 1
				 * Use this as the indexStart
				 * Grab the index of "HTTP" - 1
				 * Use this as the indexEnd
				 * Create a substring of line starting with indexStart and ending with indexEnd
				 * Save the substring as fileName
				 * This is the name of the file to send back to the client
				 */
				if(line.contains("GET")) {
					int indexStart = line.indexOf("/") + 1;
					int indexEnd = line.indexOf("HTTP") - 1;
					fileName = line.substring(indexStart, indexEnd);
				}
				// Print the header information here
				System.out.println(line); 
				line = bufferedReader.readLine(); 
			}			
			System.out.println("*** Header received ***");
			
			// Enable this for testing to see what file name was grabbed by parser
			//System.out.println(fileName);
			
			/* Create file for reply here
			 * Also name the html 404 response file found in the package: src/404response.html
			 * Create a byte array to store the bytes from the file for sending
			 * contentType is a dynamic string to add to the html header field. either text/plain
			 * or text/html depending on what kind of file is being sent.
			 * dateFormat is a dynamic date argument to pass the current date in the header.
			 */
			File fileToSend = new File("src/" + fileName);
			File htmlResponse = new File("src/404response.html");
			byte [] myByteArray;
			os = connectionSocket.getOutputStream();
			String contentType;
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:Ss z");
			
			/* Read the fileToSend File into the FileInputStream
			 * Try to send the file requested by the GET
			 * Send the header information first so the client knows how to interpret 
			 * the info being sent.
			 * Then convert the fileToSend into bytes for transfer
			 * Write the bytes to the OutputStream
			 * 
			 * If a FileNotFoundException is caught (i.e. the GET request is trying to grab a file
			 * that is not available.)
			 * Then read the htmlResponse File into the FileInputStream
			 * Send the header information so the client knows how to interpret the info being sent.
			 * Then convert the htmlResponse into bytes for transfer
			 * Write the bytes to the OutputStream
			 */
			try {
				fis = new FileInputStream(fileToSend);
				contentType = "text/plain";
				outToClient.println("HTTP/1.1 200 OK");
				outToClient.println("Server: SimpleWebServer");
				outToClient.println("Date: " + dateFormat.format(new Date()));
				outToClient.println("Content-Type: " + contentType);
				outToClient.println("");
				myByteArray = new byte [(int)fileToSend.length()];
				int count;
				
				while ((count=fis.read(myByteArray)) > 0) {
					os.write(myByteArray, 0, count);
				}
				
			} catch (FileNotFoundException e) {
				fis = new FileInputStream(htmlResponse);
				contentType = "text/html";
				outToClient.println("HTTP/1.1 404 Not Found");
				outToClient.println("Server: SimpleWebServer");
				outToClient.println("Date: " + dateFormat.format(new Date()));
				outToClient.println("Content-Type: " + contentType + ";charset=UTF-8");
				outToClient.println("");
				myByteArray = new byte [(int)htmlResponse.length()];
				int count;
				
				while ((count=fis.read(myByteArray)) > 0) {
					os.write(myByteArray, 0, count);
				}

			}
			
			// Clear the OutputStream
			os.flush();	

			// Close connection
			connectionSocket.close();
		}
	}
}

