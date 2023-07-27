package com.employee.management.manager.entity;

import com.employee.management.manager.model.EmployeeDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDetailsRepository extends MongoRepository<EmployeeDetails, String> {

}