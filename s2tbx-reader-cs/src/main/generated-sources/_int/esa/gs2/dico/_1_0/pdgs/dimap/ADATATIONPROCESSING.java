//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_DATATION_PROCESSING complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATATION_PROCESSING">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Processing_Step_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PROCESSING_STEP_LIST"/>
 *         &lt;element name="Inventory_GIPP_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GIPP_LIST"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATATION_PROCESSING", propOrder = {
    "processingStepList",
    "inventoryGIPPList"
})
public class ADATATIONPROCESSING {

    @XmlElement(name = "Processing_Step_List", required = true)
    protected APROCESSINGSTEPLIST processingStepList;
    @XmlElement(name = "Inventory_GIPP_List", required = true)
    protected AGIPPLIST inventoryGIPPList;

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

}
