package uk.gov.companieshouse;

import io.github.robwin.swagger.test.SwaggerAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AppealApplication.class)
@WebAppConfiguration
public class AssertjSwaggerDocumentationDrivenTest {
    @Test
    public void validateThatImplementationMatchesDocumentationSpecification(){
        String designFirstSwagger = AssertjSwaggerDocumentationDrivenTest.class.getResource("/swagger.yaml").getPath();
        SwaggerAssertions.assertThat("http://localhost:8080/v2/api-docs")
            .isEqualTo(designFirstSwagger);
    }

//    @Test
//    public void validateThatImplementationFitsDesignSpecification() throws Exception {
//        String designFirstDocumentationSwaggerLocation = Swagger2MarkupTest.class.getResource("/swagger.yaml").getPath();
//
//        MvcResult mvcResult = this.mockMvc.perform(get("/v2/api-docs")
//            .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andReturn();
//
//        String springfoxSwaggerJson = mvcResult.getResponse().getContentAsString();
//        SwaggerAssertions.assertThat(new SwaggerParser().parse(springfoxSwaggerJson)).isEqualTo(designFirstDocumentationSwaggerLocation);
//    }
}
