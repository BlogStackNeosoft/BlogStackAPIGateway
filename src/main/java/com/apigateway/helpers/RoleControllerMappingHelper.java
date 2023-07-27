package com.apigateway.helpers;

import com.apigateway.commons.BlogStackQnaHubServiceBaseEndpoints;
import com.apigateway.commons.BlogStackUserManagementServiceBaseEndpoints;
import com.apigateway.commons.BlogStackUserRolesCommons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class RoleControllerMappingHelper {

    // data structure to store role as key and permitted endpoints
    private static Map<String,Set<String>> blogStackRoleToControllerMapper = new HashMap<>();

    public static Map<String, Set<String>> getBlogStackAllRoleToControllerMapping(){

        log.info("Printing the values fetched from resources: ");
        log.info(BlogStackUserManagementServiceBaseEndpoints.AUTHENTICATION_CONTROLLER);
        // Data structure to store all the admin endpoints
        Set<String> blogStackAdminEndpoints = new LinkedHashSet<>();

        blogStackAdminEndpoints.add(BlogStackUserManagementServiceBaseEndpoints.USER_CONTROLLER);
        blogStackAdminEndpoints.add(BlogStackUserManagementServiceBaseEndpoints.ROLES_CONTROLLER);
        blogStackAdminEndpoints.add(BlogStackUserManagementServiceBaseEndpoints.AUTHENTICATION_CONTROLLER);
        blogStackAdminEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.ANSWERS_CONTROLLER);
        blogStackAdminEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.COMMENTS_CONTROLLER);
        blogStackAdminEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.QUESTION_CONTROLLER);
        blogStackAdminEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.SUB_CATEGORY_CONTROLLER);

        // Adding the admin permitted endpoints to the map as key and value
        blogStackRoleToControllerMapper.put(BlogStackUserRolesCommons.BLOG_STACK_ADMIN_ROLE,blogStackAdminEndpoints);

        // Data structure to store all the user endpoints
        Set<String> blogStackUserEndpoints = new LinkedHashSet<>();

        blogStackUserEndpoints.add(BlogStackUserManagementServiceBaseEndpoints.AUTHENTICATION_CONTROLLER);
        blogStackUserEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.QUESTION_CONTROLLER);
        blogStackUserEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.ANSWERS_CONTROLLER);
        blogStackUserEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.COMMENTS_CONTROLLER);
        blogStackUserEndpoints.add(BlogStackQnaHubServiceBaseEndpoints.SUB_CATEGORY_CONTROLLER);


        // adding the user permitted endpoints to map as key and value
        blogStackRoleToControllerMapper.put(BlogStackUserRolesCommons.BLOG_STACK_USER_ROLE,blogStackUserEndpoints);

        return blogStackRoleToControllerMapper;
    }
}
