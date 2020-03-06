package uk.gov.companieshouse;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AppealApplication.class)
@WebAppConfiguration
@ComponentScan()
public class AssertjSwaggerDocumentationDrivenTest {
//    @Test
//    public void validateThatImplementationMatchesDocumentationSpecification(){
//        String designFirstSwagger = SwaggerAssertTest.class.getResource("/swagger.yml").getPath();
//        SwaggerAssertions.assertThat("http://localhost:8080/v2/api-docs")
//            .isEqualTo(designFirstSwagger);
//    }

}
