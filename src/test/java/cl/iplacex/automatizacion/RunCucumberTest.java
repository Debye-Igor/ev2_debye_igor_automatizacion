package cl.iplacex.automatizacion;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runner de Cucumber integrado con JUnit Platform.
 
 * Indica a JUnit que descubra los archivos .feature en
 * src/test/resources/features y los ejecute usando losstep definitions del paquete cl.iplacex.automatizacion.steps.
 
 * Tambien configura los reportes que se generaran:
 * - Pretty: salida legible en consola.
 * - HTML: reporte navegable en target/cucumber-report.html.
 * - JUnit XML: para integracion con CI.
 **/

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "cl.iplacex.automatizacion.steps"
)
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty,html:target/cucumber-report.html,junit:target/cucumber-junit.xml"
)
public class RunCucumberTest {
    // Sin contenido: las anotaciones definen la suite completa
}