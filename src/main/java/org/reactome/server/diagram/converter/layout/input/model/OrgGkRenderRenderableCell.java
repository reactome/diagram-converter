package org.reactome.server.diagram.converter.layout.input.model;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Properties"/>
 *         &lt;element ref="{}NodeAttachments" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="bounds" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="bgColor" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="fgColor" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="lineColor" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="lineWidth" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="position" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="reactomeId" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="schemaClass" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="textPosition" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="needDashedBorder" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "properties",
        "nodeAttachments"
})
@XmlRootElement(name = "org.gk.render.RenderableCell")
public class OrgGkRenderRenderableCell {

    @XmlElement(name = "Properties", required = true)
    protected Properties properties;
    @XmlElement(name = "NodeAttachments")
    protected NodeAttachments nodeAttachments;
    @XmlAttribute(name = "bounds", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String bounds;
    @XmlAttribute(name = "bgColor")
    @XmlSchemaType(name = "anySimpleType")
    protected String bgColor;
    @XmlAttribute(name = "fgColor")
    @XmlSchemaType(name = "anySimpleType")
    protected String fgColor;
    @XmlAttribute(name = "lineColor")
    @XmlSchemaType(name = "anySimpleType")
    protected String lineColor;
    @XmlAttribute(name = "lineWidth")
    protected BigDecimal lineWidth;
    @XmlAttribute(name = "id", required = true)
    protected BigInteger id;
    @XmlAttribute(name = "position", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String position;
    @XmlAttribute(name = "reactomeId", required = true)
    protected BigInteger reactomeId;
    @XmlAttribute(name = "schemaClass", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String schemaClass;
    @XmlAttribute(name = "textPosition")
    @XmlSchemaType(name = "anySimpleType")
    protected String textPosition;
    @XmlAttribute(name = "needDashedBorder")
    protected Boolean needDashedBorder;

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
     * Gets the value of the nodeAttachments property.
     *
     * @return
     *     possible object is
     *     {@link NodeAttachments }
     *
     */
    public NodeAttachments getNodeAttachments() {
        return nodeAttachments;
    }

    /**
     * Sets the value of the nodeAttachments property.
     *
     * @param value
     *     allowed object is
     *     {@link NodeAttachments }
     *
     */
    public void setNodeAttachments(NodeAttachments value) {
        this.nodeAttachments = value;
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
     * Gets the value of the lineColor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLineColor() {
        return lineColor;
    }

    /**
     * Sets the value of the lineColor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLineColor(String value) {
        this.lineColor = value;
    }

    /**
     * Gets the value of the lineWidth property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the value of the lineWidth property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setLineWidth(BigDecimal value) {
        this.lineWidth = value;
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
     * Gets the value of the reactomeId property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getReactomeId() {
        return reactomeId;
    }

    /**
     * Sets the value of the reactomeId property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setReactomeId(BigInteger value) {
        this.reactomeId = value;
    }

    /**
     * Gets the value of the schemaClass property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSchemaClass() {
        return schemaClass;
    }

    /**
     * Sets the value of the schemaClass property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSchemaClass(String value) {
        this.schemaClass = value;
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

    /**
     * Gets the value of the needDashedBorder property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isNeedDashedBorder() {
        return needDashedBorder;
    }

    /**
     * Sets the value of the needDashedBorder property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setNeedDashedBorder(Boolean value) {
        this.needDashedBorder = value;
    }
}
