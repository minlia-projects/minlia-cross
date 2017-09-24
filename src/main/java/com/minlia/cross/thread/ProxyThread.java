package com.minlia.cross.thread;

import com.minlia.cross.message.MessageReceiver;
import com.minlia.cross.message.MessageSender;
import com.minlia.cross.client.NgrokClient;
import java.io.IOException;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			try {
				MessageSender.SendRegProxy(ClientId, s.getOutputStream());
				MessageReceiver msg = new MessageReceiver(ngrokcli);
				msg.unpack(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
}
