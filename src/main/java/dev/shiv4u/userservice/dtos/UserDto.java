package dev.shiv4u.userservice.dtos;

import dev.shiv4u.userservice.models.Role;
import dev.shiv4u.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private String email;
    private Set<Role> roles=new HashSet<>();
    //create a deep copy from excisting user object
    public static UserDto from(User user) {
        UserDto userDto=new UserDto();
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
