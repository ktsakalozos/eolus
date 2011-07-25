/*
* Copyright 2010 Konstantinos Tsakalozos
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be
* approved by the European Commission - subsequent versions of the EUPL (the Licence);
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in  writing, software
* distributed under the Licence is distributed on an AS IS basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package org.uoa.eolus.script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.uoa.eolus.DirectoryException;
import org.uoa.eolus.InternalErrorException;

public class Scripting {

	String scriptsPath = "/var/tmp";

	public Scripting(String path) {
		scriptsPath = path;
		new File(scriptsPath).mkdirs();
	}

	public boolean removeUserScript(String user, String script) 
			throws DirectoryException {
		String cmd = "rm -rf " + scriptsPath + "/" + user + "/" + script ;
		System.out.println("CMD: " + cmd);
		Runtime run = Runtime.getRuntime();
		try {
			Process child = run.exec(cmd);
			return true;
		} catch (Exception e) {
			throw new DirectoryException("Remove script directory failed.",e);
		}
	}
	
	public void addScript(String user, String name, String content) 
			throws DirectoryException {
		new File(scriptsPath + "/" + user + "/" + name).mkdir();
		try {
			FileWriter fstream = new FileWriter(scriptsPath + "/" + user + "/" + name + "/"
					+ name + ".sh");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(content);
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			throw new DirectoryException("Creating script directory failed.",e);
		}
	}

	public String[] getUserScriptsList(String user) {
		File dir = new File(scriptsPath + "/" + user);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		};
		return dir.list(filter);
	}

	public void applyScript(String user, String script, String ip)
			throws InternalErrorException {
		Runtime run = Runtime.getRuntime();
		try {
			Process child = run.exec("rm /tmp/hosts");
			child.waitFor();
			String sshcmd = "scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/tmp/hosts "
					+ scriptsPath
					+ "/" + user + "/"
					+ script
					+ "/"
					+ script
					+ ".sh"
					+ " root@"
					+ ip + ":";
			child = run.exec(sshcmd);
			child.waitFor();

			sshcmd = "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/tmp/hosts  root@"
				+ ip + " chmod +x "+ script + ".sh";
			child = run.exec(sshcmd);
			child.waitFor();

			
			sshcmd = "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/tmp/hosts  root@"
					+ ip + "  "+ script + ".sh";
			child = run.exec(sshcmd);
			child.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new InternalErrorException("Execution of script failed.",e);
//			e.printStackTrace();
		}

	}

	public String[] runCMD(String cmd, String ip) throws InternalErrorException {
		Runtime run = Runtime.getRuntime();
		try {
			Process child = run.exec("rm /tmp/hosts");
			child.waitFor();
			String sshcmd = "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/tmp/hosts  root@"
					+ ip + " " + cmd + "";
			System.out.println("CMD: " + sshcmd);
			child = run.exec(sshcmd);
			child.waitFor();

			InputStream out = child.getInputStream();
			InputStreamReader isr = new InputStreamReader(out);
			BufferedReader output = new BufferedReader(isr);

			InputStream err = child.getErrorStream();
			InputStreamReader eisr = new InputStreamReader(err);
			BufferedReader error = new BufferedReader(eisr);

			child.waitFor();

			String o = "";
			String s;
			while ((s = output.readLine()) != null) {
				o += s + "\n";
			}
			System.out.println("stdout: " + o);

			String e = "";
			s = error.readLine(); // First line is a warning: Warning:
			// Permanently added 'XX.XX.XX.XX' (RSA) to
			// the list of known hosts.
			while ((s = error.readLine()) != null)
				e += s + "\n";
			System.out.println("stderr: " + e);

			String[] res = new String[2];
			res[0] = o;
			res[1] = e;
			return res;

		} catch (Exception e) {
			throw new InternalErrorException("Could not execute comand.",e);
		}
	}
	
	public void createUser(String user) throws DirectoryException{
		
		boolean mkdir = (new File(scriptsPath + "/" + user)).mkdir();
		if (!mkdir){
			System.out.println("Template directory creation failed.");
			File d = new File(scriptsPath + "/" + user);
			if ((d==null) || (!d.isDirectory())){
				throw new DirectoryException("Template directory creation failed");
			}
		}
	}
	
	public boolean removeUser(String user) throws DirectoryException{
		 return deleteDir(new File(scriptsPath + "/" + user));
	}
	
	public boolean deleteDir(File dir) throws DirectoryException{
		if (dir.isDirectory()){ 
			String[] children = dir.list(); 
			for (int i=0; i<children.length; i++){ 
				boolean success = deleteDir(new File(dir, children[i])); 
				if (!success) {
					throw new DirectoryException("Cannot clear directory."); 
				} 
			} 
		} 
		// The directory is now empty so delete it
		return dir.delete();
	}

	public String[] getAllUsersOfRepo() {
		File dir = new File(scriptsPath);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		};
		return dir.list(filter);
	}


}
