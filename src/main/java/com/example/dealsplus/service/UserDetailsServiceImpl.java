package com.example.dealsplus.service;

import com.example.dealsplus.exception.CustomException;
import com.example.dealsplus.model.Role;
import com.example.dealsplus.model.User;
import com.example.dealsplus.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("user details service.");
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not present!");
        }
        User verifiedUser = user.get();
        List<Role> roles = verifiedUser.getRoles();
        List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();

        return new org.springframework.security.core.userdetails.User(
                verifiedUser.getUsername(),
                verifiedUser.getPassword(),
                verifiedUser.isEnabled(), true, true, true,
                authorities);
    }

    public User getPrincipalUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepo.findByUsername(authentication.getName());
        if (user.isEmpty()) {
            throw new CustomException("Invalid user.");
        } else if (!user.get().isEnabled()) {
            throw new CustomException("User is not valid.");
        }
        return user.get();
    }

}
