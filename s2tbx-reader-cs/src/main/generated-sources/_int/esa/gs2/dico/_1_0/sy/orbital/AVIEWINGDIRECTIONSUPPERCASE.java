//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_VIEWING_DIRECTIONS_UPPER_CASE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_VIEWING_DIRECTIONS_UPPER_CASE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NB_OF_PIXELS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TAN_PSI_X_LIST" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
 *         &lt;element name="TAN_PSI_Y_LIST" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_VIEWING_DIRECTIONS_UPPER_CASE", propOrder = {
    "nbofpixels",
    "tanpsixlist",
    "tanpsiylist"
})
public class AVIEWINGDIRECTIONSUPPERCASE {

    @XmlElement(name = "NB_OF_PIXELS")
    protected int nbofpixels;
    @XmlList
    @XmlElement(name = "TAN_PSI_X_LIST", type = Double.class)
    protected List<Double> tanpsixlist;
    @XmlList
    @XmlElement(name = "TAN_PSI_Y_LIST", type = Double.class)
    protected List<Double> tanpsiylist;

    /**
     * Obtient la valeur de la propriété nbofpixels.
     * 
     */
    public int getNBOFPIXELS() {
        return nbofpixels;
    }

    /**
     * Définit la valeur de la propriété nbofpixels.
     * 
     */
    public void setNBOFPIXELS(int value) {
        this.nbofpixels = value;
    }

    /**
     * Gets the value of the tanpsixlist property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tanpsixlist property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTANPSIXLIST().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getTANPSIXLIST() {
        if (tanpsixlist == null) {
            tanpsixlist = new ArrayList<Double>();
        }
        return this.tanpsixlist;
    }

    /**
     * Gets the value of the tanpsiylist property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tanpsiylist property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTANPSIYLIST().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getTANPSIYLIST() {
        if (tanpsiylist == null) {
            tanpsiylist = new ArrayList<Double>();
        }
        return this.tanpsiylist;
    }

}
