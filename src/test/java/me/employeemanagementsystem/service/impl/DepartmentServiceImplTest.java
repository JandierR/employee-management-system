package me.employeemanagementsystem.service.impl;

import me.employeemanagementsystem.dto.DepartmentDto;
import me.employeemanagementsystem.entity.Department;
import me.employeemanagementsystem.exception.ResourceNotFoundException;
import me.employeemanagementsystem.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentServiceImpl;

    @Test
    void createDepartment() {
        DepartmentDto inputDto = new DepartmentDto(null, "Engineering", "Engineering Description");
        Department savedDepartment = new Department(1L, "Engineering", "Engineering Description");

        when(departmentRepository.existsDepartmentByName("Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);


        DepartmentDto result = departmentServiceImpl.createDepartment(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Engineering", result.getName());
        assertEquals("Engineering Description", result.getDescription());

        //I can also use ArgumentCaptor to check what is going to be saved before the save command into the database. For example:
//        ArgumentCaptor<Department> departmentArgumentCaptor = ArgumentCaptor.forClass(Department.class);
//        verify(departmentRepository).save(departmentArgumentCaptor.capture());
//
//        Department savedEntity = departmentArgumentCaptor.getValue();
//        assertEquals("Engineering", savedEntity.getName());

        //And it is important to use to make sure there wasn't blind spots that changes the data the client first entered before being saved.

        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void createInvalidDepartment() {
        DepartmentDto inputDto = new DepartmentDto(1L, "Engineering", "Engineering Description");

        when(departmentRepository.existsDepartmentByName("Engineering")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> departmentServiceImpl.createDepartment(inputDto));

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void getDepartmentById() {
        Department mockDepartment = new Department(1L, "Department", "Department Description");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(mockDepartment));

        DepartmentDto result = departmentServiceImpl.getDepartmentById(1L);

        assertNotNull(result);

        assertEquals("Department", result.getName());
        assertEquals("Department Description", result.getDescription());

        verify(departmentRepository, times(1)).findById(1L);

    }

    @Test
    void getInvalidDepartmentById() {

        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentServiceImpl.getDepartmentById(999L));

        assertEquals("Department not found with ID 999", exception.getMessage());

        verify(departmentRepository, times(1)).findById(999L);

    }

    @Test
    void getAllDepartments() {
        Department department1 = new Department(1L, "Department1", "Department Description 1");
        Department department2 = new Department(2L, "Department2", "Department Description 2");
        Department department3 = new Department(3L, "Department3", "Department Description 3");

        List<Department> mockDepartmentList = new ArrayList<>(List.of(department1, department2, department3));

        when(departmentRepository.findAll()).thenReturn(mockDepartmentList);

        List<DepartmentDto> result = departmentServiceImpl.getAllDepartments();

        assertEquals(3, result.size());

        DepartmentDto firstElement = result.get(0);

        assertEquals(1, firstElement.getId());
        assertEquals("Department1", firstElement.getName());
        assertEquals("Department Description 1", firstElement.getDescription());

        DepartmentDto secondElement = result.get(1);

        assertEquals(2, secondElement.getId());
        assertEquals("Department2", secondElement.getName());
        assertEquals("Department Description 2", secondElement.getDescription());

        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getEmptyAllDepartments() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        List<DepartmentDto> result = departmentServiceImpl.getAllDepartments();

        assertTrue(result.isEmpty());

        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void updateDepartment() {
        Long departmentId = 1L;
        Department existingDepartment = new Department(departmentId, "Engineering", "Engineering Description");

        DepartmentDto updateRequestDto = new DepartmentDto(departmentId, "New name", "New Description");

        Department updatedDepartment = new Department(departmentId, "New name", "New Description");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);

        DepartmentDto result = departmentServiceImpl.updateDepartment(departmentId, updateRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New name", result.getName());
        assertEquals("New Description", result.getDescription());

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(any(Department.class));

    }

    @Test
    void updateDepartmentInvalidId() {
        Long invalidDepartmentId = 999L;

        when(departmentRepository.findById(invalidDepartmentId)).thenReturn(Optional.empty());

        DepartmentDto updateRequestDto = new DepartmentDto(invalidDepartmentId, "New name", "New Description");


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentServiceImpl.updateDepartment(invalidDepartmentId, updateRequestDto));


        assertEquals("Department not found with ID 999", exception.getMessage());

        verify(departmentRepository, times(1)).findById(invalidDepartmentId);
        verify(departmentRepository, never()).save(any(Department.class));

    }

    @Test
    void deleteDepartment() {
        Long departmentId = 1L;
        Department existingDepartment = new Department(departmentId, "Engineering", "Engineering Description");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));

        departmentServiceImpl.deleteDepartment(departmentId);

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).delete(any(Department.class));
        verify(departmentRepository, times(1)).flush();

    }

    @Test
    void deleteDepartmentInvalidId() {
        Long invalidDepartmentId = 999L;
        when(departmentRepository.findById(invalidDepartmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentServiceImpl.deleteDepartment(invalidDepartmentId));

        assertEquals("Department not found with ID 999", exception.getMessage());

        verify(departmentRepository, times(1)).findById(invalidDepartmentId);
        verify(departmentRepository, never()).delete(any(Department.class));
        verify(departmentRepository, never()).flush();


    }
}