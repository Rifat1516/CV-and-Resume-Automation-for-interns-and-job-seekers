package com.cvmatcher.dao;

import com.cvmatcher.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findByPhone(String phone);
    Optional<User> findById(int id);
    boolean existsByPhone(String phone);
    List<User> findAll();
}
