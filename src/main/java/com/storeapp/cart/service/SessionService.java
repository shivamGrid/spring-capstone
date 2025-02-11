package com.storeapp.cart.service;

import com.storeapp.cart.exception.UnauthorizedException;
import com.storeapp.cart.util.Constants;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    public Long getUserIdFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            throw new UnauthorizedException(Constants.UNAUTHORIZED);
        }
        return userId;
    }
}