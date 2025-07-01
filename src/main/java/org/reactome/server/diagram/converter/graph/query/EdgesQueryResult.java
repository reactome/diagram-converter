package org.reactome.server.diagram.converter.graph.query;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.util.List;

@SuppressWarnings("unused")
public class EdgesQueryResult extends QueryResult implements CustomQuery {

    private List<Long> inputs;
    private List<Long> outputs;
    private List<Long> catalysts;
    private List<Long> efs;
    private List<RegulationQueryResult> regulation;
    private List<Long> preceding;
    private List<Long> following;

    public List<Long> getInputs() {
        return inputs == null || inputs.isEmpty() ? null : inputs;
    }

    public List<Long> getOutputs() {
        return outputs == null || outputs.isEmpty() ? null : outputs;
    }

    public List<Long> getCatalysts() {
        return catalysts == null || catalysts.isEmpty() ? null : catalysts;
    }

    public List<Long> getEfs() {
        return efs;
    }

    public List<RegulationQueryResult> getRegulation() {
        return regulation;
    }

    public List<Long> getPreceding() {
        return preceding == null || preceding.isEmpty() ? null : preceding;
    }

    public List<Long> getFollowing() {
        return following == null || following.isEmpty() ? null :following;
    }

    public void setInputs(List<Long> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(List<Long> outputs) {
        this.outputs = outputs;
    }

    public void setCatalysts(List<Long> catalysts) {
        this.catalysts = catalysts;
    }

    public void setEfs(List<Long> efs) {
        this.efs = efs;
    }

    public void setRegulation(List<RegulationQueryResult> regulation) {
        this.regulation = regulation;
    }

    public void setPreceding(List<Long> preceding) {
        this.preceding = preceding;
    }

    public void setFollowing(List<Long> following) {
        this.following = following;
    }

    @Override
    public CustomQuery build(Record r) {
        EdgesQueryResult edgesQueryResult = new EdgesQueryResult();
        edgesQueryResult.setDbId(r.get("dbId").asLong(0));
        edgesQueryResult.setDisplayName(r.get("displayName").asString(null));
        edgesQueryResult.setSchemaClass(r.get("schemaClass").asString(null));
        edgesQueryResult.setSpeciesID(r.get("speciesID").asLong(0));
        edgesQueryResult.setStId(r.get("stId").asString(null));

        if (!r.get("inputs").isNull()) edgesQueryResult.setInputs(r.get("inputs").asList(Value::asLong));
        if (!r.get("outputs").isNull()) edgesQueryResult.setOutputs(r.get("outputs").asList(Value::asLong));
        if (!r.get("catalysts").isNull()) edgesQueryResult.setCatalysts(r.get("catalysts").asList(Value::asLong));
        if (!r.get("efs").isNull()) edgesQueryResult.setEfs(r.get("efs").asList(Value::asLong));
        if (!r.get("regulation").isNull()) edgesQueryResult.setRegulation(r.get("regulation").asList(RegulationQueryResult::build));
        if (!r.get("preceding").isNull()) edgesQueryResult.setPreceding(r.get("preceding").asList(Value::asLong));
        if (!r.get("following").isNull()) edgesQueryResult.setFollowing(r.get("following").asList(Value::asLong));

        return edgesQueryResult;
    }
}
