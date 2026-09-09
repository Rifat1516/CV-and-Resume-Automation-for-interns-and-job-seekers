package com.cvmatcher.service;

import com.cvmatcher.model.User;
import java.util.List;

/**
 * Proxy pattern, same shape as AvailabilityServiceProxy: the UI only ever
 * talks to this proxy, never to UserDirectoryServiceImpl directly. It
 * enforces that only an admin can list other users' account/profile data.
 * A regular user has no code path to another user's data -- there is no
 * "view user by id" method exposed to them at all, only their own session.
 */
public class UserDirectoryServiceProxy implements UserDirectoryService {

    private final UserDirectoryService realService;
    private final User requestingUser;

    public UserDirectoryServiceProxy(UserDirectoryService realService, User requestingUser) {
        this.realService = realService;
        this.requestingUser = requestingUser;
    }

    @Override
    public List<User> listAllUsers() {
        if (!requestingUser.isAdmin()) {
            throw new SecurityException("Only an admin can view the user directory.");
        }
        return realService.listAllUsers();
    }
}
