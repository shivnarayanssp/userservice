package dev.shiv4u.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class validatetokenRequestDto {
    private Long userId;
    private String token;
}
