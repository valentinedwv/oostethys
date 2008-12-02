<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:oost="http://www.oostethys.org/schemas/0.1.0/oostethys">
    <xsl:output method="xml" indent="yes"/>


    <xsl:template match="/oost:oostethys">
        
        <kml xmlns="http://www.opengis.net/kml/2.0">
            <Document>
                <Folder>
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
                   
                </Folder>
                
                <name>Sensor Observation Service (SOS) for <xsl:value-of select="/oost:oostethys/oost:serviceContact/oost:longNameOrganization"> </xsl:value-of>
                </name>
                    
               </Document>
            
        </kml>

       
       </xsl:template>            
    
      
   
    
    <xsl:template name="observationOffering">
        <xsl:param name="id"/>
        
        <Placemark>
            <description> <![CDATA[
                        <b>Salinity: 34 PSU @ Sep 2, 8:00 PM ET</b><br> 
                        
                        ]]>
            
            
                <xsl:for-each select="oost:output/oost:variables/oost:variable">
                    <xsl:if test="not(exists(@isCoordinate)) or @isCoordinate!='true'">
                       <p>
                            
                                <xsl:value-of select="@uri"/>
                       
                       </p>
                    </xsl:if>
                </xsl:for-each>
            </description>
            <Point>
                <coordinates>
                    <xsl:variable name="lowerCorner" select="oost:extend/oost:boundingBox/oost:envelope/oost:lowerCorner"/>
                    <xsl:variable name="coordinates" select="tokenize($lowerCorner, ' ')" as="xs:string+"/>
                    <xsl:for-each select="$coordinates[position() >= 1]">
                        <xsl:value-of select="normalize-space(.)"></xsl:value-of>
                        <xsl:text xml:space="preserve"> </xsl:text>
                      </xsl:for-each>
                 
                   
                </coordinates>
            </Point>
            
            <Point>
                <coordinates>
                    <xsl:variable name="data" select="normalize-space(oost:output/oost:values)"></xsl:variable>
                    <xsl:variable name="coordinate" select="tokenize($data, '&#xa;')" as="xs:string+"/>
                    <xsl:for-each select="$coordinate[position() >= 1]">
                      <xsl:variable name="pos" select="position()"/>
                        <xsl:variable name="last" select="last()"/>
                        <xsl:if test="$pos=$last">
                        <xsl:value-of select="normalize-space(.)"></xsl:value-of>
                        
                    </xsl:if>
                        </xsl:for-each>
                    
                    
                </coordinates>
                
            </Point>
        </Placemark>
        
       
        
    </xsl:template>
        


</xsl:stylesheet>
