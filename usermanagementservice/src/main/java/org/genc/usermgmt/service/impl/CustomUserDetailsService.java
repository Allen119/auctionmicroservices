package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        log.info("User   found username {} , role s ize {}" , user.getUsername(),user.getRoles().size());
      //  user.getRoles().forEach(r-> log.info("Role is {} ", r.getName()));
       /* AbstractUserDetailsAuthenticationProvider.authenticate -->
       DaoAuthenticationProvider.additionalAuthenticationChecks  requires password*/
         new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                        .collect(Collectors.toList())
        );

         return new CustomUserDetails(user);
    }
}