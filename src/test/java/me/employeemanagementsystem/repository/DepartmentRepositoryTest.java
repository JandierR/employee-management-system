package me.employeemanagementsystem.repository;

import me.employeemanagementsystem.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void existsByName_ShouldReturnTrue_WhenDepartmentExists() {
        Department department = new Department(null, "IT", "IT Description");
        departmentRepository.save(department);

        Boolean exists = departmentRepository.existsDepartmentByName("IT");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_ShouldReturnFalse_WhenDepartmentDoesNotExist() {

        Boolean exists = departmentRepository.existsDepartmentByName("IT");

        assertThat(exists).isFalse();
    }
}