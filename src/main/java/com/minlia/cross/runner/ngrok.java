package com.minlia.cross.runner;


import static com.minlia.cross.constant.Constant.DOMAIN;

import com.minlia.cross.client.NgrokClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
public class ngrok {

  static String ClientId = "";

  public static void main(String[] args) {
    //new
    NgrokClient ngclient = new NgrokClient();
    //addtunnel
    ngclient.addTun("127.0.0.1", 7719, "http",
         DOMAIN, RandomStringUtils.randomAlphabetic(4).toLowerCase(), 4443, "");
//		ngclient.addTun("127.0.0.1",80,"http","","",0,"");
    //start
    ngclient.start();
    //check error
//    while (true) {
//      if (ngclient.lasttime + 30 < (System.currentTimeMillis() / 1000) && ngclient.lasttime > 0) {
//        log.debug("Check status with error");
//
//        ngclient.trfalg = false;
//        ngclient.tunnelinfos.clear();//
//        try {
//          Thread.sleep(10000);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        //reconnct
//        ngclient.trfalg = true;
//        ngclient.start();
//
//      } else {
//        log.debug("Check status with OK");
//      }
//      try {
//        Thread.sleep(30000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
  }
}
