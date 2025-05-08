package com.readhub.userservice.user.service;

import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.global.security.utils.CustomAuthorityUtils;
import com.readhub.userservice.user.entity.User;
import com.readhub.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    private final CustomAuthorityUtils authorityUtils;

    private final PasswordEncoder passwordEncoder;



    public User createUser(User user){

        checkExistEmail(user.getEmail());
        checkExistNickName(user.getNickName());
        setEncodedPassword(user);
        setRole(user);
        User savedUser = userRepository.save(user);

        return savedUser;

    }
    public User updateNickName(User user){
        User findUser = findVerifiedUser(user.getId());

        checkUserStatus(findUser);
        Optional.ofNullable(user.getNickName())
                .ifPresent(nickName -> findUser.setNickName(nickName));

        return userRepository.save(findUser);
    }

    public User updatePassword(User user){
        User findUser = findVerifiedUser(user.getId());

        checkUserStatus(findUser);
        Optional.ofNullable(user.getPassword())
                .ifPresent(password -> findUser.setPassword(passwordEncoder.encode(password)));
        return userRepository.save(findUser);
    }

    public User findUser(User user) {
        User findUser = findVerifiedUser(user.getId());
        return findUser;
    }

    public void deleteUser(User user){
        User findUser = findVerifiedUser(user.getId());

        checkUserStatus(findUser);

        findUser.setUserStatus(User.UserStatus.QUIT);
        userRepository.save(findUser);
    }

    public void setRole(User user){
        List<String> roles = authorityUtils.createRoles(user.getEmail());
        user.setRoles(roles);
    }

    public void setEncodedPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public User findVerifiedUser(long id) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
                });
        verifiedActiveUser(findUser);
        return findUser;
    }
    private static void verifiedActiveUser (User user){
        if(user.getUserStatus().getNumber() == 2){
            throw new BusinessLogicException(ExceptionCode.SLEEPER_ACCOUNT);
        }else if(user.getUserStatus().getNumber() ==3){
            throw new BusinessLogicException(ExceptionCode.USER_QUIT);
        }
    }

    public void checkExistEmail(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent())
            throw new BusinessLogicException(ExceptionCode.USER_EMAIL_EXISTS);
    }

    public void checkExistNickName(String nickName){
        Optional<User> optionalUser = userRepository.findByNickName(nickName);

        if(optionalUser.isPresent())
            throw new BusinessLogicException(ExceptionCode.USER_NICKNAME_EXISTS);
    }

    private void checkUserStatus(User user){
        if(user.getUserStatus() == User.UserStatus.QUIT)
        throw new BusinessLogicException(ExceptionCode.USER_QUIT);
    }
}
