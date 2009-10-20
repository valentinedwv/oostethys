<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:app="http://www.deegree.org/app" xmlns:csw="http://www.opengis.net/cat/csw" xmlns:gml="http://www.opengis.net/gml" xmlns:iso19115="http://schemas.opengis.net/iso19115full" xmlns:ogc="http://www.opengis.net/ogc" xmlns:smXML="http://metadata.dgiwg.org/smXML" xmlns:wfs="http://www.opengis.net/wfs" version="1.0">
	<xsl:template match="iso19115:MD_Metadata">
		<app:MD_Metadata>
			<xsl:for-each select="iso19115:referenceSystemInfo">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:for-each select="iso19115:contact">
				<app:contact>
					<xsl:apply-templates select="smXML:CI_ResponsibleParty"/>
				</app:contact>
			</xsl:for-each>
			<xsl:apply-templates select="iso19115:distributionInfo/smXML:MD_Distribution/smXML:distributor"/>
			<xsl:apply-templates select="iso19115:distributionInfo/smXML:MD_Distribution/smXML:distributionFormat"/>
			<xsl:for-each select="iso19115:contentInfo/smXML:MD_FeatureCatalogueDescription">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:choose>
				<xsl:when test="boolean( iso19115:identificationInfo != '' )" >
					<xsl:apply-templates select="iso19115:identificationInfo"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:message>
						------------- Mandatory Element 'iso19115:identificationInfo' missing! ----------------
					</xsl:message>
					<app:exception>
						Mandatory Element 'iso19115:identificationInfo' missing!
					</app:exception>
				</xsl:otherwise>
			</xsl:choose>
			<!--  portrayalCatalogReference missing 
			<xsl:apply-templates select="iso19115:portrayalCatalogReference"/>
			-->
			<xsl:for-each select="iso19115:spatialRepresentationInfo">
				<app:spatialReprenstationInfo>
					<xsl:apply-templates select="smXML:MD_VectorSpatialRepresentation"/>
				</app:spatialReprenstationInfo>
			</xsl:for-each>
			<xsl:for-each select="iso19115:dataQualityInfo">
				<app:dataQualityInfo>
					<xsl:apply-templates select="smXML:DQ_DataQuality"/>
				</app:dataQualityInfo>
			</xsl:for-each>
			<xsl:for-each select="iso19115:distributionInfo/smXML:MD_Distribution/smXML:transferOptions">
				<xsl:apply-templates select="smXML:MD_DigitalTransferOptions"/>
			</xsl:for-each>
			<xsl:variable name="fileIdentifier"><xsl:value-of select="iso19115:fileIdentifier/smXML:CharacterString"/></xsl:variable>
			<xsl:if test="$fileIdentifier">
				<app:fileidentifier>
					<xsl:value-of select="$fileIdentifier"/>
				</app:fileidentifier>
			</xsl:if>
			<xsl:variable name="language"><xsl:value-of select="iso19115:language/smXML:CharacterString"/></xsl:variable>
			<xsl:if test="boolean( $language != '' )">
				<app:language>
					<xsl:value-of select="$language"/>
				</app:language>
			</xsl:if>
			<xsl:if test="boolean( iso19115:characterSet/smXML:MD_CharacterSetCode/@codeListValue != '' )">
				<app:characterSet>
					<app:MD_CharacterSetCode>
						<app:codelistvalue>
							<xsl:value-of select="iso19115:characterSet/smXML:MD_CharacterSetCode/@codeListValue"/>
						</app:codelistvalue>
					</app:MD_CharacterSetCode>
				</app:characterSet>
			</xsl:if>
			<xsl:variable name="parentIdentifier"><xsl:value-of select="iso19115:parentIdentifier/smXML:CharacterString"/></xsl:variable>
			<xsl:if test="boolean( $parentIdentifier != ''  )">
				<app:parentidentifier>
					<xsl:value-of select="$parentIdentifier"/>
				</app:parentidentifier>
			</xsl:if>
			<xsl:variable name="date" select="iso19115:dateStamp/smXML:Date"/>
			<xsl:variable name="dateTime" select="iso19115:dateStamp/smXML:DateTime"/>
			<xsl:if test="boolean( $dateTime != '' or $date != '' ) and 
						  not( boolean( $dateTime and $date ) )">
					<app:dateStamp>
						<xsl:value-of select="$dateTime"/>
						<xsl:value-of select="$date"/>
					</app:dateStamp>
			</xsl:if>
			<xsl:variable name="metaName" select="iso19115:metadataStandardName/smXML:CharacterString"/>
			<xsl:if test="$metaName != ''">
				<app:metadataStandardName>
					<xsl:value-of select="$metaName"/>
				</app:metadataStandardName>
			</xsl:if>
			<xsl:variable name="metaVersion" select="iso19115:metadataStandardVersion/smXML:CharacterString"/>
			<xsl:if test="$metaVersion != ''">
				<app:metadataStandardVersion>
					<xsl:value-of select="$metaVersion"/>
				</app:metadataStandardVersion>
			</xsl:if>
			<xsl:for-each select="iso19115:hierarchyLevel">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:for-each select="iso19115:hierarchyLevelName">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</app:MD_Metadata>
	</xsl:template>
	<xsl:template match="iso19115:hierarchyLevel">
		<xsl:variable name="HL">
			<xsl:value-of select="smXML:MD_ScopeCode/@codeListValue"/>
		</xsl:variable>
		<app:hierarchyLevelCode>
			<app:HierarchyLevelCode>
				<app:codelistvalue>
					<xsl:choose>
						<xsl:when test="$HL = 'dataset'">dataset</xsl:when>
						<xsl:when test="$HL = 'series'">series</xsl:when>
						<xsl:when test="$HL = 'application'">application</xsl:when>
						<xsl:otherwise>invalid hierarchylevel! must be dataset, datasetcollection or application</xsl:otherwise>
					</xsl:choose>
				</app:codelistvalue>
			</app:HierarchyLevelCode>
		</app:hierarchyLevelCode>
	</xsl:template>
	<xsl:template match="iso19115:hierarchyLevelName">
		<xsl:if test=" smXML:CharacterString != ''">
			<app:hierarchyLevelName>
				<app:HierarchyLevelName>
					<app:name>
						<xsl:value-of select="smXML:CharacterString"/>
					</app:name>
				</app:HierarchyLevelName>
			</app:hierarchyLevelName>
		</xsl:if>
	</xsl:template>
	<xsl:template match="iso19115:referenceSystemInfo">
		<app:referenceSystemInfo>
			<app:RS_Identifier>
				<xsl:apply-templates select="smXML:MD_ReferenceSystem/smXML:referenceSystemIdentifier/smXML:RS_Identifier"/>
			</app:RS_Identifier>
		</app:referenceSystemInfo>
	</xsl:template>
	<xsl:template match="iso19115:identificationInfo">
		<app:dataIdentification>
			<app:MD_DataIdentification>
				<app:identificationInfo>
					<app:MD_Identification>
						<xsl:if test="boolean( smXML:MD_DataIdentification/smXML:status/smXML:MD_ProgressCode/@codeListValue != '' )">
							<app:status>
								<app:MD_ProgressCode>
									<app:codelistvalue>
										<xsl:value-of select="smXML:MD_DataIdentification/smXML:status/smXML:MD_ProgressCode/@codeListValue"/>
									</app:codelistvalue>
								</app:MD_ProgressCode>
							</app:status>
						</xsl:if>
						<xsl:if test="boolean( smXML:MD_DataIdentification/smXML:citation/smXML:CI_Citation )">
							<app:citation>
								<xsl:apply-templates select="smXML:MD_DataIdentification/smXML:citation/smXML:CI_Citation"/>
							</app:citation>
						</xsl:if>
						<xsl:for-each select="smXML:MD_DataIdentification/smXML:pointOfContact">
							<app:pointOfContact>
								<xsl:apply-templates select="smXML:CI_ResponsibleParty"/>
							</app:pointOfContact>
						</xsl:for-each>
						<xsl:for-each select="smXML:MD_DataIdentification/smXML:resourceSpecificUsage">
							<app:resourceSpecificUsage>
								<xsl:apply-templates select="smXML:MD_Usage"/>
							</app:resourceSpecificUsage>
						</xsl:for-each>
						<xsl:for-each select="smXML:MD_DataIdentification/smXML:resourceMaintenance">
							<app:resourceMaintenance>
								<app:MD_MainFreqCode>
									<app:codelistvalue>
										<xsl:value-of select="smXML:MD_MaintenanceInformation/smXML:maintenanceAndUpdateFrequency/smXML:MD_MaintenanceFrequencyCode/@codeListValue"/>
									</app:codelistvalue>
								</app:MD_MainFreqCode>
							</app:resourceMaintenance>
						</xsl:for-each>
						<xsl:for-each select="smXML:MD_DataIdentification/smXML:graphicOverview/smXML:MD_BrowseGraphic">
							<app:graphicOverview>
								<xsl:apply-templates select="."/>
							</app:graphicOverview>
						</xsl:for-each>
						<xsl:for-each select="smXML:MD_DataIdentification/smXML:resourceConstraints">
							<xsl:if test="boolean( smXML:MD_LegalConstraints )">
								<app:resourceConstraints>
									<xsl:apply-templates select="smXML:MD_LegalConstraints"/>
								</app:resourceConstraints>
							</xsl:if>
						</xsl:for-each>
						<xsl:for-each select="smXML:MD_DataIdentification/smXML:descriptiveKeywords">
							<app:descriptiveKeywords>
								<xsl:apply-templates select="smXML:MD_Keywords"/>
							</app:descriptiveKeywords>
						</xsl:for-each>
						<xsl:variable name="abstract"><xsl:value-of select="smXML:MD_DataIdentification/smXML:abstract/smXML:CharacterString"/></xsl:variable>
						<xsl:if test="$abstract != ''">
							<app:abstract>
								<xsl:value-of select="$abstract"/>
							</app:abstract>
						</xsl:if>
						<xsl:if test="boolean( smXML:MD_DataIdentification/smXML:purpose/smXML:CharacterString != '' )">
							<app:purpose>
								<xsl:value-of select="smXML:MD_DataIdentification/smXML:purpose/smXML:CharacterString"/>
							</app:purpose>
						</xsl:if>
					</app:MD_Identification>
				</app:identificationInfo>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:topicCategory">
					<app:topicCategory>
						<app:MD_TopicCategoryCode>
							<app:category>
								<xsl:value-of select="smXML:MD_TopicCategoryCode"/>
							</app:category>
						</app:MD_TopicCategoryCode>
					</app:topicCategory>
				</xsl:for-each>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:spatialRepresentationType">
					<app:spatialRepresentationType>
						<app:MD_SpatialRepTypeCode>
							<app:codelistvalue>
								<xsl:value-of select="smXML:MD_SpatialRepresentationTypeCode/@codeListValue"/>
							</app:codelistvalue>
						</app:MD_SpatialRepTypeCode>
					</app:spatialRepresentationType>
				</xsl:for-each>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:resourceMaintenance/smXML:MD_MaintenanceInformation">
					<xsl:if test="boolean(smXML:dateOfNextUpdate) or boolean(smXML:userDefinedMaintenanceFrequency) or boolean(smXML:updateScope) or boolean(smXML:maintenanceNote)">
						<app:resourceMaintenance>
							<xsl:apply-templates select="."/>
						</app:resourceMaintenance>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:spatialResolution">
					<app:spatialResolution>
						<xsl:apply-templates select="smXML:MD_Resolution"/>
					</app:spatialResolution>
				</xsl:for-each>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:extent/smXML:EX_Extent/smXML:verticalElement">
					<app:verticalExtent>
						<xsl:call-template name="VERTEX"/>
					</app:verticalExtent>
				</xsl:for-each>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:extent/smXML:EX_Extent/smXML:temporalElement">
					<app:temportalExtent>
						<xsl:call-template name="TEMPEX"/>
					</app:temportalExtent>
				</xsl:for-each>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:extent/smXML:EX_Extent/smXML:geographicElement/smXML:EX_GeographicBoundingBox">
					<xsl:call-template name="GEOEX"/>
				</xsl:for-each>
				<xsl:for-each select="smXML:MD_DataIdentification/smXML:extent/smXML:EX_Extent/smXML:geographicElement/smXML:EX_GeographicDescription">
					<xsl:call-template name="GEODESCEX"/>
				</xsl:for-each>
				<xsl:if test="boolean( smXML:MD_DataIdentification/smXML:language/smXML:CharacterString != '' )">
					<app:language>
						<xsl:value-of select="smXML:MD_DataIdentification/smXML:language/smXML:CharacterString"/>
					</app:language>
				</xsl:if>
				<xsl:if test="smXML:MD_DataIdentification/smXML:characterSet">
					<app:characterSet>
						<app:MD_CharacterSetCode>
							<app:codelistvalue>
								<xsl:value-of select="smXML:MD_DataIdentification/smXML:characterSet/smXML:MD_CharacterSetCode/@codeListValue"/>
							</app:codelistvalue>
						</app:MD_CharacterSetCode>
					</app:characterSet>
				</xsl:if>
			</app:MD_DataIdentification>
		</app:dataIdentification>
	</xsl:template>
	<xsl:template match="smXML:MD_BrowseGraphic">
		<app:MD_BrowseGraphic>
			<xsl:if test="boolean( smXML:fileName/smXML:CharacterString != '' )">
				<app:filename>
					<xsl:value-of select="smXML:fileName/smXML:CharacterString"/>
				</app:filename>
			</xsl:if>
			<xsl:if test="boolean(smXML:fileDescription)">
				<app:filedescription>
					<xsl:value-of select="smXML:fileDescription/smXML:CharacterString"/>
				</app:filedescription>
			</xsl:if>
			<xsl:if test="boolean(smXML:fileType)">
				<app:filetype>
					<xsl:value-of select="smXML:fileType/smXML:CharacterString"/>
				</app:filetype>
			</xsl:if>
		</app:MD_BrowseGraphic>
	</xsl:template>
	<xsl:template match="smXML:MD_VectorSpatialRepresentation">
		<app:MD_VectorSpatialReprenstation>
			<xsl:if test="smXML:topologyLevel">
				<app:topoLevelCode>
					<app:MD_TopoLevelCode>
						<app:codelistvalue>
							<xsl:value-of select="smXML:topologyLevel/smXML:MD_TopologyLevelCode/@codeListValue"/>
						</app:codelistvalue>
					</app:MD_TopoLevelCode>
				</app:topoLevelCode>
			</xsl:if>
			<xsl:if test="smXML:geometricObjects">
				<app:geoTypeObjectTypeCode>
					<app:MD_GeoObjTypeCode>
						<app:codelistvalue>
							<xsl:apply-templates select="smXML:geometricObjects/smXML:MD_GeometricObjects/smXML:geometricObjectType/smXML:MD_GeometricObjectTypeCode/@codeListValue"/>
						</app:codelistvalue>
					</app:MD_GeoObjTypeCode>
				</app:geoTypeObjectTypeCode>
				<xsl:if test="boolean( smXML:geometricObjects/smXML:MD_GeometricObjects/smXML:geometricObjectCount ) ">
					<app:geoobjcount>
						<xsl:value-of select="smXML:geometricObjects/smXML:MD_GeometricObjects/smXML:geometricObjectCount/smXML:CharacterString"/>
					</app:geoobjcount>
				</xsl:if>
			</xsl:if>
		</app:MD_VectorSpatialReprenstation>
	</xsl:template>
	<xsl:template match="smXML:DQ_DataQuality">
		<app:DQ_DataQuality>
			<xsl:apply-templates select="smXML:report/smXML:DQ_AbsoluteExternalPositionalAccuracy | 
										 smXML:report/smXML:DQ_CompletenessCommission"/>
			<xsl:if test="smXML:scope/smXML:DQ_Scope/smXML:level/smXML:MD_ScopeCode/@codeListValue != '' ">
				<app:scopelevelcodelistvalue>
					<xsl:value-of select="smXML:scope/smXML:DQ_Scope/smXML:level/smXML:MD_ScopeCode/@codeListValue"/>
				</app:scopelevelcodelistvalue>
			</xsl:if>
			<xsl:variable name="lineagestatement"><xsl:value-of select="smXML:lineage/smXML:LI_Lineage/smXML:statement/smXML:CharacterString"/></xsl:variable>
			<xsl:if test="$lineagestatement != '' ">
				<app:lineagestatement>
					<xsl:value-of select="$lineagestatement"/>
				</app:lineagestatement>
			</xsl:if>
			<xsl:variable name="lineagsourcedesc"><xsl:value-of select="smXML:lineage/smXML:LI_Lineage/smXML:source/smXML:LI_Source/smXML:description/smXML:CharacterString"/></xsl:variable>
			<xsl:if test="$lineagsourcedesc != '' ">
				<app:lineagesourcedesc>
					<xsl:value-of select="$lineagsourcedesc"/>
				</app:lineagesourcedesc>
			</xsl:if>
			<xsl:variable name="lineageprocdesc"><xsl:value-of select="smXML:lineage/smXML:LI_Lineage/smXML:processStep/smXML:LI_ProcessStep/smXML:description/smXML:CharacterString"/></xsl:variable>
			<xsl:if test="$lineageprocdesc != ''">
				<app:lineageprocessdesc>
					<xsl:value-of select="$lineageprocdesc"/>
				</app:lineageprocessdesc>
			</xsl:if>
			<xsl:variable name="lineagedate"><xsl:value-of select="smXML:lineage/smXML:LI_Lineage/smXML:processStep/smXML:LI_ProcessStep/smXML:dateTime/smXML:DateTime"/></xsl:variable>
			<xsl:if test="$lineagedate != ''">
				<app:lineageprocessdatetime>
					<xsl:value-of select="$lineagedate"/>
				</app:lineageprocessdatetime>
			</xsl:if>
		</app:DQ_DataQuality>
	</xsl:template>
	<xsl:template match="smXML:DQ_AbsoluteExternalPositionalAccuracy | 
						 smXML:DQ_CompletenessCommission">
		<app:DQ_Element>
			<app:DQ_Element>
				<xsl:if test="smXML:nameOfMeasure">
					<app:nameofmeasure>
						<xsl:value-of select="smXML:nameOfMeasure/smXML:CharacterString"/>
					</app:nameofmeasure>
				</xsl:if>
				<xsl:for-each select="smXML:DQ_QuantitativeResult">
					<xsl:if test="position() = 1">
						<app:uomname1>
							<xsl:value-of select="smXML:UomLength/smXML:uomName/smXML:CharacterString"/>
						</app:uomname1>
						<app:convtoisostdunit1>
							<xsl:value-of select="smXML:UomLength/smXML:conversionTolSOstandardUnit/smXML:Real"/>
						</app:convtoisostdunit1>
						<app:value1>
							<xsl:value-of select="smXML:value/smXML:Record"/>
						</app:value1>
					</xsl:if>
					<xsl:if test="position() = 2">
						<app:uomname2>
							<xsl:value-of select="smXML:UomLength/smXML:uomName/smXML:CharacterString"/>
						</app:uomname2>
						<app:convtoisostdunit2>
							<xsl:value-of select="smXML:UomLength/smXML:conversionTolSOstandardUnit/smXML:Real"/>
						</app:convtoisostdunit2>
						<app:value2>
							<xsl:value-of select="smXML:value/smXML:Record"/>
						</app:value2>
					</xsl:if>
				</xsl:for-each>
				<app:type>
					<xsl:value-of select="local-name(.)"/>
				</app:type>
			</app:DQ_Element>
		</app:DQ_Element>
	</xsl:template>
</xsl:stylesheet>
