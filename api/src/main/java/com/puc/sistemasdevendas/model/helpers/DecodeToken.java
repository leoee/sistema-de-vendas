package com.puc.sistemasdevendas.model.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puc.sistemasdevendas.controllers.configurations.AuthorizeFilter;
import com.puc.sistemasdevendas.model.exceptions.InternalErrorException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jboss.logging.Logger;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Configuration
public class DecodeToken {
    private ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = Logger.getLogger(DecodeToken.class);

    private String decode(String encodedToken) {
        try {
            String[] pieces = encodedToken.split("\\.");
            String b64payload = pieces[1];
            return new String(Base64.decodeBase64(b64payload), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            this.logger.error("Failed to decode token: " + e.getMessage());
            throw new InternalErrorException("Failed to decode token");
        }
    }

    public String getGetFromToken(String token) {
        String json = this.decode(token);
        try {
            Map<String, String> mappedJson = this.mapper.readValue(json, Map.class);

            return mappedJson.get("sub");
        } catch (JsonProcessingException e) {
            this.logger.error("Failed to get email from token: " + e.getMessage());
            throw new InternalErrorException("Failed to convert json to object");
        }

    }

}
