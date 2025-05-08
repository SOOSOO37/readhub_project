package com.readhub.userservice.user.mapper;

import com.readhub.userservice.user.dto.UserCreateDto;
import com.readhub.userservice.user.dto.UserResponseDto;
import com.readhub.userservice.user.dto.UserUpdateDto;
import com.readhub.userservice.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userCreateDtoToUser(UserCreateDto userCreateDtoDto);

    User userUpdateDtoToUser(UserUpdateDto userPatchDto);


    UserResponseDto userToUserResponseDto(User user);

    List<UserResponseDto> usersToUserResponseDtos(List<User> userList);
}
