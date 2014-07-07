//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHUNITATTR;


/**
 * <p>Classe Java pour AN_ANGLE_GRID complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ANGLE_GRID">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="COL_STEP" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="ROW_STEP" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="Values_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_FLOAT" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_ANGLE_GRID", propOrder = {
    "colstep",
    "rowstep",
    "valuesList"
})
public class ANANGLEGRID {

    @XmlElement(name = "COL_STEP", required = true)
    protected ADOUBLEWITHUNITATTR colstep;
    @XmlElement(name = "ROW_STEP", required = true)
    protected ADOUBLEWITHUNITATTR rowstep;
    @XmlElement(name = "Values_List", required = true)
    protected ANANGLEGRID.ValuesList valuesList;

    /**
     * Obtient la valeur de la propriété colstep.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getCOLSTEP() {
        return colstep;
    }

    /**
     * Définit la valeur de la propriété colstep.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setCOLSTEP(ADOUBLEWITHUNITATTR value) {
        this.colstep = value;
    }

    /**
     * Obtient la valeur de la propriété rowstep.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getROWSTEP() {
        return rowstep;
    }

    /**
     * Définit la valeur de la propriété rowstep.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setROWSTEP(ADOUBLEWITHUNITATTR value) {
        this.rowstep = value;
    }

    /**
     * Obtient la valeur de la propriété valuesList.
     * 
     * @return
     *     possible object is
     *     {@link ANANGLEGRID.ValuesList }
     *     
     */
    public ANANGLEGRID.ValuesList getValuesList() {
        return valuesList;
    }

    /**
     * Définit la valeur de la propriété valuesList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANGLEGRID.ValuesList }
     *     
     */
    public void setValuesList(ANANGLEGRID.ValuesList value) {
        this.valuesList = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_FLOAT" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "values"
    })
    public static class ValuesList {

        @XmlElementRef(name = "VALUES", type = JAXBElement.class)
        protected List<JAXBElement<List<Float>>> values;

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
         * {@link JAXBElement }{@code <}{@link List }{@code <}{@link Float }{@code >}{@code >}
         * 
         * 
         */
        public List<JAXBElement<List<Float>>> getVALUES() {
            if (values == null) {
                values = new ArrayList<JAXBElement<List<Float>>>();
            }
            return this.values;
        }

    }

}
