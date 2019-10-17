import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class Servidor extends Thread
{
	private static ArrayList<BufferedWriter> clientes;           
	private static ServerSocket server;
	private static ArrayList<String> nomes;
	private String nome;
	private Socket con;
	private InputStream in;  
	private InputStreamReader inr;  
	private BufferedReader bfr;
	
	
	public Servidor(Socket con)
	{
	   this.con = con;
	   try 
	   {
		   in  = con.getInputStream();
		   inr = new InputStreamReader(in);
		   bfr = new BufferedReader(inr);
	   } 
	   catch (IOException e) 
	   {
	          e.printStackTrace();
	   }                          
	}
	
	public void run()
	{
		try
		{	                                      
			String msg;
			OutputStream ou =  this.con.getOutputStream();
			Writer ouw = new OutputStreamWriter(ou);
			BufferedWriter bfw = new BufferedWriter(ouw); 
			clientes.add(bfw);
			nome = msg = bfr.readLine();
			nomes.add(nome);
			           
			while(!"/quit".equalsIgnoreCase(msg) && msg != null)
			{           
				msg = bfr.readLine();
				
				String[] a = msg.split(" "); //a[0] = /w; a[1] = nick; a[2] = /m; a[3] = msg
				
				if(a.length >= 4 && a[0].equals("/w") && a[2].equals("/m"))
					sussurrar(bfw, a[1], a[3]);
				else
					sendToAll(bfw, msg);                                         
			}
			
			clientes.remove(bfw);
			
			System.out.println("O cliente " + nome + " desconectou-se!");
			                                      
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}                       
	}
	
	public void sussurrar(BufferedWriter bwSaida, String nick, String msg) throws IOException
	{
		if(nomes.indexOf(nick) == -1)
		{
			sendToAll(bwSaida, msg);
			return;
		}
		
		BufferedWriter bwS = clientes.get(nomes.indexOf(nick));
		
		if(!(bwSaida == bwS))
		{
			bwS.write(nome + " sussurrou para voc�: " + msg + "\n");
			bwS.flush(); 
		}
	}
	
	public void sendToAll(BufferedWriter bwSaida, String msg) throws  IOException 
	{
		BufferedWriter bwS;
		    
		for(BufferedWriter bw : clientes)
		{
			bwS = (BufferedWriter)bw;
			if(!(bwSaida == bwS))
			{
				if(msg.equals("Entrou no grupo."))
					bw.write(nome + " entrou no grupo.\n");
				else
					bw.write(nome + ": " + msg+"\n");
				
				bw.flush(); 
			}
		}          
	}
	
	public static void main(String[] args) 
	{
		try
		{
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField txtPorta = new JTextField("12345");
			Object[] texts = {lblMessage, txtPorta };  
			JOptionPane.showMessageDialog(null, texts);
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			clientes = new ArrayList<BufferedWriter>();
			nomes = new ArrayList<String>();
			JOptionPane.showMessageDialog(null,"Servidor ativo na porta: "+         
			txtPorta.getText());
					    
			while(true)
			{
				System.out.println("Aguardando conex�o...");
				Socket con = server.accept();
				System.out.println("Cliente conectado...");
				Thread t = new Servidor(con);
				t.start();   
			}                              
		}
		catch (Exception e) 
		{
		e.printStackTrace();
		}              
	}
}
