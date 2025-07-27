package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.UserLoginDto;
import com.example.bankcards.dto.user.UserRegisterDto;

public interface AuthService {

    AuthResponseDto register(UserRegisterDto userRegisterDto);

    AuthResponseDto login(UserLoginDto userLoginDto);
}
