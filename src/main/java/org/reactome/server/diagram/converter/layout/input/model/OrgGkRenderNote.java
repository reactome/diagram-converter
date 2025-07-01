
package org.reactome.server.diagram.converter.layout.input.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Java class for anonymous complex type.
 * 
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}Properties"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="bounds" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="position" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="bgColor" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="fgColor" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="textPosition" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "properties"
})
@XmlRootElement(name = "org.gk.render.Note")
public class OrgGkRenderNote {

    @XmlElement(name = "Properties", required = true)
    protected Properties properties;
    @XmlAttribute(name = "id", required = true)
    protected BigInteger id;
    @XmlAttribute(name = "bounds", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String bounds;
    @XmlAttribute(name = "position", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String position;
    @XmlAttribute(name = "bgColor")
    @XmlSchemaType(name = "anySimpleType")
    protected String bgColor;
    @XmlAttribute(name = "fgColor")
    @XmlSchemaType(name = "anySimpleType")
    protected String fgColor;
    @XmlAttribute(name = "textPosition", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String textPosition;

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the bounds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBounds() {
        return bounds;
    }

    /**
     * Sets the value of the bounds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBounds(String value) {
        this.bounds = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPosition(String value) {
        this.position = value;
    }

    /**
     * Gets the value of the bgColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBgColor() {
        return bgColor;
    }

    /**
     * Sets the value of the bgColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBgColor(String value) {
        this.bgColor = value;
    }

    /**
     * Gets the value of the fgColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFgColor() {
        return fgColor;
    }

    /**
     * Sets the value of the fgColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFgColor(String value) {
        this.fgColor = value;
    }

    /**
     * Gets the value of the textPosition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextPosition() {
        return textPosition;
    }

    /**
     * Sets the value of the textPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextPosition(String value) {
        this.textPosition = value;
    }

}
