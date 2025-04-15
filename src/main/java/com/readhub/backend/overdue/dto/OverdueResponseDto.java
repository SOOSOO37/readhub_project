package com.readhub.backend.overdue.dto;
import com.readhub.backend.overdue.entity.Overdue;
import lombok.*;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OverdueResponseDto {

    private Long id;
    private String title;
    private String email;
    private Overdue.OverdueStatus overdueStatus;
}
