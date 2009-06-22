<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1"
    xmlns:sos="http://www.opengis.net/sos/1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:oost="http://www.oostethys.org/schemas/0.1.0/oostethys">
    <xsl:output method="xml" indent="yes"/>


    <xsl:template match="/oost:oostethys">

        <sos:Capabilities
            xsi:schemaLocation="http://www.opengis.net/sos/1.0 http://schemas.opengis.net/sos/1.0.0/sosGetCapabilities.xsd"
            version="1.0.0">
            
            <ows:ServiceIdentification>
                <ows:Title>Sensor Observation Service (SOS) for <xsl:value-of select="/oost:oostethys/oost:serviceContact/oost:longNameOrganization"/>
                </ows:Title>
                <ows:Abstract>Sensor Observation Service (SOS) for <xsl:value-of
                    select="/oost:oostethys/oost:serviceContact/oost:longNameOrganization"/>
                </ows:Abstract>
                <ows:ServiceType codeSpace="http://opengeospatial.net">OGC:SOS</ows:ServiceType>
                <ows:ServiceTypeVersion>1.0.0</ows:ServiceTypeVersion>
                
            </ows:ServiceIdentification>
            <xsl:apply-templates select="oost:serviceContact"></xsl:apply-templates>
            
            <ows:OperationsMetadata>
                <ows:Operation name="GetCapabilities">
                    <ows:DCP>
                        <ows:HTTP>
                            <ows:Get>
                                <xsl:attribute name="xlink:href">
                                    <xsl:value-of select="oost:webServerURL"></xsl:value-of>
                                </xsl:attribute>
                            </ows:Get>
                            
                            <ows:Post>
                                <xsl:attribute name="xlink:href">
                                    <xsl:value-of select="oost:webServerURL"></xsl:value-of>
                                </xsl:attribute>
                            </ows:Post>
                            
                        </ows:HTTP>
                    </ows:DCP>
                    <ows:Parameter name="service">
                        <ows:AllowedValues>
                            <ows:Value>SOS</ows:Value>
                        </ows:AllowedValues>
                    </ows:Parameter>
                    <ows:Parameter name="version">
                        <ows:AllowedValues>
                            <ows:Value>1.0.0</ows:Value>
                        </ows:AllowedValues>
                    </ows:Parameter>
                </ows:Operation>
                <ows:Operation name="GetObservation">
                    <ows:DCP>
                        <ows:HTTP>
                            <ows:Get>
                                <xsl:attribute name="xlink:href">
                                    <xsl:value-of select="oost:webServerURL"></xsl:value-of>
                                </xsl:attribute>
                            </ows:Get>
                            
                            <ows:Post>
                                <xsl:attribute name="xlink:href">
                                    <xsl:value-of select="oost:webServerURL"></xsl:value-of>
                                </xsl:attribute>
                            </ows:Post>
                            
                        </ows:HTTP>
                    </ows:DCP>
                    <ows:Parameter name="offering">
                        <ows:AllowedValues>
                            
                            <xsl:for-each-group
                                select="oost:components//oost:system" group-by="oost:metadata/oost:systemIdentifier">
                                <xsl:if test="exists(oost:output)">
                                    <ows:Value>
                                        <xsl:value-of select="concat('observationOffering',position())"/>
                                    </ows:Value>
                                    </xsl:if>
                                </xsl:for-each-group>
                   
                        
                        </ows:AllowedValues>
                    </ows:Parameter>
                    <ows:Parameter name="observedProperty">
                        <ows:AllowedValues>
                            
                            <xsl:for-each-group
                                select="oost:components//oost:variable"
                                group-by="@uri">
                                
                                <xsl:if test="not(exists(@isCoordinate)) or @isCoordinate!='true'">
                                    <ows:Value>
                                        <xsl:value-of select="@uri"/>
                                    </ows:Value>
                                </xsl:if>
                            </xsl:for-each-group>
                        </ows:AllowedValues>
                    </ows:Parameter>
                </ows:Operation>
                <ows:Operation name="DescribeSensor">
                    <ows:DCP>
                       
                        <ows:HTTP>
                            <ows:Get>
                                <xsl:attribute name="xlink:href">
                                    <xsl:value-of select="normalize-space(oost:webServerURL)"></xsl:value-of>
                                </xsl:attribute>
                            </ows:Get>
                            
                            <ows:Post>
                                <xsl:attribute name="xlink:href">
                                    <xsl:value-of select="normalize-space(oost:webServerURL)"></xsl:value-of>
                                </xsl:attribute>
                            </ows:Post>
                          
                        </ows:HTTP>
                    </ows:DCP>
		    
