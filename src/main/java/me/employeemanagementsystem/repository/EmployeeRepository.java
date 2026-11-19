package me.employeemanagementsystem.repository;

import me.employeemanagementsystem.entity.Employee;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsById(@NonNull Long id);

    List<Employee> findByDepartmentId(Long id);
}
