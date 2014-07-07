//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Orbit_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Orbit_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Absolute_Orbit" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}PositiveInteger_Type"/>
 *         &lt;element name="Relative_Orbit" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}PositiveInteger_Type"/>
 *         &lt;element name="Cycle_Number" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}PositiveInteger_Type"/>
 *         &lt;element name="Phase_Number" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}PositiveInteger_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Orbit_Type", propOrder = {
    "absoluteOrbit",
    "relativeOrbit",
    "cycleNumber",
    "phaseNumber"
})
public class OrbitType {

    @XmlElement(name = "Absolute_Orbit", required = true)
    protected BigInteger absoluteOrbit;
    @XmlElement(name = "Relative_Orbit", required = true)
    protected BigInteger relativeOrbit;
    @XmlElement(name = "Cycle_Number", required = true)
    protected BigInteger cycleNumber;
    @XmlElement(name = "Phase_Number", required = true)
    protected BigInteger phaseNumber;

    /**
     * Obtient la valeur de la propriété absoluteOrbit.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAbsoluteOrbit() {
        return absoluteOrbit;
    }

    /**
     * Définit la valeur de la propriété absoluteOrbit.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAbsoluteOrbit(BigInteger value) {
        this.absoluteOrbit = value;
    }

    /**
     * Obtient la valeur de la propriété relativeOrbit.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRelativeOrbit() {
        return relativeOrbit;
    }

    /**
     * Définit la valeur de la propriété relativeOrbit.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRelativeOrbit(BigInteger value) {
        this.relativeOrbit = value;
    }

    /**
     * Obtient la valeur de la propriété cycleNumber.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCycleNumber() {
        return cycleNumber;
    }

    /**
     * Définit la valeur de la propriété cycleNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCycleNumber(BigInteger value) {
        this.cycleNumber = value;
    }

    /**
     * Obtient la valeur de la propriété phaseNumber.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPhaseNumber() {
        return phaseNumber;
    }

    /**
     * Définit la valeur de la propriété phaseNumber.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPhaseNumber(BigInteger value) {
        this.phaseNumber = value;
    }

}
