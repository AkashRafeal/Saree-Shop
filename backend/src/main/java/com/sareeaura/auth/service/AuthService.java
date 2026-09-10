package com.sareeaura.auth.service;

import com.sareeaura.auth.dto.AuthResponse;
import com.sareeaura.auth.dto.LoginRequest;
import com.sareeaura.auth.dto.RegisterRequest;
import com.sareeaura.auth.dto.UserDto;
import com.sareeaura.cart.entity.Cart;
import com.sareeaura.cart.repository.CartRepository;
import com.sareeaura.exception.BadRequestException;
import com.sareeaura.security.JwtUtils;
import com.sareeaura.user.entity.Role;
import com.sareeaura.user.entity.RoleType;
import com.sareeaura.user.entity.User;
import com.sareeaura.user.repository.RoleRepository;
import com.sareeaura.user.repository.UserRepository;
import com.sareeaura.wishlist.entity.Wishlist;
import com.sareeaura.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String rawEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (rawEmail.contains("@gamil.com") || rawEmail.endsWith("@gamil.com")) {
            throw new BadRequestException("Spelling error detected: You entered '@gamil.com'. Please use '@gmail.com'");
        }
        if (!rawEmail.endsWith("@gmail.com")) {
            throw new BadRequestException("Email must end with @gmail.com");
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String digits = request.getPhone().replaceAll("\\D", "");
            if (digits.length() != 10) {
                throw new BadRequestException("Phone number count must be exactly 10 digits");
            }
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_CUSTOMER).build()));

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phone(request.getPhone())
                .active(true)
                .roles(Collections.singleton(customerRole))
                .build();

        user = userRepository.save(user);

        // Initialize cart and wishlist for customer
        cartRepository.save(Cart.builder().user(user).build());
        wishlistRepository.save(Wishlist.builder().user(user).build());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());
        return AuthResponse.builder()
                .token(jwt)
                .user(mapToDto(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return AuthResponse.builder()
                .token(jwt)
                .user(mapToDto(user))
                .build();
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadRequestException("User is not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BadRequestException("Current user not found"));
    }

    public UserDto getCurrentUserDto() {
        return mapToDto(getCurrentUser());
    }

    public UserDto mapToDto(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .roles(roleNames)
                .build();
    }
}
