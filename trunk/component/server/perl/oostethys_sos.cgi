#!/usr/bin/perl
######################
#   Copyright 2009 www.oostethys.org
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       docs/LICENSE-2.0.txt or
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
######################
# Author: Eric Bridger eric@gomoos.org eric.bridger@gmail.com
#         The NDBC's DIF SOS was used as a reference implementation for the DIF sections. See:  http://ioos.gov/library/ndbcsos.zip
######################
use strict; 
# Some required libraries.
use Time::Local;
use File::Basename;
use XML::LibXML;
use CGI ":cgi";
# if using the SOS Config file DBI is optional
#### LOCAL EDIT
use DBI;
#### END LOCAL EDIT
################################################################################
# SOS OVERVIEW
#  This Perl script implements the OGC's Sensor Observation Service Version 1.0
#    The three mandatory core operations/requests are supported:
#       - GetCapabilities
#       - DescribeSensor
#       - GetObservation
#
#  There are two ways to use this script controlled by the $use_config variable below.
#    1. It allows sensor and organization metadata to be retrieved either from a simple XML configuration file
#       the sos_config.xml file  which must be edited with your local Metadata information.
#       Observation data is retrieved from ASCII text files named in the configuration file.
#    2. OR it allows sensor and organization metadata and observations to be retrieved from a local database via the DBI module
#       with two separate SQL queries. One for Metadata and another for Observation data.
#    Note: it is possible to use the sos_config.xml file to set Metadata and still use the Database to retrieve observation
#    data.  This is done by setting the <SOSDataFile> element in the config file to DataBase.
#
#  Each core operation has a corresponding XML template which is parsed via the XML::LibXML Perl library and the
#  SOS servers metadata and observations are inserted into the template and returned as XML.
#    SOS Templates:
#       - sosGetCapabilities.xml
#       - sosDescribeSensor.xml
#       - sosGetObservation.xml
#  The assumption is that the templates are in the same directory as the script but can reside anywhere by setting the $base_dir global
#  In general the templates contain all the necessary required elements without local values for SOS the response, but some of the elements or
#  attributes are hard coded and are not modified by the script. 
#  These can be edited in the templates. For example, in the sosGetCapabilites.xml, the timeInterval attribute which is set to 1 hour.
#  Some of the elements exist as models which are cloned by this script and updated and repeatedly output as necessary. For example the list
#  of observedProperties.
#  Since XML->appendText is often used for element values, elements should not have holding values.  Attribute values can and do have holding
#  values since they get overwritten.
#  
#  SOS is a Web Service and a Web Server such as Apache is required.  The script runs in your Web Servers
#  /cgi-bin directory or sub directtory and must be made executable:  chmod 755 and owned by the Web user.
################################################################################
# IOOS DIF Compatibility
# The NOAA IOOS Program Office has endorsed an SOS which uses a custom GML schema, referred to as the Data Integration Framework (DIF).
# This script has been modified to support outputing DIF compliant XML. Thus there are now 3 new SOS Tempates:
#        - difGetCapabilities.xml
#        - difDescribeSensor.xml
#        - difGetObservation.xml
# These work in the same way as the SOS Templates described above.
# There are two variables which can be set to control whether the script outputs DIF XML.
#  $use_DIF
#  $use_DIF_properties
#  To be fully DIF compliant both should be set to 1.  In that case only certain core DMAC data types are supported. See the %dif_lookup_map below.
#  That maps, e.g. Winds to local variable names: wind_speed,wind_from_direction,wind_gust.  You must have either ASCII data files or
#  Existing OOSTethys configuration files and both ASCII and RDBMS setups should work seemlessly with this option.
#  If use_DIF_properites is set to 0, then no attempt is made to map the top level DMAC data types to mulitple local datatypes.
#  Your local property names will be used, e.g. wind_speed  or wind_direction and only one observation will be returned but DIF formats will be used.
################################################################################
# OGC CITE Testing.
# Changes have been made throughout to support the OGC's CITE Tests for SOS which are still in Beta.
# Thus, e.g. all requests now require service=SOS, etc.  A much stricter requirements then previous versions of the script.
# Most of these tests have been separated: search for CITE and can be commented out if you don't care about the CITE tests.
# If you have use_DIF set to 1 then no CITE required tests are attempted.
################################################################################
# This is a Units of Measure lookup table based on the values and units used by GoMOOS
# It must be edited to match the observedProperty names and units utilized by your service database.
# For Database users all the observedProperties you wish to serve must be listed here.
# It is not used at all by the XML configuration file which contains a <uom> member for each Platform/observedProperty
################################################################################

#### LOCAL EDITS
# To utilize your database set $use_config to 0. To utilize the sos_config.xml file set to 1.
# Note: You can keep set $use_config = 1  get your metadata from your local config file but still get observation data from you database by setting the <SOSDataFile> element to 'DataBase' instead of a local ASCII file name
# E.g. <SOSDataFile>DataBase</SOSDataFile>
our $use_config = 1;

################################################################################
# 04/2009  at the recommendation of UAH and the SOS SpaceTime Toolkit we are switching to using the ISO's UCUM
# see: http://unitsofmeasure.org/
################################################################################

our %uom_lookup = (
	'air_temperature'								=> 'Cel',
	'chlorophyll'									=> 'mg.m-3',
	'dissolved_oxygen'								=> 'ml.l-1',
	'percent_oxygen_saturation'						=> '%',
	'oxygen_saturation'								=> 'ml.l-1',,
	'Ed_PAR'										=> 'W.m-2.sr-1',
	'sea_level_pressure'							=> 'mbar',
	'sea_water_density'								=> 'kg.m-3',
	'sea_water_salinity'							=> 'psu',
 	'sea_water_electrical_conductivity'				=> 'S.m-1',
	'sea_water_speed'								=> 'cm.s-1',
	'direction_of_sea_water_velocity'				=> 'deg',
	'sea_water_temperature'							=> 'Cel',
	'wind_speed'									=> 'm.s-1',
	'wind_gust'										=> 'm.s-1',
	'wind_from_direction'							=> 'deg',
	'visibility_in_air'								=> 'm',
	'significant_height_of_wind_and_swell_waves'	=> 'm',
	'dominant_wave_period'							=> 's',
	'turbidity'										=> 'ntu',
);

################################################################################
# IOOS DIF Options
# There are two.
################################################################################
# Can just use local, CF observed properties or attempt to limit and map local properties to the DIF
# WaterTemperature, Salinity, Currents, Winds, OceanColor, WaterLevel, Waves, etc.
# Often this involves mapping e.g. Winds to wind_speed, wind_direction, wind_gust
################################################################################
our $use_DIF = 0;
our $use_DIF_properties = 0;
############
# You only need to edit these if you are setting $use_DIF = 1; AND use_DIF_properties = 1;
# These map the top-level DIF observedProperties to your CF observedProperties or list of properties.
# E.g. Winds includes WindSpeed, WindDirection and WindGust.
# Note: the order is important.
############
our $dif_recDef="http://www.csc.noaa.gov/ioos/schema/IOOS-DIF/IOOS/0.6.1/recordDefinitions/%sPointDataRecordDefinition.xml";
# dif_lookup_map maps DIF ObservedProperties to all the local observedProperties which are needed.
# For some it's just one to one but for Winds, Waves and Currents DIF expects several observedProperties returned.
our %dif_lookup_map = (
	'WaterTemperature'			=> 'sea_water_temperature',
	'Salinity'					=> 'sea_water_salinity',
	'Currents'					=> 'sea_water_speed,direction_of_sea_water_velocity',
	'Winds'						=> 'wind_speed,wind_from_direction,wind_gust',
	'Waves'						=> 'significant_height_of_wind_and_swell_waves,dominant_wave_period',
	'OceanColor'				=> 'chlorophyll',
	'WaterLevel'				=> 'water_level',
);

# This lookups local observedProperties to the top level DIF properties.
our %dif_lookup2 = (
	'sea_water_temperature'		=> 'WaterTemperature',
	'sea_water_salinity'		=> 'Salinity',
	'chlorophyll'				=> 'OceanColor',
	'sea_water_speed'			=> 'Currents',
	'direction_of_sea_water_velocity' => 'Currents',
	'wind_from_direction'		=> 'Winds',
	'wind_speed'				=> 'Winds',
	'wind_gust'					=> 'Winds',
	'significant_height_of_wind_and_swell_waves' => 'Waves',
	'dominant_wave_period'		=> 'Waves',
);

# This lookups local observedProperties to the second level DIF properties
our %dif_lookup3 = (
	'sea_water_temperature'		=> 'WaterTemperature',
	'sea_water_salinity'		=> 'Salinity',
	'chlorophyll'				=> 'OceanColor',
	'sea_water_speed'			=> 'CurrentSpeed',
	'direction_of_sea_water_velocity' => 'CurrentDirection',
	'wind_from_direction'		=> 'WindDirection',
	'wind_speed'				=> 'WindSpeed',
	'wind_gust'					=> 'WindGust',
	'significant_height_of_wind_and_swell_waves' => 'SignificantWaveHeight',
	'dominant_wave_period'		=> 'DominantWavePeriod',
);

#### END LOCAL EDIT

# This is the location of the XML Templates
# The base_dir can be hard-coded.  We assume the XML Templates are located in the same directory as this script is.
our $base_dir = dirname($0);

our $parser = XML::LibXML->new();

########################################
# EXCEPTION HANDLING
########################################

our $etemplate = <<EOT;
<?xml version="1.0" encoding="UTF-8"?>
<ows:ExceptionReport
   xmlns:ows="http://www.opengis.net/ows/1.1"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   version="1.0.0"
   xsi:schemaLocation="http://schemas.opengis.net/ows/1.1.0/owsExceptionReport.xsd">
   <ows:Exception></ows:Exception>
</ows:ExceptionReport>
EOT

########################################
sub exception_error
{
	my ($code, $msg, $locator) = @_;

	my  @exception_codes = (
	'OperationNotSupported',
	'MissingParameterValue',
	'InvalidParameterValue',
	'VersionNegotiationFailed',
	'InvalidUpdateSequence',
	'NoApplicableCode',
	'InvalidRequest',
	);

	my $exception = $parser->parse_string($etemplate);
	my $xc = XML::LibXML::XPathContext->new($exception);
	registerNameSpaces($exception, $xc);
	my $node = ($xc->findnodes("/ows:ExceptionReport/ows:Exception"))[0];
	my $ecode = $exception_codes[$code];
	$node->setAttribute('exceptionCode', $exception_codes[$code]);
	if($locator){
		$node->setAttribute('locator', $locator);
	}
	$node->appendTextChild('ExceptionText', $msg);
	print header(
			-type => 'text/xml',
	);
	print $exception->serialize;
	exit;
}

########################################
# METADATA
#  All the core operations/requests require some metadata about the sensor and the datatypes the sensor measures.
#  So we set the metadata no matter what the SOS request is.
########################################
# Some Global MetaData Values
# These globals are used by various responses.
# They must be set here if you are using a database for using metadata.
# If you are using the sos_config.xml configuration file they will be overrwritten by the
# values found in the sos_config.xml file.
########################################
#### LOCAL EDIT
# Full name of your organization
our $org = 'Gulf of Maine Ocean Observing System';
our $short_org = 'GoMOOS';
our $org_acronym = 'GoMOOS';
our @key_words = ('OCEANOGRAPHY', 'Ocean Observations', 'NERACOOS', 'GoMOOS', 'Gulf of Maine', 'Weather', 'Ocean Conditions', 'meteorology');
our $org_url = 'http://www.gomoos.org/';
our $title = "Gulf of Maine Ocean Observing System SOS";
our $ra_name = 'NERACOOS';
our $contact = 'Eric Bridger';
our $email = 'eric@gomoos.org';

# This must point to the full URL path to this CGI script.
# Actually $script_name will handle the name of this script, so only the location must be specified.
# NB the sos_config.xml value will overwrite this
my $script_name = basename($0);
our $sos_url = "http://www.gomoos.org/cgi-bin/sos/V1.0/$script_name";

#####################
# platform_uri.  To ensure unique platform identifiers acroos the internet your local platform or sensor id 
# should be turned into a urn.   Typically this should be: urn:your_org.your_domaine:source.moooring#
# The local platform name will be appended after the #
#####################
# OGC CITE Engine wants :ogc: !? That seems wrong
#our $platform_uri = 'urn:gomoos.org:def:source:mooring::';
our $platform_uri = 'urn:ogc:gomoos.org:def:source:mooring:';

if($use_DIF){
	$platform_uri =~ s/#/::/g;
}

