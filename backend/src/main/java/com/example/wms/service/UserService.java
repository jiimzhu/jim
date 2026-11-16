package com.example.wms.service;

import com.example.wms.common.BusinessException;
import com.example.wms.common.PageResult;
import com.example.wms.domain.User;
import com.example.wms.dto.CreateUserRequest;
import com.example.wms.dto.UpdateUserRequest;
import com.example.wms.dto.UserVO;
import com.example.wms.repository.UserRepository;
import com.example.wms.security.UserPrincipal;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResult<UserVO> page(int page, int size, String keyword, Integer status) {
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "id"));

        Specification<User> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                String like = "%" + keyword.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("username"), like),
                        cb.like(root.get("nickname"), like),
                        cb.like(root.get("email"), like)));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> result = userRepository.findAll(specification, pageRequest);
        return PageResult.of(result, UserVO::from);
    }

    @Transactional
    public UserVO create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("登录账号已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(StringUtils.hasText(request.getRole()) ? request.getRole() : User.Role.USER);
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        return UserVO.from(userRepository.save(user));
    }

    @Transactional
    public UserVO update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        if (StringUtils.hasText(request.getRole())) {
            // 不允许把最后一个管理员降级，避免系统失去管理员
            if (User.Role.ADMIN.equals(user.getRole()) && User.Role.USER.equals(request.getRole())) {
                ensureNotLastAdmin(user.getId(), "不能把最后一个管理员降级为普通用户");
            }
            user.setRole(request.getRole());
        }

        if (request.getStatus() != null) {
            if (request.getStatus() == 0) {
                ensureNotSelf(id, "不能禁用自己的账号");
                if (User.Role.ADMIN.equals(user.getRole())) {
                    ensureNotLastAdmin(user.getId(), "不能禁用最后一个管理员");
                }
            }
            user.setStatus(request.getStatus());
        }

        return UserVO.from(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        ensureNotSelf(id, "不能删除自己的账号");
        if (User.Role.ADMIN.equals(user.getRole())) {
            ensureNotLastAdmin(user.getId(), "不能删除最后一个管理员");
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserVO changeStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (status == 0) {
            ensureNotSelf(id, "不能禁用自己的账号");
            if (User.Role.ADMIN.equals(user.getRole())) {
                ensureNotLastAdmin(user.getId(), "不能禁用最后一个管理员");
            }
        }
        user.setStatus(status);
        return UserVO.from(userRepository.save(user));
    }

    private void ensureNotSelf(Long targetId, String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal
                && principal.getId().equals(targetId)) {
            throw new BusinessException(message);
        }
    }

    private void ensureNotLastAdmin(Long excludeId, String message) {
        long adminCount = userRepository.findAll().stream()
                .filter(u -> User.Role.ADMIN.equals(u.getRole()) && u.getStatus() == 1)
                .filter(u -> !u.getId().equals(excludeId))
                .count();
        if (adminCount == 0) {
            throw new BusinessException(message);
        }
    }
}
