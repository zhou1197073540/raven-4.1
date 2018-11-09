package com.raven.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

@WebFilter(urlPatterns = "/*", filterName = "IpFilter")
public class InnerNetFilter implements Filter {

    private static final String WIN = "win";
    private static final String LIN = "lin";
    private static final String os = getOSName();
    private static String LOCAL_HOST;

    private static final Logger logger = LoggerFactory.getLogger(InnerNetFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("access host filter init start");
        if (os.equals(WIN)) {
            LOCAL_HOST = getWinLocalHost();
        } else {
            LOCAL_HOST = getLinuxLocalHost();
        }
        if (LOCAL_HOST == null) {
            logger.error("access host filter init failed,LOCAL_HOST is null");
        }
        logger.info("access host filter init over");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private static String getOSName() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return WIN;
        } else {
            return LIN;
        }
    }

    private String getWinLocalHost() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("win ip error:\n", e);
            return null;
        }
    }

    private String getLinuxLocalHost() {
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            logger.error("Linux ip error:\n", e);
            return null;
        }
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    System.out.println("本机的IP = " + ip.getHostAddress());
                    return ip.getHostAddress();
                }
            }
        }
        return null;
    }
}
