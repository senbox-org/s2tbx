//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_LOCAL_RESIDUAL_LIST complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_LOCAL_RESIDUAL_LIST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Local_Residual" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_LOCAL_RESIDUAL" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_LOCAL_RESIDUAL_LIST", propOrder = {
    "localResidual"
})
public class ALOCALRESIDUALLIST {

    @XmlElement(name = "Local_Residual", required = true)
    protected List<ALOCALRESIDUAL> localResidual;

    /**
     * Gets the value of the localResidual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localResidual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalResidual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ALOCALRESIDUAL }
     * 
     * 
     */
    public List<ALOCALRESIDUAL> getLocalResidual() {
        if (localResidual == null) {
            localResidual = new ArrayList<ALOCALRESIDUAL>();
        }
        return this.localResidual;
    }

}