# Depends on your data ouput array format.  These are the defaults. for $use_DIF only 
our $dif_platformPos = 0;
our $dif_depthPos = 4;
our $dif_timePos = 1;


#### END LOCAL EDIT

# All observed properties (data types are preceeded by this urn to ensure uniqueness)
# OGC CITE Engine wants :ogc: !? That seems wrong, can't use a url?
#our $observed_property_url = 'http://mmisw.org/cf#';
our $observed_property_url =  'urn:ogc:def:phenomenon:mmisw.org:cf:';
our $foi_urn =  'urn:ogc:def:object:feature:FOI_';
# urn:ogc:def:phenomenon:OGC:1.0.30:temperature
if($use_DIF_properties){
	$observed_property_url = 'http://www.csc.noaa.gov/ioos/schema/IOOS-DIF/IOOS/0.6.1/dictionaries/phenomenaDictionary.xml#';
}

# All observed properties units of measure (uom) are preceeded by this urn to ensure uniqueness
our $uom_urn = 'urn:mmisw.org:units#';
if($use_DIF_properties ){
	$uom_urn = '';
}

# Metadata about each sensor and arrays of observedProperties and Units of Measure reported by that sensor.
my %sensor_list = ();

getMetaData($use_config, \%sensor_list);

if($use_DIF){
	addDIFProp(\%sensor_list);
}

#######################################
# Determine the REQUEST type and make it upper case.
#######################################
my %in_params;

getInputParams(\%in_params);

my $request = '';
$request = $in_params{REQUEST} if ( $in_params{REQUEST});

# Default request is GetCapabilities
#$request = 'GetCapabilities' if not $request;



