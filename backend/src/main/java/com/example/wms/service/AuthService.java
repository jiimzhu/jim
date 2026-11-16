package com.example.wms.service;

import com.example.wms.common.BusinessException;
import com.example.wms.dto.LoginRequest;
import com.example.wms.dto.LoginResponse;
import com.example.wms.dto.UserVO;
import com.example.wms.domain.User;
import com.example.wms.repository.UserRepository;
import com.example.wms.security.JwtUtil;
import com.example.wms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        String token = jwtUtil.generateToken(principal);
        return new LoginResponse(token, jwtUtil.getExpireMillis() / 1000, UserVO.from(user));
    }

    /**
     * 读取当前登录用户信息。
     */
    public UserVO currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BusinessException(401, "用户已被删除"));
        return UserVO.from(user);
    }
}
