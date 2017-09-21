package com.minlia.cross.thread;

import com.minlia.cross.client.NgrokClient;
import com.minlia.cross.message.MessageSender;
import java.io.IOException;
import java.net.SocketException;
import javax.net.ssl.SSLSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PingThread extends Thread {

  SSLSocket sock;
  NgrokClient ngrokcli;

  public PingThread(NgrokClient ngrokcli, SSLSocket s) {
    super();
    this.ngrokcli = ngrokcli;
    this.sock = s;
  }

  public void run() {
    while (ngrokcli.trfalg) {
      try {
        try {
          MessageSender.SendPing(sock.getOutputStream());
        } catch (SocketException e) {
          ngrokcli.trfalg = false;
        }
        log.debug("Ping ....");
      } catch (IOException e) {
        ngrokcli.trfalg = false;
      }
      try {
        Thread.sleep(30000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }
}