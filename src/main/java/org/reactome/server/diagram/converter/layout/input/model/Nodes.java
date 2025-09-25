
package org.reactome.server.diagram.converter.layout.input.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{}org.gk.render.ProcessNode" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableChemical" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableChemicalDrug" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableCompartment" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableComplex" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableEntity" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableGene" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableEntitySet" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableProtein" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableProteinDrug" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableRNA" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.RenderableRNADrug" minOccurs="0"/>
 *         &lt;element ref="{}org.gk.render.Note" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug"
})
@XmlRootElement(name = "Nodes")
public class Nodes {

    @XmlElements({
        @XmlElement(name = "org.gk.render.ProcessNode", type = OrgGkRenderProcessNode.class),
        @XmlElement(name = "org.gk.render.RenderableChemical", type = OrgGkRenderRenderableChemical.class),
        @XmlElement(name = "org.gk.render.RenderableChemicalDrug", type = OrgGkRenderRenderableChemicalDrug.class),
        @XmlElement(name = "org.gk.render.RenderableCompartment", type = OrgGkRenderRenderableCompartment.class),
        @XmlElement(name = "org.gk.render.RenderableComplex", type = OrgGkRenderRenderableComplex.class),
        @XmlElement(name = "org.gk.render.RenderableEntity", type = OrgGkRenderRenderableEntity.class),
        @XmlElement(name = "org.gk.render.RenderableGene", type = OrgGkRenderRenderableGene.class),
        @XmlElement(name = "org.gk.render.RenderableEntitySet", type = OrgGkRenderRenderableEntitySet.class),
        @XmlElement(name = "org.gk.render.RenderableProtein", type = OrgGkRenderRenderableProtein.class),
        @XmlElement(name = "org.gk.render.RenderableCell", type = OrgGkRenderRenderableCell.class),
        @XmlElement(name = "org.gk.render.RenderableProteinDrug", type = OrgGkRenderRenderableProteinDrug.class),
        @XmlElement(name = "org.gk.render.RenderableRNA", type = OrgGkRenderRenderableRNA.class),
        @XmlElement(name = "org.gk.render.RenderableRNADrug", type = OrgGkRenderRenderableRNADrug.class),
        @XmlElement(name = "org.gk.render.Note", type = OrgGkRenderNote.class)
    })
    protected List<Object> orgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug;

    /**
     * Gets the value of the orgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the
     * orgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrgGkRenderProcessNode }
     * {@link OrgGkRenderRenderableChemical }
     * {@link OrgGkRenderRenderableChemicalDrug }
     * {@link OrgGkRenderRenderableCompartment }
     * {@link OrgGkRenderRenderableComplex }
     * {@link OrgGkRenderRenderableEntity }
     * {@link OrgGkRenderRenderableGene }
     * {@link OrgGkRenderRenderableEntitySet }
     * {@link OrgGkRenderRenderableProtein }
     * {@link OrgGkRenderRenderableCell }
     * {@link OrgGkRenderRenderableProteinDrug }
     * {@link OrgGkRenderRenderableRNA }
     * {@link OrgGkRenderRenderableRNADrug }
     * {@link OrgGkRenderNote }
     * 
     * 
     */
    public List<Object> getOrgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug() {
        if (orgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug == null) {
            orgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug = new ArrayList<Object>();
        }
        return this.orgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug;
    }

}
