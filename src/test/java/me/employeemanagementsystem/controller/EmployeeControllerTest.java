package me.employeemanagementsystem.controller;

import me.employeemanagementsystem.dto.EmployeeDto;
import me.employeemanagementsystem.exception.ResourceNotFoundException;
import me.employeemanagementsystem.service.EmployeeService;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @MockitoBean
    EmployeeService employeeService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    void getEmployeeById_ShouldReturn200AndEmployeeDto_WhenIdExists() {
        Long employeeId = 1L;
        Long departmentId = 1L;

        EmployeeDto employeeDto = new EmployeeDto(employeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", departmentId, "Active", "Developer");

        when(employeeService.getEmployeeById(employeeId)).thenReturn(employeeDto);

        assertThat(mockMvcTester.get().uri("/api/employees/{id}", employeeId))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getFirstName()).isEqualTo("Jandier");
                    assertThat(responseDto.getLastName()).isEqualTo("Rojas");
                    assertThat(responseDto.getEmail()).isEqualTo("JanRo@gmail.com");
                    assertThat(responseDto.getDepartmentId()).isEqualTo(1L);
                    assertThat(responseDto.getStatus()).isEqualTo("Active");
                    assertThat(responseDto.getPosition()).isEqualTo("Developer");
                });
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    void getEmployeeById_ShouldReturn404_WhenIdDoesNotExist() {
        Long invalidEmployeeId = 999L;

        when(employeeService.getEmployeeById(invalidEmployeeId))
                .thenThrow(new ResourceNotFoundException("Employee not found with ID 999"));

        assertThat(mockMvcTester.get().uri("/api/employees/{id}", invalidEmployeeId))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void getEmployeesByDepartment_ShouldReturn200AndEmployeeList_WhenDepartmentExist() {
        Long employeeId = 1L;
        Long departmentId = 1L;

        EmployeeDto employeeDto1 = new EmployeeDto(employeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", departmentId, "Active", "Developer");

        EmployeeDto employeeDto2 = new EmployeeDto(employeeId, "Leo", "Messi",
                "LeoMe@gmail.com", departmentId, "Inactive", "IT");

        List<EmployeeDto> employeeDtoList = new ArrayList<>(List.of(employeeDto2, employeeDto1));

        when(employeeService.getEmployeesByDepartment(departmentId)).thenReturn(employeeDtoList);

        assertThat(mockMvcTester.get().uri("/api/employees/department/{id}", departmentId))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(EmployeeDto.class))
                .hasSize(2)
                .satisfies(response -> {
                    assertThat(response.get(0).getFirstName()).isEqualTo("Leo");
                    assertThat(response.get(1).getFirstName()).isEqualTo("Jandier");
                });

        verify(employeeService, times(1)).getEmployeesByDepartment(departmentId);
    }

    @Test
    void getEmployeesByDepartment_ShouldReturn404_WhenDepartmentDoesNotExist() {
        Long departmentId = 999L;

        List<EmployeeDto> employeeDtoList = new ArrayList<>(List.of());

        when(employeeService.getEmployeesByDepartment(departmentId)).thenReturn(employeeDtoList);

        assertThat(mockMvcTester.get().uri("/api/employees/department/{id}", departmentId))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(EmployeeDto.class))
                .isEmpty();

        verify(employeeService, times(1)).getEmployeesByDepartment(departmentId);
    }

    @Test
    void getAllEmployees_ShouldReturn200AndEmployeesList_WhenEmployeesExist() {
        Long employeeId = 1L;
        Long departmentId = 1L;

        EmployeeDto employeeDto1 = new EmployeeDto(employeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", departmentId, "Active", "Developer");

        EmployeeDto employeeDto2 = new EmployeeDto(employeeId, "Leo", "Messi",
                "LeoMe@gmail.com", departmentId, "Inactive", "IT");

        when(employeeService.getAllEmployees()).thenReturn(List.of(employeeDto1, employeeDto2));

        assertThat(mockMvcTester.get().uri("/api/employees"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(EmployeeDto.class))
                .satisfies(response -> {
                    assertThat(response.get(0).getFirstName()).isEqualTo("Jandier");
                    assertThat(response.get(1).getFirstName()).isEqualTo("Leo");
                }).hasSize(2);

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void getAllEmployees_ShouldReturn200AndEmptyEmployeesList_WhenEmployeesDoNotExist() {

        when(employeeService.getAllEmployees()).thenReturn(List.of());

        assertThat(mockMvcTester.get().uri("/api/employees"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(EmployeeDto.class))
                .isEmpty();

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void createEmployee_ShouldReturn201AndSavedDto_WhenRequestIsValid() {
        Long employeeId = 1L;
        Long departmentId = 1L;

        EmployeeDto employeeDto = new EmployeeDto(employeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", departmentId, "Active", "Developer");

        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(employeeDto);

        String jsonRequest = objectMapper.writeValueAsString(employeeDto);

        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(response -> {
                    assertThat(response.getFirstName()).isEqualTo("Jandier");
                    assertThat(response.getLastName()).isEqualTo("Rojas");
                    assertThat(response.getEmail()).isEqualTo("JanRo@gmail.com");
                    assertThat(response.getDepartmentId()).isEqualTo(1L);
                    assertThat(response.getStatus()).isEqualTo("Active");
                    assertThat(response.getPosition()).isEqualTo("Developer");
                });
        verify(employeeService, times(1)).createEmployee(any(EmployeeDto.class));
    }

    @Test
    void createEmployee_ShouldReturn400_WhenRequestIsInvalid() {
        Long employeeId = 1L;
        Long departmentId = 1L;

        EmployeeDto invalidRequestDto = new EmployeeDto(employeeId, "", "Rojas",
                "JanRo@gmail.com", departmentId, "Active", "Developer");

        String jsonRequest = objectMapper.writeValueAsString(invalidRequestDto);

        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatus(HttpStatus.BAD_REQUEST);


        verify(employeeService, never()).createEmployee(invalidRequestDto);
    }

    @Test
    void createEmployee_ShouldReturn404_WhenDepartmentIdDoesNotExist() {
        Long employeeId = 1L;
        Long departmentId = 999L;

        EmployeeDto invalidRequestDto = new EmployeeDto(employeeId, "Jandier", "Rojas",
                "JanRo@gmail.com", departmentId, "Active", "Developer");

        when(employeeService.createEmployee(any(EmployeeDto.class)))
                .thenThrow(new ResourceNotFoundException("Department not found with ID 999"));

        String jsonRequest = objectMapper.writeValueAsString(invalidRequestDto);

        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatus(HttpStatus.NOT_FOUND);

    }
}