<ows:Parameter name="version">
  <ows:AllowedValues>
    <ows:Value>1.0.0</ows:Value>
  </ows:AllowedValues>
</ows:Parameter>
<ows:Parameter name="service">
  <ows:AllowedValues>
    <ows:Value>SOS</ows:Value>
  </ows:AllowedValues>
</ows:Parameter>
<ows:Parameter name="outputFormat">
  <ows:AllowedValues>
    <ows:Value>text/xml;subtype="sensorML/1.0.1"</ows:Value>
  </ows:AllowedValues>
</ows:Parameter>
		    
                    <ows:Parameter name="procedure">
                       
                        <ows:AllowedValues>
                            
                         
                            <xsl:for-each-group
                                select="oost:components//oost:system" group-by="oost:metadata/oost:systemIdentifier">
                              
                                    <ows:Value>
                                        <xsl:value-of select="oost:metadata/oost:systemIdentifier"/>
                                    </ows:Value>
                              
                            </xsl:for-each-group>
                          
                          
                        </ows:AllowedValues>
                        
                    </ows:Parameter>
                </ows:Operation>
            </ows:OperationsMetadata>
            
            <xsl:call-template name="content"/>

        </sos:Capabilities>
    </xsl:template>
    
    <xsl:template match="oost:serviceContact">
        <xsl:if test="@type='http://mmisw.org/mmi/20080520/obs.owl#serviceProvider'">
            
            <ows:ServiceProvider>
                <ows:ProviderName>
                    <xsl:value-of select="oost:longNameOrganization"/>
                </ows:ProviderName>
                <xsl:element name="ows:ProviderSite">
                    <xsl:attribute name="xlink:href">
                        <xsl:value-of select="normalize-space(oost:urlOrganization)"/>
                    </xsl:attribute>
                </xsl:element>
                
                <ows:ServiceContact>
                    <ows:IndividualName>
                        <xsl:value-of select="oost:individualName"/>
                    </ows:IndividualName>
                    <ows:ContactInfo>
                        <ows:Address>
                            <ows:ElectronicMailAddress>
                                <xsl:value-of select="oost:individualEmail"/>
                            </ows:ElectronicMailAddress>
                        </ows:Address>
                    </ows:ContactInfo>
                </ows:ServiceContact>
            </ows:ServiceProvider>
        </xsl:if>
        
    </xsl:template>
    
      
    <xsl:template name="content">
        <sos:Contents>
            
          
            <sos:ObservationOfferingList>
             
               
                <!--  check systems at level 1 -->
                <xsl:for-each-group
                    select=".//oost:system"
                    group-by="oost:metadata/oost:systemIdentifier" >
                    <xsl:if test="exists(oost:output)">
                        <xsl:call-template name="observationOffering">
                            <xsl:with-param name="id">
                                <xsl:value-of select="oost:metadata/oost:systemShortName"></xsl:value-of>
                            </xsl:with-param>
                        </xsl:call-template>
                        
                    </xsl:if>
                   
                  
                </xsl:for-each-group>
                
               

            </sos:ObservationOfferingList>
        </sos:Contents>
    </xsl:template>
    
    <xsl:template name="observationOffering">
        <xsl:param name="id"/>
        
        <sos:ObservationOffering>
            
            <xsl:attribute name="gml:id">
                
                <xsl:value-of select="concat('observationOffering_',$id)"/>
               
              
            </xsl:attribute>
            <gml:description/>
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
            
            <sos:time>
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
            </sos:time>
            
            <sos:procedure>
                <xsl:attribute name="xlink:href">
                    <ows:Value>
                        <xsl:value-of select="normalize-space(oost:metadata/oost:systemIdentifier)"/>
                    </ows:Value>
                </xsl:attribute>
            </sos:procedure>
            
            
            
            <xsl:for-each select="oost:output/oost:variables/oost:variable">
                <xsl:if test="not(exists(@isCoordinate)) or @isCoordinate!='true'">
                    <sos:observedProperty>
                        <xsl:attribute name="xlink:href">
                            <xsl:value-of select="@uri"/>
                        </xsl:attribute>
                    </sos:observedProperty>
                </xsl:if>
            </xsl:for-each>
            
            <sos:featureOfInterest xlink:href="http://mmisw.org/mmi/20080516/system#EarthRealm"/>
            <sos:responseFormat>text/xml; subtype="om/1.0.0"</sos:responseFormat>
            <sos:responseMode>inline</sos:responseMode>
            
        </sos:ObservationOffering>
        
    </xsl:template>


</xsl:stylesheet>
