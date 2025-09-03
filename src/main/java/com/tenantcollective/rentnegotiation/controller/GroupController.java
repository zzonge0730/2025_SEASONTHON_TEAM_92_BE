package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.Group;
import com.tenantcollective.rentnegotiation.service.GroupingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class GroupController {
    
    private final GroupingService groupingService;
    
    @Autowired
    public GroupController(GroupingService groupingService) {
        this.groupingService = groupingService;
    }
    
    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<List<Group>>> getGroups(@RequestParam(defaultValue = "building") String scope) {
        try {
            if (!scope.equals("building") && !scope.equals("neighborhood")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "Scope must be 'building' or 'neighborhood'"));
            }
            
            List<Group> groups = groupingService.getGroups(scope);
            return ResponseEntity.ok(new ApiResponse<>(true, groups));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve groups: " + e.getMessage()));
        }
    }
}