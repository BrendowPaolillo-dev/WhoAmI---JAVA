package clientServerOneWay;

import java.net.ServerSocket;            
import java.net.Socket;            
import java.io.InputStream;
import java.io.DataInputStream;
 
public class WishesServer
{
   public static void main(String args[]) throws Exception
   {
     ServerSocket sersock = new ServerSocket(5000); 
     System.out.println("server is ready");  //  message to know the server is running
 
     Socket sock = sersock.accept();               
                                                                                          
     InputStream istream = sock.getInputStream();  
     DataInputStream dstream = new DataInputStream(istream);
 
     @SuppressWarnings("deprecation")
	 String message2 = dstream.readLine();
     System.out.println(message2);
     dstream .close(); istream.close(); sock.close(); sersock.close();
  }
}