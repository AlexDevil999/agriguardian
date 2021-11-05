package com.agriguardian.controller;

import com.agriguardian.dto.appUser.AddUserMasterDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.Status;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/followers")
public class FollowerController {

}
