package com.minlia.cross.thread;

import com.minlia.cross.message.MessageReceiver;
import com.minlia.cross.message.MessageSender;
import com.minlia.cross.client.NgrokClient;
import java.io.IOException;

import javax.net.ssl.SSLSocket;

public class ProxyThread extends Thread {
	String ClientId = "";
	NgrokClient ngrokcli;
	public ProxyThread(NgrokClient ngrokcli,String ClientIdp) {
		super();
		this.ngrokcli=ngrokcli;
		this.ClientId = ClientIdp;
	}
	

	public void run() {
			SSLSocket s=ngrokcli.connectSSL();
			//
			try {
				MessageSender.SendRegProxy(ClientId, s.getOutputStream());
				MessageReceiver msg = new MessageReceiver(ngrokcli);
				msg.unpack(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
