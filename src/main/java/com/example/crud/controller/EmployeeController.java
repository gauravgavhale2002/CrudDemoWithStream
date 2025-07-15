package com.example.crud.controller;

import com.example.crud.exception.EmployeeNotFoundException;
import com.example.crud.model.Employee;
import com.example.crud.repository.EmployeeRepository;
import com.example.crud.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/employee")
//400 Bad Request
//🧨 Client (Postman, Browser, App) ne galat data bheja ya validation fail hua

//404 Not Found 🧨 Client ne valid URL diya, lekin resource exist nahi karta

//500 Internal Server Error
//🧨 Code ke andar kuch gadbad ho gaya (unexpected bug, null pointer, DB issue, etc.)

//401	Unauthorized	User not logged in or token missing	JWT token not provided in header
//403	Forbidden	Logged in, but not allowed to access that resource	Role = USER tries to access ADMIN endpoint

public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/add")//201 created
    public ResponseEntity<String> addEmployee(@RequestBody @Valid Employee employee) {
        String response = employeeService.addEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body("Employee added successfully");
    }

    @GetMapping("/all")//200 ok // if not found
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")//200 ok // if not found 404
    public ResponseEntity<Employee> getEmployeeById(@PathVariable long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/update/{id}")//200ok if not found 404
    public ResponseEntity<String> updateEmployee(@PathVariable Long id, @RequestBody @Valid Employee employee) {
        String response = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/patch/{id}") //200ok// if not found 404
    public ResponseEntity<String> patchEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        String response = employeeService.patchEmployee(id, employee);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")//200 ok  // if not found 404
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        String response = employeeService.deleteEmployee(id);
        return ResponseEntity.ok(response);
    }//204	No Content	Success, but no response body (usually for DELETE/PUT)


    @PutMapping("/format-names")
    public ResponseEntity<String> formatAllEmployeeNames() {
        String response = employeeService.formatAllNames();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/highest-salary")
    public ResponseEntity<Employee> getHighestPaidEmployee() {
        Employee topEmployee = employeeService.getHighestPaidEmployee();
        return ResponseEntity.ok(topEmployee);
    }

    @GetMapping("/min-salary")
    public ResponseEntity<List<Employee>> getEmployeesWithMinSalary() {
        List<Employee> employees = employeeService.getEmployeesWithMinSalary();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/second-highest-salary")
    public ResponseEntity<List<Employee>> getSecondHighestSalaryEmployees() {
        List<Employee> result = employeeService.getEmployeesWithSecondHighestSalary();
        return ResponseEntity.ok(result);
    }


    //http://localhost:8080/api/employee/starts-with?letters=m,g
    //http://localhost:8080/api/employee/starts-with?letters=m
    @GetMapping("/starts-with")
    public ResponseEntity<List<Employee>> getEmployeesByStartingLetters(
            @RequestParam List<String> letters) {
        List<Employee> result = employeeService.getEmployeesByStartingLetters(letters);
        return ResponseEntity.ok(result);
    }
}
