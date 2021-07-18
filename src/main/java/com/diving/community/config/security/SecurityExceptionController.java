package com.diving.community.config.security;

import com.diving.community.exception.CAccessDeniedException;
import com.diving.community.exception.CAuthenticationEntryPointException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/exception")
public class SecurityExceptionController {

    @GetMapping(value = "/entrypoint")
    public void entrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @GetMapping(value = "/accessDenied")
    public void accessDeniedException() {
        throw new CAccessDeniedException();
    }
}
