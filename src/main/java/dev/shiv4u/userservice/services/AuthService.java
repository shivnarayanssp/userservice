package dev.shiv4u.userservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shiv4u.userservice.dtos.UserDto;
import dev.shiv4u.userservice.models.Session;
import dev.shiv4u.userservice.models.SessionStatus;
import dev.shiv4u.userservice.models.User;
import dev.shiv4u.userservice.repositories.SessionRepository;
import dev.shiv4u.userservice.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey secretKey;

    @Autowired
    public AuthService(UserRepository userRepository,
                       SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository=userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        secretKey = Jwts.SIG.HS256.key().build();
    }

    public ResponseEntity<UserDto> login(String email,String password) {
       Optional<User> userOptional = userRepository.findByEmail(email);
       if(userOptional.isEmpty()){
           return null;
       }
       User user=userOptional.get();
       if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
           throw new RuntimeException("Password/username does not match");
       }
       //String token = RandomStringUtils.randomAlphanumeric(30);
        Map<String,Object> jwtData=new HashMap<>();
        jwtData.put("email",email);
        jwtData.put("createdAt",new Date());
        jwtData.put("expiryAt",new Date(LocalDate.now().plusDays(3).toEpochDay()));
        String token = Jwts
                .builder()
                .claims(jwtData)
                .signWith(secretKey)
                .compact();
        Session session=new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);
        UserDto userDto=UserDto.from(user);
        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);
        return new ResponseEntity<UserDto>(userDto,headers, HttpStatus.OK);
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
        user.setPassword(bCryptPasswordEncoder.encode(password));
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
        Jws<Claims> claimsJws=Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        String email=(String) claimsJws.getPayload().get("email");
        return SessionStatus.ACTIVE;
    }
}
