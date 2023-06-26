package com.cti.controllers;

import com.cti.models.Title;
import com.cti.payload.request.TitleAddRequest;
import com.cti.service.TitleService;
import com.cti.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertTrue;

public class TitleControllerTests {
    private MockMvc mockMvc;

    @InjectMocks
    private TitleController titleController;

    @Mock
    private UserService userService;

    @Mock
    private TitleService titleService;

    private static final String URL = "/title-controller";

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(titleController).build();
    }

    @Test
    public void getAllTitles() throws Exception {
        Title title = new Title();
        title.setName("test");

        Mockito.when(this.titleService.getAllTitles()).thenReturn(List.of(title));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URL)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc. perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"name\":\"test\"}]"));
    }

    @Test
    public void addTitleTest() throws Exception {
        TitleAddRequest titleAddRequest = new TitleAddRequest();
        titleAddRequest.setName("test");

        Mockito.when(this.titleService.addTitle(titleAddRequest)).thenReturn("test");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(URL)
                .content(asJsonString(titleAddRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

//        assertTrue(content.contains("[{\"username\":\"test\",\"emailAddress\":\"test\",\"title\":\"test\",\"superior\":\"test\",\"specialization\":\"test\"}]"));
    }

    @Test
    public void deleteTitleTest() throws Exception {
        String name = "test_1";

        Title title = new Title();
        title.setName("test");

        Mockito.when(this.titleService.deleteTitle(name)).thenReturn(List.of(title));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URL)
                .param("name", name)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("[{\"name\":\"test\"}]"));
    }

    private static String asJsonString(final Object obj) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
