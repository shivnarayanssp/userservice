package dev.shiv4u.userservice.services;

import dev.shiv4u.userservice.dtos.UserDto;
import dev.shiv4u.userservice.models.Role;
import dev.shiv4u.userservice.models.User;
import dev.shiv4u.userservice.repositories.RoleRepository;
import dev.shiv4u.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository){
        this.userRepository=userRepository;
        this.roleRepository = roleRepository;
    }
    public UserDto getUserDetails(long userId){
        Optional<User> userOptional=userRepository.findById(userId);
        User user=userOptional.get();
        UserDto userDto=new UserDto();
        userDto.setEmail(user.getEmail());
        return userDto;
    }
    public UserDto setUserRoles(Long userId, List<Long> roleIds){
        Optional<User> userOptional=userRepository.findById(userId);
        List<Role> roles=roleRepository.findAllByIdIn(roleIds);
        if (userOptional.isEmpty()){
            return null;
        }
        User user=userOptional.get();
        user.setRoles(Set.copyOf(roles));
        User savedUser=userRepository.save(user);
        return UserDto.from(savedUser);
    }
}
