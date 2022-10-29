package com.mvc.backend.service;

import com.mvc.backend.entity.Employee;

import java.text.ParseException;
import java.util.List;


public interface EmployeeService {
    List<Employee> getAllEmployee();

    void saveEmployee(Employee employee);

    Employee getEmployeeById(long id);

    void calculateEmployee(Employee employee) throws ParseException;


}
