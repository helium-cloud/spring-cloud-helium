package com.coral.learning.data.jpa.service.impl;

import com.coral.learning.data.jpa.dao.UserDao;
import com.coral.learning.data.jpa.entity.User;
import com.coral.learning.data.jpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    public User getUserById(int userId) {
        return userDao.findById(userId);
    }

}