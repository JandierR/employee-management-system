package me.employeemanagementsystem.controller;

import me.employeemanagementsystem.dto.DepartmentDto;
import me.employeemanagementsystem.exception.ResourceNotFoundException;
import me.employeemanagementsystem.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private DepartmentServiceImpl departmentService;


    @Test
    void getDepartmentById_ShouldReturn200andDepartmentDto_whenIdExists(){
        Long departmentId = 1L;

        DepartmentDto mockDto = new DepartmentDto(departmentId, "Engineering", "Engineering Description");

        when(departmentService.getDepartmentById(departmentId)).thenReturn(mockDto);

        assertThat(mockMvcTester.get().uri("/api/departments/{id}", departmentId))
                .hasStatusOk()
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isEqualTo(1L);
                    assertThat(responseDto.getName()).isEqualTo("Engineering");
                    assertThat(responseDto.getDescription()).isEqualTo("Engineering Description");
                });
    }

    @Test
    void getDepartmentById_ShouldReturn404_whenIdDoesNotExist(){
        Long invalidDepartmentId = 999L;

        when(departmentService.getDepartmentById(invalidDepartmentId))
                .thenThrow(new ResourceNotFoundException("Department not found with ID 999"));

        assertThat(mockMvcTester.get().uri("/api/departments/{id}", invalidDepartmentId))
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}