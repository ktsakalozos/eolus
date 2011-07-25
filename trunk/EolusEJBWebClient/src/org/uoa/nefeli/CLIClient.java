package org.uoa.nefeli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.ws.BindingProvider;



public class CLIClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String epr = "http://n18.di.uoa.gr:9998/Madgik/Nefeli?wsdl";
		if (args.length == 1) {
			epr = args[0];
		}

		String username;
		String password;

		NefeliService service = new NefeliService();
		Nefeli nefeli = service.getNefeliPort();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String str = "";
			System.out.print("Username: ");
			username = in.readLine();
			System.out.print("Password: ");
			password = in.readLine();
			BindingProvider bp = (BindingProvider)nefeli;    
			Map<String, Object> rc = bp.getRequestContext();   
			rc.put(BindingProvider.USERNAME_PROPERTY, username);
			rc.put(BindingProvider.PASSWORD_PROPERTY, password);

			while (!str.equalsIgnoreCase("q")) {
				System.out.print("\n" +
						"Q) Quit\n"+
						"1) Send \"ping\" workflow\n" +
						"2) Send workflow from file\n" +
						"3) Remove workflow\n"+
						"4) Send signal\n"+
				"");
				str = in.readLine();
				process(nefeli, in, str);
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

	}

	private static void process(Nefeli nefeli, BufferedReader in, String cmd) 
	throws java.lang.Exception{
		if (cmd.equalsIgnoreCase("1")){ //List Users
			Long id = nefeli.addWorkload("<WorkFlow>"+
					"<Machines>"+
					"<Machine>"+
					"<ID>1</ID>"+
					"<RAM>256</RAM>"+
					"<HD>10</HD>"+
					"</Machine>"+
					"</Machines>"+
					"</WorkFlow>"+
			"");
			//					"<Wishes></Wishes>"+
			//					"<States/>"+
			//					"<Events/>"+
			System.out.println("Workflow id: "+ id);
		}
		if (cmd.equalsIgnoreCase("2")){ //Send workflow from file
			System.out.print("Filename: ");
			String filename = in.readLine();
			String content = readFileAsString(filename);
			Long id = nefeli.addWorkload(content);
			System.out.println("Workflow id: "+ id);
		}
		if (cmd.equalsIgnoreCase("3")){ //Remove workflow
			System.out.print("Workflow ID: ");
			String id = in.readLine();
			boolean res = nefeli.removeWorkload(Long.parseLong(id));
			if (res){
				System.out.println("OK");				
			}else{
				System.out.println("Not OK");								
			}
		}
		if (cmd.equalsIgnoreCase("4")){ //Send signal
			System.out.print("Workflow ID: ");
			String id = in.readLine();
			Long lid = Long.parseLong(id);
			System.out.print("Port: ");
			String port = in.readLine();
			Integer iport = Integer.parseInt(port);
			System.out.print("Keyword: ");
			String word = in.readLine();
			boolean res = nefeli.magicWord(lid, iport, word);
			if (res){
				System.out.println("OK");				
			}else{
				System.out.println("Not OK");								
			}
		}

	}


	private static String readFileAsString(String filePath)
	throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
				new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}


