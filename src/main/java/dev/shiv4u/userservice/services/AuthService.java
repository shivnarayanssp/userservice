package dev.shiv4u.userservice.services;

import dev.shiv4u.userservice.dtos.UserDto;
import dev.shiv4u.userservice.models.Session;
import dev.shiv4u.userservice.models.SessionStatus;
import dev.shiv4u.userservice.models.User;
import dev.shiv4u.userservice.repositories.SessionRepository;
import dev.shiv4u.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

@Autowired
    public AuthService(UserRepository userRepository,
            SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.sessionRepository = sessionRepository;
        this.userRepository=userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }

    public ResponseEntity<UserDto> login(String email,String password) {
       Optional<User> userOptional = userRepository.findByEmail(email);
       if(userOptional.isEmpty()){
           return null;
       }
       User user=userOptional.get();
       /*if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
           throw new RuntimeException("Password/username does not match");
       }*/
       String token = RandomStringUtils.randomAlphanumeric(30);
        Session session=new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);
        UserDto userDto=UserDto.from(user);
        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);
        return new ResponseEntity<>(userDto,headers, HttpStatus.OK);
    }

    public void logout(String token,Long userId) {
        Optional<Session> sessionOptional=sessionRepository
                .findBytokenAndUser_id(token,userId);
        if(sessionOptional.isEmpty()){
            return;
        }
        Session session=sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
    }
    public UserDto signUp(String email, String password) {
        User user=new User();
        user.setEmail(email);
        user.setPassword(password);
        User savedUser=userRepository.save(user);
        return UserDto.from(savedUser);
    }
    public SessionStatus validate(String token, Long userId) {
        Optional<Session> sessionOptional=sessionRepository.findBytokenAndUser_id(token,userId);
        if(sessionOptional.isEmpty()){
            return SessionStatus.ENDED;
        }
        Session session=sessionOptional.get();
        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return SessionStatus.ENDED;
        }
        return SessionStatus.ACTIVE;
    }
}
