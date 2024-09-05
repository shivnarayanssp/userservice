package dev.shiv4u.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUprequestDto {
    private String email;
    private String password;
}
