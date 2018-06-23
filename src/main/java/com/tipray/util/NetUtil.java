package com.tipray.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 网络工具类
 *
 * @author chenlong
 */
public class NetUtil {
    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    private NetUtil() {
    }

    /**
     * 获取访问者IP
     * <p>
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。<br>
     * 本方法先从Header中获取X-Real-IP，如果不存在，再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request.getRemoteAddr()。
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String} 访问者IP
     */
    public static String getRequestIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    /**
     * 是否手机请求
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Boolean}
     */
    public static boolean isMobile(HttpServletRequest request) {
        String[] mobileAgents = {"ipad", "iphone os", "rv:1.2.3.4", "ucweb", "android", "windows ce", "windows mobile"};
        String ua = request.getHeader("User-Agent").toLowerCase();
        for (String mua : mobileAgents) {
            if (ua.indexOf(mua) > -1) {
                return true;//手机端
            }
        }
        return false;//PC端
    }

    /**
     * 是否微信浏览器
     *
     * @param request {@link HttpServletRequest}
     * @return {@link Boolean}
     */
    public static boolean isWechat(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent").toLowerCase();
        if (ua.indexOf("micromessenger") > -1) {
            return true;//微信
        }
        return false;//非微信手机浏览器
    }


    /**
     * 获取来访者的浏览器版本
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String} 来访者的浏览器版本
     */
    public static String getRequestBrowserInfo(HttpServletRequest request) {
        String browserVersion = null;
        String header = request.getHeader("user-agent");
        if (header == null || header.equals("")) {
            return "";
        }
        if (header.indexOf("MSIE") > 0) {
            browserVersion = "IE";
        } else if (header.indexOf("Firefox") > 0) {
            browserVersion = "Firefox";
        } else if (header.indexOf("Chrome") > 0) {
            browserVersion = "Chrome";
        } else if (header.indexOf("Safari") > 0) {
            browserVersion = "Safari";
        } else if (header.indexOf("Camino") > 0) {
            browserVersion = "Camino";
        } else if (header.indexOf("Konqueror") > 0) {
            browserVersion = "Konqueror";
        }
        return browserVersion;
    }

    /**
     * 获取来访者的系统版本信息
     *
     * @param request {@link HttpServletRequest}
     * @return {@link String} 来访者的系统版本信息
     */
    public static String getRequestSystemInfo(HttpServletRequest request) {
        String systenInfo = null;
        String header = request.getHeader("user-agent");
        if (header == null || header.equals("")) {
            return "";
        }
        // 得到用户的操作系统
        if (header.indexOf("NT 6.0") > 0) {
            systenInfo = "Windows Vista/Server 2008";
        } else if (header.indexOf("NT 5.2") > 0) {
            systenInfo = "Windows Server 2003";
        } else if (header.indexOf("NT 5.1") > 0) {
            systenInfo = "Windows XP";
        } else if (header.indexOf("NT 6.0") > 0) {
            systenInfo = "Windows Vista";
        } else if (header.indexOf("NT 6.1") > 0) {
            systenInfo = "Windows 7";
        } else if (header.indexOf("NT 6.2") > 0) {
            systenInfo = "Windows Slate";
        } else if (header.indexOf("NT 6.3") > 0) {
            systenInfo = "Windows 9";
        } else if (header.indexOf("NT 5") > 0) {
            systenInfo = "Windows 2000";
        } else if (header.indexOf("NT 4") > 0) {
            systenInfo = "Windows NT4";
        } else if (header.indexOf("Me") > 0) {
            systenInfo = "Windows Me";
        } else if (header.indexOf("98") > 0) {
            systenInfo = "Windows 98";
        } else if (header.indexOf("95") > 0) {
            systenInfo = "Windows 95";
        } else if (header.indexOf("Mac") > 0) {
            systenInfo = "Mac";
        } else if (header.indexOf("Unix") > 0) {
            systenInfo = "UNIX";
        } else if (header.indexOf("Linux") > 0) {
            systenInfo = "Linux";
        } else if (header.indexOf("SunOS") > 0) {
            systenInfo = "SunOS";
        }
        return systenInfo;
    }

    /**
     * 获取来访者的主机名称
     *
     * @param ip {@link String} 来访者的IP地址
     * @return {@link String} 来访者的主机名称
     */
    public static String getHostName(String ip) {
        InetAddress inet;
        try {
            inet = InetAddress.getByName(ip);
            return inet.getHostName();
        } catch (UnknownHostException e) {
            logger.error("the local host name could not be resolved into an address! \n{}", e.toString());
        }
        return "";
    }

    /**
     * 获取本机IP地址
     *
     * @return {@link InetAddress} 本机IP地址
     */
    public static InetAddress getLocalHostLANAddress() {
        InetAddress candidateAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            // 遍历所有的网络接口
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddrs = networkInterface.getInetAddresses();
                // 在所有的接口下再遍历IP
                while (inetAddrs.hasMoreElements()) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback类型地址
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress == null) {
                // 如果没有发现 non-loopback地址.只能用最次选的方案 jdkSuppliedAddress
                candidateAddress = InetAddress.getLocalHost();
            }
        } catch (SocketException e) {
            logger.error("an I/O error occurs when get NetworkInterfaces! \n{}", e.toString());
        } catch (UnknownHostException e) {
            logger.error("the local host name could not be resolved into an address! \n{}", e.toString());
        }
        return candidateAddress;
    }

    /**
     * 获取MAC地址
     *
     * @param ia {@link InetAddress}
     * @return {@link String} MAC地址
     */
    public static String getMacAddress(InetAddress ia) {
        try {
            // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            // 下面代码是把mac地址拼装成String
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            // 把字符串所有小写字母改为大写成为正规的mac地址并返回
            return sb.toString().toUpperCase();
        } catch (SocketException e) {
            logger.error("an I/O error occurs when get HardwareAddress! \n{}", e.toString());
            return null;
        }
    }
}
