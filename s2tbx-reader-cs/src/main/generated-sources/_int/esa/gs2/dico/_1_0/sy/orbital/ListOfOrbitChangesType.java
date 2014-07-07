//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour List_of_Orbit_Changes_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="List_of_Orbit_Changes_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Orbit_Change" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}Orbit_Change_Type" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="count" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}PositiveInteger_Type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "List_of_Orbit_Changes_Type", propOrder = {
    "orbitChange"
})
public class ListOfOrbitChangesType {

    @XmlElement(name = "Orbit_Change", required = true)
    protected List<OrbitChangeType> orbitChange;
    @XmlAttribute(name = "count", required = true)
    protected BigInteger count;

    /**
     * Gets the value of the orbitChange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orbitChange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrbitChange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrbitChangeType }
     * 
     * 
     */
    public List<OrbitChangeType> getOrbitChange() {
        if (orbitChange == null) {
            orbitChange = new ArrayList<OrbitChangeType>();
        }
        return this.orbitChange;
    }

    /**
     * Obtient la valeur de la propriété count.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Définit la valeur de la propriété count.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

}
