package com.readhub.bookservice.overdue.dto;
import com.readhub.bookservice.overdue.entity.Overdue;
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
