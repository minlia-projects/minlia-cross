package com.minlia.cross.message;

import com.minlia.cross.util.BytesUtil;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

@Slf4j
public class MessageSender {

  public static void SendAuth(String ClientId, String user, OutputStream o) {

    try {
      JSONObject msgjson = new JSONObject();
      msgjson.put("Type", "Auth");
      JSONObject Payloadjson = new JSONObject();
      Payloadjson.put("Version", "2");
      Payloadjson.put("MmVersion", "1.7");
      Payloadjson.put("User", user);
      Payloadjson.put("Password", "");
      Payloadjson.put("OS", "darwin");
      Payloadjson.put("Arch", "amd64");
      Payloadjson.put("ClientId", ClientId);
      msgjson.put("Payload", Payloadjson);
      pack(msgjson.toString(), o);

    } catch (JSONException e) {
      e.printStackTrace();
    }


  }

  public static void SendReqTunnel(OutputStream o, String ReqId, String Protocol, String hostname,
      String subdomain, String RemotePort, String HttpAuth) {
    //
    try {
      JSONObject msgjson = new JSONObject();
      msgjson.put("Type", "ReqTunnel");

      JSONObject Payloadjson = new JSONObject();
      Payloadjson.put("ReqId", ReqId);
      Payloadjson.put("Protocol", Protocol);
      if (Protocol.equals("tcp")) {
        Payloadjson.put("RemotePort", RemotePort);
      } else {
        Payloadjson.put("Subdomain", subdomain);
        Payloadjson.put("HttpAuth", HttpAuth);
        hostname=subdomain+"."+hostname;
        Payloadjson.put("Hostname", hostname);
      }
      msgjson.put("Payload", Payloadjson);
      pack(msgjson.toString(), o);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  public static void SendPong(OutputStream o) {
    pack("{\"Type\":\"Pong\",\"Payload\":{}}", o);
  }

  public static void SendPing(OutputStream o) {
    pack("{\"Type\":\"Ping\",\"Payload\":{}}", o);
  }


  public static void SendRegProxy(String ClientId, OutputStream o) {

    pack("{\"Type\":\"RegProxy\",\"Payload\":{\"ClientId\":\""
        + ClientId + "\"}}", o);
  }

  public static void pack(String str, OutputStream o) {
    byte[] lenbuf = BytesUtil.longToBytes(str.length(), 0);
    byte[] xx = str.getBytes();
    byte[] msgpack = BytesUtil.addBytesnew(str.length() + 8, lenbuf, xx);
    log.debug("Sending message :{}", str);
      try {
        o.write(msgpack, 0, str.length() + 8);
      } catch (IOException e) {
    }
  }
}
