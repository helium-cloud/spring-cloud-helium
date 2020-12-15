package com.coral.learning.data.jpa.service;

import com.coral.learning.data.jpa.entity.User;

public interface UserService {
    User getUserById(int userId);

}