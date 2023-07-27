package com.employee.management.manager.service;


import com.employee.management.manager.model.User;

public interface UserService {
    User registerUser(String username, String password);

    User loadUserByUsername(String username);
}
