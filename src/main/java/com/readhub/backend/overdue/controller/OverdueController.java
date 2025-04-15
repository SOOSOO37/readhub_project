package com.readhub.backend.overdue.controller;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.overdue.dto.OverdueResponseDto;
import com.readhub.backend.overdue.entity.Overdue;
import com.readhub.backend.overdue.mapper.OverdueMapper;
import com.readhub.backend.overdue.service.OverdueService;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/overdue")
@RestController
public class OverdueController {

    private final OverdueService overdueService;
    private final OverdueMapper overdueMapper;


    @PostMapping("/auto")
    public ResponseEntity registerAutoOverdue() {
        List<Overdue> registered = overdueService.autoRegisterOverdue();
        List<OverdueResponseDto> response = overdueMapper.overdueToResponseDtos(registered);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity findMyOverdue(@AuthenticationPrincipal User user,
                                        @RequestParam int page,
                                        @RequestParam int size) {
        Page<Overdue> overduePage = overdueService.findUserOverdue(page - 1, size, user);
        List<Overdue> overdueList = overduePage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(overdueMapper.overdueToResponseDtos(overdueList),overduePage),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity findAllOverdue(@RequestParam int page,
                                         @RequestParam int size) {
        Page<Overdue> overduePage = overdueService.findAllOverdue(page - 1, size);
        List<Overdue> overdueList = overduePage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(overdueMapper.overdueToResponseDtos(overdueList),overduePage),HttpStatus.OK);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity cancelOverdue(@PathVariable("id") Long id) {
        overdueService.cancelOverdue(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}/not-rentable")
    public ResponseEntity updateNotRentable(@PathVariable("id") Long id) {
        overdueService.updateNotRentable(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/request")
    public ResponseEntity canRequestUnban(@AuthenticationPrincipal User user) {
        boolean result = overdueService.canRequestUnban(user.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{userId}/approve")
    public ResponseEntity approveUnban(@PathVariable("userId") Long userId) {
        overdueService.approveUnban(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
