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



from mod_python import apache, util, psp
import mod_python
import sys, urllib
import cStringIO
import traceback

import xml.xpath as xpath
import xml
import xml.dom.minidom
from xml.dom.ext.reader import Sax2


# If you see this in your browser, then 
# you need to configure Apache!

# Uses mod_python's "publisher handler"
# Make sure mod_python is installed, 
# and add this to httpd.conf:

# <Directory /var/www/html/cmop/ws>
#  AddHandler mod_python .py
#  DirectoryIndex index.html index.py
#  PythonHandler mod_python.publisher
#  PythonDebug On
# </Directory>

# This may be replaced with more 
# sophisticated config file handling

class SOSException(ValueError):
  exceptionreport = '''
<ExceptionReport xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://schemas.opengis.net/ows/1.0.0/owsExceptionReport.xsd" version="1.0.0" language="en">
%s  
</ExceptionReport>
'''
  exception = '''
<Exception locator="service" exceptionCode="%s">
%s  
</Exception>
'''
  exceptiontext = '''<ExceptionText><![CDATA[
%s
]]></ExceptionText>'''
  
  def __init__(self, code, msg, othermsgs = []):
    self.code = code
    self.msg = msg
    self.children = othermsgs
  
  def __str__(self):
    return self.ToXML()

  def __repr__(self):
    return self.ToXML()
  
  def AddText(self, text):
    self.children.append(text)

  def ToXML(self):
    exchildren = [self.exceptiontext % (c,) for c in self.children]
    extextlist = [self.exceptiontext % (self.msg,)] + exchildren
    extext = "\n".join(extextlist)
    body = self.exception % (self.code, extext)
    return self.exceptionreport % (body,)

import config
conf = config.config


namespaces = {
  "xsi":"http://www.w3.org/2001/XMLSchema-instance",
  "swe":"http://www.opengis.net/swe/0",
  "gml":"http://www.opengis.net/gml",
  "sos":"http://www.opengis.net/sos/0",
  "om":"http://www.opengis.net/om",
  "ows":"http://www.opengeospatial.net/ows",
  "xlink":"http://www.w3.org/1999/xlink",

  "soap":"http://schemas.xmlsoap.org/soap/envelope/"
}

# inherit from an ordinary dictionary
class KVPFromXML(dict):
  '''Convenience wrapper for extracting key-value pairs from an XML document'''
  def __init__(self, server, xmlnode):
    self.server = server
    self.context = xml.xpath.Context.Context(xmlnode)
    self.context.setNamespaces(namespaces)
    self.errs = {}

  def ExtractArgument(self, name, xpath):
    '''Extracts name, value pairs from an XML fragment
using xpath.  Values are quoted for use in a url
args is a dictionary mapping names to xpath expressions
xmlnode is DOM node for constructing a context
appends results to env and errs, returning true
if no errors were generated
'''
    if type(xpath) == str:
      xpath = xml.xpath.Compile(xpath)

    nodes = xpath.evaluate(self.context)

    # expects a single result node 
    if len(nodes) == 1:
      node = nodes[0]
      if node.nodeName == "#text":
        # text node
        value = node.nodeValue
      elif node.nodeValue == None:
        # element node
        value = node.localName
      else:
        # attribute node, or something we don't want
        value = node.nodeValue
      self[name] = Quote(value.strip())
      return True
    elif len(nodes) == 2:
      node = nodes[0]
      if node.nodeName == "#text":
        # text node
        value = node.nodeValue + "," + nodes[1].nodeValue
      elif node.nodeValue == None:
        # element node
        value = node.localName
      else:
        # attribute node, or something we don't want
        value = node.nodeValue
      self[name] = Quote(value.strip())
      return True      
    else:
      errtext = "Found %s nodes matching xpath expression; expected 1 (%s)"
      err = ValueError(errtext % (len(nodes), xpath))
      self.errs[name] = err
      return False

  def ExtractArguments(self, args):
    for name, xpath in args.iteritems():
      if not self.ExtractArgument(name, xpath):
        for a in args: self.pop(a, None)
        return False
    return True

  def Submit(self):
    qs = urllib.urlencode(self)
    debug(self.server + '?' + qs)
    # Don't pas the qs as the 'data' argument or you'll get a POST
    response = urllib.urlopen(self.server + '?' + qs)
    # parse and re-serialize to strip the document header
    # we can speed this up if necessary
    dom = xml.dom.minidom.parseString(response.read())
    root = dom.documentElement
    return root.toxml("utf-8")
    
