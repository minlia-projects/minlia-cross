package com.minlia.cross.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by will on 9/21/17.
 */
@ConfigurationProperties(
    prefix = "minlia.cross",
    ignoreUnknownFields = true
)
@Data
public class CrossProperties {


  /**
   * 本地主机地址
   */
  private String localhost;

  /**
   * 本地端口
   */
  private Integer localPort;


  /**
   * 远程服务器地址
   */
  private String remoteServer;

  /**
   * 子域名
   */
  private String subdomain;

  /**
   * 远程服务器端口, 默认为4443
   */
  private Integer remotePort;


}
