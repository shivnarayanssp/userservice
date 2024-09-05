package dev.shiv4u.userservice.controllers;

import dev.shiv4u.userservice.dtos.*;
import dev.shiv4u.userservice.models.SessionStatus;
import dev.shiv4u.userservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    @Autowired
    public AuthController(AuthService authService){
        this.authService=authService;
    }
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request){
        return authService.login(request.getEmail(),request.getPassword());
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request){
        authService.logout(request.getToken(),request.getUserId());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUprequestDto request){
        UserDto userDto=authService.signUp(request.getEmail(),request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody validatetokenRequestDto request){
        SessionStatus sessionStatus=authService.validate(request.getToken(),request.getUserId());
        return new ResponseEntity<>(sessionStatus,HttpStatus.OK);
    }
}
