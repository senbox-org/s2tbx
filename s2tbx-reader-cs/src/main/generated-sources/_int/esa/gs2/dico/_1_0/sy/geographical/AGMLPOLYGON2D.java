//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.geographical;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * List of 2D vertices counter-clockwise oriented (for WFS compatibility). The polygon must be closed (the first and last vertices are the same).
 * 
 * <p>Classe Java pour A_GML_POLYGON_2D complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GML_POLYGON_2D">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EXT_POS_LIST" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
 *         &lt;element name="INT_POS_LIST" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GML_POLYGON_2D", propOrder = {
    "extposlist",
    "intposlist"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANINITLOCPRODPRODUCTFOOTPRINTSAFE.UnitaryFootprintList.UnitaryFootprint.Footprint.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANINITLOCINVPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint.Footprint.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANINITLOCPRODPRODUCTFOOTPRINT.ProductFootprint.UnitaryFootprintList.UnitaryFootprint.Footprint.class
})
public class AGMLPOLYGON2D {

    @XmlList
    @XmlElement(name = "EXT_POS_LIST", type = Double.class)
    protected List<Double> extposlist;
    @XmlElementRef(name = "INT_POS_LIST", type = JAXBElement.class)
    protected List<JAXBElement<List<Double>>> intposlist;

    /**
     * Gets the value of the extposlist property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extposlist property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEXTPOSLIST().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getEXTPOSLIST() {
        if (extposlist == null) {
            extposlist = new ArrayList<Double>();
        }
        return this.extposlist;
    }

    /**
     * Gets the value of the intposlist property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the intposlist property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getINTPOSLIST().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}
     * 
     * 
     */
    public List<JAXBElement<List<Double>>> getINTPOSLIST() {
        if (intposlist == null) {
            intposlist = new ArrayList<JAXBElement<List<Double>>>();
        }
        return this.intposlist;
    }

}
