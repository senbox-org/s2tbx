//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.geographical.LongitudeType;


/**
 * <p>Classe Java pour Cycle_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Cycle_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Repeat_Cycle" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Repeat_Cycle_Type"/>
 *         &lt;element name="Cycle_Length" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Cycle_Length_Type"/>
 *         &lt;element name="ANX_Longitude" type="{http://gs2.esa.int/DICO/1.0/SY/geographical/}Longitude_Type"/>
 *         &lt;element name="MLST" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}Time_Type"/>
 *         &lt;element name="MLST_Drift" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}MLST_Drift_Type"/>
 *         &lt;element name="MLST_Nonlinear_Drift" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}MLST_Nonlinear_Drift_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cycle_Type", propOrder = {
    "repeatCycle",
    "cycleLength",
    "anxLongitude",
    "mlst",
    "mlstDrift",
    "mlstNonlinearDrift"
})
public class CycleType {

    @XmlElement(name = "Repeat_Cycle", required = true)
    protected RepeatCycleType repeatCycle;
    @XmlElement(name = "Cycle_Length", required = true)
    protected CycleLengthType cycleLength;
    @XmlElement(name = "ANX_Longitude", required = true)
    protected LongitudeType anxLongitude;
    @XmlElement(name = "MLST", required = true)
    protected XMLGregorianCalendar mlst;
    @XmlElement(name = "MLST_Drift", required = true)
    protected MLSTDriftType mlstDrift;
    @XmlElement(name = "MLST_Nonlinear_Drift", required = true)
    protected MLSTNonlinearDriftType mlstNonlinearDrift;

    /**
     * Obtient la valeur de la propriété repeatCycle.
     * 
     * @return
     *     possible object is
     *     {@link RepeatCycleType }
     *     
     */
    public RepeatCycleType getRepeatCycle() {
        return repeatCycle;
    }

    /**
     * Définit la valeur de la propriété repeatCycle.
     * 
     * @param value
     *     allowed object is
     *     {@link RepeatCycleType }
     *     
     */
    public void setRepeatCycle(RepeatCycleType value) {
        this.repeatCycle = value;
    }

    /**
     * Obtient la valeur de la propriété cycleLength.
     * 
     * @return
     *     possible object is
     *     {@link CycleLengthType }
     *     
     */
    public CycleLengthType getCycleLength() {
        return cycleLength;
    }

    /**
     * Définit la valeur de la propriété cycleLength.
     * 
     * @param value
     *     allowed object is
     *     {@link CycleLengthType }
     *     
     */
    public void setCycleLength(CycleLengthType value) {
        this.cycleLength = value;
    }

    /**
     * Obtient la valeur de la propriété anxLongitude.
     * 
     * @return
     *     possible object is
     *     {@link LongitudeType }
     *     
     */
    public LongitudeType getANXLongitude() {
        return anxLongitude;
    }

    /**
     * Définit la valeur de la propriété anxLongitude.
     * 
     * @param value
     *     allowed object is
     *     {@link LongitudeType }
     *     
     */
    public void setANXLongitude(LongitudeType value) {
        this.anxLongitude = value;
    }

    /**
     * Obtient la valeur de la propriété mlst.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMLST() {
        return mlst;
    }

    /**
     * Définit la valeur de la propriété mlst.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMLST(XMLGregorianCalendar value) {
        this.mlst = value;
    }

    /**
     * Obtient la valeur de la propriété mlstDrift.
     * 
     * @return
     *     possible object is
     *     {@link MLSTDriftType }
     *     
     */
    public MLSTDriftType getMLSTDrift() {
        return mlstDrift;
    }

    /**
     * Définit la valeur de la propriété mlstDrift.
     * 
     * @param value
     *     allowed object is
     *     {@link MLSTDriftType }
     *     
     */
    public void setMLSTDrift(MLSTDriftType value) {
        this.mlstDrift = value;
    }

    /**
     * Obtient la valeur de la propriété mlstNonlinearDrift.
     * 
     * @return
     *     possible object is
     *     {@link MLSTNonlinearDriftType }
     *     
     */
    public MLSTNonlinearDriftType getMLSTNonlinearDrift() {
        return mlstNonlinearDrift;
    }

    /**
     * Définit la valeur de la propriété mlstNonlinearDrift.
     * 
     * @param value
     *     allowed object is
     *     {@link MLSTNonlinearDriftType }
     *     
     */
    public void setMLSTNonlinearDrift(MLSTNonlinearDriftType value) {
        this.mlstNonlinearDrift = value;
    }

}
