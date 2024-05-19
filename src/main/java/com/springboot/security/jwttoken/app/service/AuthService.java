package com.springboot.security.jwttoken.app.service;

import com.springboot.security.jwttoken.app.dto.ReqRes;
import com.springboot.security.jwttoken.app.entity.OurUsers;
import com.springboot.security.jwttoken.app.respository.OurUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes signUp(ReqRes resgistartionRequest){
        ReqRes res = new ReqRes();

        try {

            OurUsers ourUsers = new OurUsers();

            ourUsers.setEmail(resgistartionRequest.getEmail());
            ourUsers.setPassword(resgistartionRequest.getPassword());
            ourUsers.setRole(resgistartionRequest.getRole());

            OurUsers ourResult = ourUserRepo.save(ourUsers);

            if(ourResult != null && ourResult.getId() > 0){
                res.setOurUsers(ourResult);
                res.setMessage("User added successfully");
                res.setStatusCode(200);
            }

        }catch(Exception e) {
            res.setError(e.getMessage());
            res.setStatusCode(500);
        }

        return res;
    }


    public ReqRes signIn(ReqRes request){
        ReqRes res = new ReqRes();

        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            var user = ourUserRepo.findByEmail(request.getEmail()).orElseThrow();
            System.out.println("USER is "+ user);

            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            res.setStatusCode(200);
            res.setToken(jwt);
            res.setRefreshToken(refreshToken);
            res.setExpirationTime("24Hr");
            res.setMessage("Singed successfully");

        }catch(Exception e) {
            res.setError(e.getMessage());
            res.setStatusCode(500);
        }

        return res;
    }


    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();

        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        OurUsers users = ourUserRepo.findByEmail(ourEmail).orElseThrow();

        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }

        response.setStatusCode(500);
        return response;
    }
}

