# Software written by Bill Howe, OHSU.
# Copyright 2008, Oregon Health & Science University.
# All Rights Reserved.
#
# Permission to use, copy, modify, and distribute any part of this program for non-profit 
# scientific research or educational use, without fee, and without a written agreement is 
# hereby  granted,  provided  that the above copyright notice, and this license agreement 
# appear  in all copies.  Inquiries regarding use of this software in commercial products 
# or for commercial purposes should be directed to:
# Technology & Research Collaborations, Oregon Health & Science University,
# 2525 SW 1st Ave, Suite 120, Portland, OR 97210
# Ph: 503-494-8200, FAX: 503-494-4729, Email: techmgmt@ohsu.edu.
#
# IN  NO EVENT SHALL OREGON HEALTH & SCIENCE UNIVERSITY BE LIABLE TO ANY PARTY FOR DIRECT, 
# INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING 
# OUT  OF THE USE OF THIS SOFTWARE.  THE SOFTWARE IS PROVIDED "AS IS", AND OREGON HEALTH & 
# SCIENCE  UNIVERSITY  HAS  NO  OBLIGATION  TO  PROVIDE  MAINTENANCE, SUPPORT, UPDATES, OR 
# ENHANCEMENTS.  OREGON  HEALTH  & SCIENCE UNIVERSITY MAKES NO REPRESENTATIONS NOR EXTENDS 
# WARRANTIES  OF  ANY  KIND, EITHER IMPLIED OR EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE 
# IMPLIED  WARRANTIES  OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE 
# USE OF THE SOFTWARE WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.


import urllib

GetCapabilities = '''
<GetCapabilities xmlns="http://www.opengis.net/ows" xmlns:ows="http://www.opengis.net/ows" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/ows fragmentGetCapabilitiesRequest.xsd" service="WCS" updateSequence="XYZ123">
</GetCapabilities>
'''

DescribeSensor = '''
<DescribeSensor xmlns="http://www.opengis.net/sos/0.0" service="SOS" outputFormat="text/xml;subtype=&quot;TML/1.0&quot;" version="0.0.0">

 <procedure>Twanoh</procedure>

</DescribeSensor>
'''

GetObservationByLatest = '''
<sos:GetObservation xmlns:sos="http://www.opengis.net/sos/0.0" xmlns:om="http://www.opengis.net/om/0.0" 
 service="SOS"
 version="0.0.0"
 srsName="EPSG:4326">
  <sos:offering>Twanoh</sos:offering>
  <sos:observedProperty>
    http://marinemetadata.org/cf#sea_water_salinity
  </sos:observedProperty>
  <sos:responseFormat>
     text/xml;subtype=&quot;om/0.0.0&quot;</sos:responseFormat>
  <sos:resultModel>sos:TupleObservation</sos:resultModel>  
  <sos:responseMode>inline</sos:responseMode> 
</sos:GetObservation>
'''

GetObservationByTimePeriod = '''
<GetObservation xmlns="http://www.opengis.net/sos/0.0"
                xmlns:gml="http://www.opengis.net/gml"
                xmlns:ogc="http://www.opengis.net/ogc"
                xmlns:ows="http://www.opengeospatial.net/ows"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/sos/0.0 ../sosGetObservation.xsd"
			    service="SOS" version="0.0.0">
	<offering>Twanoh</offering>
	<eventTime>
		<ogc:T_During>
		<ogc:PropertyName>om:eventTime</ogc:PropertyName> 
			<gml:TimePeriod>
				<gml:beginPosition>2006-11-05T17:18:58.000-06:00</gml:beginPosition>
				<gml:endPosition>2006-11-05T18:07:29.000-06:00</gml:endPosition>
			</gml:TimePeriod>
		</ogc:T_During>
	</eventTime>
	<observedProperty>http://marinemetadata.org/cf#sea_water_salinity</observedProperty>
	<responseFormat>text/xml; subtype=&quot;om/0.0.0&quot;</responseFormat>
</GetObservation>
'''

GetObservationByTimeInstant = '''
<GetObservation xmlns="http://www.opengis.net/sos/0.0"
                xmlns:gml="http://www.opengis.net/gml"
                xmlns:ogc="http://www.opengis.net/ogc"
                xmlns:ows="http://www.opengeospatial.net/ows"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/sos/0.0 ../sosGetObservation.xsd"
                            service="SOS" version="0.0.0">
        <offering>Twanoh</offering>
        <eventTime>
                <ogc:T_During>
                <ogc:PropertyName>om:eventTime</ogc:PropertyName> 
                        <gml:TimeInstant>
                                <gml:timePosition>2006-11-05T16:07:28.5-08:00</gml:timePosition>
                        </gml:TimeInstant>
                </ogc:T_During>
        </eventTime>
        <observedProperty>http://marinemetadata.org/cf#sea_water_salinity</observedProperty>
        <responseFormat>text/xml; subtype=&quot;om/0.0.0&quot;</responseFormat>
</GetObservation>
'''

GetObservationByBoundingBox = '''
<sos:GetObservation xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc"
xmlns:sos="http://www.opengis.net/sos/0.0" xmlns:om="http://www.opengis.net/om/0.0" 
  service="SOS" 
  version="1.0.0" 
  srsName="EPSG:4326">
    <sos:offering>Hoodsport</sos:offering>
    <sos:observedProperty>http://marinemetadata.org/cf#sea_water_salinity</sos:observedProperty>
    <sos:featureOfInterest>
      <ogc:BBOX>
          <ogc:PropertyName>gml:location</ogc:PropertyName>
          <gml:Envelope>
            <gml:lowerCorner>47 -124</gml:lowerCorner>
            <gml:upperCorner>48 -124</gml:upperCorner>
          </gml:Envelope>
      </ogc:BBOX>
    </sos:featureOfInterest>
    <sos:responseFormat>text/xml; subtype=&quot;om/1.0.0&quot;</sos:responseFormat>
    <sos:resultModel>om:Observation</sos:resultModel>
<sos:responseMode>inline</sos:responseMode>
</sos:GetObservation>
'''


import sys
if len(sys.argv) != 3:
  print '''
Test a SOAP SOS server using a canned request.  
Print the response to stdout and 'response.xml'

Usage:
python test_post.py GetCapabilities sos_url
python test_post.py DescribeSensor sos_url
python test_post.py GetObservationByLatest sos_url
python test_post.py GetObservationByTimePeriod sos_url
python test_post.py GetObservationByTimeInstant sos_url
python test_post.py GetObservationByBoundingBox sos_url
'''
  sys.exit(1)

# put any additional wrapping in this format string
req = "%s"

s = "data = req %% (%s,)" % (sys.argv[1],)
exec(s)
print data

sos_url = sys.argv[2] 

raw_input("press return to submit this request...")

f = urllib.urlopen(sos_url, data=data)
out = file('response.xml', 'w+')
resp = f.read()
out.write(resp)
print resp
