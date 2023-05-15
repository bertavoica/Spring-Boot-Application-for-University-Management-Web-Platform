package com.cti;

import com.cti.controllers.CourseController;
import com.cti.models.Course;
import com.cti.repository.CourseRepository;
import com.cti.repository.StudentRepository;
import com.cti.repository.UserRepository;
import com.cti.security.WebSecurityConfig;
import com.cti.security.jwt.AuthEntryPointJwt;
import com.cti.security.services.UserDetailsServiceImpl;
import com.cti.service.UserService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(CourseController.class)
@ContextConfiguration(classes = {WebSecurityConfig.class})
@WebAppConfiguration
public class TestNG extends AbstractTestNGSpringContextTests {


    @Autowired
    private CourseController courseController;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    private final static String url = "/course-controller";

    @Test
    public void testShouldReturnAllCourses() throws Exception {
        this.mockMvc.perform(get("/course-controller"))
                .andExpect(status().isOk());
    }

//    @Test
//    void testShouldReturnAllCourses() throws Exception {
//        List<Course> courseList = List.of(new Course(), new Course());
//        Mockito.when(this.courseRepository.findAll())
//                .thenReturn(courseList);
//
//        this.mockMvc.perform(
//                        get(url)
//                                .contentType(MediaType.ALL_VALUE))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }

    @Test
    public void test() {
        Assert.assertEquals(1, 1);
    }

    @BeforeTest
    public void beforeMethod() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        System.out.println("Starting Test On Chrome Browser");
    }
}



