package com.cvmatcher.service;

import com.cvmatcher.dao.UserDao;
import com.cvmatcher.model.User;
import java.util.List;

public class UserDirectoryServiceImpl implements UserDirectoryService {

    private final UserDao userDao;

    public UserDirectoryServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> listAllUsers() {
        return userDao.findAll();
    }
}
