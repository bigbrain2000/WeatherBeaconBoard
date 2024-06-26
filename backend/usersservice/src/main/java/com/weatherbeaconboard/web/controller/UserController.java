package com.weatherbeaconboard.web.controller;

import com.weatherbeaconboard.service.converter.NullSafeConverter;
import com.weatherbeaconboard.service.user.UserService;
import com.weatherbeaconboard.web.model.User;
import com.weatherbeaconboard.web.model.UserDetailsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/v1", produces = APPLICATION_JSON_VALUE)
@Validated
public class UserController {

    private final UserService userService;
    private final NullSafeConverter converter;

    @GetMapping("/users/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") @NotNull Integer id) {
        final User foundUser = userService.getById(id);
        return new ResponseEntity<>(foundUser, OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") @NotNull Integer id) {
        userService.deleteById(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable @NotNull Integer id,
                                           @RequestBody @NotNull @Valid User user) {

        final User updateUser = userService.update(id, user);
        return new ResponseEntity<>(updateUser, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @NotNull @Valid User user) {
        return new ResponseEntity<>(userService.register(user), CREATED);
    }

    @PostMapping("/confirmEmail/{token}")
    public ResponseEntity<Void> confirmEmailValidation(@PathVariable @NotNull String token) {
        userService.confirmToken(token);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/userDetails")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@RequestParam @NotBlank String username) {

        final UserDetails foundUserDetails = userService.loadUserByUsername(username);
        final UserDetailsResponse userDetailsResponse = converter.convert(foundUserDetails, UserDetailsResponse.class);

        return new ResponseEntity<>(userDetailsResponse, OK);
    }
}
