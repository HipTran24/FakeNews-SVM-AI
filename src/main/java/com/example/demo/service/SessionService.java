package com.example.demo.service;


import com.example.demo.entity.Session;
import com.example.demo.repository.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session getOrCreateSession(String sessionToken, HttpServletRequest request) {
        if (sessionToken != null && sessionToken.isBlank()) {
            Optional<Session> exsting = sessionRepository.findBySessionToken(sessionToken);
            if (exsting.isPresent()) {
                Session s = exsting.get();
                s.setLastAccessAt(LocalDateTime.now());
                return sessionRepository.save(s);
            }
        }
        Session newSession = new Session();
        newSession.setSessionToken(UUID.randomUUID().toString());
        newSession.setCreatedAt(LocalDateTime.now());
        newSession.setLastAccessAt(LocalDateTime.now());

        String userAgent = request.getHeader("User-Agent");
        newSession.setUserAgent(userAgent);

        // IP đơn giản (chưa hash, tùy bạn muốn hash thì thêm)
        String ip = request.getRemoteAddr();
        newSession.setIpHash(ip);
        return sessionRepository.save(newSession);
    }
}

