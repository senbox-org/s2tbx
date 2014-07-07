//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.data_structure.olqcreport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}EarthExplorerFileType">
 *       &lt;sequence>
 *         &lt;element name="Earth_Explorer_Header" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}EarthExplorerHeaderType"/>
 *         &lt;element name="Data_Block" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}EarthExplorerDataBlockType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "earthExplorerHeader",
    "dataBlock"
})
@XmlRootElement(name = "Earth_Explorer_File")
public class EarthExplorerFile
    extends EarthExplorerFileType
{

    @XmlElement(name = "Earth_Explorer_Header", required = true)
    protected EarthExplorerHeaderType earthExplorerHeader;
    @XmlElement(name = "Data_Block", required = true)
    protected EarthExplorerDataBlockType dataBlock;

    /**
     * Obtient la valeur de la propriété earthExplorerHeader.
     * 
     * @return
     *     possible object is
     *     {@link EarthExplorerHeaderType }
     *     
     */
    public EarthExplorerHeaderType getEarthExplorerHeader() {
        return earthExplorerHeader;
    }

    /**
     * Définit la valeur de la propriété earthExplorerHeader.
     * 
     * @param value
     *     allowed object is
     *     {@link EarthExplorerHeaderType }
     *     
     */
    public void setEarthExplorerHeader(EarthExplorerHeaderType value) {
        this.earthExplorerHeader = value;
    }

    /**
     * Obtient la valeur de la propriété dataBlock.
     * 
     * @return
     *     possible object is
     *     {@link EarthExplorerDataBlockType }
     *     
     */
    public EarthExplorerDataBlockType getDataBlock() {
        return dataBlock;
    }

    /**
     * Définit la valeur de la propriété dataBlock.
     * 
     * @param value
     *     allowed object is
     *     {@link EarthExplorerDataBlockType }
     *     
     */
    public void setDataBlock(EarthExplorerDataBlockType value) {
        this.dataBlock = value;
    }

}
