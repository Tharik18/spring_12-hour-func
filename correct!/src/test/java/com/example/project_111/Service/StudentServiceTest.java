package com.example.project_111.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.example.project_111.Entity.Student;
import com.example.project_111.Repository.StudentRepository;
import com.example.project_111.Repository.TeacherRepository;

@PrepareForTest({LocalDateTime.class, LocalTime.class})
public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testChangeTimingMoreThan12HoursBefore() {
        // Mock current time
        LocalTime currentTime = LocalTime.of(8, 0);
        LocalTime studentStartTiming = LocalTime.of(21, 0);
        LocalDateTime testTime = LocalDateTime.of(2024, 6, 6, 8, 0);
        
        Student student = new Student();
        student.setId(1L);
        student.setCourse("Math");
        student.setTeacherId(1L);
        student.setTiming("21:00 - 00:00");
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);

        String result = studentService.changeStudentTiming(1L, "00:00 - 03:00");
        assertEquals("Timing updated successfully!", result);
    }

    @Test
    public void testChangeTimingExactly12HoursBefore() {
        // Mock current time
        LocalTime currentTime = LocalTime.of(9, 0);
        LocalTime studentStartTiming = LocalTime.of(21, 0);
        LocalDateTime testTime = LocalDateTime.of(2024, 6, 6, 9, 0);
        
        Student student = new Student();
        student.setId(1L);
        student.setCourse("Math");
        student.setTeacherId(1L);
        student.setTiming("21:00 - 00:00");
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);

        String result = studentService.changeStudentTiming(1L, "00:00 - 03:00");
        assertEquals("Timing updated successfully!", result);
    }

    @Test
    public void testChangeTimingLessThan12HoursBefore() {
        // Mock current time
        LocalTime currentTime = LocalTime.of(12, 0);
        LocalTime studentStartTiming = LocalTime.of(21, 0);
        LocalDateTime testTime = LocalDateTime.of(2024, 6, 6, 12, 0);
        
        Student student = new Student();
        student.setId(1L);
        student.setCourse("Math");
        student.setTeacherId(1L);
        student.setTiming("21:00 - 00:00");
        
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        String result = studentService.changeStudentTiming(1L, "00:00 - 03:00");
        assertEquals("Sorry, changes can only be made 12 hours before the current timing.", result);
    }

    // Mock current time using PowerMock
    private void mockCurrentTime(LocalDateTime mockTime) {
        PowerMockito.mockStatic(LocalDateTime.class);
        PowerMockito.when(LocalDateTime.now()).thenReturn(mockTime);
    }
}
