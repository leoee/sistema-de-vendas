package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.controllers.configurations.AuthorizeFilter;
import com.puc.sistemasdevendas.model.entities.User;
import com.puc.sistemasdevendas.model.entities.UserPatch;
import com.puc.sistemasdevendas.model.exceptions.DuplicateEntity;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.UserRepository;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.puc.sistemasdevendas.model.constants.ApiConstans.ADM_ROLE;
import static com.puc.sistemasdevendas.model.constants.ApiConstans.BASIC_ROLE;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

@MockitoSettings
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private final Logger logger = Logger.getLogger(AuthorizeFilter.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartService shoppingCartService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private DecodeToken decodeToken;
    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    public void shouldCreateUser() {
        User mockedUser = this.mockUser();

        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.empty());
        when(this.userRepository.insert((User) any())).thenReturn(mockedUser);

        User createdUser = this.userService.createUser(mockedUser);

        verify(this.shoppingCartService, times(1)).createShoppingCart(any());

        assertNull(createdUser.getPassword());
    }

    @Test
    public void shouldThrowDuplicatedEntity() {
        User mockedUser = this.mockUser();

        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));

        assertThrows(DuplicateEntity.class,
                () ->  this.userService.createUser(mockedUser));
    }

    @Test
    public void shouldGetAllUsers() {
        final String email = "test@mail.com";
        User mockedUser = this.mockUser();
        mockedUser.setRole(ADM_ROLE);

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));

        this.userService.getUsers("token");
        verify(this.userRepository, times(1)).findAll();
    }

    @Test
    public void shouldThrowForbiddenWhenGettingAsBasicUser() {
        final String email = "test@mail.com";
        User mockedUser = this.mockUser();

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));

        assertThrows(ForbidenException.class,
                () ->  this.userService.getUsers("token"));
    }

    @Test
    public void shouldReturnAdmin() {
        User mockedUser = this.mockUser();
        mockedUser.setRole(ADM_ROLE);
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));

        assertTrue(this.userService.isAdminRole("test@mail.com"));
    }

    @Test
    public void shouldReturnBasicRole() {
        User mockedUser = this.mockUser();
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));

        assertFalse(this.userService.isAdminRole("test@mail.com"));
    }

    @Test
    public void shouldReturnCurrentUser() {
        User mockedUser = this.mockUser();

        when(this.decodeToken.getGetFromToken(any())).thenReturn("test@mail.com");
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));

        User currentUser = this.userService.getCurrentUser("123");

        assertNull(currentUser.getPassword());
    }

    @Test
    public void shouldReturnNullThereIsNoUser() {
        when(this.decodeToken.getGetFromToken(any())).thenReturn("test@mail.com");
        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.empty());

        assertNull(this.userService.getCurrentUser("123"));
    }

    @Test
    public void shouldReturnValidUserCredentials() {
        User mockedUser = this.mockUser();

        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));
        when(this.bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

        assertTrue(this.userService.checkUserCredentials("test@mail.com", "XXXX"));
    }

    @Test
    public void shouldReturnInvalidUserCredentials() {
        User mockedUser = this.mockUser();

        when(this.userRepository.findUserByEmail(any())).thenReturn(Optional.of(mockedUser));
        when(this.bCryptPasswordEncoder.matches(any(), any())).thenReturn(false);

        assertFalse(this.userService.checkUserCredentials("test@mail.com", "XXXX"));
    }

    @Test
    public void shouldUpdateUser() {
        final String email = "test@mail.com";
        final String name = "test";
        final String cpf = "cpf";
        final String cellphoneNumber = "number";
        final String password = "password";
        final String encodedPassword = "encodedPassword";

        User mockedUser = this.mockUser();
        UserPatch requestPatchPayload = new UserPatch();
        requestPatchPayload.setName(name);
        requestPatchPayload.setCpf(cpf);
        requestPatchPayload.setCellphoneNumber(cellphoneNumber);
        requestPatchPayload.setPassword(password);

        when(this.decodeToken.getGetFromToken(any())).thenReturn(email);
        when(this.userRepository.findById(any())).thenReturn(Optional.of(mockedUser));
        when(this.bCryptPasswordEncoder.encode(requestPatchPayload.getPassword())).thenReturn(encodedPassword);

        Update expectedUpdate = new Update();
        expectedUpdate.set("name", name);
        expectedUpdate.set("cpf", cpf);
        expectedUpdate.set("cellphoneNumber", cellphoneNumber);
        expectedUpdate.set("password", encodedPassword);

        this.userService.updateUser("123", "userId", requestPatchPayload);

        verify(this.mongoTemplate, times(1))
                .findAndModify(any(), eq(expectedUpdate), (FindAndModifyOptions) any(), any());
    }

    @Test
    public void shouldThrowForbiddenWhenUpdatingDifferentUser() {
        User mockedUser = this.mockUser();

        when(this.decodeToken.getGetFromToken(any())).thenReturn("different@mail.com");
        when(this.userRepository.findById(any())).thenReturn(Optional.of(mockedUser));

        UserPatch requestPatchPayload = new UserPatch();
        requestPatchPayload.setName("name");

        assertThrows(ForbidenException.class,
                () ->  this.userService.updateUser("token", "userId", requestPatchPayload));
    }

    private User mockUser() {
        User mockedUser = new User();
        mockedUser.setId("123");
        mockedUser.setEmail("test@mail.com");
        mockedUser.setRole(BASIC_ROLE);
        mockedUser.setName("Test");
        mockedUser.setCpf("1234567890");
        mockedUser.setPassword("XXXXXX");

        return mockedUser;
    }

}