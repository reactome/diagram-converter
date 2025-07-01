package org.reactome.server.diagram.converter.layout.input;

import org.reactome.server.diagram.converter.layout.input.model.Process;
import org.reactome.server.diagram.converter.layout.input.xml.SchemaProvider;
import org.reactome.server.diagram.converter.layout.input.xml.XMLValidationEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * This class is responsible for deserializing the diagram from its XML
 * format into a{@link org.reactome.server.diagram.converter.layout.input.model.Process Process} object.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */

public class ProcessFactory {
    private static Logger logger = LoggerFactory.getLogger("converter");

    private Unmarshaller jaxbUnmarshaller = null;
    private XMLValidationEventHandler xmlValidationEventHandler = null;

    public ProcessFactory(String schemaLocation) {
        try {
            xmlValidationEventHandler = new XMLValidationEventHandler();
            JAXBContext jaxbContext = JAXBContext.newInstance(Process.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setSchema(SchemaProvider.getSchema(schemaLocation));
            jaxbUnmarshaller.setEventHandler(xmlValidationEventHandler);
        } catch (JAXBException e) {
            logger.error("Error instantiating ProcessFactory:", e);
        } catch (SAXException e) {
            logger.error("Error instantiating ProcessFactory schema:", e);
        }
    }

    public Process createProcess(String xmlContent, String stId) {
        Process process = null;
        try {
            process = deserializeXML(xmlContent);

            //Check for events during deserialization
            if (!xmlValidationEventHandler.getEvents().isEmpty()) {
                logger.error(
                        "[{}] Error(s) in XML deserialisation of [{}]: ",
                        stId, process.getProperties().getIsChangedOrDisplayName().get(0)
                );
                logger.error(" >> {} <<", xmlValidationEventHandler.getEvents());
            }
        } catch (JAXBException e) {
            logger.error("Error creating Process:", e);
        }
        return process;
    }

    private Process deserializeXML(String inputXMLString) throws JAXBException {
        // Reset the events buffer before every deserialization
        xmlValidationEventHandler.clearEvents();
        return deserialize(inputXMLString);
    }

    private Process deserialize(String inputXMLContent) throws JAXBException {
        StringReader stringReader = new StringReader(inputXMLContent);
        return (Process) jaxbUnmarshaller.unmarshal(stringReader);
    }
}
