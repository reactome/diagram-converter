package org.reactome.server.diagram.converter.layout.input.xml;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.net.URL;

/**
 * Returns the schema from an xsd schema file
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SchemaProvider {

    public static Schema getSchema(String schemaLocation) throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;

        ClassLoader classLoader = SchemaProvider.class.getClassLoader();
        URL url = classLoader.getResource(schemaLocation);
        if (url != null) {
            schema = sf.newSchema(url);
        }
        return schema;
    }
}
