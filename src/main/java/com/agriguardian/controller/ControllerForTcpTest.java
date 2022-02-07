package com.agriguardian.controller;

import com.google.common.io.CharStreams;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequestMapping("/")
@Controller
@Log4j2
public class ControllerForTcpTest {

    @PostMapping("/ws/v1/cluster/apps/new-application")
    public void testEndpoint(HttpServletRequest request){
        log.debug("tcp testEndpoint :" + request.getHeaderNames());
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
               log.debug("tcp testEndpoint :" + CharStreams.toString(request.getReader()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
