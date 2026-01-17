package com.dormex.security;

import com.dormex.entity.User;
import com.dormex.entity.enums.AuthProvider;
import com.dormex.entity.enums.Role;
import com.dormex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user's info from Google
            user.setName(name);
            user.setProfilePicture(picture);
            user = userRepository.save(user);
        } else {
            // Create new user from Google OAuth
            user = User.builder()
                .name(name)
                .email(email)
                .authProvider(AuthProvider.GOOGLE)
                .role(Role.STUDENT)
                .profilePicture(picture)
                .enabled(true)
                .build();
            user = userRepository.save(user);
        }

        return CustomUserDetails.create(user, attributes);
    }
}
