//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.25 at 04:18:07 PM CST 
//


package com.cisex.tims.namespace;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.cisex.tims.namespace package.
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

    private final static QName _ID_QNAME = new QName("http://tims.cisex.com/namespace", "ID");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.cisex.tims.namespace
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims }
     * 
     */
    public Tims createTims() {
        return new Tims();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport }
     * 
     */
    public Tims.ResultsSummaryReport createTimsResultsSummaryReport() {
        return new Tims.ResultsSummaryReport();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.ResultSummary }
     * 
     */
    public Tims.ResultsSummaryReport.ResultSummary createTimsResultsSummaryReportResultSummary() {
        return new Tims.ResultsSummaryReport.ResultSummary();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.Specifications }
     * 
     */
    public Tims.ResultsSummaryReport.Specifications createTimsResultsSummaryReportSpecifications() {
        return new Tims.ResultsSummaryReport.Specifications();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.Specifications.ColumnSpecification }
     * 
     */
    public Tims.ResultsSummaryReport.Specifications.ColumnSpecification createTimsResultsSummaryReportSpecificationsColumnSpecification() {
        return new Tims.ResultsSummaryReport.Specifications.ColumnSpecification();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.Specifications.ReportingConfigs }
     * 
     */
    public Tims.ResultsSummaryReport.Specifications.ReportingConfigs createTimsResultsSummaryReportSpecificationsReportingConfigs() {
        return new Tims.ResultsSummaryReport.Specifications.ReportingConfigs();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.NewDataSet }
     * 
     */
    public NewDataSet createNewDataSet() {
        return new NewDataSet();
    }

    /**
     * Create an instance of {@link ID }
     * 
     */
    public ID createID() {
        return new ID();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.DatabaseID }
     * 
     */
    public Tims.DatabaseID createTimsDatabaseID() {
        return new Tims.DatabaseID();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ProjectID }
     * 
     */
    public Tims.ProjectID createTimsProjectID() {
        return new Tims.ProjectID();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.Owner }
     * 
     */
    public Tims.ResultsSummaryReport.Owner createTimsResultsSummaryReportOwner() {
        return new Tims.ResultsSummaryReport.Owner();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.ResultSummary.ResultTicker }
     * 
     */
    public Tims.ResultsSummaryReport.ResultSummary.ResultTicker createTimsResultsSummaryReportResultSummaryResultTicker() {
        return new Tims.ResultsSummaryReport.ResultSummary.ResultTicker();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.Specifications.StartingContext }
     * 
     */
    public Tims.ResultsSummaryReport.Specifications.StartingContext createTimsResultsSummaryReportSpecificationsStartingContext() {
        return new Tims.ResultsSummaryReport.Specifications.StartingContext();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.Specifications.ColumnSpecification.Column }
     * 
     */
    public Tims.ResultsSummaryReport.Specifications.ColumnSpecification.Column createTimsResultsSummaryReportSpecificationsColumnSpecificationColumn() {
        return new Tims.ResultsSummaryReport.Specifications.ColumnSpecification.Column();
    }

    /**
     * Create an instance of {@link com.cisex.tims.namespace.Tims.ResultsSummaryReport.Specifications.ReportingConfigs.ConfigID }
     * 
     */
    public Tims.ResultsSummaryReport.Specifications.ReportingConfigs.ConfigID createTimsResultsSummaryReportSpecificationsReportingConfigsConfigID() {
        return new Tims.ResultsSummaryReport.Specifications.ReportingConfigs.ConfigID();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link ID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tims.cisex.com/namespace", name = "ID")
    public JAXBElement<ID> createID(ID value) {
        return new JAXBElement<ID>(_ID_QNAME, ID.class, null, value);
    }

}
