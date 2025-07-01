package org.reactome.server.diagram.converter.layout.output;

import org.reactome.server.diagram.converter.layout.input.model.Properties;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class DiagramObject {

    protected Map<String, Serializable> properties = new HashMap<>();

    public Long id; //unique per diagram id 643809
    public Long reactomeId; //dbID e.g. 109276

    public String displayName;

    public String schemaClass;
    public String renderableClass;

    public Coordinate position;

    @Deprecated
    public transient Float lineWidth;
    public Boolean isDisease = null;
//    public Boolean isOverlaid = null;
    public Boolean isFadeOut = null;

    public Integer minX; public Integer maxX;
    public Integer minY; public Integer maxY;

    //Objects passed to this constructor are automatically generated and
    //do not implement any common interface. That is the reason why it is
    //implemented this way
    public DiagramObject(Object obj){
        for (Method method : obj.getClass().getMethods()) {
            switch (method.getName()){
                case "getId":           this.id = getLong(method, obj);             break;
                case "getReactomeId":   this.reactomeId = getLong(method, obj);     break;
                case "getProperties":   this.setProperties(method, obj);            break;
                case "getSchemaClass":  this.schemaClass = getString(method, obj);  break;
                case "getPosition":
                    String position = getString(method, obj);
                    this.position = extractPositionFromString(position, " ");
                    break;
                case "getLineColor":
                    String color = getString(method, obj);
                    Color lineColor = extractColorFromString(color, " ");
                    if(lineColor!=null && lineColor.isReddish()){
                        this.isDisease = true;
                    }
                    break;
                case "getLineWidth":    this.lineWidth = getFloat(method, obj);     break;
            }
        }
        this.displayName = (String) properties.get("displayName");
        this.renderableClass = shortenRenderableClass(obj.getClass().getSimpleName());
    }

    public void translate(Coordinate panning){
        this.position.translate(panning);
        this.minX += panning.x;
        this.maxX += panning.x;
        this.minY += panning.y;
        this.maxY += panning.y;
    }

    public static Boolean getBoolean(Method method, Object obj){
        try{
            return (Boolean) method.invoke(obj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Float getFloat(Method method, Object obj){
        try{
            BigDecimal aux = (BigDecimal) method.invoke(obj);
            if(aux != null) return aux.floatValue();
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getInteger(Method method, Object obj) {
        try {
            BigInteger id = (BigInteger) method.invoke(obj);
            if (id != null) return id.intValue();
        }catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getLong(Method method, Object obj) {
        try {
            BigInteger id = (BigInteger) method.invoke(obj);
            if (id != null) return id.longValue();
        }catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(Method method, Object obj){
        try {
            return (String) method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void setProperties(Method method, Object obj){
        try {
            Properties prop = (Properties) method.invoke(obj);
            if(prop!=null) this.properties = extractProperties(prop);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /*
     * Convert a string of values into a list of integer pairs
     * 123 45, 23 77, 90 54,
     * "," here is tha external separator
     * " " here is the internal separator
     */
    public static List<Coordinate> extractIntegerPairsListFromString(String inputString,
                                                                       String externalSeparator,
                                                                       String internalSeparator){
        List<Coordinate> outputList = null;
        if(inputString!=null) {
            //Split inputString using the external separator
            String[] tempPairsStrArray = inputString.split(externalSeparator);
            if (tempPairsStrArray.length > 0) {
                outputList = new ArrayList<>();
                for (String tempPairStr : tempPairsStrArray) {
                    if (tempPairStr != null && !tempPairStr.isEmpty()) {
                        //Split string using the internal separator
                        String[] tempSinglePairStrArray = tempPairStr.trim().split(internalSeparator);
                        if (tempSinglePairStrArray.length > 0) {

                            ArrayList<Integer> innerList = new ArrayList<>();
                            for(String tempSinleValueStr : tempSinglePairStrArray) {
                                //convert String to Integer
                                innerList.add( Integer.parseInt(tempSinleValueStr.trim() ) );
                            }
                            outputList.add(new Coordinate(innerList));
                        }
                    }
                }
            }//end if
        }
        return outputList;
    }

    public static List<Segment> getSegments(List<Coordinate> inputCoordinates){
        List<Segment> rtn = new LinkedList<>();

        Coordinate from = inputCoordinates.get(0);
        Coordinate to;
        for (int i = 1; i < inputCoordinates.size(); i++) {
            to = inputCoordinates.get(i);
            Segment segment = new Segment(from, to);
            if(!segment.isPoint()) {
                rtn.add(segment);
            }
            from = to;
        }

        return rtn;
    }


    public static Map<String, Serializable> extractProperties(Properties inputProps) {
        Map<String, Serializable> props = new HashMap<>();
        if (inputProps != null && inputProps.getIsChangedOrDisplayName() != null) {
            for (Serializable item : inputProps.getIsChangedOrDisplayName()) {
                if (item instanceof String) {
                    props.put("displayName", item);
                } else if (item instanceof Boolean) {
                    props.put("isChanged", item);
                }
            }
        }
        return props;
    }

    /*
 * Convert a string of values into a Position
 */
    public static Bound extractBoundFromString(String inputString, String separator){
        List<Integer> outputList = extractIntegerListFromString(inputString, separator);
        if(outputList!=null) return new Bound(outputList);
        return null;
    }

    /*
     * Convert a string of values into a Position
     */
    public static Coordinate extractPositionFromString(String inputString, String separator){
        List<Integer> outputList = extractIntegerListFromString(inputString, separator);
        if(outputList!=null) return new Coordinate(outputList);
        return null;
    }

    /*
     * Convert a string of values into a Color
     */
    public static Color extractColorFromString(String inputString, String separator){
        List<Integer> outputList = extractIntegerListFromString(inputString, separator);
        if(outputList!=null) return new Color(outputList);
        return null;
    }

    /*
     * Convert a string of values into a list of integers
     * 123 45 23 77 90 54
     */
    public static List<Integer> extractIntegerListFromString(String inputString, String separator){
        List<Integer> outputList = null;
        if(inputString!=null) {
            //Split inputString using the separator
            String[] tempStrArray = inputString.split(separator);

            if (tempStrArray.length > 0) {
                outputList = new ArrayList<>();
                for (String tempStr : tempStrArray) {
                    if (tempStr != null && !tempStr.isEmpty()) {
                        //convert String to Integer
                        outputList.add(Integer.parseInt(tempStr.trim()));
                    }

                }//end for every string
            }//end if
        }
        return outputList;
    }

    private static String shortenRenderableClass(String longName){
        String shortName = longName.replace("OrgGkRender", "");
        return shortName.replace("Renderable", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiagramObject that = (DiagramObject) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DiagramObject{" +
                "id=" + id +
                ", reactomeId=" + reactomeId +
                ", displayName='" + displayName + '\'' +
                ", schemaClass='" + schemaClass + '\'' +
                '}';
    }
}