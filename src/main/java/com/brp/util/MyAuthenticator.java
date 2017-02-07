package com.brp.util;

import javax.mail.*;
  /**
   *身份验证
   */
public class MyAuthenticator extends Authenticator{
	String userName=null;
	String password=null;
	 
	public MyAuthenticator(){
	}
	public MyAuthenticator(String username, String password) { 
		this.userName = username; 
		this.password = password; 
	} 
	protected PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(userName, password);
	}
}
 
