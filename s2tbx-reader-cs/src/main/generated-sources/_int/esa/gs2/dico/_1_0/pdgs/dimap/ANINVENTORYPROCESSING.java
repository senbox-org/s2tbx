//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour AN_INVENTORY_PROCESSING complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_INVENTORY_PROCESSING">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Processing_Step_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_STEP_LIST" minOccurs="0"/>
 *         &lt;element name="Inventory_GIPP_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GIPP_LIST"/>
 *         &lt;element name="INVENTORY_DEM_TYPE">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
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
@XmlType(name = "AN_INVENTORY_PROCESSING", propOrder = {
    "processingStepList",
    "inventoryGIPPList",
    "inventorydemtype"
})
public class ANINVENTORYPROCESSING {

    @XmlElement(name = "Processing_Step_List")
    protected APROCESSINGSTEPLIST processingStepList;
    @XmlElement(name = "Inventory_GIPP_List", required = true)
    protected AGIPPLIST inventoryGIPPList;
    @XmlElement(name = "INVENTORY_DEM_TYPE", required = true)
    protected ANINVENTORYPROCESSING.INVENTORYDEMTYPE inventorydemtype;

    /**
     * Obtient la valeur de la propriété processingStepList.
     * 
     * @return
     *     possible object is
     *     {@link APROCESSINGSTEPLIST }
     *     
     */
    public APROCESSINGSTEPLIST getProcessingStepList() {
        return processingStepList;
    }

    /**
     * Définit la valeur de la propriété processingStepList.
     * 
     * @param value
     *     allowed object is
     *     {@link APROCESSINGSTEPLIST }
     *     
     */
    public void setProcessingStepList(APROCESSINGSTEPLIST value) {
        this.processingStepList = value;
    }

    /**
     * Obtient la valeur de la propriété inventoryGIPPList.
     * 
     * @return
     *     possible object is
     *     {@link AGIPPLIST }
     *     
     */
    public AGIPPLIST getInventoryGIPPList() {
        return inventoryGIPPList;
    }

    /**
     * Définit la valeur de la propriété inventoryGIPPList.
     * 
     * @param value
     *     allowed object is
     *     {@link AGIPPLIST }
     *     
     */
    public void setInventoryGIPPList(AGIPPLIST value) {
        this.inventoryGIPPList = value;
    }

    /**
     * Obtient la valeur de la propriété inventorydemtype.
     * 
     * @return
     *     possible object is
     *     {@link ANINVENTORYPROCESSING.INVENTORYDEMTYPE }
     *     
     */
    public ANINVENTORYPROCESSING.INVENTORYDEMTYPE getINVENTORYDEMTYPE() {
        return inventorydemtype;
    }

    /**
     * Définit la valeur de la propriété inventorydemtype.
     * 
     * @param value
     *     allowed object is
     *     {@link ANINVENTORYPROCESSING.INVENTORYDEMTYPE }
     *     
     */
    public void setINVENTORYDEMTYPE(ANINVENTORYPROCESSING.INVENTORYDEMTYPE value) {
        this.inventorydemtype = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class INVENTORYDEMTYPE {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "version", required = true)
        protected String version;

        /**
         * Obtient la valeur de la propriété value.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété version.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersion() {
            return version;
        }

        /**
         * Définit la valeur de la propriété version.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersion(String value) {
            this.version = value;
        }

    }

}
