
package org.reactome.server.diagram.converter.layout.input.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element ref="{}Nodes"/&gt;
 *         &lt;element ref="{}Edges"/&gt;
 *         &lt;element ref="{}Pathways"/&gt;
 *         &lt;element ref="{}normalComponents" minOccurs="0"/&gt;
 *         &lt;element ref="{}diseaseComponents" minOccurs="0"/&gt;
 *         &lt;element ref="{}crossedComponents" minOccurs="0"/&gt;
 *         &lt;element ref="{}overlaidComponents" minOccurs="0"/&gt;
 *         &lt;element ref="{}lofNodes" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="hideCompartmentInName" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="nextId" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="reactomeDiagramId" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="isDisease" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="forNormalDraw" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "properties",
    "nodes",
    "edges",
    "pathways",
    "normalComponents",
    "diseaseComponents",
    "crossedComponents",
    "overlaidComponents",
    "lofNodes"
})
@XmlRootElement(name = "Process")
public class Process {

    @XmlElement(name = "Properties", required = true)
    protected Properties properties;
    @XmlElement(name = "Nodes", required = true)
    protected Nodes nodes;
    @XmlElement(name = "Edges", required = true)
    protected Edges edges;
    @XmlElement(name = "Pathways", required = true)
    protected Pathways pathways;
    protected String normalComponents;
    protected String diseaseComponents;
    protected String crossedComponents;
    protected String overlaidComponents;
    protected String lofNodes;
    @XmlAttribute(name = "hideCompartmentInName")
    protected Boolean hideCompartmentInName;
    @XmlAttribute(name = "nextId")
    protected BigInteger nextId;
    @XmlAttribute(name = "reactomeDiagramId", required = true)
    protected BigInteger reactomeDiagramId;
    @XmlAttribute(name = "isDisease")
    protected Boolean isDisease;
    @XmlAttribute(name = "forNormalDraw")
    protected Boolean forNormalDraw;

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
     * Gets the value of the nodes property.
     * 
     * @return
     *     possible object is
     *     {@link Nodes }
     *     
     */
    public Nodes getNodes() {
        return nodes;
    }

    /**
     * Sets the value of the nodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Nodes }
     *     
     */
    public void setNodes(Nodes value) {
        this.nodes = value;
    }

    /**
     * Gets the value of the edges property.
     * 
     * @return
     *     possible object is
     *     {@link Edges }
     *     
     */
    public Edges getEdges() {
        return edges;
    }

    /**
     * Sets the value of the edges property.
     * 
     * @param value
     *     allowed object is
     *     {@link Edges }
     *     
     */
    public void setEdges(Edges value) {
        this.edges = value;
    }

    /**
     * Gets the value of the pathways property.
     * 
     * @return
     *     possible object is
     *     {@link Pathways }
     *     
     */
    public Pathways getPathways() {
        return pathways;
    }

    /**
     * Sets the value of the pathways property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pathways }
     *     
     */
    public void setPathways(Pathways value) {
        this.pathways = value;
    }

    /**
     * Gets the value of the normalComponents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNormalComponents() {
        return normalComponents;
    }

    /**
     * Sets the value of the normalComponents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNormalComponents(String value) {
        this.normalComponents = value;
    }

    /**
     * Gets the value of the diseaseComponents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiseaseComponents() {
        return diseaseComponents;
    }

    /**
     * Sets the value of the diseaseComponents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiseaseComponents(String value) {
        this.diseaseComponents = value;
    }

    /**
     * Gets the value of the crossedComponents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrossedComponents() {
        return crossedComponents;
    }

    /**
     * Sets the value of the crossedComponents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrossedComponents(String value) {
        this.crossedComponents = value;
    }

    /**
     * Gets the value of the overlaidComponents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOverlaidComponents() {
        return overlaidComponents;
    }

    /**
     * Sets the value of the overlaidComponents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOverlaidComponents(String value) {
        this.overlaidComponents = value;
    }

    /**
     * Gets the value of the lofNodes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLofNodes() {
        return lofNodes;
    }

    /**
     * Sets the value of the lofNodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLofNodes(String value) {
        this.lofNodes = value;
    }

    /**
     * Gets the value of the hideCompartmentInName property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHideCompartmentInName() {
        return hideCompartmentInName;
    }

    /**
     * Sets the value of the hideCompartmentInName property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHideCompartmentInName(Boolean value) {
        this.hideCompartmentInName = value;
    }

    /**
     * Gets the value of the nextId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNextId() {
        return nextId;
    }

    /**
     * Sets the value of the nextId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNextId(BigInteger value) {
        this.nextId = value;
    }

    /**
     * Gets the value of the reactomeDiagramId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getReactomeDiagramId() {
        return reactomeDiagramId;
    }

    /**
     * Sets the value of the reactomeDiagramId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setReactomeDiagramId(BigInteger value) {
        this.reactomeDiagramId = value;
    }

    /**
     * Gets the value of the isDisease property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsDisease() {
        return isDisease;
    }

    /**
     * Sets the value of the isDisease property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDisease(Boolean value) {
        this.isDisease = value;
    }

    /**
     * Gets the value of the forNormalDraw property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isForNormalDraw() {
        return forNormalDraw;
    }

    /**
     * Sets the value of the forNormalDraw property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setForNormalDraw(Boolean value) {
        this.forNormalDraw = value;
    }

}
