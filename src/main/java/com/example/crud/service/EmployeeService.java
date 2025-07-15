package com.example.crud.service;

import com.example.crud.exception.EmployeeNotFoundException;
import com.example.crud.model.Employee;
import com.example.crud.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public String addEmployee(Employee employee) {
        if (employee.getName() == null || employee.getName().isEmpty()) {
            throw new EmployeeNotFoundException("Employee name cannot be empty");
        }
        employeeRepository.save(employee);
        return "Employee added successfully";
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }


    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
    }

    @Transactional
    public String updateEmployee(Long id, Employee updatedEmployee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        existing.setName(updatedEmployee.getName());
        existing.setSalary(updatedEmployee.getSalary());
        existing.setAge(updatedEmployee.getAge());

        employeeRepository.save(existing);
        return "Employee updated successfully";
    }

    @Transactional
    public String patchEmployee(Long id, Employee partialEmployee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        if (partialEmployee.getName() != null && !partialEmployee.getName().isBlank()) {
            if (!partialEmployee.getName().matches("^[A-Za-z ]+$")) {
                throw new EmployeeNotFoundException("Name must contain only letters");
            }
            existing.setName(partialEmployee.getName());
        }

        if (partialEmployee.getSalary() > 0) {
            existing.setSalary(partialEmployee.getSalary());
        }

        if (partialEmployee.getAge() > 0) {
            existing.setAge(partialEmployee.getAge());
        }

        employeeRepository.save(existing);
        return "Employee patched successfully";
    }

    @Transactional
    public String deleteEmployee(Long id) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        employeeRepository.delete(existing);
        return "Employee deleted successfully";
    }

    @Transactional
    public String formatAllNames() {
        List<Employee> allEmployees = employeeRepository.findAll();

        allEmployees.forEach(emp -> {
            if (emp.getName() != null && !emp.getName().isBlank()) {
                emp.setName(formatName(emp.getName()));
            }
        });

        employeeRepository.saveAll(allEmployees);

        return "All employee names formatted successfully";
    }

    // 🔽 Name format method
    private String formatName(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public Employee getHighestPaidEmployee() {
        return employeeRepository.findAll().stream()
                .max(Comparator.comparingDouble(Employee::getSalary))
                .orElseThrow(() -> new EmployeeNotFoundException("No employees found"));
    }

    public List<Employee> getEmployeesWithMinSalary() {
        List<Employee> allEmployees = employeeRepository.findAll();

        if (allEmployees.isEmpty()) {
            throw new EmployeeNotFoundException("No employees found");
        }

        // 🔽 Step 1: Find min salary value
        double minSalary = allEmployees.stream()
                .mapToDouble(Employee::getSalary)
                .min()
                .orElseThrow(() -> new EmployeeNotFoundException("Could not determine minimum salary"));

        // 🔽 Step 2: Filter employees with that min salary
        return allEmployees.stream()
                .filter(emp -> emp.getSalary() == minSalary)
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeesWithSecondHighestSalary() {
        List<Employee> allEmployees = employeeRepository.findAll();

        if (allEmployees.size() < 2) {
            throw new EmployeeNotFoundException("Less than 2 employees in the system");
        }

        // Step 1: Unique sorted salaries
        List<Double> sortedSalaries = allEmployees.stream()
                .map(Employee::getSalary)
                .distinct()
                .sorted(Comparator.reverseOrder()) // high to low
                .collect(Collectors.toList());

        if (sortedSalaries.size() < 2) {
            throw new EmployeeNotFoundException("Second highest salary not found");
        }

        double secondHighest = sortedSalaries.get(1); //get(1) = 78000 if two employee have same salary

        // Step 2: Return all employees with that salary
        return allEmployees.stream()
                .filter(emp -> emp.getSalary() == secondHighest)
                .collect(Collectors.toList());
    }


    public List<Employee> getEmployeesByStartingLetters(List<String> letters) {
        if (letters == null || letters.isEmpty()) {
            throw new IllegalArgumentException("At least one letter is required");
        }

        // Convert all to uppercase
        List<String> upperLetters = letters.stream()
                .map(l -> l.substring(0, 1).toUpperCase())
                .collect(Collectors.toList());

        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getName() != null && upperLetters.contains(
                        emp.getName().substring(0, 1)))
                .collect(Collectors.toList());
    }

}
