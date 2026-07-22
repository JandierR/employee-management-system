package me.employeemanagementsystem.mapper;

import me.employeemanagementsystem.dto.EmployeeDto;
import me.employeemanagementsystem.entity.Department;
import me.employeemanagementsystem.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment() != null ? employee.getDepartment().getId() : null
        );
    }

    public static Employee mapToEmployee(EmployeeDto employeeDto, Department department) {
        Employee employee = new Employee();

        employee.setId(employeeDto.getId());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employee.getEmail());
        employee.setDepartment(department);

        return employee;
    }
}
