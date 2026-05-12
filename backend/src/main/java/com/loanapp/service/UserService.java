package com.loanapp.service;

import com.loanapp.dto.AdminCreateUserRequest;
import com.loanapp.dto.AdminUserUpdateRequest;
import com.loanapp.dto.ChangePasswordRequest;
import com.loanapp.dto.ProfileResponse;
import com.loanapp.dto.UserProfileUpdateRequest;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ProfileResponse getProfile(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        return new ProfileResponse(
                user.getUsername(),
                user.getFullName(),
                user.getParentName(),
                user.getEmail(),
                user.getAddress(),
                user.getKycStatus(),
                user.getMobileNumber(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getBankAccountNumber(),
                user.getProfileImagePath(),
                user.getMaritalStatus(),
                user.getNomineeName(),
                user.getNomineeRelationship(),
                user.getJobStatus(),
                user.getReligion(),
                user.getPanCard(),
                user.getAadharNumber()
        );
    }

    public User createUser(AdminCreateUserRequest request) throws Exception {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserByAdmin(Long userId, AdminUserUpdateRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setKycStatus(request.getKycStatus());

        return userRepository.save(user);
    }

    public void deleteUser(Long userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new Exception("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public User updateUserProfile(Long userId, UserProfileUpdateRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setProfileImagePath(request.getProfileImagePath());
        user.setParentName(request.getParentName());
        user.setAddress(request.getAddress());
        user.setMobileNumber(request.getMobileNumber());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setBankAccountNumber(request.getBankAccountNumber());
        user.setMaritalStatus(request.getMaritalStatus());
        user.setNomineeName(request.getNomineeName());
        user.setNomineeRelationship(request.getNomineeRelationship());
        user.setJobStatus(request.getJobStatus());
        user.setReligion(request.getReligion());
        user.setPanCard(request.getPanCard());
        user.setAadharNumber(request.getAadharNumber());

        return userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new Exception("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
