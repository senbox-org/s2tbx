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
 * Indicators allowing to evaluate the success of the correlation
 * 
 * <p>Classe Java pour A_CORRELATION_QUALITY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_CORRELATION_QUALITY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Global_Residual" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RESIDUAL"/>
 *         &lt;element name="Local_Residual_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_LOCAL_RESIDUAL_LIST"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_CORRELATION_QUALITY", propOrder = {
    "globalResidual",
    "localResidualList"
})
public class ACORRELATIONQUALITY {

    @XmlElement(name = "Global_Residual", required = true)
    protected ARESIDUAL globalResidual;
    @XmlElement(name = "Local_Residual_List", required = true)
    protected ALOCALRESIDUALLIST localResidualList;

    /**
     * Obtient la valeur de la propriété globalResidual.
     * 
     * @return
     *     possible object is
     *     {@link ARESIDUAL }
     *     
     */
    public ARESIDUAL getGlobalResidual() {
        return globalResidual;
    }

    /**
     * Définit la valeur de la propriété globalResidual.
     * 
     * @param value
     *     allowed object is
     *     {@link ARESIDUAL }
     *     
     */
    public void setGlobalResidual(ARESIDUAL value) {
        this.globalResidual = value;
    }

    /**
     * Obtient la valeur de la propriété localResidualList.
     * 
     * @return
     *     possible object is
     *     {@link ALOCALRESIDUALLIST }
     *     
     */
    public ALOCALRESIDUALLIST getLocalResidualList() {
        return localResidualList;
    }

    /**
     * Définit la valeur de la propriété localResidualList.
     * 
     * @param value
     *     allowed object is
     *     {@link ALOCALRESIDUALLIST }
     *     
     */
    public void setLocalResidualList(ALOCALRESIDUALLIST value) {
        this.localResidualList = value;
    }

}
