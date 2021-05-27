package com.geoit.mrm.users.api.services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificacionsApiService {
    @Value("${notifications.api.url}")
    private String notificationsApiUrl;

    public Object sendMail(String to, String subject, String fromTitle, String content, String jwt, boolean async) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", jwt);

        HttpEntity<?> entity = new HttpEntity<>(content, headers);
        final String url = notificationsApiUrl + "/email?to=" + to + "&subject=" + subject + "&fromTitle=" + fromTitle
                + "&async=" + async;
        String r = new RestTemplate().postForObject(url, entity, String.class);
        return r;
    }
}
