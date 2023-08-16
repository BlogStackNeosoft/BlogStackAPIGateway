package com.apigateway.httpexchange;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json" , contentType="application/json")
public interface IUserManagementHttpExchange {

    @GetMapping(value = "/{email_id}")
    ResponseEntity<?> fetchUserById(@PathVariable(value = "email_id") String emailId);

    @GetMapping(value = "/v1.0/user/user-id/{user_id}")
    ResponseEntity<?> getUserById(@PathVariable("user_id") String userId);
}
