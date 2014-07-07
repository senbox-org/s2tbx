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


/**
 * <p>Classe Java pour Orbit_Change_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Orbit_Change_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Orbit" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Orbit_Type"/>
 *         &lt;element name="Cycle" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Cycle_Type"/>
 *         &lt;element name="Time_of_ANX" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Time_of_ANX_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Orbit_Change_Type", propOrder = {
    "orbit",
    "cycle",
    "timeOfANX"
})
public class OrbitChangeType {

    @XmlElement(name = "Orbit", required = true)
    protected OrbitType orbit;
    @XmlElement(name = "Cycle", required = true)
    protected CycleType cycle;
    @XmlElement(name = "Time_of_ANX", required = true)
    protected TimeOfANXType timeOfANX;

    /**
     * Obtient la valeur de la propriété orbit.
     * 
     * @return
     *     possible object is
     *     {@link OrbitType }
     *     
     */
    public OrbitType getOrbit() {
        return orbit;
    }

    /**
     * Définit la valeur de la propriété orbit.
     * 
     * @param value
     *     allowed object is
     *     {@link OrbitType }
     *     
     */
    public void setOrbit(OrbitType value) {
        this.orbit = value;
    }

    /**
     * Obtient la valeur de la propriété cycle.
     * 
     * @return
     *     possible object is
     *     {@link CycleType }
     *     
     */
    public CycleType getCycle() {
        return cycle;
    }

    /**
     * Définit la valeur de la propriété cycle.
     * 
     * @param value
     *     allowed object is
     *     {@link CycleType }
     *     
     */
    public void setCycle(CycleType value) {
        this.cycle = value;
    }

    /**
     * Obtient la valeur de la propriété timeOfANX.
     * 
     * @return
     *     possible object is
     *     {@link TimeOfANXType }
     *     
     */
    public TimeOfANXType getTimeOfANX() {
        return timeOfANX;
    }

    /**
     * Définit la valeur de la propriété timeOfANX.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeOfANXType }
     *     
     */
    public void setTimeOfANX(TimeOfANXType value) {
        this.timeOfANX = value;
    }

}
