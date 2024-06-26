package com.weatherbeaconboard.service.converter;

import com.weatherbeaconboard.model.UserEntity;
import com.weatherbeaconboard.web.model.User;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static com.weatherbeaconboard.model.enums.RoleType.USER;
import static com.weatherbeaconboard.utils.DateUtils.getDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserEntityToUserConverterTest {

    private final UserEntityToUserConverter uut = new UserEntityToUserConverter();

    @Test
    void convert_validUserEntity_returnsUser() {
        final String username = "username";
        final String password = "12345678";
        final String firstName = "Armi";
        final String ciuci = "Ciuci";
        final String phoneNumber = "1234567890";
        final String email = "alex@gmail.com";
        final String cityAddress = "Caracal";
        final OffsetDateTime NOW = getDateTime();

        final UserEntity expectedUserEntity = UserEntity.builder()
                .id(1)
                .username(username)
                .password(password)
                .firstName(firstName)
                .lastName(ciuci)
                .role(USER)
                .email(email)
                .address(cityAddress)
                .phoneNumber(phoneNumber)
                .enabled(false)
                .locked(false)
                .version(0)
                .build();

        final User expectedUser = User.builder()
                .id(1)
                .username(username)
                .password(password)
                .firstName(firstName)
                .lastName(ciuci)
                .role(USER)
                .email(email)
                .address(cityAddress)
                .phoneNumber(phoneNumber)
                .enabled(false)
                .locked(false)
                .version(0)
                .build();

        User actualUser = uut.convert(expectedUserEntity);

        assertEquals(expectedUser, actualUser);
    }
}