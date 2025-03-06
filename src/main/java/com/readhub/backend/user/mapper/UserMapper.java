package com.readhub.backend.user.mapper;

import com.readhub.backend.user.dto.UserCreateDto;
import com.readhub.backend.user.dto.UserResponseDto;
import com.readhub.backend.user.dto.UserUpdateDto;
import com.readhub.backend.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userCreateDtoToUser(UserCreateDto userCreateDtoDto);

    User userUpdateDtoToUser(UserUpdateDto userPatchDto);

    UserResponseDto userToUserResponseDto(User user);

    List<UserResponseDto> usersToUserResponseDtos(List<User> userList);
}
