package eu.delimata.bookstore.utils;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public final class AllureEnvironmentWriter {

    private AllureEnvironmentWriter() {
    }

    public static void writeAllureEnvironment(Map<String, String> environmentValuesSet) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element environment = doc.createElement("environment");
            doc.appendChild(environment);

            environmentValuesSet
                    .forEach((key, value) -> {
                        Element parameter = doc.createElement("parameter");
                        Element keyElement = doc.createElement("key");
                        Element valueElement = doc.createElement("value");

                        keyElement.appendChild(doc.createTextNode(key));
                        valueElement.appendChild(doc.createTextNode(value));
                        parameter.appendChild(keyElement);
                        parameter.appendChild(valueElement);
                        environment.appendChild(parameter);
                    });

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            String allureResultsDirectoryPath = "build/allure-results";
            String environmentFilePath = allureResultsDirectoryPath + "/environment.xml";

            Files.createDirectories(Paths.get(allureResultsDirectoryPath));
            StreamResult result = new StreamResult(new File(environmentFilePath));
            transformer.transform(source, result);
            log.info("Allure environment data saved.");
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
