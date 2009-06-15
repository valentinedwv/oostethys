<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:om="http://www.opengis.net/om/1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:swe="http://www.opengis.net/swe/1.0.1"
    xmlns:oost="http://www.oostethys.org/schemas/0.1.0/oostethys">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/oost:oostethys">
        <om:ObservationCollection
            xsi:schemaLocation="http://www.opengis.net/om/1.0  http://schemas.opengis.net/om/1.0.0/om.xsd
    http://www.opengis.net/swe/1.0.1 http://schemas.opengis.net/sweCommon/1.0.1/swe.xsd">
     
                    <xsl:for-each-group
                        select="//oost:system"
                        group-by="oost:metadata/oost:systemIdentifier" >                   
                        <xsl:if test="exists(oost:output)">
                            <xsl:call-template name="member"/>
                        </xsl:if>
                    </xsl:for-each-group>
        </om:ObservationCollection>
    </xsl:template>
    
    <xsl:template name="member">
        <!-- should match the offering allowed values -->
        <om:member>
            <xsl:attribute name="xlink:href">
                <xsl:value-of select="oost:metadata/oost:systemIdentifier"/>
            </xsl:attribute>
            <om:Observation>
                <!-- should match the ObservationOffering gml:id -->
                
                <xsl:attribute name="gml:id">
                    <xsl:value-of select="concat('observationOffering_',oost:metadata/oost:systemShortName)"/>
                    
                </xsl:attribute>
                
                <gml:boundedBy>
                    <gml:Envelope srsName="urn:ogc:def:crs:EPSG:6.5:4326">
                        <gml:lowerCorner>
                            <xsl:value-of
                                select="oost:extend/oost:boundingBox/oost:envelope/oost:lowerCorner"/>
                        </gml:lowerCorner>
                        <gml:upperCorner>
                            <xsl:value-of
                                select="oost:extend/oost:boundingBox/oost:envelope/oost:upperCorner"/>
                        </gml:upperCorner>
                    </gml:Envelope>
                </gml:boundedBy>
                
                <om:samplingTime>
                    
                    <gml:TimePeriod>
                        <xsl:attribute name="gml:id">
                            <xsl:value-of select="concat('timePeriod',position())"></xsl:value-of>
                        </xsl:attribute>
                        
                        <gml:beginPosition>
                            <xsl:value-of select="oost:extend/oost:timePeriod/oost:start"/>
                        </gml:beginPosition>
                        <gml:endPosition>
                            <xsl:value-of select="oost:extend/oost:timePeriod/oost:end"/>
                        </gml:endPosition>
                    </gml:TimePeriod>
                </om:samplingTime>
                
                <om:procedure>
                    <xsl:attribute name="xlink:href">
                        <xsl:value-of select="oost:metadata/oost:systemIdentifier"/>
                    </xsl:attribute>
                </om:procedure>
                <om:observedProperty>
                    <swe:CompositePhenomenon>
                        <xsl:attribute name="dimension">
                            <xsl:value-of
                                select="count(oost:output/oost:variables/oost:variable/@isCoordinate='false')"/>
                        </xsl:attribute>
                        <xsl:attribute name="gml:id">
                            
                            <xsl:value-of select="concat('compositePhenomena_',position())"></xsl:value-of>
                            
                        </xsl:attribute>
                        
                        <gml:name>
                            <xsl:text>Phenomenon measured by </xsl:text>
                            <xsl:value-of select="oost:sytemLongName"/>
                        </gml:name>
                        
                        <xsl:for-each select="oost:output/oost:variables/oost:variable">
                            <xsl:if test="@isCoordinate!='true' or not(exists(@isCoordinate))">
                                <swe:component>
                                    <xsl:attribute name="xlink:href">
                                        <xsl:value-of select="@uri"/>
                                    </xsl:attribute>
                                </swe:component>
                            </xsl:if>
                        </xsl:for-each>
                        
                    </swe:CompositePhenomenon>
                </om:observedProperty>
                
                
                <!-- use earth realm -->
                
                
                
                <om:featureOfInterest
                    xlink:href="http://mmisw.org/mmi/20080516/system#EarthRealm"/>
                
                
                
                <om:result>
                    
                    
                    <swe:DataArray>
                        <swe:elementCount />
                        <swe:elementType name="SimpleDataArray">
                            <swe:DataRecord>
                                <xsl:for-each select="oost:output/oost:variables/oost:variable">
                                    <swe:field>
                                        <xsl:attribute name="name">
                                            <xsl:value-of select="@name" />
                                        </xsl:attribute>
                                        <xsl:if test="@isTime='false' or not(exists(@isTime))">
                                            <swe:Quantity>
                                                <xsl:attribute name="definition">
                                                    <xsl:value-of select="@uri" />
                                                </xsl:attribute>
                                                <xsl:if test="@referenceFrame">
                                                    <xsl:attribute name="referenceFrame">
                                                        <xsl:value-of select="@referenceFrame" />
                                                    </xsl:attribute>
                                                </xsl:if>
                                                <xsl:if test="not(empty(@uom)) and string-length(@uom)>0">
                                                    
                                                <swe:uom>
                                                    <xsl:attribute name="code">
                                                        <xsl:value-of select="@uom" />
                                                    </xsl:attribute>
                                                </swe:uom>
                                                 </xsl:if>
                                            </swe:Quantity>
                                        </xsl:if>
                                        <xsl:if test="@isTime='true'">
                                            
                                            <swe:Time definition="urn:ogc:phenomenon:time:iso8601" />
                                            
                                        </xsl:if>
                                    </swe:field>
                                </xsl:for-each>
                            </swe:DataRecord>
                        </swe:elementType>
                        <swe:encoding>
                            <swe:TextBlock tokenSeparator="," blockSeparator=" "
                                decimalSeparator="."/>
                        </swe:encoding>
                        <swe:values>
                            <xsl:value-of select="oost:output/oost:values" />
                        </swe:values>
                    </swe:DataArray>
                    
                    
                    
                </om:result>
            </om:Observation>
            
        </om:member>
        
    </xsl:template>

</xsl:stylesheet>
