package com.cvmatcher.service;

import com.cvmatcher.dao.UserDao;
import com.cvmatcher.model.Role;
import com.cvmatcher.model.User;
import com.cvmatcher.util.PasswordHasher;

import java.util.Optional;

/**
 * Self-service registration always creates a USER account -- there is no
 * signup path that can create an ADMIN. The single admin account is seeded
 * once at startup (see Main.registerFixedAdminIfMissing) with fixed
 * credentials, matching the "admin account is fixed" requirement.
 */
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /** Used only by the user-facing "sign up" screen. Always creates role USER. */
    public User registerUser(String fullName, String phone, String plainPassword, Integer age, String city) {
        if (userDao.existsByPhone(phone)) {
            throw new IllegalArgumentException("An account with this phone number already exists.");
        }
        User user = new User(0, fullName, phone, PasswordHasher.hash(plainPassword), age, city, Role.USER);
        return userDao.save(user);
    }

    /** Used internally to seed the single fixed admin account at startup. */
    public User registerFixedAdmin(String fullName, String phone, String plainPassword) {
        if (userDao.existsByPhone(phone)) {
            return userDao.findByPhone(phone).orElseThrow();
        }
        User admin = new User(0, fullName, phone, PasswordHasher.hash(plainPassword), null, null, Role.ADMIN);
        return userDao.save(admin);
    }

    /**
     * Login now checks name + phone + password, matching the sign-up form.
     * The name is compared case-insensitively against what was stored at
     * sign-up, so a correct phone+password alone is no longer sufficient.
     */
    public Optional<User> login(String name, String phone, String plainPassword) {
        return userDao.findByPhone(phone)
                .filter(u -> u.getFullName() != null && u.getFullName().trim().equalsIgnoreCase(name.trim()))
                .filter(u -> PasswordHasher.verify(plainPassword, u.getPasswordHash()));
    }

    /** Convenience for the admin-only login screen: rejects a correct password if the account isn't ADMIN. */
    public Optional<User> loginAsAdmin(String name, String phone, String plainPassword) {
        return login(name, phone, plainPassword).filter(User::isAdmin);
    }
}
