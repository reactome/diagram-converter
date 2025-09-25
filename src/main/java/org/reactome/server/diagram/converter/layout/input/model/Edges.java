
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
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{}org.gk.render.FlowLine" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{}org.gk.render.EntitySetAndMemberLink" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{}org.gk.render.RenderableReaction" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{}org.gk.render.RenderableInteraction" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{}org.gk.render.EntitySetAndEntitySetLink" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction"
})
@XmlRootElement(name = "Edges")
public class Edges {

    @XmlElements({
        @XmlElement(name = "org.gk.render.FlowLine", type = OrgGkRenderFlowLine.class),
        @XmlElement(name = "org.gk.render.EntitySetAndMemberLink", type = OrgGkRenderEntitySetAndMemberLink.class),
        @XmlElement(name = "org.gk.render.RenderableReaction", type = OrgGkRenderRenderableReaction.class),
        @XmlElement(name = "org.gk.render.RenderableInteraction", type = OrgGkRenderRenderableInteraction.class),
        @XmlElement(name = "org.gk.render.EntitySetAndEntitySetLink", type = OrgGkRenderEntitySetAndEntitySetLink.class)
    })
    protected List<Object> orgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction;

    /**
     * Gets the value of the orgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the
     * orgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrgGkRenderFlowLine }
     * {@link OrgGkRenderEntitySetAndMemberLink }
     * {@link OrgGkRenderRenderableReaction }
     * {@link OrgGkRenderRenderableInteraction }
     * {@link OrgGkRenderEntitySetAndEntitySetLink }
     * 
     * 
     */
    public List<Object> getOrgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction() {
        if (orgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction == null) {
            orgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction = new ArrayList<Object>();
        }
        return this.orgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction;
    }

}
