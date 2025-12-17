package com.rayala.oauthimpl.controller;

import com.rayala.oauthimpl.dto.IdTokenRequest;
import com.rayala.oauthimpl.dto.Role;
import com.rayala.oauthimpl.dto.UserInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final String GOOGLE_CLIENT_ID = "874069712950-3i4q6tgqeumbctrmbfpq31ce2f0sorjt.apps.googleusercontent.com";

    @PostMapping("/auth/google")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<?> googleAuth(@RequestBody IdTokenRequest request, HttpSession session) {

        if (request.getId_token() == null) {
            return ResponseEntity.badRequest().body("Missing id_token");
        }

        String idToken = request.getId_token();

        try {
            // Verify token using Google's tokeninfo endpoint
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            RestTemplate restTemplate = new RestTemplate();

            Map<String, String> tokenInfo = restTemplate.getForObject(url, Map.class);

            if (tokenInfo == null || tokenInfo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // Validate audience (IMPORTANT)
            if (!GOOGLE_CLIENT_ID.equals(tokenInfo.get("aud"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token audience mismatch");
            }

            // Extract user info
            String name = tokenInfo.get("name");
            String email = tokenInfo.get("email");
            String picture = tokenInfo.get("picture");

            UserInfo user = UserInfo.builder()
                    .name(name)
                    .email(email)
                    .pictureUrl(picture)
                    .role(Role.USER)
                    .build();

            // Store in session
            session.setAttribute("user", user);

            return ResponseEntity.ok("Authenticated");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
        }
    }

    @GetMapping("/home")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<?> home(HttpSession session) {
        UserInfo user = (UserInfo) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/user")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<?> getDummyUser(HttpSession session) {
        UserInfo user = UserInfo.getDummyUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        return ResponseEntity.ok(user);
    }

}
