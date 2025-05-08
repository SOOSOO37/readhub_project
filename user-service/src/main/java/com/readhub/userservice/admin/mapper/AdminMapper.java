package com.readhub.userservice.admin.mapper;

import com.readhub.userservice.admin.dto.AdminResponseDto;
import com.readhub.userservice.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminResponseDto userToAdminResponseDto(User user);
}
