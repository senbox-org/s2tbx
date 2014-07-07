//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.spatio;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * The histogram defines : a space-separated list of percent values, the first (MIN) and last (MAX) indexes for significant percent values (i.e. sup 0.0), the mean and standard deviation values
 * 
 * <p>Classe Java pour A_LOCAL_HISTOGRAM_DEFINITION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_LOCAL_HISTOGRAM_DEFINITION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DIRECTION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="X"/>
 *               &lt;enumeration value="Y"/>
 *               &lt;enumeration value="Z"/>
 *               &lt;enumeration value="COL"/>
 *               &lt;enumeration value="ROW"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_FLOAT"/>
 *         &lt;element name="STEP" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="MIN" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="MAX" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="MEAN" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="STD_DEV" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_LOCAL_HISTOGRAM_DEFINITION", propOrder = {
    "direction",
    "values",
    "step",
    "min",
    "max",
    "mean",
    "stddev"
})
public class ALOCALHISTOGRAMDEFINITION {

    @XmlElement(name = "DIRECTION", required = true)
    protected String direction;
    @XmlList
    @XmlElement(name = "VALUES", type = Float.class)
    protected List<Float> values;
    @XmlElement(name = "STEP")
    protected double step;
    @XmlElement(name = "MIN")
    protected double min;
    @XmlElement(name = "MAX")
    protected double max;
    @XmlElement(name = "MEAN")
    protected double mean;
    @XmlElement(name = "STD_DEV")
    protected double stddev;

    /**
     * Obtient la valeur de la propriété direction.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDIRECTION() {
        return direction;
    }

    /**
     * Définit la valeur de la propriété direction.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDIRECTION(String value) {
        this.direction = value;
    }

    /**
     * Gets the value of the values property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the values property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVALUES().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Float }
     * 
     * 
     */
    public List<Float> getVALUES() {
        if (values == null) {
            values = new ArrayList<Float>();
        }
        return this.values;
    }

    /**
     * Obtient la valeur de la propriété step.
     * 
     */
    public double getSTEP() {
        return step;
    }

    /**
     * Définit la valeur de la propriété step.
     * 
     */
    public void setSTEP(double value) {
        this.step = value;
    }

    /**
     * Obtient la valeur de la propriété min.
     * 
     */
    public double getMIN() {
        return min;
    }

    /**
     * Définit la valeur de la propriété min.
     * 
     */
    public void setMIN(double value) {
        this.min = value;
    }

    /**
     * Obtient la valeur de la propriété max.
     * 
     */
    public double getMAX() {
        return max;
    }

    /**
     * Définit la valeur de la propriété max.
     * 
     */
    public void setMAX(double value) {
        this.max = value;
    }

    /**
     * Obtient la valeur de la propriété mean.
     * 
     */
    public double getMEAN() {
        return mean;
    }

    /**
     * Définit la valeur de la propriété mean.
     * 
     */
    public void setMEAN(double value) {
        this.mean = value;
    }

    /**
     * Obtient la valeur de la propriété stddev.
     * 
     */
    public double getSTDDEV() {
        return stddev;
    }

    /**
     * Définit la valeur de la propriété stddev.
     * 
     */
    public void setSTDDEV(double value) {
        this.stddev = value;
    }

}
