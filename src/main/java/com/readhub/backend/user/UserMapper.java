package com.readhub.backend.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userCreateDtoToUser(UserCreateDto userCreateDtoDto);

    User userUpdateDtoToUser(UserUpdateDto userPatchDto);

    UserResponseDto userToUserResponseDto(User user);

    List<UserResponseDto> usersToUserResponseDtos(List<User> userList);

}
