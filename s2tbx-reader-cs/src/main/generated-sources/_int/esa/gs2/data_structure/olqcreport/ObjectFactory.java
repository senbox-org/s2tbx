//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.data_structure.olqcreport;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.gs2.data_structure.olqcreport package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Report_QNAME = new QName("http://gs2.esa.int/DATA_STRUCTURE/olqcReport", "report");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.gs2.data_structure.olqcreport
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CheckListType }
     * 
     */
    public CheckListType createCheckListType() {
        return new CheckListType();
    }

    /**
     * Create an instance of {@link CheckListType.Check }
     * 
     */
    public CheckListType.Check createCheckListTypeCheck() {
        return new CheckListType.Check();
    }

    /**
     * Create an instance of {@link CheckListType.Check.ExtraValues }
     * 
     */
    public CheckListType.Check.ExtraValues createCheckListTypeCheckExtraValues() {
        return new CheckListType.Check.ExtraValues();
    }

    /**
     * Create an instance of {@link EarthExplorerHeaderType }
     * 
     */
    public EarthExplorerHeaderType createEarthExplorerHeaderType() {
        return new EarthExplorerHeaderType();
    }

    /**
     * Create an instance of {@link EarthExplorerHeaderType.FixedHeader }
     * 
     */
    public EarthExplorerHeaderType.FixedHeader createEarthExplorerHeaderTypeFixedHeader() {
        return new EarthExplorerHeaderType.FixedHeader();
    }

    /**
     * Create an instance of {@link EarthExplorerFile }
     * 
     */
    public EarthExplorerFile createEarthExplorerFile() {
        return new EarthExplorerFile();
    }

    /**
     * Create an instance of {@link EarthExplorerFileType }
     * 
     */
    public EarthExplorerFileType createEarthExplorerFileType() {
        return new EarthExplorerFileType();
    }

    /**
     * Create an instance of {@link EarthExplorerDataBlockType }
     * 
     */
    public EarthExplorerDataBlockType createEarthExplorerDataBlockType() {
        return new EarthExplorerDataBlockType();
    }

    /**
     * Create an instance of {@link ReportType }
     * 
     */
    public ReportType createReportType() {
        return new ReportType();
    }

    /**
     * Create an instance of {@link InspectionType }
     * 
     */
    public InspectionType createInspectionType() {
        return new InspectionType();
    }

    /**
     * Create an instance of {@link ItemType }
     * 
     */
    public ItemType createItemType() {
        return new ItemType();
    }

    /**
     * Create an instance of {@link CheckListType.Check.Message }
     * 
     */
    public CheckListType.Check.Message createCheckListTypeCheckMessage() {
        return new CheckListType.Check.Message();
    }

    /**
     * Create an instance of {@link CheckListType.Check.ExtraValues.Value }
     * 
     */
    public CheckListType.Check.ExtraValues.Value createCheckListTypeCheckExtraValuesValue() {
        return new CheckListType.Check.ExtraValues.Value();
    }

    /**
     * Create an instance of {@link EarthExplorerHeaderType.FixedHeader.ValidityPeriod }
     * 
     */
    public EarthExplorerHeaderType.FixedHeader.ValidityPeriod createEarthExplorerHeaderTypeFixedHeaderValidityPeriod() {
        return new EarthExplorerHeaderType.FixedHeader.ValidityPeriod();
    }

    /**
     * Create an instance of {@link EarthExplorerHeaderType.FixedHeader.Source }
     * 
     */
    public EarthExplorerHeaderType.FixedHeader.Source createEarthExplorerHeaderTypeFixedHeaderSource() {
        return new EarthExplorerHeaderType.FixedHeader.Source();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReportType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gs2.esa.int/DATA_STRUCTURE/olqcReport", name = "report")
    public JAXBElement<ReportType> createReport(ReportType value) {
        return new JAXBElement<ReportType>(_Report_QNAME, ReportType.class, null, value);
    }

}
