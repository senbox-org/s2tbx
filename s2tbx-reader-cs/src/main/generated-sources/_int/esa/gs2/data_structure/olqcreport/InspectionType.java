//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.data_structure.olqcreport;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour InspectionType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="InspectionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="creation" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="duration" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="execution" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="item" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="itemURL" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="priority" use="required" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *       &lt;attribute name="processingStatus" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="status" use="required" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}PassFailType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InspectionType")
public class InspectionType {

    @XmlAttribute(name = "creation", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creation;
    @XmlAttribute(name = "duration", required = true)
    protected BigDecimal duration;
    @XmlAttribute(name = "execution", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar execution;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "item", required = true)
    protected String item;
    @XmlAttribute(name = "itemURL", required = true)
    protected String itemURL;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "priority", required = true)
    protected byte priority;
    @XmlAttribute(name = "processingStatus", required = true)
    protected String processingStatus;
    @XmlAttribute(name = "status", required = true)
    protected PassFailType status;

    /**
     * Obtient la valeur de la propriété creation.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreation() {
        return creation;
    }

    /**
     * Définit la valeur de la propriété creation.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreation(XMLGregorianCalendar value) {
        this.creation = value;
    }

    /**
     * Obtient la valeur de la propriété duration.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDuration() {
        return duration;
    }

    /**
     * Définit la valeur de la propriété duration.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDuration(BigDecimal value) {
        this.duration = value;
    }

    /**
     * Obtient la valeur de la propriété execution.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExecution() {
        return execution;
    }

    /**
     * Définit la valeur de la propriété execution.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExecution(XMLGregorianCalendar value) {
        this.execution = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété item.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItem() {
        return item;
    }

    /**
     * Définit la valeur de la propriété item.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItem(String value) {
        this.item = value;
    }

    /**
     * Obtient la valeur de la propriété itemURL.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemURL() {
        return itemURL;
    }

    /**
     * Définit la valeur de la propriété itemURL.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemURL(String value) {
        this.itemURL = value;
    }

    /**
     * Obtient la valeur de la propriété name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété priority.
     * 
     */
    public byte getPriority() {
        return priority;
    }

    /**
     * Définit la valeur de la propriété priority.
     * 
     */
    public void setPriority(byte value) {
        this.priority = value;
    }

    /**
     * Obtient la valeur de la propriété processingStatus.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessingStatus() {
        return processingStatus;
    }

    /**
     * Définit la valeur de la propriété processingStatus.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessingStatus(String value) {
        this.processingStatus = value;
    }

    /**
     * Obtient la valeur de la propriété status.
     * 
     * @return
     *     possible object is
     *     {@link PassFailType }
     *     
     */
    public PassFailType getStatus() {
        return status;
    }

    /**
     * Définit la valeur de la propriété status.
     * 
     * @param value
     *     allowed object is
     *     {@link PassFailType }
     *     
     */
    public void setStatus(PassFailType value) {
        this.status = value;
    }

}
