package com.emre.api.walletapp.utils;

import com.emre.api.walletapp.config.AppUserDetails;
import com.emre.api.walletapp.model.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof AppUserDetails userDetails) {
            return userDetails.getUser();
        }

        return null;
    }

}
