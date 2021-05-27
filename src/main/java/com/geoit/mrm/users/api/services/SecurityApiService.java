package com.geoit.mrm.users.api.services;

import com.geoit.mrm.users.api.models.ConfirmacionToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SecurityApiService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityApiService.class);

    @Value("${security.api.url}")
    private String securityApiUrl;

    @Value("${security.api.basic.auth.username}")
    private String securityApiUsername;

    @Value("${security.api.basic.auth.password}")
    private String securityApiPassword;

    public ConfirmacionToken obtenerNuevoUsuarioConfirmacionToken(long idUsuario) {
        try {
            final String url = securityApiUrl + "/jwt/uct/au";
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(securityApiUsername, securityApiPassword);

            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("idu", idUsuario);
            HttpEntity<?> request = new HttpEntity<>(map, headers);

            return new RestTemplate().exchange(url, HttpMethod.POST, request, ConfirmacionToken.class).getBody();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}