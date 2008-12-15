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


import sys, os, os.path, traceback
import time, datetime, xml.utils.iso8601
import types, tempfile

fullwritedir = "/tmp/"

try:
    from mod_python import apache, util, psp
    # running from a webserver
    os.environ["HOME"] = fullwritedir
    import matplotlib
    matplotlib.use('Agg')
except:
    import matplotlib

import pylab
from pylab import *

UTC = matplotlib.pytz.timezone('UTC')
PST = matplotlib.pytz.timezone('US/Pacific')

import matplotlib.dates as pylabdates

import cmop.sosclient as sos

getcaps = 'http://data.stccmop.org/ws/sos.py'

def info(msg):
  apache.log_error(msg, apache.APLOG_NOTICE)

def debug(msg):
  apache.log_error(msg, apache.APLOG_NOTICE)


FORM = '''<form>%s
<div class="control">
</div>
</form>'''

GETCAPSFORM = '''
<div class="control">
Enter one or more GetCapabilities URLs, separated by commas:<br>
<textarea name="getcaps">%s</textarea><br>
<input type="submit" value="Submit"/>
</div>
'''

OPTION = '''
<option value="%s" %s>%s</option>
'''

SELECT = '''
<div class="control">
Choose %s: <br>
<select %s size=6 name="%s">%s</select><br>
<input type="submit" value="Submit"/>
</div>
'''

PLOTCONFIG = '''
<div class="control">
Starttime: <input type="text" name="starttime" value="%s"></input>
Endtime: <input type="text" name="endtime" value="%s"></input>
</div>
%s
'''

STYLE = '''
<style>
select, textarea { width:60em; }
.control { padding-top:1em; }
</style>
'''

SCRIPT = '''
<script type="text/javascript">
</script>
'''

HTML = '''
<html>
<head>%s</head>
<body>%s</body>
</html>
'''

def makeselect(name, options, selected, attrs="multiple", label=None):
  if not label: label = name
  highlight = [OPTION % (o, "selected", o) for o in options if o in selected]
  normal = [OPTION % (o, "", o) for o in options if o not in selected]
  return SELECT % (label, attrs, name, "\n".join(highlight + normal))

def makeform(rows):
  cols = zip(*rows)
  urls = cols[0]
  coffs = cols[1]
  cvars = cols[2]

  uniqueurls = dict([(url, sos.GetCapabilities(url)) for url in urls])
  for k,r in uniqueurls.iteritems():
    r.FetchXML()

  for i, (u,o,v) in enumerate(rows):
    r = uniqueurls[u]
    offs = r.GetOfferings()
    select = makeselect("offering", offs, o)
    ROW % (INPUT % (u,), offerings)

def makevarform(r):
  offs = r.GetOfferings()
  vars = []
  for o in offs:
    vars += [(o, v) for v in r.GetObservedProperties(o)]
  options = [OPTION % (p,v,p,v) for p,v in vars]
  return VARFORM % (options,)

def GetCapabilitiesRequests(urls):
  urls = set(urls)
  pairs = [(u, sos.GetCapabilities(u)) for u in urls]
  
  for u, r in pairs:
    #try:
     r.FetchXML()
    #except:
    #  raise ValueError("Bad URL? %s" % (u,))

  return dict(pairs)

OFFDELIM = ' -- '

