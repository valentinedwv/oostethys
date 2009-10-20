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

# Edit this file to configure pysos

config = {
 # Database connection information
 'dbname'                   : 'cmop',
 #'dbname'                   : 'mysql',
 'host'                     : 'cdb02.stccmop.org',
 #'host'                     : 'localhost',
 'user'                     : 'nobody',
 'password'                 : '',

 'rdbms'                    : 'PostgreSQL', # or MySQL

 # any SQL statement, or view name, or table name, 
 # or SQL function call that returns a set
 # must use return the appropriate tuple types, as follows each

 'offering_query'            : '''
SELECT * FROM sos.offering
''',
 #(description, offering, srid, 
 # xmin, ymin, xmax, ymax, 
 # starttime, endtime, uri, featureofinterest)

 'observedproperty_query'    : '''
SELECT * FROM sos.observedproperty
''',
 # (offering, output, variable, mmiuri, featureofinterest, uom)

 'sensor_query'              : '''
SELECT * FROM sos.sensor
''',
 #(description, offering, srid, 
 # xmin, ymin, xmax, ymax, 
 # starttime, endtime, uri, featureofinterest)

 'observation_query'         : '''
SELECT * FROM sos.observation
''',
 # (offering, output, time, lat, lon, depth, value)


 # any SQL statement (or SQL function call)
 # include a '%s' placeholder for the 
 # offering and observedProperty ids, in that order

 # We use a function here, since the Postgresql optimizer 
 # does a rubbish job at optimizing our query,
 # (refusing to use a filter condition on a backward index scan)
 'latesttime_query'          : """
         SELECT coalesce(getlatest('%(offering)s', '%(observedProperty)s'),
                         getlatestorcasurface('%(offering)s', '%(observedProperty)s'));
                               """,

 # time format expression in your database
 # use "%(attribute)s" as a placeholder for a 
 # time attribute in the queries above
 
 # This expression is unusally complex because Postgresql's time 
 # formatting does not have a code for fractional seconds, so we compute 
 # it ourselves
 'timeformat'                : '''
    to_char(timezone('UTC', (%(attribute)s)::timestamptz), 'YYYY-MM-DD"T"HH24:MI:') 
 || to_char(extract('seconds' from (%(attribute)s)::timestamptz), 'FM00.0999') 
 || '+00:00'
''',
 
 # We use PostGIS constructs to implement the bounding box query.  
 # Leave placeholders for the two corners of the bounding box
 # LL, UR (make sure the order is lon, lat corresponding to x,y)
  'bboxformat'              : '''
   setsrid(makebox2d(
        makepoint(%s, %s), 
        makepoint(%s, %s))
   ,4326) 
   && 
   transform(location, 4326)
''',

 # administrative metadata
 'title'                   : 'NANOOS SOS Service',
 'provider'                : 'Northwest Association of Networked Ocean Observing Systems (NANOOS)',
 'contactname'             : 'Bill Howe',
 'contactposition'         : 'DMAC Services Coordinator',
 'contactemail'            : 'howeb@stccmop.org',
 # TODO: add the other fields

 # This value will be the prefix of all urns, notably for observedProperty
 # leave blank if your urns come from the database fully formed.
 'urnprefix'               : '',

 # location metadata
 'rooturl'                 : 'http://www.nanoos.org/', 
 'appserver'               : 'http://data.stccmop.org/ws/sos.py',
 'path'                    : '/var/www/html/ws',

 'sensorMLschema'          : 'http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd',
 'getcapabilitiesschema'   : 'http://schemas.opengis.net/sos/1.0.0/sosGetCapabilities.xsd',

 'tokenSeparator'          : ',',
 'blockSeparator'          : '|',
} 

