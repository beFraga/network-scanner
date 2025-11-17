package com.seguranca.rede.scanner.ProjectPatterns.one;

import java.util.ArrayList;
import java.util.List;

public class HttpComposite implements NetworkComponent {


    private final String method;
    private final String uri;
    private final int headerSize; // bytes do cabeçalho HTTP
    private final List<NetworkComponent> children = new ArrayList<>();

    public HttpComposite(String method, String uri, int headerSize) {
        this.method = method;
        this.uri = uri;
        this.headerSize = headerSize;
    }

    public void addComponent(NetworkComponent component) {
        children.add(component);
    }

    public void removeComponent(NetworkComponent component) {
        children.remove(component);
    }

    @Override
    public void showInfo() {
        System.out.println("HTTP Request:");
        System.out.println(" Method: " + method);
        System.out.println(" URI: " + uri);
        System.out.println(" TCP packets attached: " + children.size());
        System.out.println("sum: " + getByteSize());
        System.out.println("------------------------------");

        for (NetworkComponent c : children) {
            c.showInfo();
        }
    }

    @Override
    public int getByteSize() {
        int sum = headerSize;
        for (NetworkComponent c : children) {
            sum += c.getByteSize();
        }
        return sum;
    }
}
