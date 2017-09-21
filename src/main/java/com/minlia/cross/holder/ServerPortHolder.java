package com.minlia.cross.holder;

public class ServerPortHolder {
    private static Integer port;

    public static Integer getPort() {
        return ServerPortHolder.port;
    }

    public static void setPort(Integer port) {
        ServerPortHolder.port=port;
    }
}
