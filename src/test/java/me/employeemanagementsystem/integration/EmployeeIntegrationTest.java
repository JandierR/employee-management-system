package me.employeemanagementsystem.integration;

import me.employeemanagementsystem.dto.DepartmentDto;
import me.employeemanagementsystem.dto.EmployeeDto;
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
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EmployeeIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MockMvcTester mockMvcTester;

    @BeforeEach
    void setUp(){
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    void fullEmployeeCrudLifecycle() {
        DepartmentDto createDepartmentRequest =
                new DepartmentDto(null, "Engineering", "Engineering Description");

        AtomicReference<Long> departmentGeneratedId = new AtomicReference<>();


        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDepartmentRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto ->{
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("Engineering");
                    assertThat(responseDto.getDescription()).isEqualTo("Engineering Description");
                    departmentGeneratedId.set(responseDto.getId());
                });

        Long departmentId = departmentGeneratedId.get();

        EmployeeDto createEmployeeRequest = new EmployeeDto(null, "Jandier", "Rojas",
                "Janro@gmail.com", departmentId, "Active", "Software Engineer");

        AtomicReference<Long> employeeGeneratedId = new AtomicReference<>();

        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getFirstName()).isEqualTo("Jandier");
                    assertThat(responseDto.getLastName()).isEqualTo("Rojas");
                    assertThat(responseDto.getEmail()).isEqualTo("Janro@gmail.com");
                    assertThat(responseDto.getDepartmentId()).isEqualTo(departmentId);
                    assertThat(responseDto.getStatus()).isEqualTo("Active");
                    assertThat(responseDto.getPosition()).isEqualTo("Software Engineer");
                    employeeGeneratedId.set(responseDto.getId());
                });

        Long employeeId = employeeGeneratedId.get();

        assertThat(mockMvcTester.get().uri("/api/employees/{id}", employeeId))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getFirstName()).isEqualTo("Jandier");
                    assertThat(responseDto.getLastName()).isEqualTo("Rojas");
                    assertThat(responseDto.getEmail()).isEqualTo("Janro@gmail.com");
                    assertThat(responseDto.getDepartmentId()).isEqualTo(departmentId);
                    assertThat(responseDto.getStatus()).isEqualTo("Active");
                    assertThat(responseDto.getPosition()).isEqualTo("Software Engineer");
                });

        EmployeeDto updateEmployeeRequest = new EmployeeDto(employeeId, "Leonardo", "Da Vinci",
                "Leonardo@gmail.com", departmentId, "Inactive", "Artist");

        assertThat(mockMvcTester.put().uri("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEmployeeRequest)))
                .hasStatusOk()
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getFirstName()).isEqualTo("Leonardo");
                    assertThat(responseDto.getLastName()).isEqualTo("Da Vinci");
                    assertThat(responseDto.getEmail()).isEqualTo("Leonardo@gmail.com");
                    assertThat(responseDto.getDepartmentId()).isEqualTo(departmentId);
                    assertThat(responseDto.getStatus()).isEqualTo("Inactive");
                    assertThat(responseDto.getPosition()).isEqualTo("Artist");
                });

        assertThat(mockMvcTester.delete().uri("/api/employees/{id}", employeeId))
                .hasStatusOk();

        assertThat(mockMvcTester.get().uri("/api/employees/{id}", employeeId))
                .hasStatus(HttpStatus.NOT_FOUND);

        assertThat(departmentRepository.existsById(departmentId)).isTrue();
    }


    @Test
    void getEmployeesByDepartment() {
        DepartmentDto createDepartmentRequest1 =
                new DepartmentDto(null, "Engineering", "Engineering Description");

        DepartmentDto createDepartmentRequest2 =
                new DepartmentDto(null, "IT", "IT Description");

        AtomicReference<Long> engineeringDepartmentGeneratedId = new AtomicReference<>();


        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDepartmentRequest1)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto ->{
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("Engineering");
                    assertThat(responseDto.getDescription()).isEqualTo("Engineering Description");
                    engineeringDepartmentGeneratedId.set(responseDto.getId());
                });

        Long engineeringDepartmentId = engineeringDepartmentGeneratedId.get();


        AtomicReference<Long> ITDepartmentGeneratedId = new AtomicReference<>();
        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDepartmentRequest2)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto ->{
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("IT");
                    assertThat(responseDto.getDescription()).isEqualTo("IT Description");
                    ITDepartmentGeneratedId.set(responseDto.getId());
                });

        Long ITDepartmentId = ITDepartmentGeneratedId.get();

        EmployeeDto createJandierEmployeeRequest = new EmployeeDto(null, "Jandier", "Rojas",
                "Janro@gmail.com", engineeringDepartmentId, "Active", "Software Engineer");


        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createJandierEmployeeRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getFirstName()).isEqualTo("Jandier");
                    assertThat(responseDto.getLastName()).isEqualTo("Rojas");
                    assertThat(responseDto.getEmail()).isEqualTo("Janro@gmail.com");
                    assertThat(responseDto.getDepartmentId()).isEqualTo(engineeringDepartmentId);
                    assertThat(responseDto.getStatus()).isEqualTo("Active");
                    assertThat(responseDto.getPosition()).isEqualTo("Software Engineer");
                });

        EmployeeDto createLeonardoEmployeeRequest = new EmployeeDto(null, "Leonardo", "Da Vinci",
                "Leonardo@gmail.com", engineeringDepartmentId, "Inactive", "Artist");

        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createLeonardoEmployeeRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getFirstName()).isEqualTo("Leonardo");
                    assertThat(responseDto.getLastName()).isEqualTo("Da Vinci");
                    assertThat(responseDto.getEmail()).isEqualTo("Leonardo@gmail.com");
                    assertThat(responseDto.getDepartmentId()).isEqualTo(engineeringDepartmentId);
                    assertThat(responseDto.getStatus()).isEqualTo("Inactive");
                    assertThat(responseDto.getPosition()).isEqualTo("Artist");
                });

        //Creating this Pablo Employee and assigning IT department, is to ensure that if there is an error and findAll
        //rather than findByDepartmentId is called, then it returns 3 and not 2, to prevent that the expected 2 doesn't pass the test. 
        EmployeeDto createPabloEmployeeRequest = new EmployeeDto(null, "Pablo", "Pavloski",
                "Papaski@gmail.com", ITDepartmentId, "Inactive", "Football Player");

        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPabloEmployeeRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getFirstName()).isEqualTo("Pablo");
                    assertThat(responseDto.getLastName()).isEqualTo("Pavloski");
                    assertThat(responseDto.getEmail()).isEqualTo("Papaski@gmail.com");
                    assertThat(responseDto.getDepartmentId()).isEqualTo(ITDepartmentId);
                    assertThat(responseDto.getStatus()).isEqualTo("Inactive");
                    assertThat(responseDto.getPosition()).isEqualTo("Football Player");
                });


        assertThat(mockMvcTester.get().uri("/api/employees/department/{id}", engineeringDepartmentId))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(EmployeeDto.class))
                .hasSize(2)
                .satisfies(responseDto -> {
                    assertThat(responseDto.get(0).getId()).isNotNull();
                    assertThat(responseDto.get(0).getFirstName()).isEqualTo("Jandier");
                    assertThat(responseDto.get(0).getLastName()).isEqualTo("Rojas");
                    assertThat(responseDto.get(0).getEmail()).isEqualTo("Janro@gmail.com");
                    assertThat(responseDto.get(0).getDepartmentId()).isEqualTo(engineeringDepartmentId);
                    assertThat(responseDto.get(0).getStatus()).isEqualTo("Active");
                    assertThat(responseDto.get(0).getPosition()).isEqualTo("Software Engineer");

                    assertThat(responseDto.get(1).getId()).isNotNull();
                    assertThat(responseDto.get(1).getFirstName()).isEqualTo("Leonardo");
                    assertThat(responseDto.get(1).getLastName()).isEqualTo("Da Vinci");
                    assertThat(responseDto.get(1).getEmail()).isEqualTo("Leonardo@gmail.com");
                    assertThat(responseDto.get(1).getDepartmentId()).isEqualTo(engineeringDepartmentId);
                    assertThat(responseDto.get(1).getStatus()).isEqualTo("Inactive");
                    assertThat(responseDto.get(1).getPosition()).isEqualTo("Artist");
                });
    }

    @Test
    void createEmployee_InvalidDepartment() {
        Long invalidDepartmentId = 999L;

        EmployeeDto createEmployeeRequest = new EmployeeDto(null, "Jandier", "Rojas",
                "Janro@gmail.com", invalidDepartmentId, "Active", "Software Engineer");


        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                .hasStatus(HttpStatus.NOT_FOUND);

        assertThat(mockMvcTester.get().uri("/api/employees"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(EmployeeDto.class))
                .isEmpty();
    }

    @Test
    void deleteDepartment_WithEmployees() {
        DepartmentDto createDepartmentRequest =
                new DepartmentDto(null, "Engineering", "Engineering Description");
        AtomicReference<Long> departmentGeneratedId = new AtomicReference<>();

        assertThat(mockMvcTester.post().uri("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDepartmentRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DepartmentDto.class)
                .satisfies(responseDto -> {
                    assertThat(responseDto.getId()).isNotNull();
                    departmentGeneratedId.set(responseDto.getId());
                });

        Long departmentId = departmentGeneratedId.get();

        EmployeeDto createEmployeeRequest = new EmployeeDto(null, "Jandier", "Rojas",
                "Janro@gmail.com", departmentId, "Active", "Software Engineer");

        assertThat(mockMvcTester.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(EmployeeDto.class)
                .satisfies(responseDto -> assertThat(responseDto.getId()).isNotNull());

        assertThat(mockMvcTester.delete().uri("/api/departments/{id}", departmentId))
                .hasStatus(HttpStatus.CONFLICT);

        assertThat(departmentRepository.existsById(departmentId)).isTrue();
        assertThat(employeeRepository.count()).isEqualTo(1);
    }
}
