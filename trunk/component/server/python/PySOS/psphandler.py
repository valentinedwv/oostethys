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
import cStringIO
import traceback
import sys,os,time

class bufferProxy:
    # Adapted from
    # http://www.modpython.org/pipermail/mod_python/2004-November/016841.html
    # allows clearing output in case of error
    def __init__(self,outputBuffer):
        self.outputBuffer = outputBuffer
    def write(self,data,flush=None):
        self.outputBuffer.write(data)

def info(msg): 
  apache.log_error(msg, apache.APLOG_NOTICE)

def debug(msg):
  apache.log_error(msg, apache.APLOG_NOTICE)


def handler(req):
  req.content_type = 'text/xml'
  req.headers_out['Cache-Control'] = 'no_cache'
  info("PSPhandler: Received Web Request: %s%s" % (req.hostname, req.unparsed_uri))

  # Adapted from
  # http://www.modpython.org/pipermail/mod_python/2004-November/016841.html
  # allows clearing output in case of error
  outputBuffer = cStringIO.StringIO()
  outputBufferProxy = bufferProxy(outputBuffer)

  oldReqWrite = req.write
  req.write = outputBufferProxy.write

  try:
    start = time.time()
    req.form = util.FieldStorage(req)
    template = psp.PSP(req, filename=req.filename + ".template")
    env = {}
    env.update(globals())
    env.update(req.form)
    template.run(env)

    result = outputBuffer.getvalue()
    req.write = oldReqWrite
    req.write(result)
    info("Request served in %s seconds" % (time.time() - start,))
    outputBuffer.close()
    return apache.OK
    
  except Exception, e:
    outputBuffer.seek(0)
    outputBuffer.truncate(0)
    req.write = oldReqWrite
    stackframes = traceback.format_exception(*sys.exc_info())
    xmlframes = '\n'.join(['<stackframe><![CDATA[%s]]></stackframe>' % (frame,) for frame in stackframes])
    req.write('<exception><error><![CDATA[%s]]></error>%s</exception>' % (e, xmlframes))
    return apache.OK
  
