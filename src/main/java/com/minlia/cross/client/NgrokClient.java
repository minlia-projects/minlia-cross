package com.minlia.cross.client;

import static com.minlia.cross.constant.Constant.DOMAIN;

import com.minlia.cross.message.MessageSender;
import com.minlia.cross.thread.CmdThread;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;

@Slf4j
public class NgrokClient extends Thread {


  private TaskExecutor taskExecutor;

  String serveraddr = DOMAIN;
  int serverport = 4443;
  SSLSocket sslSocket;
  SocketFactory sf = null;
  public String ClientId = "";
  public String localhost = "127.0.0.1";
  public int localport = 8080;
  public String protocol = "http";
  public boolean trfalg = true;
  public long lasttime = 0;
  public String authtoken = "";
  public List<HashMap<String, String>> tunnels = new ArrayList<HashMap<String, String>>();


  public HashMap<String, HashMap<String, String>> tunnelinfos = new HashMap<String, HashMap<String, String>>();

  public TaskExecutor getTaskExecutor() {
    return taskExecutor;
  }

  public void setTaskExecutor(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  public NgrokClient(String serveraddr, int serverport, String authtoken, Boolean debug) {
    this.serveraddr = serveraddr;
    this.serverport = serverport;
  }


  public NgrokClient() {
  }

  public void stopNgrokClient() {
    try {
      trfalg=false;
//      sslSocket.shutdownInput();
//      sslSocket.shutdownOutput();
      sslSocket.close();
      interrupt();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void run() {
    sslSocket = connectSSL();
    // 发送登录认证
    try {
      MessageSender.SendAuth("", authtoken, sslSocket.getOutputStream());
      //启动线程监听
      CmdThread cmdThread=new CmdThread(this, sslSocket);
      taskExecutor.execute(cmdThread);
    } catch (IOException e) {
      e.printStackTrace();
    }


  }


  public void addTun(String localhost, int localport, String Protocol, String Hostname,
      String Subdomain, int RemotePort, String HttpAuth) {

    HashMap<String, String> tunelInfo = new HashMap<String, String>();
    tunelInfo.put("localhost", localhost);
    tunelInfo.put("localport", localport + "");
    tunelInfo.put("Protocol", Protocol);
    tunelInfo.put("Hostname", Hostname);
    tunelInfo.put("Subdomain", Subdomain);
    tunelInfo.put("HttpAuth", HttpAuth);
    tunelInfo.put("RemotePort", RemotePort + "");
    tunnels.add(tunelInfo);

  }


  /*
   *
   */
  public SSLSocket connectSSL() {
    SSLSocket sslSocket1=null;
    if (sf == null) {
      try {
        sf = trustAllSocketFactory();
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }

    try {
      sslSocket1 = (SSLSocket) sf.createSocket(this.serveraddr, this.serverport);
    } catch (UnknownHostException e) {
      log.debug("Error with UnknownHostException {}",e.getMessage());
    } catch (IOException e) {
      log.debug("Error with IOException {}",e.getMessage());
    }

    try {
      sslSocket1.startHandshake();
    } catch (SSLHandshakeException e) {
      log.debug("Error with SSLHandshakeException {}",e.getMessage());
    } catch (IOException e) {
      log.debug("Error with IOException {}",e.getMessage());
    }

    sslSocket=sslSocket1;

    return sslSocket;
  }





  /*忽略证书*/
  public static SSLSocketFactory trustAllSocketFactory() throws Exception {
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          public void checkClientTrusted(X509Certificate[] certs, String authType) {
          }

          public void checkServerTrusted(X509Certificate[] certs, String authType) {
          }

        }
    };
    SSLContext sslCxt = SSLContext.getInstance("TLSv1.2");
    sslCxt.init(null, trustAllCerts, null);
    return sslCxt.getSocketFactory();
  }

}
