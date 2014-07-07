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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_DATASTRIP_REPORT_LIST complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATASTRIP_REPORT_LIST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="REPORT_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="datastripId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATASTRIP_REPORT_LIST", propOrder = {
    "reportfilename"
})
public class ADATASTRIPREPORTLIST {

    @XmlElement(name = "REPORT_FILENAME", required = true)
    protected List<String> reportfilename;
    @XmlAttribute(name = "datastripId", required = true)
    protected String datastripId;

    /**
     * Gets the value of the reportfilename property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reportfilename property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getREPORTFILENAME().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getREPORTFILENAME() {
        if (reportfilename == null) {
            reportfilename = new ArrayList<String>();
        }
        return this.reportfilename;
    }

    /**
     * Obtient la valeur de la propriété datastripId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatastripId() {
        return datastripId;
    }

    /**
     * Définit la valeur de la propriété datastripId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatastripId(String value) {
        this.datastripId = value;
    }

}
