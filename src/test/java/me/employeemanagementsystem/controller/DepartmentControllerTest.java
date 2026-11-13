package me.employeemanagementsystem.controller;

import me.employeemanagementsystem.dto.DepartmentDto;
import me.employeemanagementsystem.exception.ResourceNotFoundException;
import me.employeemanagementsystem.service.impl.DepartmentServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private DepartmentServiceImpl departmentService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void getDepartmentById_ShouldReturn200andDepartmentDto_whenIdExists() {
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
    void getDepartmentById_ShouldReturn404_whenIdDoesNotExist() {
        Long invalidDepartmentId = 999L;

        when(departmentService.getDepartmentById(invalidDepartmentId))
                .thenThrow(new ResourceNotFoundException("Department not found with ID 999"));

        assertThat(mockMvcTester.get().uri("/api/departments/{id}", invalidDepartmentId))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllDepartments_ShouldReturn200AndDepartmentDtoList_WhenDepartmentsExist() {
        DepartmentDto mockDto1 = new DepartmentDto(1L, "Engineering", "Engineering Description");
        DepartmentDto mockDto2 = new DepartmentDto(2L, "IT", "IT Description");
        DepartmentDto mockDto3 = new DepartmentDto(3L, "Finance", "Finance Description");

        List<DepartmentDto> mockList = new ArrayList<>(List.of(mockDto1, mockDto2, mockDto3));

        when(departmentService.getAllDepartments()).thenReturn(mockList);

        assertThat(mockMvcTester.get().uri("/api/departments"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(DepartmentDto.class))
                .hasSize(3)
                .satisfies(responseList -> {
                    assertThat(responseList.get(0).getName()).isEqualTo("Engineering");
                    assertThat(responseList.get(1).getName()).isEqualTo("IT");
                    assertThat(responseList.get(2).getName()).isEqualTo("Finance");
                });
    }

    @Test
    void getAllDepartments_ShouldReturn200AndEmptyList_WhenNoDepartmentsExist() {

        when(departmentService.getAllDepartments())
                .thenReturn(List.of());

        assertThat(mockMvcTester.get().uri("/api/departments"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(DepartmentDto.class))
                .isEmpty();

    }

    @Test
    void createDepartment_ShouldReturn201AndSavedDto_WhenRequestIsValid() {
        DepartmentDto requestDto = new DepartmentDto(null, "Engineering", "Engineering Description");
        DepartmentDto savedDto = new DepartmentDto(1L, "Engineering", "Engineering Description");

        when(departmentService.createDepartment(any(DepartmentDto.class))).thenReturn(savedDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getName()).isEqualTo("Engineering");
                    assertThat(responseDto.getDescription()).isEqualTo("Engineering Description");
                    assertThat(responseDto.getId()).isEqualTo(1L);
                });
    }

    @Test
    void createDepartment_ShouldReturn400_WhenRequestIsInvalid() {
        DepartmentDto invalidRequestDto = new DepartmentDto(null, "", "Engineering Description");

        String jsonRequest = objectMapper.writeValueAsString(invalidRequestDto);

        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatus(HttpStatus.BAD_REQUEST);

        verify(departmentService, never()).createDepartment(invalidRequestDto);
    }

    @Test
    void updateDepartment_ShouldReturn200_WhenRequestIsValid() {
        Long departmentId = 1L;
        DepartmentDto requestedDto = new DepartmentDto(departmentId, "IT", "IT Description");
        DepartmentDto updatedDto = new DepartmentDto(departmentId, "IT", "IT Description");

        when(departmentService.updateDepartment(eq(departmentId), any(DepartmentDto.class)))
                .thenReturn(updatedDto);

        String jsonRequest = objectMapper.writeValueAsString(requestedDto);

        assertThat(mockMvcTester.put().uri("/api/departments/{id}", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatusOk()
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getName()).isEqualTo("IT");
                    assertThat(responseDto.getDescription()).isEqualTo("IT Description");
                    assertThat(responseDto.getId()).isEqualTo(1L);
                });
    }

    @Test
    void updateDepartment_ShouldReturn404_WhenIdDoesNotExist() {
        Long invalidDepartmentId = 999L;

        DepartmentDto updateRequestDto = new DepartmentDto(invalidDepartmentId, "IT", "IT Description");

        when(departmentService.updateDepartment(eq(invalidDepartmentId), any(DepartmentDto.class)))
                .thenThrow(new ResourceNotFoundException("Department not found with ID 999"));

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        assertThat(mockMvcTester.put().uri("/api/departments/{id}", invalidDepartmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatus(HttpStatus.NOT_FOUND);

    }

    @Test
    void updateDepartment_ShouldReturn400_WhenInvalidRequest() {
        Long departmentId = 1L;

        DepartmentDto updatedRequestDto = new DepartmentDto(departmentId, "", "IT Description");

        when(departmentService.updateDepartment(eq(departmentId), any(DepartmentDto.class)))
                .thenReturn(updatedRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(updatedRequestDto);

        assertThat(mockMvcTester.put().uri("/api/departments/{id}", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .hasStatus(HttpStatus.BAD_REQUEST);

        verify(departmentService, never()).updateDepartment(departmentId, updatedRequestDto);
    }

    @Test
    void deleteDepartment_ShouldReturn200_WhenIdExist() {

        Long departmentId = 1L;
        doNothing().when(departmentService).deleteDepartment(departmentId);

        assertThat(mockMvcTester.delete().uri("/api/departments/{id}", departmentId))
                .hasStatusOk();

        verify(departmentService, times(1)).deleteDepartment(departmentId);
    }

    @Test
    void deleteDepartment_ShouldReturn404_WhenIdDoesNotExist() {

        Long invalidDepartmentId = 999L;
        doThrow(new ResourceNotFoundException("Department not found with ID 999"))
                .when(departmentService).deleteDepartment(invalidDepartmentId);

        assertThat(mockMvcTester.delete().uri("/api/departments/{id}", invalidDepartmentId))
                .hasStatus(HttpStatus.NOT_FOUND);

        verify(departmentService, times(1)).deleteDepartment(invalidDepartmentId);
    }
}