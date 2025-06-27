package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String firebaseUid) throws UsernameNotFoundException {
        User appUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with Firebase UID: " + firebaseUid));

        if (!appUser.isActive()) {
            throw new UsernameNotFoundException("User account is not active");
        }

        // The "ROLE_" prefix is a standard convention for Spring Security's hasRole() check
        return new org.springframework.security.core.userdetails.User(
                appUser.getFirebaseUid(),
                "", // Password field is not used by us
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + appUser.getRole().name()))
        );
    }
}