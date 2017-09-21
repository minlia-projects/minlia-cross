package com.minlia.cross.thread;

import com.minlia.cross.message.MessageReceiver;
import com.minlia.cross.client.NgrokClient;
import javax.net.ssl.SSLSocket;

public class CmdThread extends Thread {
  SSLSocket sock;
  NgrokClient ngrokcli;
  public CmdThread(NgrokClient ngrokcli,SSLSocket s) {
    super();
    this.sock = s;
    this.ngrokcli=ngrokcli;
  }

  public void run() {
    while (ngrokcli.trfalg) {
      // 监听
      MessageReceiver msg = new MessageReceiver(this.ngrokcli);
      msg.unpack(sock);
    }

  }
}