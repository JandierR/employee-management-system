package me.employeemanagementsystem.integration;

import me.employeemanagementsystem.dto.DepartmentDto;
import me.employeemanagementsystem.repository.DepartmentRepository;
import me.employeemanagementsystem.repository.EmployeeRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DepartmentIntegrationTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp(){
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
    }


    @Test
    void fullDepartmentCrudLifecycle_ShouldCreateReadUpdateAndDelete() {
        DepartmentDto createRequest = new DepartmentDto(null, "IT", "IT Description");
        AtomicReference<Long> generateId = new AtomicReference<>();

        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("IT");
                    assertThat(responseDto.getDescription()).isEqualTo("IT Description");
                    generateId.set(responseDto.getId());
                });

        Long departmentId = generateId.get();

        assertThat(mockMvcTester.get().uri("/api/departments/{id}", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .hasStatusOk()
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isEqualTo(departmentId);
                    assertThat(responseDto.getName()).isEqualTo("IT");
                    assertThat(responseDto.getDescription()).isEqualTo("IT Description");
                });

        DepartmentDto updateRequest = new DepartmentDto(departmentId, "Engineering", "Engineering Description");

        assertThat(mockMvcTester.put().uri("/api/departments/{id}", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .hasStatusOk()
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getName()).isEqualTo("Engineering");
                    assertThat(responseDto.getDescription()).isEqualTo("Engineering Description");
                });


        assertThat(mockMvcTester.delete().uri("/api/departments/{id}", departmentId))
                .hasStatusOk();

        assertThat(mockMvcTester.get().uri("/api/departments/{id}", departmentId))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void createDepartment_DuplicateName() {
        DepartmentDto createRequest = new DepartmentDto(null, "IT", "IT Description");

        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("IT");
                    assertThat(responseDto.getDescription()).isEqualTo("IT Description");
                });

        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .hasStatus(HttpStatus.CONFLICT);

        assertThat(departmentRepository.count()).isEqualTo(1);
    }

    @Test
    void getAllDepartments() {
        DepartmentDto createRequest1 = new DepartmentDto(null, "IT", "IT Description");
        DepartmentDto createRequest2 = new DepartmentDto(null, "Engineering", "Engineering Description");

        //POST for the FIRST department -> createRequest1
        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest1)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("IT");
                    assertThat(responseDto.getDescription()).isEqualTo("IT Description");
                });

        //POST for the SECOND department -> createRequest2
        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest2)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("Engineering");
                    assertThat(responseDto.getDescription()).isEqualTo("Engineering Description");
                });


        //Get all Departments
        assertThat(mockMvcTester.get().uri("/api/departments"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(DepartmentDto.class))
                .satisfies(responseDto -> {
                    assertThat(responseDto.size()).isEqualTo(2);
                    assertThat(responseDto.get(0).getName()).isEqualTo("IT");
                    assertThat(responseDto.get(0).getDescription()).isEqualTo("IT Description");
                    assertThat(responseDto.get(1).getName()).isEqualTo("Engineering");
                    assertThat(responseDto.get(1).getDescription()).isEqualTo("Engineering Description");
                });
    }
}
