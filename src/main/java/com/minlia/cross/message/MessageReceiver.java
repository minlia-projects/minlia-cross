package com.minlia.cross.message;

import com.minlia.cross.client.NgrokClient;
import com.minlia.cross.thread.PingThread;
import com.minlia.cross.thread.ProxyThread;
import com.minlia.cross.thread.SocksToThread;
import com.minlia.cross.util.BytesUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import javax.net.ssl.SSLSocket;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

@Slf4j
public class MessageReceiver {

  boolean isrecv = true;
  NgrokClient ngrokcli;


  public MessageReceiver(NgrokClient ngrokcli) {
    this.ngrokcli = ngrokcli;
  }

  public void jsonunpack(String str, SSLSocket s) {
    JSONObject json;
    try {
      log.debug("Received :{}" , str);
      json = new JSONObject(str);

      String type = json.getString("Type");
      // Auth back
      if (type.equals("AuthResp")) {
        JSONObject Payload = json.getJSONObject("Payload");
        String Error = Payload.getString("Error");
        if (Error.endsWith("")) {
          log.debug("AuthResp .....OK....");
          AuthResp(json, s);
        } else {
          log.debug("AuthResp .....Error....");
        }
      }

      if (type.equals("ReqProxy")) {
        ReqProxy(json);
      }

      // ping ack
      if (type.equals("Ping")) {

        Ping(json, s);
      }
      if (type.equals("Pong")) {

        Pong();
      }

      // NewTunnel
      if (type.equals("NewTunnel")) {
        JSONObject Payload = json.getJSONObject("Payload");
        String Error = Payload.getString("Error");
        if (Error.endsWith("")) {
          log.debug("NewTunnel .....OK....");
          NewTunnel(json);
        } else {
          log.debug("NewTunnel .....error....");
        }
      }

      // StartProxy
      if (type.equals("StartProxy")) {
        StartProxy(json, s);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void AuthResp(JSONObject json, SSLSocket s) {

    // 请求映射
    try {
      JSONObject Payload = json.getJSONObject("Payload");
      ngrokcli.ClientId = Payload.getString("ClientId");

      //
      HashMap<String, String> tunelInfo;

      for (int i = 0; i < ngrokcli.tunnels.size(); i++) {
        tunelInfo = ngrokcli.tunnels.get(i);
        String ReqId = UUID.randomUUID().toString().toLowerCase().replace("-", "").substring(0, 8);
        MessageSender.SendReqTunnel(s.getOutputStream(), ReqId, tunelInfo.get("Protocol"),
            tunelInfo.get("Hostname"), tunelInfo.get("Subdomain"), tunelInfo.get("RemotePort"),
            tunelInfo.get("HttpAuth"));
        HashMap<String, String> tunelInfo1 = new HashMap<String, String>();
        tunelInfo1.put("localhost", tunelInfo.get("localhost"));
        tunelInfo1.put("localport", tunelInfo.get("localport"));
        ngrokcli.tunnelinfos.put(ReqId, tunelInfo1);

      }

      // start ping thread
      new PingThread(ngrokcli, s).start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void ReqProxy(JSONObject json) {
    new ProxyThread(ngrokcli, ngrokcli.ClientId).start();
  }

  public void Ping(JSONObject json, SSLSocket s) {

    try {
      MessageSender.SendPong(s.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void Pong() {
    ngrokcli.lasttime = System.currentTimeMillis() / 1000;
  }

  public void NewTunnel(JSONObject json) {

    try {
      JSONObject Payload = json.getJSONObject("Payload");
      String ReqId = Payload.getString("ReqId");
      //添加到通道队列

      ngrokcli.tunnelinfos.put(Payload.getString("Url"), ngrokcli.tunnelinfos.get(ReqId));
      ngrokcli.tunnelinfos.remove(ReqId);//remove
      System.out.println("\r\nCross Access Address: " + Payload.getString("Url")+"\r\n");
//          + "  Protocol:" + Payload.getString("Protocol"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public void StartProxy(JSONObject json, SSLSocket s) {
    try {
      // 不再接收命令,
      this.isrecv = false;
      try {
        JSONObject Payload = json.getJSONObject("Payload");
        String Url = Payload.getString("Url");
        Socket locals = new Socket(ngrokcli.tunnelinfos.get(Url).get("localhost"),
            Integer.parseInt(ngrokcli.tunnelinfos.get(Url).get("localport")));
        new SocksToThread(s.getInputStream(),
            locals.getOutputStream());

        // 读取本地数据给远程
        new SocksToThread(locals.getInputStream(),
            s.getOutputStream());
      } catch (JSONException e) {
        e.printStackTrace();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void unpack(SSLSocket s) {
    byte[] packbuff = new byte[2048];
    int packbufflen = 0;
    byte[] buffer = new byte[1024];
    try {
      InputStream zx = s.getInputStream();
      while (isrecv) {
        int len = zx.read(buffer);
        if (len == -1) {
          break;
        }
        if (len == 0) {
          continue;
        }
        BytesUtil.myaddBytes(packbuff, packbufflen, buffer, len);
        packbufflen = packbufflen + len;

        if (packbufflen > 8) {
          // 发送时间
          int packlen = (int) BytesUtil
              .bytes2long(BytesUtil.leTobe(
                  BytesUtil.cutOutByte(packbuff, 0, 8), 8), 0);
          // 加上头8个字节
          packlen = packlen + 8;
          if (packbufflen == packlen) {
            jsonunpack(
                new String(BytesUtil.cutOutByte(packbuff, 8,
                    packlen - 8)), s);
            packbufflen = 0;
          } else if (packbufflen > packlen) {
            jsonunpack(
                new String(BytesUtil.cutOutByte(packbuff, 8,
                    packlen - 8)), s);
            packbufflen = packbufflen - packlen;
            BytesUtil.myaddBytes(packbuff, 0, BytesUtil.cutOutByte(
                packbuff, packlen, packbufflen), packbufflen);
          }
        }
      }
    } catch (IOException e) {
      //异常关闭连接
      isrecv = false;
      //e.printStackTrace();
      return;
    }
  }
}
