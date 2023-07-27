package com.employee.management.manager.controller;

import com.employee.management.manager.entity.EmployeeDetailsRepository;
import com.employee.management.manager.model.EmployeeDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeDetailsRepository employeeDetailsRepository;

    public EmployeeController(EmployeeDetailsRepository employeeDetailsRepository) {
        this.employeeDetailsRepository = employeeDetailsRepository;
    }

    @GetMapping("/employee-details")
    public List<EmployeeDetails> getAllEmployeeDetails() {

        return employeeDetailsRepository.findAll();
    }
}
