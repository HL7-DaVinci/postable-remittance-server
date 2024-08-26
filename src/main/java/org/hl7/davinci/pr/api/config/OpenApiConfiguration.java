package org.hl7.davinci.pr.api.config;

import static org.hl7.davinci.pr.api.utils.ApiConstants.OPEN_API_TAG_SERVICE_NAME;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  /**
   * Creates and returns an instance of the OpenAPI bean.
   *
   * @return the OpenAPI bean
   */
  @Bean
  public OpenAPI appOpenApi() {
    return new OpenAPI().info(buildInfo());
  }

  /**
   * Builds and returns an Info object for the Open API configuration.
   *
   * @return the Info object with the title, description, and contact information
   */
  private Info buildInfo() {
    return new Info()
        .title(OPEN_API_TAG_SERVICE_NAME)
        .description("APIs for the postable remittance reference implementation service.")
        .contact(new Contact()
            .name("DaVinci Postable Remittance")
            .email("amouradian@athenahealth.com"));
  }
}