def HandleSOSRequest(xmlnode):
  r = KVPFromXML(conf['appserver'], xmlnode)
  args = {"request": '.'}
    
  # Is this a GetCapabilities request?
  if r.ExtractArgument('request', '//*[local-name()="GetCapabilities"]'):
    # we know we have a GetCapabilities request; just submit it
    return r.Submit()

  # Is this a DescribeSensor request?
  elif r.ExtractArgument('request', '//*[local-name()="DescribeSensor"]'):
    xpath = xml.xpath.Compile('//*[local-name()="procedure"]/text()')
    if r.ExtractArgument('SystemId', xpath):
      return r.Submit()

  # Is this a GetObservation request?
  elif r.ExtractArgument('request', '//*[local-name()="GetObservation"]'):
   
    args = { 
     'offering': xml.xpath.Compile('//*[local-name()="offering"]/text()'),
     'observedProperty':xml.xpath.Compile('//*[local-name()="observedProperty"]/text()'),
    }
    if r.ExtractArguments(args):
      # Now see which type of GetObservation request we have

      # TimePeriod
      periodargs = {
      'starttime':xml.xpath.Compile('//*[local-name()="eventTime"]//*[local-name()="TimePeriod"]/*[local-name()="beginPosition"]/text()'),
      'endtime':xml.xpath.Compile('//*[local-name()="eventTime"]//*[local-name()="TimePeriod"]/*[local-name()="endPosition"]/text()'),
      }
      
      # BBox
      bboxargs = {
      'lowerCorner':xml.xpath.Compile('//*[local-name()="BBOX"]//*[local-name()="Envelope"]/*[local-name()="lowerCorner"]/text()'),
      'upperCorner':xml.xpath.Compile('//*[local-name()="BBOX"]//*[local-name()="Envelope"]/*[local-name()="upperCorner"]/text()'),            
      }
      
      if r.ExtractArguments(periodargs):
        r['eventTime'] = "%s/%s" % (r.pop('starttime'), r.pop('endtime'))
        return r.Submit()

      # TimeInstant
      elif r.ExtractArgument('eventTime', xml.xpath.Compile('//*[local-name()="eventTime"]//*[local-name()="TimeInstant"]/*[local-name()="timePosition"]/text()')):
        return r.Submit()

      # BBox
      elif r.ExtractArguments(bboxargs):
        return r.Submit()

      # Default to GetLatest 
      else:
        return r.Submit()

  errmsg = "Could not parse XML POST Request.  You may be attempting an unsupported method; see %s?request=GetCapabilities for supported methods.  Otherwise, you may have omitted arguments, or supplied them in a form not understood by the server.  This exception report also contains a log of failed attempts to parse the request." % (conf['appserver'],)
  s = SOSException('ParseError', errmsg)
  for n, v in r.errs.iteritems():
    s.AddText("'%s' not found using xpath: %s" % (n, v))

  return s.ToXML()


def info(msg): 
  apache.log_error(msg, apache.APLOG_NOTICE)

def debug(msg):
  apache.log_error(msg, apache.APLOG_NOTICE)

def UnQuote(s):
  if s: return urllib.unquote(s)
  else: return s

def Quote(s):
  if s: return urllib.quote(s, "/:")
  else: return s

class Connection: 
  # Change this class to use your preferred DB library
  def __init__(self, conf):
    self.conf = conf
    self.db = None

  # shut down the connection when this object is dereferenced
  # this is important due to mod_python's imperfect design of module reloading
  def __del__(self):
    self.close()

  def query(self, sql):
    if not self.db: self.connect()
    try:
      response = self.db.query(sql)
    except Exception, e: 
      raise SOSException("SQLError", "%s:\n%s" % (e,sql))
    self.close()
    return response

  def close(self):
    #info("connection object deleted");
    if self.db:
      self.db.close()
      self.db = None

class PostgreSQLConnection(Connection):
  def connect(self):
    import pg
    info("SOS connection established");
    self.db = pg.DB (conf['dbname'], \
                     conf['host'], \
                     -1, \
                     None, \
                     None, \
                     conf['user'],
                     conf['password'])


  def getDicts(self,sql):
    response = self.query(sql)
    return response.dictresult()

  def getTuples(self,sql):
    response = self.query(sql)
    return response.getresult()

  def execCommand(self, sql):
    response = self.query(sql)
    return response

