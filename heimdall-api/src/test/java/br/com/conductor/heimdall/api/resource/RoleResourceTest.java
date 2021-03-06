package br.com.conductor.heimdall.api.resource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.conductor.heimdall.api.ApiApplication;
import br.com.conductor.heimdall.api.dto.RoleDTO;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.service.RoleService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.conductor.heimdall.core.util.RabbitQueueUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=ApiApplication.class)
@AutoConfigureMockMvc
public class RoleResourceTest {

     @Autowired
     private MockMvc mockMvc;
     
     @MockBean
     private RoleService service;
     
     @BeforeClass
     public static void setup() {
          RabbitQueueUtils.init();
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"READ_ROLE", "CREATE_ROLE", "DELETE_ROLE", "UPDATE_ROLE"})
     public void getAllRoles() throws Exception {
          mockMvc.perform(MockMvcRequestBuilders.get(ConstantsPath.PATH_ROLES)
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isOk());
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"READ_ROLE", "CREATE_ROLE", "DELETE_ROLE", "UPDATE_ROLE"})
     public void notPermitToPersistRoleWithPrivilegesInexistents() throws Exception {
          Role role = new Role();
          role.setId(100L);
          Mockito.when(service.save(Mockito.any(RoleDTO.class))).thenReturn(role);
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ROLES)
                    .content("{\"name\":\"\",\"privileges\":[{\"id\":5},{\"id\":2},{\"id\":10}]}")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"erros\":[{\"defaultMessage\":\"size must be between 6 and 80\",\"objectName\":\"roleDTO\",\"field\":\"name\",\"code\":\"Size\"}]}"));
     }
}
