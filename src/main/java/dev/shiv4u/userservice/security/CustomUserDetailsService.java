package dev.shiv4u.userservice.security;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.shiv4u.userservice.models.User;
import dev.shiv4u.userservice.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Getter
@Setter
@JsonDeserialize(as= CustomUserDetailsService.class)
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional=userRepository.findByEmail(username);
        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("User does not exits");
        }
        User user=userOptional.get();
        return new CustomUserDetail(user);
    }
}
