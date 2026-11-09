package me.employeemanagementsystem.repository;

import me.employeemanagementsystem.entity.Employee;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsById(@NonNull Long id);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByDepartmentId(Long id);
}
