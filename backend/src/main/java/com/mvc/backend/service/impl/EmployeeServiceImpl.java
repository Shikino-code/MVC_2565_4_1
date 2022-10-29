package com.mvc.backend.service.impl;

import com.mvc.backend.entity.Employee;
import com.mvc.backend.repository.EmployeeRepository;
import com.mvc.backend.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    @Override
    public void saveEmployee(Employee employee) {
        DecimalFormat df = new DecimalFormat("0.00");

        long millis = System.currentTimeMillis();

        java.sql.Date date = new java.sql.Date(millis);

        employee.setSalary(Double.parseDouble(df.format(employee.getSalary())));

        if (Long.toString(employee.getId()).length() > 5) {
            throw new RuntimeException("id have more than 5: " + employee.getId());
        }
        if (employee.getSalary() < 0) {
            throw new RuntimeException("Salary less than 0");
        }

        if (date == employee.getStartdate()) {
            throw new RuntimeException("Startdate equal CurrentDate");
        }
        employee.setJob(employee.getJob().toLowerCase());
        this.employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(long id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        Employee employee = null;

        if (optional.isPresent()) {
            employee = optional.get();
        } else {
            throw new RuntimeException("Not found employee for id: " + id);
        }
        return employee;
    }

    @Override
    public void calculateEmployee(Employee employee) throws ParseException {
        int check = 60;

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String dateEmployeeStr = dateFormat.format(employee.getStartdate());
        String dateCurrentStr = dateFormat.format(date);
        log.debug("dateEmployeeStr: " + dateEmployeeStr);
        Date dateEmployee = new SimpleDateFormat("yyyy-MM-dd").parse(dateEmployeeStr);
        Date dateCurrent = new SimpleDateFormat("yyyy-MM-dd").parse(dateCurrentStr);

        long timeDiff = Math.abs(dateCurrent.getTime() - dateEmployee.getTime());
        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        if (daysDiff > check) {
            if (employee.getJob().equals("junior")) {
                employee.setSalary(employee.getSalary() / (365 * daysDiff));
            } else if (employee.getJob().equals("senior")) {
                employee.setSalary(employee.getSalary() / (365 * daysDiff * 3));
            } else if (employee.getJob().equals("manager")) {
                employee.setSalary((employee.getSalary() / (365 * daysDiff * 5)) * (150 / 100));
            } else {
                throw new RuntimeException();
            }
        }
        this.employeeRepository.save(employee);
    }


}
