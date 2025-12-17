package com.charan.job_recommender.service;

import com.charan.job_recommender.domain.user.User;
import com.charan.job_recommender.dto.CreateUserRequest;
import com.charan.job_recommender.dto.UserResponse;
import com.charan.job_recommender.exception.ResourceNotFoundException;
import com.charan.job_recommender.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .location(request.getLocation())
                .yearsOfExperience(request.getYearsOfExperience())
                .build();

        User saved = userRepository.save(user);
        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .location(saved.getLocation())
                .yearsOfExperience(saved.getYearsOfExperience())
                .build();
    }

    @Transactional
    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id: " + id));
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .location(user.getLocation())
                .yearsOfExperience(user.getYearsOfExperience())
                .build();
    }
}
