
package org.reactome.server.diagram.converter.layout.input.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element ref="{}org.gk.render.RenderableFeature" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orgGkRenderRenderableFeature"
})
@XmlRootElement(name = "NodeAttachments")
public class NodeAttachments {

    @XmlElement(name = "org.gk.render.RenderableFeature", required = true)
    protected List<OrgGkRenderRenderableFeature> orgGkRenderRenderableFeature;

    /**
     * Gets the value of the orgGkRenderRenderableFeature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orgGkRenderRenderableFeature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrgGkRenderRenderableFeature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrgGkRenderRenderableFeature }
     * 
     * 
     */
    public List<OrgGkRenderRenderableFeature> getOrgGkRenderRenderableFeature() {
        if (orgGkRenderRenderableFeature == null) {
            orgGkRenderRenderableFeature = new ArrayList<OrgGkRenderRenderableFeature>();
        }
        return this.orgGkRenderRenderableFeature;
    }

}
