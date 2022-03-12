package com.wisdge.web;

import com.wisdge.web.filetypes.FileExt;
import org.junit.Test;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IPUtilsTester {

    @Test
    public void test() throws SocketException {
        List<String> ips = IPUtils.getLocalIPs();
        System.out.println(ips);

        System.out.println("OS: " + IPUtils.getOSName());
        System.out.println("HostName: " + IPUtils.getHostName());


        System.out.println("192.168.0".matches("192.*.*.0"));
        System.out.println(IPUtils.isPermited("192.168.0.1", "192.*"));
        System.out.println("\n\n");

        Set<String> ipWhiteConfigs = new HashSet<>();
        ipWhiteConfigs.add("1.168.1.*");
        ipWhiteConfigs.add("10.*");
        ipWhiteConfigs.add("192.168.3.15-192.168.3.38");
        ipWhiteConfigs.add("192.168.1.0/24");
        ipWhiteConfigs.add("127.0.0.1");

        System.out.println(IPUtils.isPermited("1.168.1.1", ipWhiteConfigs));
        System.out.println(IPUtils.isPermited("192.168.1.2", ipWhiteConfigs));
        System.out.println(IPUtils.isPermited("192.168.2.1", ipWhiteConfigs));
        System.out.println(IPUtils.isPermited("192.168.3.16", ipWhiteConfigs));
        System.out.println(IPUtils.isPermited("10.168.3.37", ipWhiteConfigs));
        System.out.println(IPUtils.isPermited("192.168.4.1", ipWhiteConfigs));
        System.out.println(IPUtils.isPermited("127.0.0.1", ipWhiteConfigs));
    }

    public void test2() throws IOException {
        byte[] data = FileExt.getImgByExt("docx");
        System.out.println("File type image size:" + data.length);
    }
}