class MySQLConnection(Connection):
  # Change this class to use your preferred DB library
  def connect(self):
    import MySQLdb
    self.db = MySQLdb.connect(
       db=conf['dbname'],
       host=conf['host'],
       user=conf['user'],
       passwd=conf['password']
    )

  def query(self, sql):
    if not self.db: self.connect()
    try:
      c=self.db.cursor()
      c.execute(sql)
      response = c.fetchall()
    except Exception, e:
      raise SOSException("SQLError", "%s:\n%s" % (e,sql))
    self.close()
    return c, response


  def getDicts(self,sql):
    c, rs = self.query(sql)
    flds = [d[0] for d in c.description]
    return [dict(zip(flds, r)) for r in rs]

  def getTuples(self,sql):
    c, rs = self.query(sql)
    return rs

  def execCommand(self, sql):
    c, rs = self.query(sql)


# If you get a python error referencing pg_hba.conf, 
# then your permissions are not set up properly for postgres

# mod_python re-import is broken in 3.2
# there is no way to close the connection when 
# this module is reimported
if not globals().has_key('DB'):
  rdbms = conf['rdbms']
  DB = eval('%sConnection(conf)' % (rdbms,))

# for mod_python version 3.3
def __mp_clone__(module):
  module.DB = DB


def __mp_purge__(module):
  try:
    DB.close()
  except:
    pass

exceptionreport = '''
<ExceptionReport xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://schemas.opengis.net/ows/1.0.0/owsExceptionReport.xsd" version="1.0.0" language="en">
<Exception locator="service" exceptionCode="%s">
<ExceptionText><![CDATA[%s]]></ExceptionText>
</Exception>
</ExceptionReport>
'''
   
namespaces = {
  "xsi":"http://www.w3.org/2001/XMLSchema-instance", 
  "swe":"http://www.opengis.net/swe/0", 
  "gml":"http://www.opengis.net/gml", 
  "sos":"http://www.opengis.net/sos/0", 
  "om":"http://www.opengis.net/om", 
  "ows":"http://www.opengeospatial.net/ows", 
  "xlink":"http://www.w3.org/1999/xlink" 
}

def getRequiredArgument(req, arg):
  a = getOptionalArgument(req, arg)
  if not a:
    raise TypeError("%s required" % (arg,))
  else:
    return a

def urnKey(urn):
  # enforce the local urn prefix from the config file?
  urnprefix = conf["urnprefix"]
  #assert (urnprefix == urn[:len(urnprefix)])
  return urn.split(':')[-1]

def getOptionalArgument(req, arg):
  form = util.FieldStorage(req)
  a = form.get(arg, None)
  return a

def FormatTime(attribute):
  return conf['timeformat'] % {'attribute':attribute}

class TrueCondition:
  def AsSQL(self):
    return 'True'

class BoundingBoxCondition:
  def __init__(self, bbox):
    self.coords = tuple(bbox.split(','))
    if not len(self.coords) == 4:
      raise SOSException("ParseError", "A bounding box condition must be two coordinate pairs (geographic points) represented as a comma separated list of four values.  The bbox paramter value received was '%s'" % (bbox,))
      self.pairs = (tuple(coords[0:2]), tuple(coords[2:4]))

  def AsSQL(self):
    return conf["bboxformat"] % self.coords
      
