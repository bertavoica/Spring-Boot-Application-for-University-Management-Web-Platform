package com.cti.repository;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.cti.models.Project;
import com.cti.payload.request.ProjectAddRequest;
import org.mockito.Mockito;
import org.testng.annotations.Test;

public class ProjectRepositoryTest {

    @Test
    public void testFindByUniqueId() {
        ProjectRepository repository = Mockito.mock(ProjectRepository.class);
        ProjectAddRequest projectAddRequest = new ProjectAddRequest();
        projectAddRequest.setCourseUniqueId("uniqueId");
        Mockito.when(repository.findByUniqueId("uniqueId")).thenReturn(Optional.of(new Project(projectAddRequest)));

        Optional<Project> project = repository.findByUniqueId("uniqueId");
        assertEquals(project.get().getUniqueId(), "uniqueId");
    }

    @Test
    public void testFindByOwner() {
        ProjectRepository repository = Mockito.mock(ProjectRepository.class);
        Mockito.when(repository.findByOwner("username")).thenReturn(Arrays.asList(new Project(),
                new Project()));

        List<Project> projects = repository.findByOwner("username");
        System.out.println(repository);
        assertEquals(projects.size(), 2);
    }

    @Test
    public void testDeleteByUniqueId() {
        ProjectRepository repository = Mockito.mock(ProjectRepository.class);

        ProjectAddRequest projectAddRequest = new ProjectAddRequest();
        projectAddRequest.setCourseUniqueId("uniqueId");

        ProjectAddRequest projectAddRequest_2 = new ProjectAddRequest();
        projectAddRequest_2.setCourseUniqueId("uniqueId_2");

        List<Project> projects = List.of(new Project(projectAddRequest), new Project(projectAddRequest_2));
        Mockito.doNothing().when(repository).deleteByUniqueId("uniqueId");

        System.out.println(projects);

        assertEquals(projects.size(), 1);
    }
}
