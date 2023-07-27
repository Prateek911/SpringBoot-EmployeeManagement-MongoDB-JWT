package com.employee.management.manager.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "EmployeeDetails")
@Data
@EqualsAndHashCode(of = "id")
public class EmployeeDetails {

    @Id
    private String id;

    @Field(name = "Name")
    private String name;

    @Field(name = "Locale")
    private String locale;

    @Field(name = "Department")
    private String department;

    @Field(name = "Salary")
    private double salary;
}
