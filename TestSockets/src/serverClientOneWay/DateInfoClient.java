package serverClientOneWay;

import java.io.*;
import java.net.*;


public class DateInfoClient   
{
  public static void main(String args[]) throws Exception    
  {
    Socket sock = new Socket("127.0.0.1", 7000);  

    InputStream istream = sock.getInputStream();
    BufferedReader br1 = new BufferedReader(new InputStreamReader(istream));

    String s1 = br1.readLine();
    System.out.println(s1);

    br1.close();    istream.close();   sock.close( );
  }
}