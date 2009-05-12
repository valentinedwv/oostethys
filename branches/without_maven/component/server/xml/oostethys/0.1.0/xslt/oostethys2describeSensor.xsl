<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:gml="http://www.opengis.net/gml" xmlns:swe="http://www.opengis.net/swe/1.0.1"
    xmlns:oost="http://www.oostethys.org/schemas/0.1.0/oostethys">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/oost:oostethys">
        
        <sml:SensorML
            xsi:schemaLocation="http://www.opengis.net/sensorML/1.0.1 http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd"
            version="1.0.1">
        <xsl:call-template name="member"></xsl:call-template>
        </sml:SensorML>
    </xsl:template>
    
    <xsl:template name="system">
        <sml:System>
            <sml:identification>
                <sml:IdentifierList>
                    <sml:identifier name="Long Name">
                        <sml:Term
                            definition="http://mmisw.org/mmi/20080520/obs.owl#longName">
                            <sml:value>
                                <xsl:value-of select="oost:metadata/oost:sytemLongName"
                                />
                            </sml:value>
                        </sml:Term>
                    </sml:identifier>
                    <sml:identifier name="ShortName">
                        <sml:Term
                            definition="http://mmisw.org/mmi/20080520/obs.owl#shortName">
                            <sml:value>
                                <xsl:value-of
                                    select="oost:metadata/oost:systemShortName"/>
                            </sml:value>
                        </sml:Term>
                    </sml:identifier>
                    <sml:identifier name="Identifier">
                        
                        <!-- unique identifier for this system. This is unique for each sos. Should be unique universally.  -->
                        <sml:Term
                            definition="http://mmisw.org/mmi/20080520/obs.owl#identifier">
                            <sml:value>
                                <xsl:value-of
                                    select="oost:metadata/oost:systemIdentifier"/>
                            </sml:value>
                        </sml:Term>
                    </sml:identifier>
                    
                </sml:IdentifierList>
            </sml:identification>
            
            <sml:classification>
                <sml:ClassifierList>
                    <sml:classifier name="System Type">
                        <!-- we are using rdf:type which says that this system type is an RDF class, or ontology class -->
                        <sml:Term
                            definition="http://www.w3.org/1999/02/22-rdf-syntax-ns#type">
                            <sml:value>
                                <xsl:value-of select="oost:metadata/oost:systemType"/>
                            </sml:value>
                            
                        </sml:Term>
                    </sml:classifier>
                </sml:ClassifierList>
            </sml:classification>
            
            <!-- deployment information -->
            <xsl:if test="exists(oost:extend/oost:timePeriod)">
                <sml:validTime>
                    <gml:TimePeriod>
                        <gml:beginPosition>
                            <xsl:value-of
                                select="oost:extend/oost:timePeriod/oost:start"/>
                        </gml:beginPosition>
                        <gml:endPosition>
                            <xsl:value-of select="oost:extend/oost:timePeriod/oost:end"
                            />
                        </gml:endPosition>
                    </gml:TimePeriod>
                </sml:validTime>
            </xsl:if>
            
            
            <xsl:for-each select="oost:metadata/oost:systemContacts/oost:systemContact">
                <sml:contact>
                    <xsl:attribute name="xlink:arcrole">
                        <xsl:value-of select="@type"/>
                        
                    </xsl:attribute>
                    
                    <xsl:attribute name="xlink:title">
                        <xsl:value-of select="normalize-space(oost:shortNameOrganization)"/>
                        
                    </xsl:attribute>
                    
                    
                    <sml:ResponsibleParty>
                        <sml:individualName>
                            <xsl:value-of select="oost:individualName"/>
                        </sml:individualName>
                        <sml:organizationName>
                            <xsl:value-of select="oost:longNameOrganization"/>
                        </sml:organizationName>
                        <sml:contactInfo>
                            <sml:address>
                                <sml:electronicMailAddress>
                                    <xsl:value-of select="oost:individualEmail"/>
                                </sml:electronicMailAddress>
                            </sml:address>
                        </sml:contactInfo>
                    </sml:ResponsibleParty>
                </sml:contact>
            </xsl:for-each>
            
            <sml:location>
                <gml:Point>
                    <gml:coordinates>
                        <xsl:attribute name="cs" >
                            <xsl:value-of
                                select="oost:metadata/oost:lastKnownLocation/@srsName"/>
                        </xsl:attribute>
                        <xsl:value-of
                            select="oost:metadata/oost:lastKnownLocation"/>
                        
                    </gml:coordinates>
                   
                </gml:Point>
           </sml:location>
            
            <xsl:if test="exists(oost:output)">
                
                <xsl:call-template name="outputs"></xsl:call-template>
                
            </xsl:if>
            
            
            <xsl:if test="exists(oost:components/oost:system[1])">
              
                <sml:components>
                    
                    <sml:ComponentList>
                        
                        <xsl:for-each select="oost:components/oost:system">
                           <xsl:call-template name="component"></xsl:call-template>
                        </xsl:for-each>
                    </sml:ComponentList>
                </sml:components>
            </xsl:if>
            
        </sml:System>
    </xsl:template>
    
    <xsl:template name="component">
        <sml:component>
            <xsl:attribute name="name">
               
                <xsl:value-of
                    select="oost:metadata/oost:systemShortName"/>
                
            </xsl:attribute>
            <xsl:attribute name="xlink:href">
                <xsl:value-of select="oost:metadata/oost:systemIdentifier"/>
            </xsl:attribute>
            <xsl:call-template name="system"></xsl:call-template>
        </sml:component>
        
    </xsl:template>
    
    <xsl:template name="member">
      
            <xsl:for-each select="oost:components/oost:system">
                <sml:member>
                    <xsl:attribute name="xlink:href">
                        <xsl:value-of select="normalize-space(oost:metadata/oost:systemIdentifier)"/>
                    </xsl:attribute>
                    
                   <xsl:call-template name="system"></xsl:call-template>
                    
                </sml:member>
            </xsl:for-each>
            
    
        
    </xsl:template>
    
    
    <xsl:template name="outputs">
        
        <sml:outputs>
            <sml:OutputList>
                <sml:output name="pointDataRecord">
                    <swe:DataArray>
                        <swe:elementCount/>
                        <swe:elementType name="SimpleDataArray">
                            <swe:DataRecord>
                                
                                <xsl:for-each
                                    select="oost:output/oost:variables/oost:variable">
                                    
                                    <swe:field>
                                        <xsl:attribute name="name">
                                            <xsl:value-of select="@name"/>
                                        </xsl:attribute>
                                        
                                        <xsl:if test="@isTime='false' or not(exists(@isTime))">
                                            <swe:Quantity>
                                                <xsl:attribute name="definition">
                                                    <xsl:value-of select="@uri"/>
                                                </xsl:attribute>
                                                <xsl:if test="@referenceFrame">
                                                    <xsl:attribute name="referenceFrame">
                                                        <xsl:value-of select="@referenceFrame"/>
                                                    </xsl:attribute>
                                                    
                                                </xsl:if>
                                                
                                                <xsl:if test="not(empty(@uom)) and string-length(@uom)>0">
                                                
                                                <swe:uom>
                                                    <xsl:attribute name="code">
                                                        <xsl:value-of select="@uom"/>
                                                    </xsl:attribute>
                                                    </swe:uom>
                                                </xsl:if>
                                            </swe:Quantity>
                                            
                                        </xsl:if>
                                        <xsl:if test="@isTime='true'">
                                         
                                                <swe:Time definition="urn:ogc:phenomenon:time:iso8601"/>
                                         
                                        </xsl:if>
                                        
                                        
                                    </swe:field>
                                    
                                    
                                </xsl:for-each>
                                
                                
                                
                                
                            </swe:DataRecord>
                        </swe:elementType>
                    </swe:DataArray>
                </sml:output>
            </sml:OutputList>
        </sml:outputs>
        
        
    </xsl:template>

</xsl:stylesheet>
