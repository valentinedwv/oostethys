<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:app="http://www.deegree.org/app"
	xmlns:gml="http://www.opengis.net/gml" 
	xmlns:gmd="http://www.isotc211.org/2005/gmd"
	xmlns:gco="http://www.isotc211.org/2005/gco"
	xmlns:srv="http://www.isotc211.org/2005/srv">
	
	<xsl:template name="SERVICEMETADATA">
		<app:MD_Metadata>
			<xsl:for-each select="gmd:MD_Metadata/gmd:referenceSystemInfo">
				<xsl:apply-templates select="." />
			</xsl:for-each>
			<xsl:for-each select="gmd:MD_Metadata/gmd:contact">
				<app:contact>
					<xsl:apply-templates select="gmd:CI_ResponsibleParty" />
				</app:contact>
			</xsl:for-each>
			<xsl:choose>
				<xsl:when test="boolean( gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification != '' )">
					<xsl:apply-templates select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:message>
						------------- Mandatory Element 'srv:identificationInfo' missing! ----------------
					</xsl:message>
					<app:exception>Mandatory Element 'srv:identificationInfo' missing!</app:exception>
				</xsl:otherwise>
			</xsl:choose>
			<app:fileidentifier>
				<xsl:value-of select="gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString" />
			</app:fileidentifier>
			<xsl:if test="boolean( gmd:MD_Metadata/gmd:language )">
				<app:language>
					<xsl:value-of select="gmd:MD_Metadata/gmd:language/gco:CharacterString" />
				</app:language>
			</xsl:if>
			<xsl:if test="gmd:MD_Metadata/gmd:characterSet">
				<app:characterSet>
					<app:MD_CharacterSetCode>
						<app:codelistvalue>
							<xsl:value-of select="gmd:MD_Metadata/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue" />
						</app:codelistvalue>
					</app:MD_CharacterSetCode>
				</app:characterSet>
			</xsl:if>
			<app:dateStamp>
				<xsl:value-of select="gmd:MD_Metadata/gmd:dateStamp/gco:DateTime" />
				<xsl:value-of select="gmd:MD_Metadata/gmd:dateStamp/gco:Date" />
			</app:dateStamp>
			<xsl:variable name="metaName" select="gmd:MD_Metadata/gmd:metadataStandardName/gco:CharacterString" />
			<xsl:if test="$metaName != ''">
				<app:metadataStandardName>
					<xsl:value-of select="$metaName" />
				</app:metadataStandardName>
			</xsl:if>
			<xsl:variable name="metaVersion" select="gmd:MD_Metadata/gmd:metadataStandardVersion/gco:CharacterString" />
			<xsl:if test="$metaVersion != ''">
				<app:metadataStandardVersion>
					<xsl:value-of select="$metaVersion" />
				</app:metadataStandardVersion>
			</xsl:if>
			<xsl:call-template name="SERVICEHIERARCHYLEVEL"/>
			<xsl:apply-templates select="gmd:MD_Metadata/gmd:hierarchyLevelName" />
			
			<xsl:call-template name="CQPSERVICE"/>
			
		</app:MD_Metadata>
	</xsl:template>
	
	<xsl:template name="SERVICEHIERARCHYLEVEL">
		<xsl:variable name="HL">
			<xsl:value-of select="gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue" />
		</xsl:variable>
		<app:hierarchyLevelCode>
			<app:HierarchyLevelCode>
				<app:codelistvalue>
					<xsl:choose>
						<xsl:when test="$HL = 'service'">service</xsl:when>
						<xsl:otherwise>invalid hierarchylevel! must be csw:service</xsl:otherwise>
					</xsl:choose>
				</app:codelistvalue>
			</app:HierarchyLevelCode>
		</app:hierarchyLevelCode>
	</xsl:template>
	
	<xsl:template match="gmd:hierarchyLevelName">
		<app:hierarchyLevelName>
			<app:HierarchyLevelName>
				<app:name>
					<xsl:value-of select="gco:CharacterString" />
				</app:name>
			</app:HierarchyLevelName>
		</app:hierarchyLevelName>
	</xsl:template>
	
	<xsl:template match="gmd:referenceSystemInfo">
		<app:referenceSystemInfo>
			<app:RS_Identifier>
				<xsl:apply-templates select="gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier" />
			</app:RS_Identifier>
		</app:referenceSystemInfo>
	</xsl:template>
	
	<xsl:template match="srv:SV_ServiceIdentification">
		<app:serviceIdentification>
			<app:CSW_ServiceIdentification>
				<app:identificationInfo>
					<app:MD_Identification>
						<xsl:for-each select="gmd:status">
							<app:status>
								<app:MD_ProgressCode>
									<app:codelistvalue>
										<xsl:value-of select="gmd:MD_ProgressCode/@codeListValue" />
									</app:codelistvalue>
								</app:MD_ProgressCode>
							</app:status>
						</xsl:for-each>
						<xsl:for-each select="gmd:citation">
							<app:citation>
								<xsl:apply-templates select="gmd:CI_Citation">
									<xsl:with-param name="context">Identification</xsl:with-param>
								</xsl:apply-templates>
							</app:citation>
						</xsl:for-each>
						<xsl:for-each select="gmd:pointOfContact">
							<app:pointOfContact>
								<xsl:apply-templates select="gmd:CI_ResponsibleParty" />
							</app:pointOfContact>
						</xsl:for-each>
						<xsl:for-each select="gmd:resourceSpecificUsage">
							<app:resourceSpecificUsage>
								<xsl:apply-templates select="gmd:MD_Usage" />
							</app:resourceSpecificUsage>
						</xsl:for-each>
						<xsl:for-each select="gmd:resourceMaintenance">
							<app:resourceMaintenance>
								<xsl:apply-templates select="gmd:MD_MaintenanceInformation" />
							</app:resourceMaintenance>
						</xsl:for-each>
						<xsl:for-each select="gmd:resourceConstraints">
							<xsl:if test="gmd:MD_LegalConstraints">
								<app:legalConstraints>
									<xsl:apply-templates select="gmd:MD_LegalConstraints" />
								</app:legalConstraints>
							</xsl:if>
							<xsl:if test="gmd:MD_SecurityConstraints">
								<app:securityConstraints>
									<xsl:apply-templates select="gmd:MD_SecurityConstraints" />
								</app:securityConstraints>
							</xsl:if>
						</xsl:for-each>
						<xsl:for-each select="gmd:descriptiveKeywords">
							<app:descriptiveKeywords>
								<xsl:apply-templates select="gmd:MD_Keywords" />
							</app:descriptiveKeywords>
						</xsl:for-each>
						<xsl:if test="boolean(gmd:abstract)">
							<app:abstract>
								<xsl:value-of select="gmd:abstract/gco:CharacterString" />
							</app:abstract>
						</xsl:if>
						<xsl:if test="boolean(gmd:purpose)">
							<app:purpose>
								<xsl:value-of select="gmd:purpose/gco:CharacterString" />
							</app:purpose>
						</xsl:if>
					</app:MD_Identification>
				</app:identificationInfo>
				<app:servicetype>
					<xsl:value-of select="srv:serviceType/gco:LocalName" />
				</app:servicetype>
				<xsl:for-each select="srv:serviceTypeVersion">
					<app:serviceTypeVersion>
						<xsl:value-of select="gco:CharacterString" />
					</app:serviceTypeVersion>
				</xsl:for-each>
				<xsl:apply-templates select="srv:accessProperties" />
				<xsl:if test="boolean( srv:restrictions/gmd:MD_LegalConstraints )">
					<app:restrictionsLegal>
						<xsl:apply-templates select="srv:restrictions/gmd:MD_LegalConstraints" />
					</app:restrictionsLegal>
				</xsl:if>
				<xsl:if test="boolean( srv:restrictions/gmd:MD_SecurityConstraints )">
					<app:restrictionsSecurity>
						<xsl:apply-templates select="srv:restrictions/gmd:MD_SecurityConstraints" />
					</app:restrictionsSecurity>
				</xsl:if>
				<app:couplingType>
					<app:CSW_CouplingType>
						<app:codelistvalue>
							<xsl:value-of select="srv:couplingType/srv:SV_CouplingType/@codeListValue" />
						</app:codelistvalue>
					</app:CSW_CouplingType>
				</app:couplingType>
				<xsl:for-each select="srv:containsOperations">
					<app:operationMetadata>
						<xsl:apply-templates select="srv:SV_OperationMetadata" />
					</app:operationMetadata>
				</xsl:for-each>
				<xsl:for-each select="srv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox">
					<xsl:call-template name="GEOEX" />
				</xsl:for-each>
				<xsl:for-each select="srv:operatesOn/gmd:MD_DataIdentification">
					<app:operatesOn>
						<app:OperatesOn>
							<xsl:if test="boolean( gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString != '' )">
								<app:name>
									<xsl:value-of select="gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString" />
								</app:name>
							</xsl:if>
							<xsl:if test="boolean( gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString != '' )">
								<app:title>
									<xsl:value-of select="gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString" />
								</app:title>
							</xsl:if>
							<xsl:if test="boolean( gmd:abstract/gco:CharacterString != '' )">
								<app:abstract>
									<xsl:value-of select="gmd:abstract/gco:CharacterString" />
								</app:abstract>
							</xsl:if>
						</app:OperatesOn>
					</app:operatesOn>
				</xsl:for-each>
				<xsl:for-each select="srv:coupledResource/srv:SV_CoupledResource">
					<app:coupledResource>
						<xsl:apply-templates select="." />
					</app:coupledResource>
				</xsl:for-each>
			</app:CSW_ServiceIdentification>
		</app:serviceIdentification>
	</xsl:template>
	
	<xsl:template match="srv:SV_OperationMetadata">
		<app:SV_OperationMetadata>
			<app:operationName>
				<app:OperationNames>
					<app:name>
						<xsl:value-of select="srv:operationName/gco:CharacterString" />
					</app:name>
				</app:OperationNames>
			</app:operationName>
			<xsl:if test="boolean( srv:operationDescription )">
				<app:operationDescription>
					<xsl:value-of select="srv:operationDescription/gco:CharacterString" />
				</app:operationDescription>
			</xsl:if>
			<xsl:if test="boolean( gmd:invocationName ) ">
				<app:invocationName>
					<xsl:value-of select="gmd:invocationName/gco:CharacterString" />
				</app:invocationName>
			</xsl:if>
			<xsl:for-each select="srv:parameters">
				<app:parameters>
					<xsl:apply-templates select="srv:SV_Parameter" />
				</app:parameters>
			</xsl:for-each>
			<xsl:for-each select="srv:connectPoint">
				<app:connectPoint>
					<xsl:apply-templates select="gmd:CI_OnlineResource" />
				</app:connectPoint>
			</xsl:for-each>
			<xsl:for-each select="srv:DCP">
				<app:DCP>
					<app:SV_DCPList>
						<app:codelistvalue>
							<xsl:value-of select="srv:DCPList/@codeListValue" />
						</app:codelistvalue>
					</app:SV_DCPList>
				</app:DCP>
			</xsl:for-each>
		</app:SV_OperationMetadata>
	</xsl:template>
	
	<xsl:template match="srv:SV_Parameter">
		<app:SV_Parameter>
			<app:name>
				<xsl:value-of select="srv:name/gco:aName/gco:CharacterString" />
			</app:name>
			<app:type>
				<xsl:value-of select="srvname/gco:attributeType/gco:TypeName/gco:aName/gco:CharacterString" />
			</app:type>
			<app:direction>
				<xsl:value-of select="srv:direction/srv:SV_ParameterDirection" />
			</app:direction>
			<app:description>
				<xsl:value-of select="srv:description/gco:CharacterString" />
			</app:description>
			<app:optionality>
				<xsl:value-of select="srv:optionality/gco:CharacterString" />
			</app:optionality>
			<app:repeatability>
				<xsl:value-of select="srv:repeatability/gco:Boolean" />
			</app:repeatability>
			<app:valuetype>
				<xsl:value-of select="srv:valueType/gco:TypeName/gco:aName/gco:CharacterString"/>
			</app:valuetype>
		</app:SV_Parameter>
	</xsl:template>
	
	<xsl:template match="srv:SV_CoupledResource">
		<app:CoupledResource>
			<app:operationName>
				<app:OperationNames>
					<app:name>
						<xsl:value-of select="srv:operationName/gco:CharacterString" />
					</app:name>
				</app:OperationNames>
			</app:operationName>
			<xsl:for-each select="srv:identifier">
				<app:identifier>
					<xsl:value-of select="gco:CharacterString" />
				</app:identifier>
			</xsl:for-each>
		</app:CoupledResource>
	</xsl:template>
	
	<xsl:template match="srv:accessProperties">
		<xsl:if test="boolean( gmd:MD_StandardOrderProcess/gmd:fees )">
			<app:fees>
				<xsl:value-of select="gmd:MD_StandardOrderProcess/gmd:fees/gco:CharacterString" />
			</app:fees>
		</xsl:if>
		<xsl:if test="boolean(gmd:MD_StandardOrderProcess/gmd:plannedAvailableDatetime)">
			<app:fees>
				<xsl:value-of select="gmd:MD_StandardOrderProcess/gmd:plannedAvailableDatetime/gco:DateTime" />
			</app:fees>
		</xsl:if>
		<xsl:if test="boolean(gmd:MD_StandardOrderProcess/gmd:orderingInstructions)">
			<app:orderingInstructions>
				<xsl:value-of select="gmd:MD_StandardOrderProcess/gmd:orderingInstructions/gco:CharacterString" />
			</app:orderingInstructions>
		</xsl:if>
		<xsl:if test="boolean(gmd:MD_StandardOrderProcess/gmd:turnaround)">
			<app:turnaround>
				<xsl:value-of select="gmd:MD_StandardOrderProcess/gmd:turnaround/gco:CharacterString" />
			</app:turnaround>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="CQPSERVICE">	
		<app:commonQueryableProperties>
			<app:CQP_Main>
	
				<xsl:variable name="cqpKeywords" select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword"/>
				<xsl:if test="boolean( $cqpKeywords != '' )">
					<app:subject>
						<xsl:for-each select="$cqpKeywords">
							<xsl:value-of select="concat( '|', gco:CharacterString, '|' )"/>
						</xsl:for-each>
					</app:subject>
				</xsl:if>
				
				<app:title>
					<xsl:value-of select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/>
				</app:title>
	
				<app:abstract>
					<xsl:value-of select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:abstract/gco:CharacterString"/>
				</app:abstract>
	
				<app:anyText>
					<!--
						this must be used if CSW in configured on Oracle as backend
					-->
					<xsl:value-of select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/>
					<xsl:value-of select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString"/>
						<xsl:for-each select="$cqpKeywords">
							<xsl:value-of select="."/>
						</xsl:for-each>
					<!--xsl:value-of select="."/-->
				</app:anyText>
	
				<app:identifier>
					<xsl:value-of select="gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"/>
				</app:identifier>
	
				<app:modified>
					<xsl:value-of select="gmd:MD_Metadata/gmd:dateStamp/gco:DateTime"/>
					<xsl:value-of select="gmd:MD_Metadata/gmd:dateStamp/gco:Date"/>
				</app:modified>
	
				<app:type>
					<xsl:value-of select="gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue"/>
				</app:type>
				
				<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:date">
					<xsl:if test="gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'revision'">
						<app:revisionDate>
							<xsl:value-of select="gmd:CI_Date/gmd:date/gco:Date"/>
							<xsl:value-of select="gmd:CI_Date/gmd:date/gco:DateTime"/>
						</app:revisionDate>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:date">
					<xsl:if test="gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'creation'">
						<app:creationDate>
							<xsl:value-of select="gmd:CI_Date/gmd:date/gco:Date"/>
							<xsl:value-of select="gmd:CI_Date/gmd:date/gco:DateTime"/>
						</app:creationDate>
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:date">
					<xsl:if test="gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue = 'publication'">
						<app:publicationDate>
							<xsl:value-of select="gmd:CI_Date/gmd:date/gco:Date"/>
							<xsl:value-of select="gmd:CI_Date/gmd:date/gco:DateTime"/>
						</app:publicationDate>
					</xsl:if>
				</xsl:for-each>
				
				<xsl:variable name="qcpAlternateTitle" select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle"/>
				<xsl:if test="boolean( $qcpAlternateTitle != '' )">
					<app:alternateTitle>
						<xsl:for-each select="$qcpAlternateTitle">
							<xsl:value-of select="concat( '|', gco:CharacterString, '|' )"/>
						</xsl:for-each>
					</app:alternateTitle>
				</xsl:if>
				
				<xsl:variable name="qcpResourceId" select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code"/>
				<xsl:if test="boolean( $qcpResourceId != '' )">
					<app:resourceIdentifier>
						<xsl:for-each select="$qcpResourceId">
							<xsl:value-of select="concat( '|', gco:CharacterString, '|' )"/>
						</xsl:for-each>
					</app:resourceIdentifier>
				</xsl:if>
				
				<xsl:variable name="cqpDescCode" select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code"/>
				<xsl:if test="boolean( $cqpDescCode != '' )">
					<app:geographicDescripionCode>
						<xsl:for-each select="$cqpDescCode">
							<xsl:value-of select="concat( '|', gco:CharacterString, '|' )"/>
						</xsl:for-each>
					</app:geographicDescripionCode>
				</xsl:if>
				
				<app:serviceType>
					<xsl:value-of select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceType"/>
				</app:serviceType>
				
				<xsl:for-each select="gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox">
					<xsl:variable name="minx" select="gmd:westBoundLongitude/gco:Decimal"/>
					<xsl:variable name="maxx" select="gmd:eastBoundLongitude/gco:Decimal"/>
					<xsl:variable name="miny" select="gmd:southBoundLatitude/gco:Decimal"/>
					<xsl:variable name="maxy" select="gmd:northBoundLatitude/gco:Decimal"/>
					<app:bbox>
						<app:CQP_BBOX>
							<app:geom>
								<gml:Polygon srsName="EPSG:4326">
									<gml:outerBoundaryIs>
										<gml:LinearRing>
											<gml:coordinates cs="," decimal="." ts=" ">
												<xsl:value-of select="concat( $minx, ',', $miny, ' ', $minx, ',', $maxy, ' ', $maxx, ',', $maxy, ' ', $maxx, ',', $miny, ' ',$minx, ',', $miny)" />
											</gml:coordinates>
										</gml:LinearRing>
									</gml:outerBoundaryIs>
								</gml:Polygon>
							</app:geom>
						</app:CQP_BBOX>
					</app:bbox>
				</xsl:for-each>
				
				<xsl:variable name="cqpParentID" select="gmd:MD_Metadata/gmd:parentIdentifier"/>
				<xsl:if test="boolean( $cqpParentID != '' )">
					<app:parentIdentifier>
							<xsl:value-of select="$cqpParentID"/>
					</app:parentIdentifier>
				</xsl:if>
				
			</app:CQP_Main>
		</app:commonQueryableProperties>
	</xsl:template>
	
</xsl:stylesheet>