def index(req, **kwargs):
  selectedoffs = kwargs.get('offering', '')
  if type(selectedoffs) in types.StringTypes and selectedoffs:
    selectedoffs = [selectedoffs]
  elif not selectedoffs:
    selectedoffs = []

  parsedoffs = [tuple(o.split(OFFDELIM)) for o in selectedoffs]
  # set up the start time and endtime
  s,e = [t.strftime("%Y-%m-%d %H:%M:%S") for t in hoursago(5, UTC)]
  start = kwargs.get('starttime', s)
  end = kwargs.get('endtime', e)

  yaxes = kwargs.get("Yaxis", []) 
  
  if yaxes:
    if type(yaxes) in types.StringTypes:
      yaxes = [yaxes]

    plots = []
    for y in yaxes:
      overlays = [timeseries(u, p, y, start, end) for u,p in parsedoffs]
      overlays = [x for x in overlays if x]
      ylbl = y.split('#')[-1]
      plots.append((ylbl, overlays))
  
    plots = [x for x in plots if x]
    multiscatter(plots, xlabel='time')
    img = MakeImage()
    req.content_type = 'image/png'
    return img

  urls = []
  if kwargs.has_key('getcaps'): urls = kwargs['getcaps'].split(',')
  sosreqs = GetCapabilitiesRequests(urls)
  form = GETCAPSFORM % (",".join(sosreqs.keys()),)
 
  # make the offering select box
  offs = []
  for url, r in sosreqs.iteritems():
    offs += ["%s%s%s" % (url, OFFDELIM, o) for o in r.GetOfferings()]

  s = makeselect("offering", offs, selectedoffs, label=" one or more offerings")
  form += s

  # make the variable select box
  obslist = []
  for u,o in parsedoffs:
    if sosreqs.has_key(u):
      r = sosreqs[u]
    else:
      r = GetCapabilities(u)
      r.FetchXML()
    obslist.append(set(r.GetObservedProperties(o)))
  
  # show only those variables that are shared by all chosen offerings
  if not obslist: vars = set([])
  else:           vars = reduce(set.intersection, obslist)

  s = makeselect("Yaxis", vars, [], label=" one or more observedProperties from this list of those shared by all selected offerings:")

  form += PLOTCONFIG % (start, end, s)

  response = FORM % (form,)
   
  req.content_type = 'text/html'
  return HTML % (STYLE + SCRIPT, response)

def parseTime(t): 
  # horrible...there's no iso8601 parser available
  #d = xml.utils.iso8601.parse(t)
  #dt = datetime.datetime.fromordinal(d/86400.0)
  #print t, d, dt
  #raw_input()
  
  
  datepart, timepart = t.split('T')
  
  #short-circuited; we can handle string dates
  year, month, day = datepart.split('-')
  h,m,other = timepart.split(':')[:3]
  s = other.split('+')[0]
  s = s.split('-')[0]
  
  s = s.replace('Z', '')
  
  secs = s.split('.')
  si = secs[0]
  ms = 0
  if len(secs) > 1:
    if secs[1] != '': 
      ms = float("0.%s" % (secs[1],))*1000000
  
  parts = [year, month, day, h, m, si, ms]
  return "%s-%s-%s %s:%s:%s" % tuple(parts[0:6])
  parts = [int(x) for x in parts]
  d = datetime.datetime(*parts)
  
  num =  pylabdates.date2num(d)
  return num

def parseSOSTuple(row):
  others = [float(x) for x in row[1:]]
  timev = parseTime(row[0])
  return [timev] + others

def errorplot(prefix, width=10):
  msg = prefix
  s = filestring()
  traceback.print_exc(file=s)
  if s.data:
    msg += s.data

  fontsize = 10
  lines = ["      %s" % (c,) for c in msg.split('\n')]
  n = len(lines) + 1
  ht = n/4.0
  f = figure(figsize=(width,ht))
  text(0,0,"\n".join(lines),fontsize=fontsize, clip_on=False)


def timeaxis(time, **kwargs):
  '''Convert a numpy array of strings representing times, convert
to a numpy array of matplotlib floats.  Also, return an appropriate
formatter and ticklocator.  
Optional keyword args:
  sourceformat : format of source time data, defaults to '%Y-%m-%d %H:%M:%S'
  tickformat : format of tick labels, defaults to '%m-%d %H:%M'  (if left blank, this is intended to be automatically configured based on the range eventually)
'''
  dateformat = kwargs.get('sourceformat', '%Y-%m-%d %H:%M:%S')
  tickformat = kwargs.get('tickformat', '%m-%d %H:%M')
  ptime = array([todate(t, dateformat) for t in time])
  if len(ptime) != 0:
    interval = int((max(ptime) - min(ptime))*24 /5.0)
    if interval == 0: interval = 1
  else:
    interval = 4

  dateformat = kwargs.get('dateformat', tickformat)
  hrloc = matplotlib.dates.HourLocator(interval=interval)
  fmt = matplotlib.dates.DateFormatter(dateformat)

  return ptime, hrloc, fmt

def todate(t, format):
  '''Convert a string to a matplotlib time using format'''
  import time, datetime

  d = datetime.datetime(*time.strptime(t, format)[0:6])
  return matplotlib.dates.date2num(d)


