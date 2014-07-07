//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.data_structure.olqcreport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour ReportType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ReportType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="checkList" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CheckListType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="gippVersion" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="globalStatus" use="required" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}PassFailType" />
 *       &lt;attribute name="date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportType", propOrder = {
    "checkList"
})
public class ReportType {

    @XmlElement(required = true)
    protected CheckListType checkList;
    @XmlAttribute(name = "gippVersion", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String gippVersion;
    @XmlAttribute(name = "globalStatus", required = true)
    protected PassFailType globalStatus;
    @XmlAttribute(name = "date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;

    /**
     * Obtient la valeur de la propriété checkList.
     * 
     * @return
     *     possible object is
     *     {@link CheckListType }
     *     
     */
    public CheckListType getCheckList() {
        return checkList;
    }

    /**
     * Définit la valeur de la propriété checkList.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckListType }
     *     
     */
    public void setCheckList(CheckListType value) {
        this.checkList = value;
    }

    /**
     * Obtient la valeur de la propriété gippVersion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGippVersion() {
        return gippVersion;
    }

    /**
     * Définit la valeur de la propriété gippVersion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGippVersion(String value) {
        this.gippVersion = value;
    }

    /**
     * Obtient la valeur de la propriété globalStatus.
     * 
     * @return
     *     possible object is
     *     {@link PassFailType }
     *     
     */
    public PassFailType getGlobalStatus() {
        return globalStatus;
    }

    /**
     * Définit la valeur de la propriété globalStatus.
     * 
     * @param value
     *     allowed object is
     *     {@link PassFailType }
     *     
     */
    public void setGlobalStatus(PassFailType value) {
        this.globalStatus = value;
    }

    /**
     * Obtient la valeur de la propriété date.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Définit la valeur de la propriété date.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

}
