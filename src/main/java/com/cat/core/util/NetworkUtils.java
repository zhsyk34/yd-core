package com.cat.core.util;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.stream.Collectors.toList;

@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
public abstract class NetworkUtils {

    public static String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("获取网络信息出错", e);
            return null;
        }
    }

    public static List<String> findHosts() {
        try {
            return Collections.list(getNetworkInterfaces()).stream()
                    .filter(NetworkUtils::loopback)
                    .filter(NetworkUtils::isUp)
                    .map(NetworkUtils::mapToAddress)
                    .flatMap(Function.identity())
                    .collect(toList());
        } catch (SocketException e) {
            logger.error("获取网络信息出错", e);
            return null;
        }
    }

    private static Stream<String> mapToAddress(NetworkInterface networkInterface) {
        return Collections.list(networkInterface.getInetAddresses()).stream()
                .filter(address -> !(address instanceof Inet6Address))
                .map(InetAddress::getHostAddress);
    }

    private static boolean loopback(NetworkInterface networkInterface) {
        try {
            return !networkInterface.isLoopback();
        } catch (SocketException e) {
            return true;
        }
    }

    private static boolean isUp(NetworkInterface networkInterface) {
        try {
            return networkInterface.isUp();
        } catch (SocketException e) {
            return true;
        }
    }
}