if(not $use_DIF){

####################################
# BEGIN  OGC CITE TESTS
####################################
exception_error(1, "Missing parameter: $request", 'request') if ( not $request);

exception_error(2, "Unknown request parameter: $request", 'request') if ( $request ne 'GetCapabilities' and $request ne 'GetObservation' and $request ne 'DescribeSensor');

if (not $in_params{SERVICE}){
	exception_error(1, "Service parameter of SOS required", 'service');
}

if ($in_params{SERVICE} ne 'SOS'){
	exception_error(2, "Service parameter of $in_params{SERVICE} is invalid", 'service');
}


if($request eq 'GetCapabilities'){
	if ($in_params{ACCEPTVERSIONS}){
		my @platforms = keys %sensor_list;
		my $SensorID = $platforms[0];
		if( $in_params{ACCEPTVERSIONS} ne $sensor_list{$SensorID}{AcceptVersions}){
			exception_error(3, "Invalid AcceptVersions '$in_params{ACCEPTVERSIONS}", 'acceptversions' );
		}
	}
	# SECTIONS are handled in doGetCapabilities()
}

if($request eq 'DescribeSensor'){
	my $SensorID = $in_params{PROCEDURE} if $in_params{PROCEDURE};
	my @tmp = ();
	if($SensorID =~ /#/){
		@tmp = split(/#/, $SensorID);
	}else{
		@tmp = split(/:/, $SensorID);
	}
	$SensorID =  $tmp[$#tmp];
	if(not $in_params{OUTPUTFORMAT} ){
		exception_error(1, "Missing parameter", 'outputFormat');
	}
	# this must match the outputFormat in sosGetCapabilities.xml
	if( $in_params{OUTPUTFORMAT} ne $sensor_list{$SensorID}{outputFormat}){
		exception_error(2, "Invalid outputFormat '$in_params{OUTPUTFORMAT}'", 'outputFormat');
	}
}

if($request eq 'GetObservation'){
	my $SensorID = $in_params{OFFERING} if ($in_params{OFFERING});
	my @tmp = ();
	if($SensorID =~ /#/){
		@tmp = split(/#/, $SensorID);
	}else{
		@tmp = split(/:/, $SensorID);
	}
	$SensorID =  $tmp[$#tmp];

	if( $in_params{SRSNAME} and $in_params{SRSNAME} ne $sensor_list{$SensorID}{srsName}){
		exception_error(2, "Invalid srsName '$in_params{SRSNAME}'", 'srsName');
	}
	if( not $in_params{RESPONSEFORMAT}){
		exception_error(1, "Missing parameter", 'responseFormat');
	}
	if( $in_params{RESPONSEFORMAT} ne $sensor_list{$SensorID}{responseFormat}){
		exception_error(2, "Invalid responseFormat $in_params{RESPONSEFORMAT}'", 'responseFormat');
	}
	if( $in_params{RESPONSEMODE} and $in_params{RESPONSEMODE} ne $sensor_list{$SensorID}{responseMode}){
		exception_error(2, "Invalid responseMode '$in_params{RESPONSEMODE}'", 'responseMode');
	}
	if( $in_params{RESULTMODEL} and $in_params{RESULTMODEL} ne $sensor_list{$SensorID}{resultModel}){
		exception_error(2, "Invalid resultModel '$in_params{RESULTMODEL}'", 'resultModel');
	}
	if( $in_params{PROCEDURE} ){
		if($in_params{PROCEDURE} ne $sensor_list{$SensorID}{urn} ){
			exception_error(2, "Invalid procedure $in_params{PROCEDURE}", 'procedure');
		}
	}
	if( $in_params{FEATUREOFINTEREST}){
		if( $in_params{FEATUREOFINTEREST} ne $foi_urn . $SensorID){
			exception_error(2, "Invalid featureOfInterest. '$in_params{FEATUREOFINTEREST}'", 'featureOfInterest');
		}
	}
}

####################################
# END OGC CITE Tests
####################################

} # end if not use_DIF

####################################################
# Call proper sub routine based on in_params
####################################################

my $sos;
my ($SensorID, $observedProperty) = ('','','', '');
#################################################################
# GetCapabilities
#################################################################
if( $request eq 'GetCapabilities' ){
	# GetCapabilities has no parameters we care about
	# in fact we  should check the SERVICE=SOS and the VERSION=1.0.0 parameters
	$sos = doGetCapabilities(\%in_params, \%sensor_list);
}
#################################################################
# DescribeSensor
#################################################################
#  Required Input Parameters
#     procedure:   local sensor name optionally preceeded by the platform urn:
#        Examples:  procedure=urn:gomoos.org:source.mooring#A01
#                   procedure=urn:gomoos.org:source.mooring:A01
#################################################################
if( $request eq 'DescribeSensor' ){
	$SensorID = $in_params{PROCEDURE} if $in_params{PROCEDURE};
	exception_error(1, "procedure parameter required", 'procedure') if (!$SensorID);

	# deal with 2 possible urns '#' vs ':' (both contain :)
	# $tmp[$#tmp] (the list element is the value we want
	my @tmp = ();
	if($SensorID =~ /#/){
		@tmp = split(/#/, $SensorID);
	}else{
		@tmp = split(/:/, $SensorID);
	}
	$SensorID =  $tmp[$#tmp];
	exception_error(1, "procedure parameter required", 'procedure') if (!$SensorID);
	$sos = doDescribeSensor($SensorID, \%sensor_list);
}
#################################################################
# GetObservation
#  Required Input Parameters
#     offering:           local sensor name optionally preceeded by the platform urn:
#     observedProperty:   local observedProperty name optionally preceeded by the MMI uri:
#  Optional Input Parameters
#     observedProperty:   multiple properties as a comma separated list.
#     time:               ISO8601 formatted time or time range (time1/time2)
#     bbox:               minimum_longitude,minimum_latitude,maximum_longitude,maximum_latitude
#
#     The default is to currently return the most recent or latest observedProperty for the offering.
#################################################################
if( $request eq 'GetObservation' ){
	$SensorID = $in_params{OFFERING} if ($in_params{OFFERING});
	exception_error(1, "offering parameter required", 'offering') if (!$SensorID);

	# deal with 2 possible urns '#' vs ':' (both contain :)
	# $tmp[$#tmp] (the list element is the value we want
	my @tmp = ();
	if($SensorID =~ /#/){
		@tmp = split(/#/, $SensorID);
	}else{
		@tmp = split(/:/, $SensorID);
	}
	$SensorID =  $tmp[$#tmp];

	$observedProperty = $in_params{OBSERVEDPROPERTY} if $in_params{OBSERVEDPROPERTY};
	exception_error(1, "observedProperty parameter required", 'observedProperty') if (!$observedProperty);


	$sos = doGetObservation($SensorID, \%in_params, \%sensor_list);
}

########################################
# Now build the SOS from XML templates
########################################

my $sos_xml = $sos->serialize;

print header(
		-type => 'text/xml',
);

print $sos_xml;

exit;

#################################################################
#  SUBROUTINES
#################################################################

#################################################################
#  GetCapabilities
#################################################################
sub doGetCapabilities
{
	my ($in_params, $sensor_list) = @_;

 	my $sos = undef;
	if($use_DIF){
		$sos = $parser->parse_file("$base_dir/difGetCapabilities.xml");
	}else{
		$sos = $parser->parse_file("$base_dir/sosGetCapabilities.xml");
	}
	exception_error(5, "Could not open SOS template: sosGetCapabilities.xml") if ! $sos;
	
	my $tmpl_node = ($sos->findnodes("//sos:Capabilities/ows:ServiceIdentification/ows:Title"))[0];
	$tmpl_node->appendText($title) if ($tmpl_node);

	$tmpl_node = ($sos->findnodes("//sos:Capabilities/ows:ServiceIdentification/ows:Keywords"))[0];
	if(@key_words and $tmpl_node) {
		foreach my $child ($tmpl_node->childNodes){
			$tmpl_node->removeChild($child);
		}
		foreach my $word (@key_words){
			my $kw = $sos->createElement('ows:Keyword');
			$kw->appendText($word);
			$tmpl_node->appendChild($kw);
		}
	}

	
	$tmpl_node = ($sos->findnodes("//sos:Capabilities/ows:ServiceProvider/ows:ProviderName"))[0];
	$tmpl_node->appendText($org_acronym);
	
	$tmpl_node = ($sos->findnodes("//sos:Capabilities/ows:ServiceProvider/ows:ProviderSite"))[0];
	$tmpl_node->setAttribute('xlink:href', $org_url);
	
	# Another way to handle attributes
	#my @attrs = $tmpl_node->attributes();
	#$attrs[0]->setValue($org_url);
	
	$tmpl_node = ($sos->findnodes("//sos:Capabilities/ows:ServiceProvider/ows:ServiceContact/ows:IndividualName"))[0];
	$tmpl_node->appendText($contact);
	
	$tmpl_node = ($sos->findnodes("//sos:Capabilities/ows:ServiceProvider/ows:ServiceContact/ows:ContactInfo/ows:Address/ows:ElectronicMailAddress"))[0];
	$tmpl_node->appendText("$email");
	
	#############################
	# Three Operations: GetCapabilities, GetObservation and Describe Sensor
	#############################
	foreach my $node ( $sos->findnodes("//sos:Capabilities/ows:OperationsMetadata/ows:Operation") ){
		my $op = $node->getAttribute('name');
		# We assume all use the same sos_url with the Operation appended. Perhaps this should be a ? vs a /
		# Java and Perl may handle this differently 
		foreach my $http ($node->findnodes("ows:DCP/ows:HTTP/*")->get_nodelist){
			$http->setAttribute('xlink:href', "$sos_url");
		};
	
		if($op eq 'GetObservation' ){
			foreach my $param ($node->findnodes("ows:Parameter")){
				# offering is the name of the GetObservation sensor Parameter.
				if($param->getAttribute('name') eq 'offering'){
					# Here we are creating a piece of xml outside the template.<ows:Value></ows:Value>.
					# so get rid of any existing children.
					foreach my $child ($param->childNodes){
						$param->removeChild($child);
					}
					# add a Value for each sensor, but only 1
					my %done;
					my $allowedVals = $sos->createElement('ows:AllowedValues');
					foreach my $sensor (sort keys %{$sensor_list} ){
						next if exists($done{$sensor});
						$allowedVals->appendTextChild('ows:Value', $sensor);
						$done{$sensor} = 1;
					}
					$param->appendChild($allowedVals);
				} # end offering

				if($param->getAttribute('name') eq 'observedProperty'){
					foreach my $child ($param->childNodes){
						$param->removeChild($child);
					}
					# create a list of all observed properties across all sensors.
					my %obsprops = ();
					foreach my $sensor (sort keys %{$sensor_list} ){
						if($use_DIF and $use_DIF_properties){
							foreach my $prop (sort keys %{ $sensor_list->{$sensor}->{difproplist} } ) {
								$obsprops{$prop} = 1;
							}
						}else{
							foreach my $prop (sort keys %{ $sensor_list->{$sensor}->{obsproplist} } ) {
								$obsprops{$prop} = 1;
							}
						}
					}
					my $allowedVals = $sos->createElement('ows:AllowedValues');
					foreach my $prop (sort keys %obsprops ){
						$allowedVals->appendTextChild('ows:Value', $prop);
					}
					$param->appendChild($allowedVals);
				} # end observedProperty

				if($param->getAttribute('name') eq 'eventTime'){
					# create a list of  min and max times across all sensors
					# we need the minimum and maximum of all sensor times
					my $min_time = '9999-01-01T00:00:00Z';
					my $max_time = '';
					foreach my $sensor (sort keys %{$sensor_list}){
						my $stime = $sensor_list->{$sensor}->{start_time};
						my $etime = $sensor_list->{$sensor}->{end_time};
						if($stime lt $min_time){
							$min_time = $stime;
						}
						# empty implies now
						if($etime gt $max_time or $etime eq ''){
							$max_time = $etime;
						}

					}
					if($max_time eq ''){
						$max_time = 'now';
					}
					my $node2 = ($param->findnodes("ows:AllowedValues/ows:Range/ows:MinimumValue"))[0];
					$node2->appendText($min_time);
					$node2 = ($param->findnodes("ows:AllowedValues/ows:Range/ows:MaximumValue"))[0];
					$node2->appendText($max_time);

				} # end eventTime

			} # end for each Parameter
		} # end GetObservation
		if($op eq 'DescribeSensor'){
			foreach my $param ($node->findnodes("ows:Parameter")){
				if($param->getAttribute('name') eq 'procedure'){
					foreach my $child ($param->childNodes){
						$param->removeChild($child);
					}
					# add a Value for each sensor, but only 1
					my %done;
					my $allowedVals = $sos->createElement('ows:AllowedValues');
					foreach my $sensor (sort keys %{$sensor_list} ){
						next if exists( $done{$sensor});
						$allowedVals->appendTextChild('ows:Value', $sensor_list->{$sensor}->{urn});

						$done{$sensor} = 1;
					}
					$param->appendChild($allowedVals);
				}
			}
		}
	}
	
	################################################################
	# Observation Offering List, one for each Sensor and Parameter.
	################################################################
	my $offering_list = ($sos->findnodes("//sos:Capabilities/sos:Contents/sos:ObservationOfferingList"))[0];
	
	# Get the first entry from the OfferingList. This is used as the template to build the
	# Offering for each sensor and is then deleted below
	my $offering = ( $offering_list->findnodes("sos:ObservationOffering") )[0];
	# TODO: what depth to use for a sensor?
	my $depth = 0;	
	my %done;
	foreach my $sensor (sort keys %{$sensor_list} ){
		# In sensor_list there is one entry for each sensorID and parameter but we only want
		# one per sensor
		next if exists($done{$sensor});
		my $offer = $offering->cloneNode(1);
		$offer->setAttribute('gml:id', $sensor);
	
		my $node = ($offer->findnodes("gml:description"))[0];
		$node->appendText($sensor_list->{$sensor}->{comments});

		$node = ($offer->findnodes("gml:name"))[0];
		$node->appendText($sensor);
	
		# save this as ref  placeholder for property insertion.
		my $procedure = ($offer->findnodes("sos:procedure"))[0];
		$procedure->setAttribute('xlink:href', $sensor_list->{$sensor}->{urn});
		
		# For CITE added featureOfInterest but they just map 1:1 with platforms
		my $foi = ($offer->findnodes("sos:featureOfInterest"))[0];
		$foi->setAttribute('xlink:href', $foi_urn . $sensor);
	
		$node = ($offer->findnodes("gml:boundedBy/gml:Envelope/gml:lowerCorner"))[0];
		$node->appendText( $sensor_list->{$sensor}->{lat} . ' ' . $sensor_list->{$sensor}->{lon} . ' ' . $depth );
		$node = ($offer->findnodes("gml:boundedBy/gml:Envelope/gml:upperCorner"))[0];
		if($sensor eq 'ALL_PLATFORMS'){
			$node->appendText('46.0 -63.0' . ' ' . $depth );
		}else{
			$node->appendText($sensor_list->{$sensor}->{lat} . ' ' . $sensor_list->{$sensor}->{lon} . ' ' . $depth );
		}

		$node = ($offer->findnodes("sos:time/gml:TimePeriod"))[0];
		$node->setAttribute('gml:id', $sensor . '_valid_times');

		$node = ($offer->findnodes("sos:time/gml:TimePeriod/gml:beginPosition"))[0];
		$node->appendText($sensor_list->{$sensor}->{start_time} ) if($sensor_list->{$sensor}->{start_time} ne "");
		$node->setAttribute('indeterminatePosition', 'unknown') if($sensor_list->{$sensor}->{start_time} eq "");

		$node = ($offer->findnodes("sos:time/gml:TimePeriod/gml:endPosition"))[0];
		# empty Time endPostion implies data available up to now.
		$node->appendText($sensor_list->{$sensor}->{end_time} ) if($sensor_list->{$sensor}->{end_time} ne ""); 
		$node->setAttribute('indeterminatePosition', 'now') if($sensor_list->{$sensor}->{end_time} eq "");

	
		# Get rid of existing properites
		foreach my $node ($offer->findnodes("sos:observedProperty")){
			$offer->removeChild($node);
		}
	
		if($use_DIF and $use_DIF_properties){
			foreach my $param (sort keys %{ $sensor_list->{$sensor}->{difproplist} } ){
				my $property = $sos->createElement('sos:observedProperty');
				my $urn = $observed_property_url . $param;
				$property->setAttribute('xlink:href', $urn);
				$offer->insertAfter($property, $procedure);
			} # end for each $#params
		}else{
			foreach my $param (sort keys %{ $sensor_list->{$sensor}->{obsproplist} } ){
				my $property = $sos->createElement('sos:observedProperty');
				my $urn = $observed_property_url . $param;
				$property->setAttribute('xlink:href', $urn);
				$offer->insertAfter($property, $procedure);
			} # end for each $#params
		}
	
		$offering_list->appendChild($offer);
		$done{$sensor} = 1;
	
	} # end foreach sensor_list
	
	# Delete the existing child from the Offering List. we're just using it as template for new entries
	$offering_list->removeChild($offering);

	if(not $use_DIF){

		# CITE  SECTIONS
		# Should grab these from the Sections allowed values in sosGetCap.xml
		my @all_sections = qw( ows:ServiceIdentification ows:ServiceProvider ows:OperationsMetadata sos:Contents sos:Filter_Capabilities);
		# Handle any SECTIONS request
		if ( exists( $in_params->{SECTIONS} ) ){
			# If sections is null remove all optional sections
			if(not $in_params->{SECTIONS} ){
				my $remove_node = ($sos->findnodes("//sos:Capabilities/sos:Filter_Capabilities"))[0];
				$remove_node->unbindNode();
			}elsif( $in_params->{SECTIONS} =~ /^all$/i ){ # nothing to remove if All

			}else{ # remove sections unless in the sections param
				my $remove_node = ($sos->findnodes("//sos:Capabilities/sos:Filter_Capabilities"))[0];
				$remove_node->unbindNode();
				$remove_node = ($sos->findnodes("//sos:Capabilities/ows:ServiceProvider"))[0];
				$remove_node->unbindNode();
				$remove_node = ($sos->findnodes("//sos:Capabilities/sos:Contents"))[0];
				$remove_node->unbindNode();
				# Should Fix up Sections allowed values
			} # end else SECTIONS

		} # end if SECTIONS
	} # end if not $use_DIF

	return $sos;
}

#################################################################
#  doDescribeSensor()
#################################################################
sub doDescribeSensor
{
	my ($sensorID, $sensor_list) = @_;

	# Hmmmm.  The NDBC and NOS responses are very bare bones so I'm not sure this is worth it. Just postion and name info
	# Plus the NDBC PHP seems like it should produce the NOS style but the actual SOS produces something else
	if($use_DIF){
		return doDescribeSensorDIF($sensorID, $sensor_list);
	}else{
		return doDescribeSensorSWE($sensorID, $sensor_list);
	}
}
sub doDescribeSensorSWE
{
	my ($sensorID, $sensor_list) = @_;


	# TODO what depth to use for a sensor?
	my ($lat, $lon, $depth, $urn, $long_name, $comments, $provider_name) = ('','',0,'', '', '', '');
	my ($stime, $etime) = ('','');
	# Get info for this sensor and param.
	if( not exists($sensor_list->{$sensorID})){
		exception_error(2, "Unknown DescribeSensor SensorId: $sensorID", 'procedure');
	}
	$comments = $sensor_list->{$sensorID}->{comments};
	$lat = $sensor_list->{$sensorID}->{lat};
	$lon = $sensor_list->{$sensorID}->{lon};
	$urn = $sensor_list->{$sensorID}->{urn};
	$long_name = $sensor_list->{$sensorID}->{long_name};
	$stime = $sensor_list->{$sensorID}->{start_time};
	$etime = $sensor_list->{$sensorID}->{end_time};
	$provider_name = $sensor_list->{$sensorID}->{provider_name};

	my $sos = $parser->parse_file("$base_dir/sosDescribeSensor.xml");
	exception_error(5, "Could not open SOS template: sosDescribeSystem.xml") if ! $sos;
	# Because the sosDescribeSensor.xml template has a default namespace for SML with no sml: prefixes
	# we need to use XPathContext to register the namespace. This allows XPath queries
	# using sml: The XPath Spec makes no provisions for default namespaces for some reason.
	my $xc = XML::LibXML::XPathContext->new($sos);
	registerNameSpaces($sos, $xc);

	my $node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System"))[0];
	$node->setAttribute('gml:id', $provider_name . '_' . $sensorID);

	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/gml:description"))[0];
	$node->appendText($comments);

	# Keywords
	if(@key_words){
		#$node = ($xc->findnodes("//sos:Capabilities/ows:ServiceIdentification/ows:Keywords"))[0];
		$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:keywords/sml:KeywordList"))[0];
		foreach my $child ($node->childNodes){
			$node->removeChild($child);
		}
		foreach my $word (@key_words){
			my $kw = $sos->createElement('keyword');
			$kw->appendText($word);
			$node->appendChild($kw);
		}
	}


	#############################
	# Six Identifications.
	#############################
	#foreach my $node ( $xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:identification/sml:IdentifierList/sml:identifier/sml:Term") ){
	foreach my $node ( $xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:identification/sml:IdentifierList/sml:identifier") ){
		my $name = $node->getAttribute('name');
		# Note: needed the sml: prefix was trying "value" which did not work
		my $value = ($xc->findnodes("sml:Term/sml:value", $node))[0];
		$value->appendText($long_name) if ($name eq 'Long Name');
		$value->appendText($sensorID) if ($name eq 'Short Name');
		$value->appendText($urn) if ($name eq 'URN');
		$value->appendText($provider_name) if ($name =~ 'Operator Short Name');
		$value->appendText($provider_name) if ($name =~ 'Data Provider Short Name');
		$value->appendText($ra_name) if ($name =~ 'Regional Association Short Name');
	}

	#############################
	# validTime
	#############################
	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:validTime/gml:TimePeriod/gml:beginPosition"))[0];
	$node->appendText($stime );
	$node->setAttribute('indeterminatePosition', 'unknown') if (not $stime);
	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:validTime/gml:TimePeriod/gml:endPosition"))[0];
	$node->appendText( $etime );
	$node->setAttribute('indeterminatePosition', 'now') if (not $etime);

	#############################
	# Contact Info
	#############################
	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:contact/sml:ResponsibleParty/sml:individualName"))[0];
	$node->appendText($contact);

	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:contact/sml:ResponsibleParty/sml:organizationName"))[0];
	$node->appendText($provider_name);
	
	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:contact/sml:ResponsibleParty/sml:contactInfo/sml:address/sml:electronicMailAddress"))[0];
	$node->appendText("$email");

	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:contact/sml:ResponsibleParty/sml:contactInfo/sml:onlineResource"))[0];
	$node->setAttribute('xlink:href', $org_url );


	#############################
	# classification
	#############################
	foreach $node ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:classification/sml:ClassifierList/sml:classifier")){
		my $name = $node->getAttribute('name');
		if($name eq 'Platform'){
			my $node2 = ($xc->findnodes("sml:Term/sml:value", $node))[0];
			$node2->appendText($sensorID);
		}
	}

	#############################
	# location
	#############################

	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:location/gml:Point/gml:coordinates"))[0];
	$node->appendText("$lat $lon $depth");

	#############################
	#  Output and DataRecord
	#############################
	my $output = ( $xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:outputs/sml:OutputList/sml:output/swe:DataArray/swe:elementType") )[0];
	my @params = (sort keys %{ $sensor_list->{$sensorID}->{obsproplist} } );
	setDataRecord($xc, $output, $sensorID, $sensor_list, \@params);

	my $element_count = checkDataRecord($xc);

	$node = ( $xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:outputs/sml:OutputList/sml:output/swe:DataArray/swe:elementCount/swe:Count/swe:value") )[0];
	$node->appendText( $element_count );


	return $sos;
}

sub doDescribeSensorDIF
{
	my ($sensorID, $sensor_list) = @_;


	# TODO what depth to use for a sensor?
	my ($lat, $lon, $depth, $urn, $long_name, $comments, $provider_name) = ('','',0,'', '', '', '');
	my ($stime, $etime) = ('','');
	# Get info for this sensor and param.
	if( not exists($sensor_list->{$sensorID})){
		exception_error(2, "Unknown DescribeSensor SensorId: $sensorID", 'procedure');
	}
	$comments = $sensor_list->{$sensorID}->{comments};
	$lat = $sensor_list->{$sensorID}->{lat};
	$lon = $sensor_list->{$sensorID}->{lon};
	$urn = $sensor_list->{$sensorID}->{urn};
	$long_name = $sensor_list->{$sensorID}->{long_name};
	$stime = $sensor_list->{$sensorID}->{start_time};
	$etime = $sensor_list->{$sensorID}->{end_time};
	$provider_name = $sensor_list->{$sensorID}->{provider_name};

	my $sos = $parser->parse_file("$base_dir/difDescribeSensor.xml");
	exception_error(5, "Could not open SOS template: sosDescribeSystem.xml") if ! $sos;
	# Because the sosDescribeSensor.xml template has a default namespace for SML with no sml: prefixes
	# we need to use XPathContext to register the namespace. This allows XPath queries
	# using sml: The XPath Spec makes no provisions for default namespaces for some reason.
	my $xc = XML::LibXML::XPathContext->new($sos);
	registerNameSpaces($sos, $xc);

	my $node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System"))[0];
	$node->setAttribute('gml:id', $provider_name . '_' . $sensorID);
 	$node = ($xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:contact/sml:ResponsibleParty/sml:organizationName"))[0];
 	$node->appendText($provider_name) if ($node);

	#############################
	# DIF only has Three Identifications.
	#############################
	foreach my $node ( $xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:identification/sml:IdentifierList/sml:identifier") ){
		my $name = $node->getAttribute('name');
		my $value = ($xc->findnodes("sml:Term/sml:value", $node))[0];
		#$value->appendText($long_name) if ($name =~ /longName$/);
		$value->appendText($comments) if ($name =~ /longName$/);
		$value->appendText($sensorID) if ($name =~ /shortName$/);
		$value->appendText($urn) if ($name =~ /SensorId$/);

	}

	#############################
	# location
	#############################
	foreach my $node ( $xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:positions/sml:PositionList/sml:position/swe:GeoLocation") ){
		my $value = ($xc->findnodes("swe:latitude/swe:Quantity", $node))[0];
		$value->appendText($lat);
		$value = ($xc->findnodes("swe:longitude/swe:Quantity", $node))[0];
		$value->appendText($lon);
	}

	#############################
	#  outputs and DataRecord. Neither NDBC nor NOS use this yet. NOS has an inputs array as well
	#############################
	# DIF data types are different, e.g. Winds includes 3 or 4 single data types
	# This means UOM's don't work here
	my $output = ( $xc->findnodes("//sml:SensorML/sml:member/sml:System/sml:outputs/sml:OutputList") )[0];

	my @params = ();
	if($use_DIF_properties){
		@params = (sort keys %{ $sensor_list->{$sensorID}->{difproplist} } );
	}else{
		# This give local values but the template references IOOS Dictionary.xml
		@params = (sort keys %{ $sensor_list->{$sensorID}->{obsproplist} } );
	}
	setDataRecord($xc, $output, $sensorID, $sensor_list, \@params);

	return $sos;
}

#################################################################
sub doGetObservation
{
	my ($sensorID, $in_params, $sensor_list) = @_;
	if($use_DIF){
		return doGetObservationDIF($sensorID, $in_params, $sensor_list);
	}else{
		return doGetObservationSWE($sensorID, $in_params, $sensor_list);
	}

}
#################################################################
sub doGetObservationSWE
{
	my ($sensorID, $in_params, $sensor_list) = @_;

	my $in_time = $in_params->{EVENTTIME};
	# $OBSERVEDPROPERTY can be a comma separated list.
	my @params = split(',', $in_params->{OBSERVEDPROPERTY});
	# get rid of the uri portion
	for my $i (0..$#params){
		my @tmp = ();
		if($params[$i] =~ /#/){
			@tmp = split(/#/, $params[$i]);
		}else{
			@tmp = split(/:/, $params[$i]);
		}
		$params[$i] = $tmp[$#tmp];
	}

	my $bbox = $in_params->{BBOX};

	if( not exists($sensor_list->{$sensorID})){
		exception_error(2, "Unknown GetObservation sensor: $sensorID", 'offering');
	}
	foreach my $param ( @params ){
		if ( not exists( $sensor_list->{$sensorID}->{obsproplist}{$param} ) ){
			exception_error(2, "Unknown GetObservation observedPropetry: $param", 'observedProperty');
		}

	}
	my $provider_name = $sensor_list->{$sensorID}->{provider_name};
	my $sensor_urn = $sensor_list->{$sensorID}->{urn};
	my $lat = $sensor_list->{$sensorID}->{lat};
	my $lon = $sensor_list->{$sensorID}->{lon};
	# TODO: depth issue platform depth is 0
	my 	$depth = 0;
	
	my $sos = $parser->parse_file("$base_dir/sosGetObservation.xml");

	exception_error(5, "Could not open SOS template: sosGetObservation.xml") if not $sos;

	my $xc = XML::LibXML::XPathContext->new($sos);
	registerNameSpaces($sos, $xc);

	my $node;

	# Get the blockSeparator and tokenSeparator from the xml template to ensure they match.
	# This allows users to define their own blockSeparator, e.g. blockSeparator="&10;" if you want to use newlines.
	my $node =  ($xc->findnodes('//swe:encoding/swe:TextBlock'))[0];
	my $blockSep = $node->getAttribute('blockSeparator');
	my $tokenSep = $node->getAttribute('tokenSeparator');
	my @output_data = getData($sensorID, $in_time, $bbox, \@params, $sensor_list, $tokenSep);

	my $stime = '';
	my $etime = '';
	if(@output_data){
		# use the first record time for the beginPosition time
		#my @vals = split(/$tokenSep/, $output_data[0]);
		my @vals = split(/$tokenSep/, $output_data[0]);
		$stime = $vals[1];
		$stime .= 'Z' if $stime !~ /Z/;
		# use the last record's time for the TimeInstant element.
		@vals = split(/$tokenSep/, $output_data[$#output_data]);
		$etime = $vals[1];
		$etime .= 'Z' if $stime !~ /Z/;
	}

	my $root = $sos->documentElement();
 	$root->setAttribute('gml:id', $provider_name . '_sensor');

	# using the same description and name and lowerCorner and upperCorner
	# for both the <ObservationCollection> and <Observation>
	my $description = uc($params[0]);
	$description = 'Observable' if(@params > 1);
	$description .=  " measurements from $provider_name $sensorID";

	foreach $node ($xc->findnodes("//gml:description")){
		$node->appendText( $description );
	}

	$node = ( $xc->findnodes("/om:ObservationCollection/gml:name") )[0];
	$node->appendText($description);
	$node = ( $xc->findnodes("/om:ObservationCollection/om:member/om:Observation/gml:name") )[0];
	$node->appendText($description);

	# this is different than root gml:id above.
	$node = ( $xc->findnodes("/om:ObservationCollection/om:member/om:Observation") )[0];
 	$node->setAttribute('gml:id', $provider_name . '_' . $sensorID );
	# FOI
	$node = ( $xc->findnodes("/om:ObservationCollection/om:member/om:Observation/om:featureOfInterest") )[0];
 	$node->setAttribute('xlink:href', $foi_urn . $sensorID );

	#################
	# GML boundedBy, appears twice
	#################
	foreach $node ($xc->findnodes("//gml:lowerCorner")){
		$node->appendText("$lat $lon $depth");
	}
	foreach $node ($xc->findnodes("//gml:upperCorner")){
		$node->appendText("$lat $lon $depth");
	}

	#################
	# DATA_TIME Instant or Period
	#################
	my $t_elem;
	if($stime eq $etime){
		$t_elem = $sos->createElement('gml:TimeInstant');
		$t_elem->setAttribute('xsi:type', 'gml:TimeInstantType' );
		$t_elem->appendTextChild('gml:timePosition', $etime);
	}else{
		$t_elem = $sos->createElement('gml:TimePeriod');
		$t_elem->setAttribute('xsi:type', 'gml:TimePeriodType' );
		$t_elem->appendTextChild('gml:beginPosition', $stime);
		$t_elem->appendTextChild('gml:endPosition', $etime);
	}

	$node = ($xc->findnodes("//om:Observation/om:samplingTime"))[0];

	$node->appendChild($t_elem);

	#################
	# procedure or sensor URN
	#################
	$node = ($xc->findnodes("//om:Observation/om:procedure"))[0];
	$node->setAttribute('xlink:href', $sensor_urn );

	#################
	# observedProperty, can be one or more, but we always use CompositePhenomenon which can have a dimesnion of 1
	#################
	$node = ($xc->findnodes("//om:Observation/om:observedProperty/swe:CompositePhenomenon"))[0];
	$node->setAttribute('dimension', scalar(@params));
	#$node->appendTextChild('gml:name', 'Buoy Observables');
	foreach my $param (@params){
		my $obs_prop = $sos->createElement('swe:component');
		$obs_prop->setAttribute('xlink:href', $observed_property_url . $param);
		$node->appendChild($obs_prop);
	}


	$node = ($xc->findnodes("//om:Observation/om:result/swe:DataArray/swe:elementType"))[0];

	setDataRecord($xc, $node, $sensorID, $sensor_list, \@params);
	# element_count refers to the number of observation records we are returning.
	my $element_count = @output_data;

	$node = ($xc->findnodes("//om:Observation/om:result/swe:DataArray/swe:elementCount/swe:Count/swe:value"))[0];
	$node->appendText( $element_count );

	# Add the data
	my $results = '';
	foreach (@output_data){
		chomp;
		$results .= $_ . $blockSep;
	}
	$results =~ s/ $//;
	$node = ($xc->findnodes("//om:Observation/om:result/swe:DataArray/swe:values"))[0];
	$node->appendText($results);

	return $sos;
}

#################################################################
sub doGetObservationDIF
{
	my ($sensorID, $in_params, $sensor_list) = @_;

	my $in_time = $in_params->{EVENTTIME};
	# $OBSERVEDPROPERTY can be a comma separated list.
	my @params = split(',', $in_params->{OBSERVEDPROPERTY});
	# get rid of the uri portion
	for my $i (0..$#params){
		my @tmp = ();
		if($params[$i] =~ /#/){
			@tmp = split(/#/, $params[$i]);
		}else{
			@tmp = split(/:/, $params[$i]);
		}
		$params[$i] = $tmp[$#tmp];
	}

	my $bbox = $in_params->{BBOX};

	if( not exists($sensor_list->{$sensorID})){
		exception_error(2, "Unknown GetObservation sensor: $sensorID", 'offering');
	}
	foreach my $param ( @params ){
		if ( not exists( $sensor_list->{$sensorID}->{obsproplist}{$param} ) ){
			exception_error(2, "Unknown GetObservation observedPropetry: $param", 'observedProperty');
		}

	}
 	my $provider_name = $sensor_list->{$sensorID}->{provider_name};
	my $long_name = $sensor_list->{$sensorID}->{long_name};
	my $sensor_urn = $sensor_list->{$sensorID}->{urn};
	my $lat = $sensor_list->{$sensorID}->{lat};
	my $lon = $sensor_list->{$sensorID}->{lon};


	my $node;

	# Get the blockSeparator and tokenSeparator from the xml template to ensure they match.
	# This allows users to define their own blockSeparator, e.g. blockSeparator="&10;" if you want to use newlines.
	# The DIF does not support swe: block and tokenSeparators, so get them from the sosGetObservation template
	my $sos = $parser->parse_file("$base_dir/sosGetObservation.xml");
	my $xc = XML::LibXML::XPathContext->new($sos);
	registerNameSpaces($sos, $xc);

	my $node =  ($xc->findnodes('//swe:encoding/swe:TextBlock'))[0];
	my $blockSep = $node->getAttribute('blockSeparator');
	my $tokenSep = $node->getAttribute('tokenSeparator');

	# Now the DIF format	
	$sos = $parser->parse_file("$base_dir/difGetObservation.xml");

	exception_error(5, "Could not open SOS template: sosGetObservation.xml") if not $sos;

	$xc = XML::LibXML::XPathContext->new($sos);
	registerNameSpaces($sos, $xc);


	my @output_data = getData($sensorID, $in_time, $bbox, \@params, $sensor_list, $tokenSep);
	my $stime = '';
	my $etime = '';
	if(@output_data){
		# use the first record time for the beginPosition time
		#my @vals = split(/$tokenSep/, $output_data[0]);
		my @vals = split(/$tokenSep/, $output_data[0]);
		$stime = $vals[1];
		$stime .= 'Z' if $stime !~ /Z/;
		# use the last record's time for the TimeInstant element.
		@vals = split(/$tokenSep/, $output_data[$#output_data]);
		$etime = $vals[1];
		$etime .= 'Z' if $stime !~ /Z/;
	}

	my $root = $sos->documentElement();
	my $difName = $dif_lookup2{$params[0]};
	if($use_DIF_properties){
		$difName = $dif_lookup2{$params[0]};
	}else{
		$difName = $params[0];
	}

	if($difName =~ /currents/i or $difName eq 'sea_water_speed' or $difName eq 'direction_of_sea_water_velocity'){
		$root->setAttribute('gml:id', $difName . 'VerticalProfileCollectionTimeSeriesObservation');
	}else{
		$root->setAttribute('gml:id', $difName . 'PointCollectionTimeSeriesObservation');
	}

	#################
	# om:observedProperty HERE how to handle multiple observeredProperites
	#################
	$node = ($xc->findnodes("//om:CompositeObservation/om:observedProperty"))[0];
	$node->setAttribute( 'xlink:href', $observed_property_url . $difName );

	# using the same description and name and lowerCorner and upperCorner
	# for both the <ObservationCollection> and <Observation>
	my $description = uc($params[0]);
	$description = 'Observable' if(@params > 1);
	$description .=  " measurements from $provider_name $sensorID";

	foreach $node ($xc->findnodes("//gml:description")){
		$node->appendText( $description );
	}

	$node = ( $xc->findnodes("/om:CompositeObservation/gml:name") )[0];
	$node->appendText($description);

	#$node = ( $xc->findnodes("/om:CompositeObservation/om:member/om:Observation/gml:name") )[0];
	#$node->appendText($description);

	# this is different than root gml:id above.
	#$node = ( $xc->findnodes("/om:CompositeObservation/om:member/om:Observation") )[0];
	#$node->setAttribute('gml:id', $org_acronym . '_' . $sensorID );

	#################
	# GML boundedBy, appears twice
	#################
	foreach $node ($xc->findnodes("//gml:lowerCorner")){
		$node->appendText("$lat $lon");
	}
	foreach $node ($xc->findnodes("//gml:upperCorner")){
		$node->appendText("$lat $lon");
	}

	#################
	# DATA_TIME Instant or Period
	#################
	my $t_elem;
	# IOOS DIF has this wrong, they always use TimePeriod
#	if($stime eq $etime){
#		$t_elem = $sos->createElement('gml:timeInstant');
#		$t_elem->appendTextChild('gml:timePosition', $etime);
#	}else{
		$t_elem = $sos->createElement('gml:TimePeriod');
		$t_elem->appendTextChild('gml:beginPosition', $stime);
		$t_elem->appendTextChild('gml:endPosition', $etime);
#	}
	$t_elem->setAttribute('gml:id', 'ST' );

	$node = ($xc->findnodes("//om:CompositeObservation/om:samplingTime"))[0];

	$node->appendChild($t_elem);



# COUNTS: Revisit Counts
# Counts ??  NumberOfStations and NumberOfObservationPoints. I think we can only handle a single station at this time, i.e. not ALL_PLATFORMS
# Perhaps in the future
#   NumberOfSensors Assuming if they have e.g. air_temp,saliniity that two sensors were involved, BUT DIF Wind is actually speed, direction, gust
#   ioos:Count can have these name attributes
#	NumberOfStations
#	Station1NumberOfSensors
#	NumberOfObservationsPoints
#	Station1NumberOfObservationsTimes
	# DIF ioos:Count below wants  Station1NumberOfObservationsTimes

	my 	$depth = 0;
	my %dif_uniq_times = ();
	if(@output_data){
		foreach my $line (@output_data){
			chomp($line);
			my @vals = split(/$tokenSep/, $line);
			$dif_uniq_times{$vals[1]}++;
		}
		# We assume first record has sensor depth, for e.g. currents profiles. May not be true.
		# Since OOSTethys / SWE has depth with reach observation records we did not worry too much about this.
		my @vals = split(/$tokenSep/, $output_data[0]);
		$depth = $vals[$dif_depthPos];
	}

	foreach my $node ( $xc->findnodes('//ioos:Count') ){
		my $name = $node->getAttribute('name');
		$node->appendText('1') if $name eq 'NumberOfStations';
		$node->appendText('1') if $name eq 'NumberOfObservationsPoints';
		$node->appendText(scalar(@params)) if $name =~ /NumberOfSensors/;
		$node->appendText(scalar(keys %dif_uniq_times)) if $name =~ /NumberOfObservationsTimes/;
	}

	#################
	# procedure or sensor URN
	#################
	$node = ($xc->findnodes("//gml:valueComponents/ioos:StationName"))[0];
	$node->appendText($long_name);
	$node = ($xc->findnodes("//gml:valueComponents/ioos:Organization"))[0];
	$node->appendText($provider_name);
	$node = ($xc->findnodes("//gml:valueComponents/ioos:StationId"))[0];
	$node->appendText($sensor_urn);
	$node = ($xc->findnodes("//gml:valueComponents/ioos:SensorId"))[0];
	$node->appendText($sensor_urn . ':' . $params[0] );

	# We Should add water depth to sensor_list both sos_config.xml and metaDataDB().
	$node = ($xc->findnodes('//gml:valueComponents/ioos:Context[@name="TotalWaterDepth"]'))[0];
	#$node->appendText($water_depth);

	$node = ($xc->findnodes('//gml:valueComponents/ioos:Context[@name="SensorDepth"]'))[0];
	$node->appendText($depth);

	$node = ($xc->findnodes("//gml:valueComponents/gml:Point/gml:pos"))[0];
	$node->appendText("$lat $lon");

	########################
	# om:result
	########################
	# Need a gml:id
	$node = ($xc->findnodes("//om:result/ioos:Composite"))[0];
	$node->setAttribute('gml:id', $difName . 'PointCollectionTimeSeriesDataObservations');
	$node->setAttribute( 'recDef',  sprintf($dif_recDef, $difName) );

	$node = ($xc->findnodes("//om:result/ioos:Composite/gml:valueComponents/ioos:Array"))[0];
	$node->setAttribute( 'gml:id',  $difName . 'PointCollectionTimeSeries' );

	$node = ($xc->findnodes("//om:result/ioos:Composite/gml:valueComponents/ioos:Array/gml:valueComponents/ioos:Composite/gml:valueComponents/ioos:Array/gml:valueComponents"))[0];
	my $time_cnt = 1;
	foreach my $line (@output_data){
		chomp($line);
		my $result = createDIFResult($sos, $sensor_list, $time_cnt, $difName, $line, $tokenSep);
		next if not $result;
		$node->appendChild($result);
		$time_cnt++
	}

	return $sos;
}

########################
#  Creates the ioos:Composite element and it's children for a single timestamped observation and depth
#  How many ioos:Quantities depends on the observedProperty, e.g. Winds create 3 values.
#  Also depends on weather use_DIF_properties is set or not.
########################
sub createDIFResult
{
	my ($sos, $sensor_list, $cnt, $difName, $line, $tokenSep) = @_;
	my @vals = split(/$tokenSep/, $line);

	# To only handle surface observations uncomment.
	# But I have added a Depth to each DIF result so it's not needed.
	#if($use_DIF_properties){
	#	return undef if ($vals[$dif_depthPos] > 1);
	#}

	my $result = $sos->createElement('ioos:Composite');
	$result->setAttribute('gml:id', 'Station1T' . $cnt .'Point');
	my $top_valComponents = $sos->createElement('gml:valueComponents');

	my $compContext = $sos->createElement('ioos:CompositeContext');
	# this gml:id seems to imply time count, but for profiles there could be mulitple depths for a single time.
	# I belive work on a DIF profile record is ongoing.
	$compContext->setAttribute('gml:id', 'Station1T' . $cnt .'ObservationConditions');
	$compContext->setAttribute('processDef', '#Station1Info');
	my $valComponents = $sos->createElement('gml:valueComponents');
	my $timeInst = $sos->createElement('gml:TimeInstant');
	$timeInst->setAttribute('gml:id', 'Station1T' . $cnt .'Time');
	my $timePos = $sos->createElement('gml:timePosition');
	$timePos->appendText($vals[$dif_timePos]);

	$timeInst->appendChild($timePos);
	$valComponents->appendChild($timeInst);
	$compContext->appendChild($valComponents);

	my $valContext = $sos->createElement('ioos:CompositeValue');
	$valContext->setAttribute('gml:id', 'Station1T' . $cnt .'PointObservation');
	$valContext->setAttribute('processDef', '#Station1Sensor1Info');

	$valComponents = $sos->createElement('gml:valueComponents');

	# DIF only used Depth for Currents, but it can't hurt to have it for all obs
	my $ioosDepth = $sos->createElement('ioos:Context');
	$ioosDepth->setAttribute('name', 'Depth');
	$ioosDepth->setAttribute('uom', 'm');
	$ioosDepth->appendText($vals[$dif_depthPos]);
	$valComponents->appendChild($ioosDepth);

	# Note:  On UOM's I made no attempt to map uoms from %uom_lookup to whatever uom's IOOS DIF uses.
	# I think each provider should use the uom they are using.

	if($use_DIF_properties){
		my @local_props = split(',', $sensor_list->{$vals[$dif_platformPos]}->{difproplist}->{$difName});
		my $prop_cnt = $#local_props;
		foreach my $local_prop (@local_props){
			my $uom = '';
			my $subDifName = $dif_lookup3{$local_prop};
			my $ioosQuant = $sos->createElement('ioos:Quantity');
			$uom = $uom_lookup{$local_prop};
			$ioosQuant->setAttribute('name', $subDifName);
			$ioosQuant->setAttribute('uom', $uom );
			$ioosQuant->appendText($vals[$#vals - $prop_cnt]);
			$valComponents->appendChild($ioosQuant);
			$prop_cnt--;
		}
	}else{
		my $ioosQuant = $sos->createElement('ioos:Quantity');
		$ioosQuant->setAttribute('name', $difName);
		my $uom = $sensor_list->{$vals[$dif_platformPos]}->{obsproplist}->{$difName}->{uom};
		$ioosQuant->setAttribute('uom', $uom );

		$ioosQuant->appendText($vals[$#vals]);
		$valComponents->appendChild($ioosQuant);
	}


	$valContext->appendChild($valComponents);

	$top_valComponents->appendChild($compContext);
	$top_valComponents->appendChild($valContext);

	#$result->appendChild($compContext);
	#$result->appendChild($valContext);
	$result->appendChild($top_valComponents);
	return $result;
}

################################################################# SUPPORT ROUTINES

#######################################
# setDataRecord - utilized by doDescribeSensor and doGetObservation
# The assumption is that observedProperty list is the last value in the tupple
#######################################
sub setDataRecord
{
	my ($xc, $node, $sensorID, $sensor_list, $obsproplist) = @_;

	$node->setAttribute('name', $sensorID . 'Observations');

	my $data_record =  ($xc->findnodes("swe:DataRecord", $node))[0];

	foreach my $field ( $node->findnodes("swe:DataRecord/*") ){
		next if ($field->getAttribute('name') ne 'observedProperty');
		my $clone = $field->cloneNode(1);
		$data_record->removeChild($field);

		my $param_count = 1;
		foreach my $param ( @{ $obsproplist } ){
			my $param_uom = $sensor_list->{$sensorID}->{obsproplist}->{$param}->{uom};
			my $param_def = $observed_property_url . $param;
			# Leave this generic, actual definition is in the definition attribute
			$clone->setAttribute('name', 'observedProperty' . $param_count );
			my $quantity =  ($clone->findnodes("swe:Quantity"))[0];
			$quantity->setAttribute('definition', $param_def);
			my $swe_uom =  ($quantity->findnodes("swe:uom"))[0];
			$swe_uom->setAttribute('code', $param_uom);

			$data_record->appendChild($clone);
			$clone = $clone->cloneNode(1);
			$param_count++;
		}
	}

}

#######################################
# checkDataRecord 
#######################################
# This does not work for getting field positions. Just the field count
# Intended to help us deal with differing DataRecord definitions in the future, 
# in particular to deal with multiple observedProperties in one request and for the inclusion of QA/QC values in
# the responses.
# Currently used to check the number of fields in the DataRecord for setting DataArray/elementCount
#######################################
sub checkDataRecord
{
	my ($xc) = @_;

	my ($platform_pos, $time_pos, $lat_pos, $lon_pos, $depth_pos, $quality_pos, $data_pos) =
		(-1,-1,-1,-1,-1,-1,-1);
	my $fld_cnt = 0;
	# All elements with a definition attribute
	foreach my $node ( $xc->findnodes('//swe:DataRecord/swe:field/*[@definition]') )
	{
		my $def = $node->getAttribute('definition');
		$platform_pos = $fld_cnt if($def =~ /platform/i);
		$time_pos = $fld_cnt if($def =~ /time/i);
		$lat_pos = $fld_cnt if($def =~ /latitude/i);
		$lon_pos = $fld_cnt if($def =~ /longitude/i);
		$depth_pos = $fld_cnt if($def =~ /depth/i);
		$quality_pos = $fld_cnt if($def =~ /quality/i);
		# This only allows for one data_pos, one quality_pos, etc.
		# This must be fixed
		$data_pos = $fld_cnt if($def =~ /observedProperty/i);
		$fld_cnt++;
	}
	#return ($fld_cnt,$platform_pos, $time_pos, $lat_pos, $lon_pos, $depth_pos, $quality_pos, $data_pos);
	return ($fld_cnt);
}

#######################################
# getInputParams ()
# Get the SOS input Parameters.
# Supports both HTTP GET Key Value Pairs and POST XML methods.  Assumes that both  use identical KVP key names and element names.
# observedProperty is really a misnomer: it can be a comma separated list of observedProperties
#######################################
sub getInputParams
{
	my $param_ref = shift;

	my $q = new CGI;

	# Some Command line tests for GET METHOD
	# Uncommenting these allows the script to be run from the command line outputting XML for testing
	# They must be modifed for local platforms and observedProperites
	#my $q = new CGI('service=SOS&request=GetCapabilities&version=1.0.0&sections=ServiceIdentification,OperationsMetadata');
	#my $q = new CGI('request=GetCapabilities&service=SOS&version=1.0.0&sections=');
	#my $q = new CGI('request=GetCapabilities&service=SOS&version=1.0.0');
	#my $q = new CGI('request=GetCapabilities&service=SOS&version=1.0.0&sections=Filter_Capabilities');
	#my $q = new CGI('request=GetCapabilities&service=SOS&version=1.0.0&sections=ServiceIdentification,OperationsMetadata');
	#my $q = new CGI('request=GetCapabilities&service=SOS&version=1.0.0');
	#my $q = new CGI('request=DescribeSensor&service=SOS&outputFormat=text/xml%3B+subtype="sensorML/1.0.1"&procedure=urn:gomoos.org:def:source:mooring::A01');
	#my $q = new CGI('request=service=SOS&GetObservation&offering=A01&observedProperty=sea_water_salinity&eventTime=2009-07-20T10:00:00Z/2009-07-20T14:00:00Z');
	# CITE
	#my $q = new CGI('request=GetObservation&service=SOS&responseFormat=text/xml%3B+subtype="om/1.0.0"&offering=A01&observedProperty=air_temperature');
	#my $q = new CGI('request=GetObservation&offering=A01&observedProperty=significant_height_of_wind_and_swell_waves,dominant_wave_period');
	#my $q = new CGI('request=GetObservation&offering=A01&observedProperty=wind_speed,wind_from_direction,wind_gust');
	#my $q = new CGI('request=GetObservation&offering=A01&observedProperty=Winds');
	#my $q = new CGI('request=GetObservation&offering=A01&observedProperty=Currents');
	#my $q = new CGI('request=GetObservation&offering=A01&observedProperty=air_temperature&EventTime=2009-04-30T15:00:00Z/2009-04-30T21:00:00Z');

	# Parse XML submitted via POST 
	if( uc($q->request_method()) eq 'POST'){
		my $in = $q->param('POSTDATA');
		exception_error(1, "No input parameters") if (not $in);
		# Make sure it's XML
		if($in !~ /^</){
			exception_error(6, "Non XML POST input.");
		}

		my $xml_in = $parser->parse_string($in);
		if(not $xml_in){
			exception_error(6, "POST input parse error.");
		}

		# Parameter xml has a default namespace of sos
		my $xc = XML::LibXML::XPathContext->new($xml_in);
		# sos prefix may be the default namespace
		registerNameSpaces($xml_in, $xc);
		# get the name of the root element
		my $request = $xml_in->documentElement()->nodeName;

		#  Root element name may or may not have a prefix. So remove it
		$request =~ s/.+://;
		$param_ref->{REQUEST} = $request;
		# Seems like most common values are in the attributes of the request
		my @attrs = $xml_in->documentElement()->attributes();
		foreach my $a (@attrs){
			my $name = $a->getName;
			# skip namespaces
			next if $name =~ /xmlns/;
			next if $name =~ /xsi/;
			my $val = $a->getData;
			$param_ref->{uc($name)} = $val;
		}

		# Get all the child nodes. Note: this does not handle some of the more complicated GetObservation inputs
		foreach my $node ($xc->findnodes("/sos:$request/*")){
			my $name = $node->nodeName;
			my $val = $node->string_value;
			if($name){
				$param_ref->{uc($name)} = $val;
			}
		}

		#####################
		# GetObservation may have more detailed input
		#####################
		if($request eq 'GetObservation'){
			$param_ref->{EVENTTIME} = '';
			$param_ref->{BBOX} = '';

			if( $param_ref->{FEATUREOFINTEREST} ){
				$param_ref->{FEATUREOFINTEREST} = '';
				foreach my $node ( $xc->findnodes("//sos:GetObservation/sos:featureOfInterest/sos:ObjectID") ){
					$param_ref->{FEATUREOFINTEREST} = $node->string_value;
				}
			
			}

			$param_ref->{OBSERVEDPROPERTY} = '';
			# Turn observedProperties into comma separate list
			foreach my $node ( $xc->findnodes("//sos:GetObservation/sos:observedProperty") ){
				$param_ref->{OBSERVEDPROPERTY} .= $node->string_value . ',';
			}
			# remove final comma
			chop( $param_ref->{OBSERVEDPROPERTY} );

			my @nodes = $xc->findnodes("/sos:GetObservation/sos:eventTime/ogc:T_During/*");
			if(@nodes){
				my $node = $nodes[0];
				my $time = '';
				# We handle single time or min/max times
				if($node->nodeName eq 'gml:TimeInstant'){
					$time= $xc->find("gml:timePosition", $node)->string_value;
				}
				if($node->nodeName eq 'gml:TimePeriod'){
					$time= $xc->find("gml:beginPosition", $node)->string_value;
					$time .= '/';
					$time .= $xc->find("gml:endPosition", $node)->string_value;
				}
				$param_ref->{EVENTTIME} = $time;
			}
			my @nodes = $xc->findnodes("/sos:GetObservation/sos:featureOfInterest/ogc:BBOX/gml:Envelope/*");
			if(@nodes){
				my ($lc, $uc) = ('','');
				foreach my $node (@nodes){
					if($node->nodeName eq 'gml:lowerCorner'){
						$lc = $node->string_value;
						$lc =~ s/ /,/g;
					}
					if($node->nodeName eq 'gml:upperCorner'){
						$uc = $node->string_value;
						$uc =~ s/ /,/g;
					}
				}
				$param_ref->{BBOX} = "$lc,$uc";
			}
		}

	}else{
		# Convert all Keys in KVP to all uppercase.
		# Param names can be uppercase, e.g. request or REQUEST or Request
		my @param_names = $q->param;
		foreach my $p (@param_names){
			$param_ref->{uc($p)} = $q->param($p);
		}
	}
	if( $use_DIF and $use_DIF_properties and $param_ref->{REQUEST} eq 'GetObservation'){
		my $difParam = $param_ref->{OBSERVEDPROPERTY};
		if( exists( $dif_lookup_map{$difParam} ) ){
			$param_ref->{OBSERVEDPROPERTY} = $dif_lookup_map{$difParam};
		}
	}
}

########################################
# GoMOOS Specific routines
########################################
#  getMetaData()
########################################
#  This routine populates the global hash, %sensor_list
#  %sensor_list contains the metadata for each sensor  and one hash: obsproplist with the
#  observedProperites as the keys and the Units of Measure and the observation file or 'DataBase' as the values.
########################################
#
# 'A01' => {
#   'platform' => 'A01',
#   'urn' => 'urn:gomoos.org:source.mooring#A01',
#   'long_name' => 'Mooring A01 GoMOOS',
#   'provider_name' => 'GoMOOS',
#   'lat' => '42.5227336883545',
#   'lon' => '-70.5647239685059',
#   'start_time' => '2001-07-10T03:00:00Z',
#   'end_time' => '',
#   'comments' => 'Mooring A01 data from the Gulf of Maine Ocean Observing System (GoMOOS) located Massachusetts Bay',
#   'obsproplist' => {
#      'sea_water_temperature' => {
#         'obs_file' => '/home/data/web_tmp/sos/A01_SEA_WATER_TEMPERATURE.txt',
#         'uom' => 'Cel'
#        },
#      'sea_water_salinity' => {
#          'obs_file' => 'DataBase',
#         'uom' => 'psu'
#      }
#   },
# }
###############################################
sub getMetaData
{
	my ($use_config, $sensor_list) = @_;
	$use_config ? getMetaConfig($sensor_list) : getMetaDB($sensor_list);
	getMetaGetCap($sensor_list);
	return $sensor_list;
}
###############################################
sub getMetaConfig
{
	my $sensor_list = shift;
	my $config = $parser->parse_file("$base_dir/sos_config.xml");
	exception_error(5, "Could not sos_config.xml file") if ! $config;

	$org = $config->find("//DataProvider/Publisher/OrganizationName");
	$short_org = $config->find("//DataProvider/Publisher/shortOrganizationName");
	$org_acronym = $config->find("//DataProvider/Publisher/OrganizationAcronym");
	$short_org = $org_acronym if (not $short_org);
	$org_url = $config->find("//DataProvider/Publisher/OrganizationURL");
	$title = $org . ' SOS';

	$ra_name = $config->find("//DataProvider/RegionalAssociation/shortOrganizationName");
	$ra_name = $short_org if (not $ra_name);

	$contact = $config->find("//DataProvider/Publisher/ContactPersonName");
	$email = $config->find("//DataProvider/Publisher/ContactPersonEmail");
	$sos_url = $config->find("//DataProvider/Publisher/SOSURL");

	@key_words = ();
	foreach my $node ($config->findnodes("//DataProvider/Publisher/Keywords/Keyword")){
		#push(@key_words, $node->find("Keyword")->string_value);
		push(@key_words, $node->string_value);
	}

	foreach my $node ($config->findnodes("//DataProvider/ObservationList/Observation")){
		my $obsprop = $node->find("observedProperty")->string_value;
		my $uom = $node->find("uom")->string_value;
		my $obs_file = $node->find("SOSDataFile")->string_value;

		my $platform = $node->find("localPlatformName")->string_value;
		my $provider_name = $node->find("dataProviderShortName")->string_value;
		$provider_name = $short_org if(not $provider_name);

		$sensor_list->{$platform}->{platform} = $platform;
		$sensor_list->{$platform}->{urn} = $node->find("platformURI")->string_value;
		# check for missing platformURI value. and just use platform
		$sensor_list->{$platform}->{urn} = $platform if(not $sensor_list->{$platform}->{urn});
		if($use_DIF){
			# just convert any # to :: like NOAA does
			$sensor_list->{$platform}->{urn} =~ s/#/::/g; 
		}
		$sensor_list->{$platform}->{long_name} = $node->find("platformLongName")->string_value;
		$sensor_list->{$platform}->{provider_name} = $provider_name;
		$sensor_list->{$platform}->{lat} = $node->find("latitude")->string_value;
		$sensor_list->{$platform}->{lon} = $node->find("longitude")->string_value;
		$sensor_list->{$platform}->{start_time} = $node->find("startTime")->string_value;
		$sensor_list->{$platform}->{end_time} = $node->find("endTime")->string_value;
		$sensor_list->{$platform}->{comments} = $node->find("comments")->string_value;
		$sensor_list->{$platform}->{obsproplist}->{$obsprop}->{uom} = $uom;
		$sensor_list->{$platform}->{obsproplist}->{$obsprop}->{obs_file} = $obs_file;
	}
}
###############################################
sub addDIFProp{
	my ($sensor_list) = @_;
	foreach my $sensor (keys %{$sensor_list} ){
		#print "$sensor\n";
		foreach my $prop (sort keys %{ $sensor_list->{$sensor}->{obsproplist} } ) {
			#print "$prop\n";
			if( exists($dif_lookup2{$prop})){
				my $dif_prop = $dif_lookup2{$prop};
				$sensor_list->{$sensor}->{difproplist}->{$dif_prop} = $dif_lookup_map{$dif_prop};
			}
		}
	}
}
###############################################
###############################################
sub getMetaDB
{

# LOCAL EDIT
	my $sensor_list = shift;
	my $dbname = '';
	my $dbhost = '';
	my $dbuser = '';
	my $dbpass = '';

	my $dbh = DBI->connect("dbi:Pg:dbname=$dbname;host=$dbhost","$dbuser","$dbpass",{ PrintError =>1, RaiseError=>0} );

	unless ($dbh){
		die("Database Error", DBI->errstr);
	}

	my $sql_statement =  <<ESQL;
SELECT distinct ts.display_platform AS platform, ts.program as provider, ts.lon AS longitude, ts.lat AS latitude, ts.mooring_site_desc AS description, ts.data_type AS observed_property
FROM time_series ts 
JOIN active_platforms ap ON ap.platform::text = ts.platform::text
WHERE ts.program = 'GoMOOS'
ORDER BY ts.display_platform, ts.data_type
ESQL

# END LOCAL EDIT

	my $sth = $dbh->prepare($sql_statement) or die("Database Error", $dbh->errstr);
	if ($sth->execute()) {
		while (my $ref = $sth->fetchrow_hashref()) {
			my $platform = $ref->{platform};
			my $obsprop = $ref->{observed_property};
			my $start_time = get_start_time($dbh, $platform);
			my $provider_name = $ref->{provider};
 			my $comments = "Mooring $platform data from the $org ($provider_name) located ";
			$comments .= $ref->{description};

			$sensor_list->{$platform}->{platform} = $platform;
			$sensor_list->{$platform}->{urn} = $platform_uri . $platform;
			$sensor_list->{$platform}->{long_name} = "Mooring $platform $short_org";
			$sensor_list->{$platform}->{provider_name} = $provider_name;
			$sensor_list->{$platform}->{lat} = $ref->{latitude};
			$sensor_list->{$platform}->{lon} = $ref->{longitude};
			$sensor_list->{$platform}->{start_time} = $start_time;
			$sensor_list->{$platform}->{end_time} = '';
			$sensor_list->{$platform}->{comments} = $comments;
			# All observed properties must be in the %uom_lookup table
			next if (not exists($uom_lookup{$obsprop}));
			my $uom = $uom_lookup{$obsprop};

			$sensor_list->{$platform}->{obsproplist}->{$obsprop}->{uom} = $uom;
			$sensor_list->{$platform}->{obsproplist}->{$obsprop}->{obs_file} = 'DataBase';

		}
	}else{
		$dbh->disconnect();
		die("Database Error", $dbh->errstr);
	}
	$dbh->disconnect();

	#########################
	# An Example of adding a procedure which allows retrieval of obervedProperties for all GoMOOS Platforms
	# ALL_PLATFORMS
	#########################

	#my $comments = "Mooring data for all buoys from the $org ($short_org) located in the Gulf of Maine";
	#my $platform = 'ALL_PLATFORMS';
	#$sensor_list->{$platform}->{platform} = $platform;
	#$sensor_list->{$platform}->{urn} = $platform_uri . $platform;
	#$sensor_list->{$platform}->{long_name} = "Mooring $platform $short_org";
	#$sensor_list->{$platform}->{provider_name} = $short_org;
	# Hmmmm here we  would need bounding box! This is the BBox centroid
	#$sensor_list->{$platform}->{lat} = '42.75';
	#$sensor_list->{$platform}->{lon} = '-67.25';
	# Earliest GoMOOS start time
	#$sensor_list->{$platform}->{start_time} = '2000-07-10T20:00:00Z';
	#$sensor_list->{$platform}->{end_time} = '';
	#$sensor_list->{$platform}->{comments} = $comments;
	# List all observed properties in the %uom_lookup table
	#foreach my $obsprop (sort keys %uom_lookup){
	#	my $uom = $uom_lookup{$obsprop};
	#	$sensor_list->{$platform}->{obsproplist}->{$obsprop}->{uom} = $uom;
	#	$sensor_list->{$platform}->{obsproplist}->{$obsprop}->{obs_file} = 'DataBase';
	#}

}

###############################################
# Certain values, needed to pass the OGC CITE tests but not really modified by this script are gotten from the sosGetCapabilities.xml template
# These are all basically hard coded in the sosGetCapabilities.xml template and never change.
###############################################
sub getMetaGetCap
{
	my $sensor_list = shift;
 	my $sos = $parser->parse_file("$base_dir/sosGetCapabilities.xml");
	exception_error(5, "Could not open SOS template: sosGetCapabilities.xml") if ! $sos;

	my($responseFormat, $outputFormat, $srsName, $AcceptVersions, $responseMode, $resultModel) = ('', '', '', '', '', '');

	my $xc = XML::LibXML::XPathContext->new($sos);
	registerNameSpaces($sos, $xc);

	my $node = ( $xc->findnodes('//sos:Capabilities/ows:OperationsMetadata/ows:Operation[@name="GetObservation"]') )[0];
	my $node2 = ($xc->findnodes('ows:Parameter[@name="responseFormat"]/ows:AllowedValues/ows:Value', $node))[0];
	$responseFormat = $node2->string_value;

	$node2 = ($xc->findnodes('ows:Parameter[@name="resultModel"]/ows:AllowedValues/ows:Value', $node))[0];
	$resultModel = $node2->string_value;

	$node = ( $xc->findnodes('//sos:Capabilities/ows:OperationsMetadata/ows:Operation[@name="DescribeSensor"]') )[0];
	$node2 = ($xc->findnodes('ows:Parameter[@name="outputFormat"]/ows:AllowedValues/ows:Value', $node))[0];
	$outputFormat = $node2->string_value;

	# this could be a list in the future
	$node = ( $xc->findnodes('//sos:Capabilities/ows:OperationsMetadata/ows:Operation[@name="GetCapabilities"]') )[0];
	$node2 = ($xc->findnodes('ows:Parameter[@name="AcceptVersions"]/ows:AllowedValues/ows:Value', $node))[0];
	$AcceptVersions = $node2->string_value;

	$node = ($sos->findnodes("//sos:Capabilities/sos:Contents/sos:ObservationOfferingList/sos:ObservationOffering/gml:boundedBy/gml:Envelope"))[0];
	$srsName = $node->getAttribute('srsName');

	$node = ($sos->findnodes("//sos:Capabilities/sos:Contents/sos:ObservationOfferingList/sos:ObservationOffering/sos:responseMode"))[0];
	$responseMode = $node->string_value;

	# We should have allowed for top-level values good for all sensors, but here it's just repeated for each sensor
	foreach my $platform (keys %{$sensor_list}){
		$sensor_list->{$platform}->{responseFormat} = $responseFormat;
		$sensor_list->{$platform}->{outputFormat} = $outputFormat;
		$sensor_list->{$platform}->{srsName} = $srsName;
		$sensor_list->{$platform}->{responseMode} = $responseMode;
		# These could really be a lists in the future
		$sensor_list->{$platform}->{AcceptVersions} = $AcceptVersions;
		$sensor_list->{$platform}->{resultModel} = $resultModel;
	}

}

###############################################
# a generic name space registration routine for LibXML and XPathContext. It register all namespace attributes, xmlns, found in the root element.
###############################################
sub registerNameSpaces
{
	my ($doc, $context) = @_;
	my $root = $doc->documentElement();
	my @attributes = $root->attributes();
	foreach my $a (@attributes){
		next unless $a->getName =~ /xmlns/;
		# xmlns:sos e.g.
		my @tmp = split( ":", $a->getName);
		my $url = $a->getData;
		# this maps the xmlns prefix to the value which should be a URL
		$context->registerNs( $tmp[1] => $url);
	}
}

###############################################
sub get_start_time
{
	my ($dbh, $platform) = @_;
	my $sql_statement =  "select min(start_time) at time zone 'UTC' as start_time from time_series where program = 'GoMOOS' AND display_platform = '$platform'";
	my $sth = $dbh->prepare($sql_statement) or die("Database Error", $dbh->errstr);
	if ($sth->execute()) {
		my $ref = $sth->fetchrow_hashref();
		my $t = $ref->{start_time};
		$t =~ s/ /T/;
		$t .= 'Z';
		return $t;
	}
}

###############################################
sub getData
{
	my ($sensorID, $in_time, $bbox, $params, $sensor_list, $tokenSep) = @_;

	# Use the first param in the sensor_list to see if it's DB or File based observations.
	# We assume you cannot mix DB and File observations in a list of parameters.
	my $param = @{$params}[0];

	if( $sensor_list->{$sensorID}->{obsproplist}->{$param}->{obs_file} eq 'DataBase') {
		return getDataDB($sensorID, $in_time, $bbox, $params, $sensor_list, $tokenSep);
	} else {
		# not Bounding Box queries for File base observations
		return getDataFile($sensorID, $in_time, $params, $sensor_list, $tokenSep);
	}

}
# HERE NB This is working now but could really use better logic based on the DataRecord? e.g.
# What is the obs value what is the QA/QC value
###############################################
# getDataFile  Get observation data from the files listed in the sos_config.xml file.
#   We have to jump thru hoops to get 2 or more observations out of these text files which are one
#   observation per file.
###############################################
sub getDataFile
{
	my ($sensorID, $in_time, $params, $sensor_list, $tokenSep) = @_;


	my ($time1, $time2) = ('','');;
	# '/' is the separator
	if($in_time =~ /\// ){
		($time1, $time2) = split( /\//, $in_time);
	}else{
		$time1 = $in_time;
	}


	my $sse1  = time2sse($time1);
	my $sse2  = time2sse($time2);

	my @output_data = ();
	my @return_data = ();
	my %data_by_time = ();
	my @sensors = ();

	# Handle ALL_PLATFORMS
	if($sensorID eq 'ALL_PLATFORMS'){
		foreach ( keys %{$sensor_list} ){
			next if ($_ eq 'ALL_PLATFORMS');
			push @sensors, $_;
		}
	}else{
		push @sensors, $sensorID;
	}

foreach my $sensor (@sensors){

	# Make sure we have an entry for every time and depth for each param
	# but within time filters
	foreach my $param (@{$params}){
		my $obs_file = $sensor_list->{$sensor}->{obsproplist}->{$param}->{obs_file};
		if($obs_file){
			open(DATA, "< $obs_file") or exception_error(5, "Could not open: $base_dir/$obs_file $!");
			@output_data = <DATA>;
			close(DATA);
			# no data
			exception_error(5,  "Data not available for the requested Observation Offering.") if  not @output_data;
		}
		foreach my $line (@output_data){
			chomp($line);
			my @vals = split(/$tokenSep/, $line);
			# Note:  time is now second field
			my $t = $vals[1];
			my $sse = time2sse($t);
			my $depth = $vals[4];
			if($sse1 and $sse2){
				if ( not ( ($sse >= $sse1) and ($sse <= $sse2) ) ){
					next;
				}
			}
			elsif($sse1){
				next if ( $sse1 != $sse  );
			}

			#$data_by_time{$sensor}{$t}{$depth} = "$sensor," . join($tokenSep, @vals[0..$#vals]);
			# Note: not the value, everything up to the value
			$data_by_time{$sensor}{$t}{$depth} = join($tokenSep, @vals[0..$#vals-1]);
		}
	} # end first foreach $param

	exception_error(5,  "Data not available for the requested Time or BBOX.") if  not %data_by_time;

	# Now got thru again and get values
	foreach my $param (@{$params}){
		my $obs_file = $sensor_list->{$sensor}->{obsproplist}->{$param}->{obs_file};
		if($obs_file){
			open(DATA, "< $obs_file") or exception_error(5, "Could not open: $obs_file $!");
			@output_data = <DATA>;
			close(DATA);
			# no data
			exception_error(5,  "Data not available for the requested Observation Offering.") if  not @output_data;
		}

		# Add trailing comma for each param but only for this $sensor
		foreach my $dt (keys %{ $data_by_time{$sensor} }){
			foreach my $dp (keys %{ $data_by_time{$sensor}{$dt} }){
				$data_by_time{$sensor}{$dt}{$dp} .= ',';
			}
		}

		foreach my $line (@output_data){
			chomp($line);
			my @vals = split(/$tokenSep/, $line);
			# time is now second field
			my $t = $vals[1];
			my $sse = time2sse($t);
			my $depth = $vals[4];
			my $val = $vals[$#vals];
			# Time filters
			if($sse1 and $sse2){
				if ( not ( ($sse >= $sse1) and ($sse <= $sse2) ) ){
					next;
				}
			}
			elsif($sse1){
				next if ( $sse1 != $sse  );
			}
			$data_by_time{$sensor}{$t}{$depth} .= $val;
		}
	} # end foreach $param

} # end for each $sensor

	# build the return_data array
	foreach my $plt (sort keys %data_by_time){
		foreach my $dt (sort keys %{ $data_by_time{$plt} }){
			foreach my $dp (sort {$a <=> $b} keys %{ $data_by_time{$plt}{$dt} }){
				push (@return_data, $data_by_time{$plt}{$dt}{$dp});
			}
		}
	}
	return @return_data;
}

########################################
# Convert ISO8601 time string to seconds since epoch (01/01/1970)
# requires Time::Local which has the timegm function
########################################
sub time2sse
{
	my ($time) = @_;
	return 0 if not $time;
	return 0 if ($time eq '');
	
	$time =~ s/Z$//;
	my ($yr, $mo, $da) = split('-', $time);
	my ($da, $t) = split('T', $da);
	my ($hr, $min, $sec) = split(':', $t);
	$yr -= 1900;
	$mo--;
	# truncate all to the previous hour
	my $sse = timegm( (0,0,$hr,$da,$mo,$yr));
	return $sse;
}

########################################
#  getDataDB()
#    This routine should return a array of strings containing your data tuples for a GetObservation request.
#    At GoMOOS have set up two different views for returning data based on time query versus a query for the
#    most recent observations.
#    The OOSTethys SOS defines a data tuple as:
#    platform,time,latitude,longitude,depth,observedProperty, .. (with a comma field separator and a space record separator)
#      additional observedProperties for the same time and depth may be appended to the tuple when the
#      getObservation request contains a list of observedProperties.
#    e.g.
#   'platform,YYYY-MM-DDTHH:MM:SSZ,latitude,longitude,depth,observedValue1,observedValue2,observedValue3'
#	Note: no spaces, space is the record separator
#    $data = [
#        'A01,2008-13-02T18:00:00Z,43.5695,-70.055,1,3.171'
#        'A01,2008-13-02T18:00:00Z,43.5695,-70.055,2,3.157801'
#        'A01,2008-13-02T18:00:00Z,43.5695,-70.055,20,3.178'
#    ];
#######################################
sub getDataDB
{
	my ($sensorID, $in_time, $bbox, $params, $sensor_list) = @_;

	my @params = @{$params};

# LOCAL EDITS
	my $dbname = '';
	my $dbhost = '';
	my $dbuser = '';
	my $dbpass = '';


	# Get Latest requires a differnt join table than a query with time parameters
	# If you don't have such a view see the order by clause below at the END LOCAL EDITS label
	my $join_table = 'v_most_recent_hourly_readings';
	if($in_time){
		$join_table = 'readings';
	}

	my $dbh = DBI->connect("dbi:Pg:dbname=$dbname;host=$dbhost","$dbuser","$dbpass",{ PrintError =>1, RaiseError=>0} );

	unless ($dbh){
		die("Database Error", DBI->errstr);
	}


	my $sql_statement =  <<ESQL;
SELECT 
ts.display_platform  as platform,
ts.data_type as observed_property,
r.reading_time AT TIME ZONE 'UTC' AS date_time,
ts.lat AS latitude,
ts.lon AS longitude,
ts.depth AS depth,
r.reading AS observation
FROM time_series ts
JOIN $join_table r ON (ts.id = r.time_series)
WHERE
ts.program = 'GoMOOS'
AND r.quality = 3
AND ts.display_platform = '$sensorID'
ESQL

	###########################
	# Handle ALL_PLATFORMS procedure
	###########################
	#if($sensorID ne 'ALL_PLATFORMS'){
	#	$sql_statement .= "AND ts.display_platform = '$sensorID' ";
	#}

	###########################
	# Handle time range parameters we use BETWEEN even for a single time
	###########################
	if($in_time){
		my ($time1, $time2) = ('','');;
		# '/' is the separator
		if($in_time =~ /\// ){
			($time1, $time2) = split( /\//, $in_time);
		}else{
			$time1 = $in_time;
			$time2 = $in_time;
		}
		# Postgres 8.1 won't take the Z.  time inputs are UTC format: YYYY-MM-DDTHH:MM:SSZ
		$time1 =~ s/Z$//;
		$time2 =~ s/Z$//;
		$sql_statement .= " AND r.reading_time BETWEEN '$time1 UTC' AND '$time2 UTC' "
	}

	###########################
	# Handle BBOX queries $bbox = 'min_longitude, min_latitude, max_longitude, max_latitude'
	# Your lat lons must be stored as floats in your database for this to work
	###########################
	if($bbox){
		my @latlons = split(',', $bbox);
		$sql_statement .= " AND ts.lon BETWEEN $latlons[0]  AND $latlons[2]  ";
		$sql_statement .= " AND ts.lat BETWEEN $latlons[1]  AND $latlons[3]  ";

		# This an approach using PostGIS geography operators
		#$sql_statement .= " AND (GeometryFromText('POINT( ' || ts.lon || ' ' || ts.lat || ')',-1) ";
		#$sql_statement .= " && 'BOX3D( $latlons[0] $latlons[1], $latlons[2] $latlons[3])'::box3d )";
	}

	###########################
	# Handle mulitple parameters
	# E.g. we need:  AND (data_type = 'salinity' OR data_type = 'sea_water_temperature')
	###########################

	$sql_statement .= 'AND (';
	for my $idx (0 .. $#params){
		$sql_statement .= " OR ts.data_type =  '$params[$idx]' " if ($idx > 0);
		$sql_statement .= " ts.data_type =  '$params[$idx]' " if ($idx == 0);
	}

	$sql_statement .= ')';

	# If you don't have a special view for latest observations this will do the trick.
	#if(not $in_time){
	#	$sql_statement .= ' order by date_time desc limit 1';
	#}

# END LOCAL EDITS

	my @return_data = ();
	my %data_by_time = ();

	my $sth = $dbh->prepare($sql_statement) or die("Database Error", $dbh->errstr);
	if ($sth->execute()) {
		my $rows = $sth->fetchall_arrayref({});
		foreach my $row ( @{$rows} ){
			my $platform = $row->{platform};
			my $t = $row->{date_time};
			$t =~ s/ /T/;
			$t .= 'Z';
			my $depth = $row->{depth};
			my $lat = $row->{latitude};
			my $lon = $row->{longitude};
			$data_by_time{$platform}{$t}{$depth} = "$platform,$t,$lat,$lon,$depth";
		}
		$dbh->disconnect() if not %data_by_time;
		exception_error(5,  "Data not available for the requested Time or BBOX.") if  not %data_by_time;

		foreach my $this_param (@params){
			# Add trailing comma for each param
			foreach my $plt (keys %data_by_time){
				foreach my $dt (keys %{ $data_by_time{$plt} }){
					foreach my $dp (keys %{ $data_by_time{$plt}{$dt} }){
						$data_by_time{$plt}{$dt}{$dp} .= ',';
					}
				}
			}
			foreach my $row ( @{$rows} ){
				next if ($row->{observed_property} ne "$this_param");
				my $plt = $row->{platform};
				my $t = $row->{date_time};
				$t =~ s/ /T/;
				$t .= 'Z';
				my $depth = $row->{depth};
				my $obs = $row->{observation};
				$data_by_time{$plt}{$t}{$depth} .= $obs;
			}
		} # end foreach $this_param

	}else{
		$dbh->disconnect();
		die("Database Error", $dbh->errstr);
	}

	$dbh->disconnect();

	foreach my $plt (sort keys %data_by_time){
		foreach my $dt (sort keys %{ $data_by_time{$plt} }){
			foreach my $dp (sort {$a <=> $b} keys %{ $data_by_time{$plt}{$dt} }){
				push (@return_data, $data_by_time{$plt}{$dt}{$dp});
			}
		}
	}

	return @return_data;
}

###############################################
#  A much simpler example of getting observations
###############################################
sub simple_getDataDB
{
	my ($sensorID, $in_time, $bbox, $params, $sensor_list) = @_;

	my @params = @{$params};

# LOCAL EDITS
	my $dbname = '';
	my $dbhost = '';
	my $dbuser = '';
	my $dbpass = '';


	my $dbh = DBI->connect("dbi:Pg:dbname=$dbname;host=$dbhost","$dbuser","$dbpass",{ PrintError =>1, RaiseError=>0} );

	unless ($dbh){
		die("Database Error", DBI->errstr);
	}


	my $sql_statement =  <<ESQL;
SELECT 
platform,
observed_property,
date_time AT TIME ZONE 'UTC',
latitude,
longitude,
depth,
observation
FROM readings
WHERE platform = '$sensorID'
ESQL

	###########################
	# Handle time range parameters we use BETWEEN even for a single time
	###########################
	if($in_time){
		my ($time1, $time2) = ('','');;
		# '/' is the separator
		if($in_time =~ /\// ){
			($time1, $time2) = split( /\//, $in_time);
		}else{
			$time1 = $in_time;
			$time2 = $in_time;
		}
		# Postgres 8.1 won't take the Z.  time inputs are UTC format: YYYY-MM-DDTHH:MM:SSZ
		$time1 =~ s/Z$//;
		$time2 =~ s/Z$//;
		$sql_statement .= " AND reading_time BETWEEN '$time1 UTC' AND '$time2 UTC' "
	}

	###########################
	# Handle mulitple parameters
	# E.g. we need:  AND (data_type = 'salinity' OR data_type = 'sea_water_temperature')
	###########################

	$sql_statement .= 'AND (';
	for my $idx (0 .. $#params){
		$sql_statement .= " OR data_type =  '$params[$idx]' " if ($idx > 0);
		$sql_statement .= " data_type =  '$params[$idx]' " if ($idx == 0);
	}

	$sql_statement .= ')';

# END LOCAL EDITS

	my @return_data = ();
	my %data_by_time = ();

	my $sth = $dbh->prepare($sql_statement) or die("Database Error", $dbh->errstr);
	if ($sth->execute()) {
		my $rows = $sth->fetchall_arrayref({});
		foreach my $row ( @{$rows} ){
			my $platform = $row->{platform};
			my $t = $row->{date_time};
			$t =~ s/ /T/;
			$t .= 'Z';
			my $depth = $row->{depth};
			my $lat = $row->{latitude};
			my $lon = $row->{longitude};
			$data_by_time{$platform}{$t}{$depth} = "$platform,$t,$lat,$lon,$depth";
		}
		$dbh->disconnect() if not %data_by_time;
		exception_error(5,  "Data not available for the requested Time or BBOX.") if  not %data_by_time;

		foreach my $this_param (@params){
			# Add trailing comma for each param
			foreach my $plt (keys %data_by_time){
				foreach my $dt (keys %{ $data_by_time{$plt} }){
					foreach my $dp (keys %{ $data_by_time{$plt}{$dt} }){
						$data_by_time{$plt}{$dt}{$dp} .= ',';
					}
				}
			}
			foreach my $row ( @{$rows} ){
				next if ($row->{observed_property} ne "$this_param");
				my $plt = $row->{platform};
				my $t = $row->{date_time};
				$t =~ s/ /T/;
				$t .= 'Z';
				my $depth = $row->{depth};
				my $obs = $row->{observation};
				$data_by_time{$plt}{$t}{$depth} .= $obs;
			}
		} # end foreach $this_param

	}else{
		$dbh->disconnect();
		die("Database Error", $dbh->errstr);
	}

	$dbh->disconnect();

	foreach my $plt (sort keys %data_by_time){
		foreach my $dt (sort keys %{ $data_by_time{$plt} }){
			foreach my $dp (sort {$a <=> $b} keys %{ $data_by_time{$plt}{$dt} }){
				push (@return_data, $data_by_time{$plt}{$dt}{$dp});
			}
		}
	}

	return @return_data;
}
