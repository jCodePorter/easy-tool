package cn.augrain.easy.tool.digest;

import java.security.Provider;
import java.security.Security;
import java.util.Set;
import java.util.TreeSet;

public class ListDigestAlgorithms {
    public static void main(String[] args) {
        System.out.println("Supported MessageDigest Algorithms in Java " +
                System.getProperty("java.version") + ":");

        Set<String> algorithms = new TreeSet<>();

        for (Provider provider : Security.getProviders()) {
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equals("MessageDigest")) {
                    algorithms.add(service.getAlgorithm());
                }
            }
        }
        algorithms.forEach(System.out::println);
    }
}