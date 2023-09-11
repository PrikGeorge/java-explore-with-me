package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id=" + id + " was not found"));
    }

    @Transactional
    public UserDto registerUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toEntity(newUserRequest);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("User with this email already exists");
        }

        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {

        if (ids != null && !ids.isEmpty()) {
            return userRepository.findByIdIn(ids, PageRequest.of(from, size))
                    .stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(PageRequest.of(from, size))
                    .stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }

    }

    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

}
