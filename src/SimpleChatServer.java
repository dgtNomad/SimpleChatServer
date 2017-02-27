import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;


public class SimpleChatServer {
	ArrayList clientOutputStreams;
	
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket socket;
		
		public ClientHandler(Socket clientSocket) { // Constructor
			InputStreamReader isr;
			try {
				socket = clientSocket;
				isr = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("Read: " + message);
					tellEveryone(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// Tells everyone connected (via iterator) by writing out to each output stream the message
		private void tellEveryone(String message) {
			Iterator it = clientOutputStreams.iterator();
			while (it.hasNext()) {
				try {
					PrintWriter writer = (PrintWriter) it.next();
					writer.println(message);
					writer.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String args[]) {
		new SimpleChatServer().run();
	}
	
	public void run() {
		clientOutputStreams = new ArrayList();
		
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			
			while (true) {
				Socket clientSocket = serverSocket.accept(); // Creates a new Socket for each acceptance
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer); // Adds the new client to the ArrayList of output streams.
				
				// Creates a new thread for each clientSocket
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
				System.out.println("Got a connection");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
