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
        if (sessionToken != null && !sessionToken.isBlank()) {
            Optional<Session> existing = sessionRepository.findBySessionToken(sessionToken);
            if (existing.isPresent()) {
                Session s = existing.get();
                s.setLastAccessAt(LocalDateTime.now());
                return sessionRepository.save(s);
            }
        }

        Session newSession = new Session();
        newSession.setSessionToken(UUID.randomUUID().toString());
        newSession.setCreatedAt(LocalDateTime.now());
        newSession.setLastAccessAt(LocalDateTime.now());
        newSession.setUserAgent(request.getHeader("User-Agent"));
        newSession.setIpHash(request.getRemoteAddr());

        return sessionRepository.save(newSession);
    }

}