class TimeCondition:
  TItemplate = '''
<gml:TimeInstant>
%s
</gml:TimeInstant>

'''
  TPtemplate = '''
<gml:TimePeriod gml:id="%s">
%s
%s
</gml:TimePeriod>
'''
  
  positiontemplate = '''<gml:%sPosition %s>%s</gml:%sPosition>'''

  indet = '''indeterminatePosition="%s"'''

  def __init__(self, eventTime, gmlid="DATA_TIME"):
    self.eventTime = eventTime
    self.gmlid = gmlid
 
    if self.eventTime:
      self.ParseEventTime()
    else:
      raise SOSException("eventTime specified, but no value received: '%s'" % (self.eventTime,))

  def ISO(self, time):
    # TODO: should this go through postgres using config.timeformat as it is now?
    # an extra dependency vs. duplicated code....
    return DB.getTuples("SELECT %s" % (FormatTime("'%s'" % (time,)),))[0][0] 

  def ParseEventTime(self):
    ranges = [rng.split('/') for rng in self.eventTime.split(',')]
    self.instants = []
    self.periods = []
    for r in ranges:
      if len(r) == 2:
        self.periods.append(tuple(r))
      elif len(r) == 1:
        self.instants.append(r[0])
      else:
        raise SOSException("eventTime parameter should be a comma delimited list of time primitives, each of which can be of the form 'b/e' or 'i', where b, e, and i are times in ISO 8601.  One element of this list, '%s', does not match this format." % ('/'.join(r),))

  def GMLPosition(self, type, time):
    if time == 'unknown' or time == 'now':
      itime = self.indet % (time,)
      time = ""
    elif (not time or time == 'None') and type == 'end':
      itime = self.indet % ('now',)
      time = ''
    elif time == 'None' or (not time):
      itime = self.indet % ('unknown',)
      time = ''
    else:
      itime = ""
      time = self.ISO(time)
    return self.positiontemplate % (type, itime, time, type)

  def AsGML(self):
    xml = ""
    for ti in self.instants:
      p = self.GMLPosition('time', ti)
      xml += self.TItemplate % (p,)

    for tp in self.periods:
      begin = self.GMLPosition('begin', tp[0])
      end   = self.GMLPosition('end', tp[1])
      xml += self.TPtemplate % (self.gmlid, begin, end)
    return xml

  def AsSQL(self):
    periodstr = "(time BETWEEN '%s' AND '%s')"
    periodcond = " OR ".join([periodstr % r for r in self.periods]) or 'False'
    instantstr = "(time = '%s')"
    instantcond = " OR ".join([instantstr % r for r in self.instants]) or 'False'
    return "(%s)" % (" OR ".join([periodcond, instantcond]),)

class TimeConditionLatest(TimeCondition):
  def __init__(self, offering, observedProperty):
    self.offering = offering
    self.observedProperty = observedProperty
    d = {'offering':offering, 'observedProperty':observedProperty}
    sql = conf['latesttime_query'] % d
    maxt = DB.getTuples(sql)[0][0]
    #raise SOSException('debug', sql)
    if maxt:
      self.instants = [maxt]
      self.periods = []
    else:
      raise SOSException("NoData", "No data available for the requested ObservationOffering (%s) and observedProperty (%s)" % (offering, observedProperty))

def restSOS(req):
    req.form = util.FieldStorage(req)
    if not req.form.has_key('request'):
      request = 'GetCapabilities'
    else:
      request = req.form['request']
    template = psp.PSP(req, filename = request+".template.xml")
    env = { 'req' : req }
    env.update(globals())
    env.update(conf)
    env.update(req.form)
    template.run(env)


def parse(text):
  reader = Sax2.Reader()
  d = reader.fromString(text)
  c = xml.xpath.Context.Context(d)
  c.setNamespaces(namespaces)
  # is there any need for a query here?
  query = "/"
  e = xml.xpath.Compile(query)
  result = e.evaluate(c)
  return result[0]

class bufferProxy:
    # Adapted from
    # http://www.modpython.org/pipermail/mod_python/2004-November/016841.html
    # allows clearing output in case of error
    def __init__(self,outputBuffer):
        self.outputBuffer = outputBuffer
    def write(self,data,flush=None):
        self.outputBuffer.write(data)

def handler(req):
  req.content_type = 'text/xml'
  req.headers_out['Cache-Control'] = 'no_cache'
  info("Received SOS Request: %s%s" % (req.hostname, req.unparsed_uri))

  # Adapted from
  # http://www.modpython.org/pipermail/mod_python/2004-November/016841.html
  # allows clearing output in case of error
  outputBuffer = cStringIO.StringIO()
  outputBufferProxy = bufferProxy(outputBuffer)

  oldReqWrite = req.write
  req.write = outputBufferProxy.write

  try:
    if req.method == 'POST':
      data = req.read()
      if data:
        sosreq = parse(data)
        response = HandleSOSRequest(sosreq)
        req.write(response)
      else:
        raise ValueError("No POST data in SOS POST request")
    else:
      # no return value; PSP writes directly to request object
      restSOS(req)

    req.write = oldReqWrite
    result = outputBuffer.getvalue()
    outputBuffer.close()
    req.write(result)
    return apache.OK
    
  except SOSException, e:
    outputBuffer.seek(0)
    outputBuffer.truncate(0)
    req.write = oldReqWrite
    req.write(e.ToXML())
    return apache.OK
    
  except Exception, e:
    outputBuffer.seek(0)
    outputBuffer.truncate(0)
    req.write = oldReqWrite
    othertext = traceback.format_exception(*sys.exc_info())
    req.write("%s" % (SOSException(e.__class__.__name__, e, othertext),))
    return apache.OK
  
