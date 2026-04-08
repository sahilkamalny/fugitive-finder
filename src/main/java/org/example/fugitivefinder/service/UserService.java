package org.example.fugitivefinder.service;

import org.example.fugitivefinder.model.AppUser;

import java.util.*;

public final class UserService {

    private static final Map<String, AppUser> USERS_BY_EMAIL = new HashMap<>();

    private UserService() {
    }

    public static AppUser createAccount(String firstName, String lastName,
                                        String username, String email, String password) {
        if (email == null || email.isBlank() || USERS_BY_EMAIL.containsKey(email.toLowerCase())) {
            return null;
        }

        AppUser user = new AppUser(
                UUID.randomUUID().toString(),
                username,
                firstName,
                lastName,
                email,
                "/org.example.fugitivefinder/images/detective-avatar.png",
                new ArrayList<>()
        );

        USERS_BY_EMAIL.put(email.toLowerCase(), user);
        return user;
    }

    public static AppUser login(String email, String password) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return USERS_BY_EMAIL.get(email.toLowerCase());
    }

    public static void saveTargetForUser(AppUser user, String wantedUid) {
        if (user == null || wantedUid == null || wantedUid.isBlank()) {
            return;
        }

        if (!user.getSavedTargetIds().contains(wantedUid)) {
            user.getSavedTargetIds().add(wantedUid);
        }
    }

    public static void removeTargetForUser(AppUser user, String wantedUid) {
        if (user == null || wantedUid == null) {
            return;
        }
        user.getSavedTargetIds().remove(wantedUid);
    }
}
