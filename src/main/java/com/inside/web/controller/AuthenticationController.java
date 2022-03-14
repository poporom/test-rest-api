package com.inside.web.controller;

import com.inside.entity.User;
import com.inside.model.AuthenticationRequest;
import com.inside.model.ChangePassword;
import com.inside.model.UserTokenState;
import com.inside.security.SecurityUser;
import com.inside.security.TokenHelper;
import com.inside.service.CustomUserDetailsService;
import com.inside.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping( value = "/api")
public class AuthenticationController {

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/auth/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityUser user = (SecurityUser)authentication.getPrincipal();
        String jws = tokenHelper.generateToken( user.getUsername());
        long expiresIn = tokenHelper.getExpiredIn();
        return ResponseEntity.ok(new UserTokenState(jws, expiresIn));
    }

    @PostMapping(value = "/auth/refresh")
    public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request, Principal principal) {

        String authToken = tokenHelper.getToken( request );

        if (authToken != null && principal != null) {
            String refreshedToken = tokenHelper.refreshToken(authToken);
            long expiresIn = tokenHelper.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(refreshedToken, expiresIn));
        } else {
            UserTokenState userTokenState = new UserTokenState();
            return ResponseEntity.accepted().body(userTokenState);
        }
    }

    @RequestMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User user(Principal user) {
        return this.userService.findByUsername(user.getName());
    }

    @PostMapping(value = "/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
        userDetailsService.changePassword(changePassword.getOldPassword(), changePassword.getNewPassword());
        Map<String, String> result = new HashMap<>();
        result.put( "result", "success" );
        return ResponseEntity.accepted().body(result);
    }
}