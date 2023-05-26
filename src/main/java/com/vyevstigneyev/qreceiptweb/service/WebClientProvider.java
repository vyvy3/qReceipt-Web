package com.vyevstigneyev.qreceiptweb.service;


import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.isNull;

public class WebClientProvider {
    private static WebClientProvider self = null;
    private final WebClient webClient;

    private WebClientProvider() {
        this.webClient = WebClient.create("http://45.148.31.152:8080");
    }

    public static WebClient getWebClient() {
        if (isNull(self)) {
            self = new WebClientProvider();
        }
        return self.webClient;
    }

}
