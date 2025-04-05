package com.readhub.backend.admin.mapper;

import com.readhub.backend.admin.dto.AdminResponseDto;
import com.readhub.backend.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminResponseDto userToAdminResponseDto(User user);
}
