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
 * <p>Classe Java pour A_VIEWING_DIRECTIONS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_VIEWING_DIRECTIONS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nb_Of_Pixels">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Tan_Psi_X_List" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
 *         &lt;element name="Tan_Psi_Y_List" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_VIEWING_DIRECTIONS", propOrder = {
    "nbOfPixels",
    "tanPsiXList",
    "tanPsiYList"
})
public class AVIEWINGDIRECTIONS {

    @XmlElement(name = "Nb_Of_Pixels")
    protected int nbOfPixels;
    @XmlList
    @XmlElement(name = "Tan_Psi_X_List", type = Double.class)
    protected List<Double> tanPsiXList;
    @XmlList
    @XmlElement(name = "Tan_Psi_Y_List", type = Double.class)
    protected List<Double> tanPsiYList;

    /**
     * Obtient la valeur de la propriété nbOfPixels.
     * 
     */
    public int getNbOfPixels() {
        return nbOfPixels;
    }

    /**
     * Définit la valeur de la propriété nbOfPixels.
     * 
     */
    public void setNbOfPixels(int value) {
        this.nbOfPixels = value;
    }

    /**
     * Gets the value of the tanPsiXList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tanPsiXList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTanPsiXList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getTanPsiXList() {
        if (tanPsiXList == null) {
            tanPsiXList = new ArrayList<Double>();
        }
        return this.tanPsiXList;
    }

    /**
     * Gets the value of the tanPsiYList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tanPsiYList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTanPsiYList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getTanPsiYList() {
        if (tanPsiYList == null) {
            tanPsiYList = new ArrayList<Double>();
        }
        return this.tanPsiYList;
    }

}
