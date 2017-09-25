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

  private String subdomain;


}
