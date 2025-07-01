package org.reactome.server.diagram.converter.layout;

import org.gk.model.GKInstance;
import org.gk.model.ReactomeJavaConstants;
import org.reactome.server.diagram.converter.layout.input.model.*;
import org.reactome.server.diagram.converter.layout.input.model.Process;
import org.reactome.server.diagram.converter.layout.output.*;
import org.reactome.server.diagram.converter.qa.diagram.*;
import org.reactome.server.diagram.converter.utils.reports.Participant;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class LayoutFactory {

    private static final String SEPARATOR = ",";
    private static Logger logger = LoggerFactory.getLogger("converter");
    private static Diagram outputDiagram = null;

    public static Diagram getDiagramFromProcess(Process inputProcess, GKInstance pathway, String stId) {

        if (inputProcess != null) {
            outputDiagram = new Diagram();

            //Parse General fields
            outputDiagram.setDbId(pathway.getDBID());
            outputDiagram.setStableId(stId);
            outputDiagram.setDisplayName(pathway.getDisplayName());
            outputDiagram.setSpeciesName(getSpeciesName(pathway));

            outputDiagram.setIsDisease(inputProcess.isIsDisease());
            outputDiagram.setForNormalDraw(inputProcess.isForNormalDraw());

            //Parse General list-fields
            outputDiagram.setNormalComponents(ListToSet(extractListFromString(inputProcess.getNormalComponents(), SEPARATOR)));
            outputDiagram.setDiseaseComponents(ListToSet(extractListFromString(inputProcess.getDiseaseComponents(), SEPARATOR)));
            outputDiagram.setCrossedComponents(ListToSet(extractListFromString(inputProcess.getCrossedComponents(), SEPARATOR)));
            outputDiagram.setNotFadeOut(ListToSet(extractListFromString(inputProcess.getOverlaidComponents(), SEPARATOR)));
            outputDiagram.setLofNodes(ListToSet(extractListFromString(inputProcess.getLofNodes(), SEPARATOR)));

            //Parse Nodes
            List<NodeCommon> nodes = extractNodesList(inputProcess.getNodes());
            if (nodes != null) {
                for (NodeCommon nodeCommon : nodes) {
                    if (nodeCommon instanceof Node) {
                        outputDiagram.addNode((Node) nodeCommon);
                    } else if (nodeCommon instanceof Note) {
                        outputDiagram.addNote((Note) nodeCommon);
                    } else if (nodeCommon instanceof Compartment) {
                        outputDiagram.addCompartment((Compartment) nodeCommon);
                    }
                }
            }

            //Parse Edges
            for (EdgeCommon edgeCommon : extractEdgesList(inputProcess.getEdges())) {
                if (edgeCommon instanceof Link) {
                    outputDiagram.addLink((Link) edgeCommon);
                } else if (edgeCommon instanceof Edge) {
                    //check for duplicated arrows pointing to the same diagram entity
                    outputDiagram.addEdge((Edge) fixDuplicateArrows(edgeCommon));
                }
            }

            //Generate node connectors
            outputDiagram.setConnectors();

            //Generate the arrow at the backbones if needed
            outputDiagram.setBackboneArrows();

            //Generate the arrow of links
            outputDiagram.setLinkArrows();

            //In case of a disease pathway normal nodes should be faded out
            outputDiagram.fadeOutNormalComponents();

            //Process the crossed components
            outputDiagram.setCrossedComponents();

            //Set the faded out components
            outputDiagram.setOverlaidObjects();

            //Process disease components
            outputDiagram.setDiseaseComponents();

            //Sets the entities visual summary
            outputDiagram.setEntitySummaries();

            //Calculate node boundaries >> IMPORTANT: Needs to be done after outputDiagram.setEntitySummaries()
            outputDiagram.setNodesBoundaries();

            //Set universal min max >> IMPORTANT: This has to always be done at the very end
            outputDiagram.setUniversalBoundaries();

        }
        return outputDiagram;
    }

    private static List<NodeCommon> extractNodesList(Nodes inputNodes) {
        List<NodeCommon> rtn = new LinkedList<>();

        if (inputNodes == null) {
            return null;
        }

        Map<Long, Participant> participants = TestReportsHelper.getDiagramParticipants(outputDiagram.getDbId());

        List<Object> inputNodesList = inputNodes.getOrgGkRenderProcessNodeOrOrgGkRenderRenderableChemicalOrOrgGkRenderRenderableChemicalDrug();
        for (Object inputNode : inputNodesList) {
            if (inputNode != null) {
                Class clazz = inputNode.getClass();
                if (clazz.equals(OrgGkRenderNote.class)) {
                    Note note = new Note(inputNode);
                    if (!note.displayName.equals("Note")) rtn.add(note);
                } else if (clazz.equals(OrgGkRenderRenderableCompartment.class)) {
                    rtn.add(new Compartment(inputNode));
                } else {
                    try {
                        Node node = new Node(inputNode);
                        if (!fixSchemaClass(node, participants)) continue;
                        fixBrokenRenderableClass(node, participants);
                        rtn.add(node);
                    } catch (RuntimeException e) {
                        try {
                            Long id = ((BigInteger) inputNode.getClass().getMethod("getReactomeId").invoke(inputNode)).longValue();
                            T111_UnrecognisedRenderableClass.add(
                                    outputDiagram.getStableId(),
                                    outputDiagram.getDisplayName(),
                                    id,
                                    clazz.getSimpleName()
                            );
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                            logger.error(
                                    "[{}] contains a not recognised NODE type - '{}' [{}]",
                                    outputDiagram.getStableId(),
                                    clazz.getName(),
                                    clazz.getSimpleName()
                            );
                        }

                    }
                }
            }
        }
        return rtn;
    }

    // Sometimes a node does not have a schemaClass
    private static boolean fixSchemaClass(Node node, Map<Long, Participant> map) {
        Participant participant = map.get(node.reactomeId);
        String targetSchemaClass = participant != null ? participant.getSchemaClass() : null;
        boolean rtn = true;
        if (targetSchemaClass == null) {
            if (node.schemaClass == null) {
                //Cannot be fixed!
                T102_MissingSchemaClass.add(outputDiagram.getStableId(), outputDiagram.getDisplayName(), node.reactomeId);
                rtn = false;
            }
        } else if (!targetSchemaClass.equals(node.schemaClass)) {
            //Report BEFORE changing it!
            T106_SchemaClassMismatch.add(
                    outputDiagram.getStableId(),
                    outputDiagram.getDisplayName(),
                    node.reactomeId,
                    node.displayName,
                    node.schemaClass,
                    targetSchemaClass
            );
            node.schemaClass = targetSchemaClass;
        }
        return rtn;
    }

    private static List<EdgeCommon> extractEdgesList(Edges inputEdges) {
        if (inputEdges == null) {
            return null;
        }

        List<EdgeCommon> rtn = new ArrayList<>();
        List<Object> inputEdgesList = inputEdges.getOrgGkRenderFlowLineOrOrgGkRenderEntitySetAndMemberLinkOrOrgGkRenderRenderableReaction();
        for (Object item : inputEdgesList) {
            if (item != null) {
                Class clazz = item.getClass();
                if (clazz.equals(OrgGkRenderRenderableReaction.class)) {
                    rtn.add(new Edge(item));
                } else if (clazz.equals(OrgGkRenderFlowLine.class) ||
                        clazz.equals(OrgGkRenderEntitySetAndMemberLink.class) ||
                        clazz.equals(OrgGkRenderEntitySetAndEntitySetLink.class) ||
                        clazz.equals(OrgGkRenderRenderableInteraction.class)) {
                    rtn.add(new Link(item));
                } else {
                    logger.warn(
                            "[{}] contains a not recognised EDGE type - '{}' [{}]",
                            outputDiagram.getStableId(),
                            clazz.getName(),
                            clazz.getSimpleName()
                    );
                }
            }
        }
        return rtn;
    }

    private static void fixBrokenRenderableClass(DiagramObject obj, Map<Long, Participant> participants) {
        Participant participant = participants.get(obj.reactomeId);
        if (participant != null) {
            String correction = participant.getRenderableClass();
            if (!obj.renderableClass.equals(correction)) {
                //Reports has to be done BEFORE correcting it
                T105_RenderableClassMismatch.add(
                        outputDiagram.getStableId(),
                        outputDiagram.getDisplayName(),
                        obj.reactomeId,
                        obj.schemaClass,
                        obj.displayName,
                        obj.renderableClass,
                        correction
                );
                obj.renderableClass = correction;
            }
        }
    }

    // It checks and removes any duplicated arrows in a
    // single reaction pointing to the same diagram entity
    private static EdgeCommon fixDuplicateArrows(EdgeCommon edge) {
        Long edgeId = edge.reactomeId;
        processReactionParts(edgeId, edge.inputs);
        processReactionParts(edgeId, edge.outputs);
        processReactionParts(edgeId, edge.activators);
        processReactionParts(edgeId, edge.catalysts);
        processReactionParts(edgeId, edge.inhibitors);
        return edge;
    }

    private static void processReactionParts(Long edgeId, List<ReactionPart> reactionParts) {
        if (reactionParts != null) {
            Set<Long> aux = new HashSet<>();
            Iterator<ReactionPart> it = reactionParts.iterator();
            while (it.hasNext()) {
                ReactionPart reactionPart = it.next();
                if (!aux.add(reactionPart.id)) {
                    it.remove();
                    T104_DuplicatedReactionParticipants.add(
                            outputDiagram.getStableId(),
                            outputDiagram.getDisplayName(),
                            edgeId,
                            reactionPart.id
                    );
                }
            }
        }
    }

    /*
     * Convert a string of values into a Set
     * 123 45 23 77 90 54
     */
    private static <E> Set<E> ListToSet(List<E> inputList) {
        Set<E> rtn = null;
        if (inputList != null && !inputList.isEmpty()) {
            rtn = new HashSet<>(inputList);
        }
        return rtn;
    }

    /*
     * Convert a string of values into a list of longs
     */
    @SuppressWarnings("SameParameterValue")
    private static List<Long> extractListFromString(String inputString, String separator) {
        List<Long> outputList = null;
        if (inputString != null) {
            //Split inputString using the separator
            String[] tempStrArray = inputString.split(separator);
            if (tempStrArray.length > 0) {
                outputList = new ArrayList<>();
                for (String tempStr : tempStrArray) {
                    if (tempStr != null && !tempStr.isEmpty()) {
                        //convert String to Integer
                        outputList.add(Long.parseLong(tempStr.trim()));
                    }
                }//end for every string
            }//end if
        }
        return outputList;
    }

    private static String getSpeciesName(GKInstance pathway) {
        try {
            if (pathway.getSchemClass().isValidAttribute(ReactomeJavaConstants.species)) {
                List speciess = pathway.getAttributeValuesList(ReactomeJavaConstants.species);
                if (speciess != null && !speciess.isEmpty()) {
                    GKInstance species = (GKInstance) speciess.get(0);
                    return species.getDisplayName();
                }
            }
        } catch (Exception e) { /*Nothing here*/ }
        return null;
    }
}