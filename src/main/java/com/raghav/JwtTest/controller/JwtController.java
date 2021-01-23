package com.raghav.JwtTest.controller;

import com.raghav.JwtTest.helper.JwtUtil;
import com.raghav.JwtTest.model.JwtRequest;
import com.raghav.JwtTest.model.JwtResponse;
import com.raghav.JwtTest.service.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    private JwtUtil jwtUtil;


    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ResponseEntity<?> getData() {
        return new ResponseEntity<>("hy from server", HttpStatus.OK);
    }

    @RequestMapping(value = "/generateToken", method = RequestMethod.POST)
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) {

        //authenticate user name and password with database
        //we have already configured customUserService to load user data by user name from database
        //if database exist so it will take data from database and authenticate with request data
        //get from client
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest
                    .getUserName()
                    , jwtRequest
                    .getPassword()));
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            throw new BadCredentialsException("Bad Credentials");
        }
        //now authentication done
        //now we can generate token

        UserDetails user = customUserService.loadUserByUsername(jwtRequest.getUserName());
        String token = jwtUtil.generateToken(user);
        return new ResponseEntity<>(new JwtResponse(token), HttpStatus.OK);
    }
}
