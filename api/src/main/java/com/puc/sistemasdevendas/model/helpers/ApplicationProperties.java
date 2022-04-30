package com.puc.sistemasdevendas.model.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationProperties {
    @Value("${jwt.resources.ignore}")
    private List<String> authResourcesIgnores;
    @Value("${jwt.key}")
    private String jwtKey;

    public List<String> getAuthResourcesIgnores() {
        return authResourcesIgnores;
    }

    public void setAuthResourcesIgnores(List<String> authResourcesIgnores) {
        this.authResourcesIgnores = authResourcesIgnores;
    }

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }
}
