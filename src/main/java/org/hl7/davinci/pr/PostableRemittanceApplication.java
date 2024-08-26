package org.hl7.davinci.pr;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Da Vinci Postable Remittance Reference Implementation Microservice is launched with this App.
 */
@SpringBootApplication
public class PostableRemittanceApplication {

  /**
   * HAPI FHIR Context.
   * This class is expensive to create, just create one FhirContext instance for your application and reuse that instance.
   * <a href="https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/context/FhirContext.html">FhirContext Docs</a>
   */
  private static final FhirContext FHIR_CTX = FhirContext.forR4();

  /**
   * The main entry point of the Postable Remittance Microservice.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    SpringApplication.run(PostableRemittanceApplication.class, args);
  }

  /**
   * Get the FHIR Context for R4
   *
   * @return the FHIR Context for R4
   */
  public static FhirContext getFhirContext() {
    return FHIR_CTX;
  }
}
