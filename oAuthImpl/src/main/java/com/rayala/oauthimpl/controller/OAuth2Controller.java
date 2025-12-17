//package com.rayala.oauthimpl.controller;
//
//import com.rayala.oauthimpl.dto.Role;
//import com.rayala.oauthimpl.dto.UserInfo;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class OAuth2Controller {
//
//    @Value("${google.client-id}")
//    private String clientId;
//
//    @Value("${google.client-secret}")
//    private String clientSecret;
//
//    @Value("${google.redirect-uri}")
//    private String redirectUri;
//
//    @Value("${frontend.home}")
//    private String frontendHome;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @GetMapping("/oauth2/callback")
//    public ResponseEntity<?> callback(String code, HttpSession session) {
//        if (code == null || code.isEmpty()) {
//            return ResponseEntity.badRequest().body("Missing code");
//        }
//
//        try {
//            // 1) Exchange code for tokens
//            String tokenUrl = "https://oauth2.googleapis.com/token";
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
//            form.add("code", code);
//            form.add("client_id", clientId);
//            form.add("client_secret", clientSecret);
//            form.add("redirect_uri", redirectUri);
//            form.add("grant_type", "authorization_code");
//
//            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(form, headers);
//            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);
//
//            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token exchange failed");
//            }
//
//            Map<String, Object> tokenBody = tokenResponse.getBody();
//            if (tokenBody == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Empty token response");
//
//            String accessToken = (String) tokenBody.get("access_token");
//            String idToken = (String) tokenBody.get("id_token");
//            String refreshToken = (String) tokenBody.get("refresh_token"); // may be null
//
//            // 2) Call People API to fetch phone, names, photos
//            String peopleUrl = "https://people.googleapis.com/v1/people/me?personFields=names,emailAddresses,phoneNumbers,photos";
//
//            HttpHeaders authHeaders = new HttpHeaders();
//            authHeaders.setBearerAuth(accessToken);
//            HttpEntity<Void> peopleReq = new HttpEntity<>(authHeaders);
//
//            ResponseEntity<Map> peopleResp = restTemplate.exchange(peopleUrl, HttpMethod.GET, peopleReq, Map.class);
//
//            Map<String, Object> peopleBody = peopleResp.getBody();
//
//            // extract values
//            String name = null;
//            String email = null;
//            String phone = null;
//            String photoUrl = null;
//
//            if (peopleBody != null) {
//                Object namesObj = peopleBody.get("names");
//                if (namesObj instanceof List<?> namesList && !namesList.isEmpty()) {
//                    Object first = namesList.get(0);
//                    if (first instanceof Map<?, ?> nm) {
//                        name = (String) nm.get("displayName");
//                    }
//                }
//
//                Object emailsObj = peopleBody.get("emailAddresses");
//                if (emailsObj instanceof List<?> emailsList && !emailsList.isEmpty()) {
//                    Object first = emailsList.get(0);
//                    if (first instanceof Map<?, ?> em) {
//                        email = (String) em.get("value");
//                    }
//                }
//
//                Object phonesObj = peopleBody.get("phoneNumbers");
//                if (phonesObj instanceof List<?> phonesList && !phonesList.isEmpty()) {
//                    Object first = phonesList.get(0);
//                    if (first instanceof Map<?, ?> pm) {
//                        phone = (String) pm.get("value");
//                    }
//                }
//
//                Object photosObj = peopleBody.get("photos");
//                if (photosObj instanceof List<?> photosList && !photosList.isEmpty()) {
//                    Object first = photosList.get(0);
//                    if (first instanceof Map<?, ?> ph) {
//                        photoUrl = (String) ph.get("url");
//                    }
//                }
//            }
//
//            // 3) Fallback: decode id_token or call tokeninfo if needed
//            if ((name == null || email == null) && idToken != null) {
//                try {
//                    // Use Google's tokeninfo endpoint to safely read id token claims
//                    String infoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
//                    ResponseEntity<Map> info = restTemplate.getForEntity(infoUrl, Map.class);
//                    Map<String, Object> infoBody = info.getBody();
//                    if (infoBody != null) {
//                        if (name == null) name = (String) infoBody.get("name");
//                        if (email == null) email = (String) infoBody.get("email");
//                        if (photoUrl == null) photoUrl = (String) infoBody.get("picture");
//                    }
//                } catch (Exception ignored) {
//                }
//            }
//
//            // 4) store in session
//            UserInfo user = new UserInfo(name, email, phone, photoUrl, Role.USER);
//            session.setAttribute("user", user);
//
//            if (refreshToken != null) {
//                session.setAttribute("refresh_token", refreshToken);
//            }
//
//            // 5) Redirect back to frontend home (frontend will call /api/home to read session)
//            HttpHeaders redirectHeaders = new HttpHeaders();
//            redirectHeaders.setLocation(URI.create(frontendHome));
//            return new ResponseEntity<>(redirectHeaders, HttpStatus.FOUND);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OAuth callback failed: " + ex.getMessage());
//        }
//    }
//}