def multiscatter(data, **kwargs):
  '''
Creates a matplotlib figure (via pylab) displaying a vertical stack of 
scatter plots with a shared x-axis.

Arguments: 
  data     : a list of (yaxis, overlays) pairs, 
             where overlays is a list of (label, X, Y) pairs, and
             label and yaxis are strings and array is a numpy numeric array
             Examples: 
                [('salinity', [('my data', aX, asY), ('your data', bX, bsY)])]
                [
                 ('salinity', [
                               ('my data', aX, atY), 
                               ('your data', bX, btY)
                              ]
                 ),
                 ('temperature', [
                               ('my data', aX, atY), 
                               ('your data', bX, btY)
                              ]
                 ),
                ]

Optional keyword arguments are:
  xlabel       : label to place on the x-axis.  
                 If xlabel is 'time', special time formatting is used.
  dateformat   : xtick format for time axis, 
                 defaults to '%m-%d %H:%M'
  sourceformat : source format for time string data, 
                 defaults to '%Y-%m-%d %H:%M:%S'
  color_order  : order to rotate through colors, defaults to 'rgbcmykw'
  width        : width of the figure, defaults to 6in
'''

  n = len(data)
  f = figure(figsize=(kwargs.get('width', 7),n*2))
  if not data: return

  sps = []

  # get the xlabel and xdata
  xlbl = kwargs.get('xlabel', None)

  colors = kwargs.get('color_order','rgbcmykw')

  locator, fmt = None, None

  for i,(var,overlays) in enumerate(data):
    p = subplot(n,1,i+1)

    plotargs = []
    for j,(lbl, X, Y) in enumerate(overlays):
      if xlbl == 'time':
        X, locator, fmt = timeaxis(X, **kwargs)
     

      marker = colors[(j+1) % len(colors)] + '.'
      plotargs += [X, Y, marker]


    im = plot(*plotargs)
    props = matplotlib.font_manager.FontProperties(size='x-small')
    legend(tuple([lbl for lbl, X, Y in overlays]), prop=props, numpoints=1, markerscale=1.5, pad=0.18, labelsep=0.01)

    sps.append((p,gci()))
    ylabel(r'%s' % (var,), size='smaller')

    yticks(size='smaller')

    if i+1 == n:
      if xlbl == 'time':
        if not fmt:
          locator = matplotlib.dates.HourLocator(interval=2)
          fmt = matplotlib.dates.DateFormatter("%y-%m-%d %H:%M")
        gca().xaxis_date(None)
        gca().xaxis.set_major_formatter(fmt)
        gca().xaxis.set_major_locator(locator)
        setp(gca().get_xticklabels(), 'rotation', 20,
          'horizontalalignment', 'right', fontsize=8)
      elif xlbl:
        xlabel(r'%s' % (xlbl,), size='smaller')
      else:
        pass

    else:
      setp(gca(), xticklabels=[])

    grid(True)

    gca().xaxis.get_major_locator().refresh()
    labels = gca().get_xticklabels() + gca().get_yticklabels()
    for label in labels:
      label.set_size( 8 )

  subplots_adjust(hspace=0.2, left=0.1, right=0.96, top=1-0.2/(n*2), bottom=(0.38/(n*2)))

  for p,img in sps:
    axes(p)

  return f

def MakeImage():
  # save the active figure to a tempfile
  # should be able to pass in a file object, but alas
  (f, name) = tempfile.mkstemp(".png", dir=fullwritedir)
  os.close(f)
  pylab.savefig(name, format='png')

  # close the active figure
  pylab.close()

  f = file(name)
  img = f.read()
  f.close()
  os.remove(name)

  return img

def timeseries(url, offer, var, start, end):
  datasets = {}
  toplot = []
  range = (start, end)
  #times = [time.strftime(x, "%Y-%m-%d %H:%M") for x in [start, end]]

  ob = sos.GetObservation(url, offer, var)
  ob.AddTimeRangeConstraint([range])
  ob.FetchXML()
  debug(ob.url)
  descr = ob.GetValues("//om:Observation/gml:description/text()")[0]
  rs = ob.GetResult()
  if rs: 
    rs = [parseSOSTuple(t) for t in rs]
    datasets[(url,offer)] = rs
    arrays = [pylab.array(x) for x in zip(*rs)]
    return (descr, arrays[0], arrays[4])

def hoursago(n, tz=PST):
  hours = n
  endtime = datetime.datetime.now(tz)
  delta = datetime.timedelta(hours=float(hours))
  starttime = endtime - delta
  return starttime, endtime

