package com.puc.sistemasdevendas.model.helpers;

import com.puc.sistemasdevendas.model.exceptions.InternalErrorException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings
public class DecodeTokenTest {
    @InjectMocks
    private DecodeToken decodeToken;

    @Test
    public void shouldReturnEmailFromToken() {
        final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiY3JAbWFpbC5jb20iLCJpc3MiOiJsb2NhbGhvc3Q6ODA4MCIsImlhdCI6MTY1MTcwMjMwOSwiZXhwIjoxNjUxNzMxMTA5fQ.vE-3fFRBTS7Q3GE8Y7Q2GJMatBlhwGAL_Sv1bJlFxOo";
        final String expectedEmail = "bcr@mail.com";
        assertEquals(expectedEmail, this.decodeToken.getGetFromToken(token));
    }

    @Test
    public void shouldReturnInternalErrorExceptionBecauseTokenIsNull() {
        assertThrows(InternalErrorException.class, () -> this.decodeToken.getGetFromToken(null));
    }
}