package com.example.todo.services;

import com.example.todo.configurations.TodoUserDetails;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoUserDetailService implements UserDetailsService {
    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new TodoUserDetails(user.getId(), user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority("USER")));
    }
}
