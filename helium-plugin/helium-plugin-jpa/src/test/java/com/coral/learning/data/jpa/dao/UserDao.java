package com.coral.learning.data.jpa.dao;

import com.coral.learning.data.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    User findById(Integer id);
}