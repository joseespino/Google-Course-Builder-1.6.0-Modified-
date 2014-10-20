#################################################################################
## ******************************************************************************
## ************** Jose Antonio Espino Palomares (i82espaj@uco.es) ***************
## ******************************************************************************
## ****************** University: "Universidad de Cordoba" **********************
#################################################################################

import cgi
import json
import sys
import urllib2
import webapp2

from google.appengine.ext import db
from appdata import *
from registerdata import *
from google.appengine.api import namespace_manager


class Main(webapp2.RequestHandler):
  def get(self):

    namespace_manager.set_namespace('-global-')

    reg_id_list = list(entry.reg_id for entry in RegisterEntry.all().run())

    html = """<html><head><title>Notifications: Check number of devices</title></head><body>"""
    
    html += """<div style="margin: 0px;padding: 8px;background-color: #E28000;text-align: center;"></div>"""
    
    if len(reg_id_list) == 0:
      html += """<div align=center><h2>No registered device</h2></div>""" 
    else:
      html += """<div align=center><h2>Registered devices:</h2></div>"""
      html += """<div align=center><h2>%s</h2></div>""" % (len(reg_id_list))

    
    html += """</body></html>"""
   
    html += """<div style="text-align: center;
    font-size: smaller;
    padding: 4px;
    border-top: solid 1px #FFB000;">
    Powered by
    <a style="padding-left: 4px;
    padding-right: 4px;
    color: #0000FF;" href="https://code.google.com/p/course-builder/">
    Course Builder</a>
    </div>"""
   
    self.response.set_status(200)
    self.response.headers['Content-Type'] = 'text/html'
    self.response.out.write(html)


class RegisterHandler(webapp2.RequestHandler):
  def get(self):
    """
      Stores the registration ids sent via the 'reg_id' parameter

      Sample request:
      curl http://localhost:8080/register?reg_id=test_id
    """
    self._handle(self.request.GET)

  def post(self):
    """
      Stores the registration ids sent via the 'reg_id' parameter

      Sample request:
      curl -d "reg_id=test_id" http://localhost:8080/register
    """
    self._handle(self.request.POST)

  def _handle(self, param):

    namespace_manager.set_namespace('-global-')

    if 'reg_id' in param and len(param['reg_id']) > 0:
      entry = RegisterEntry(key_name=param['reg_id'])
      entry.reg_id = param['reg_id']
      entry.put()
      self.response.set_status(202)
      return
    self.response.set_status(400)


class UnregisterHandler(webapp2.RequestHandler):
  def get(self):
    """
      Stores the registration ids sent via the 'reg_id' parameter

      Sample request:
      curl http://localhost:8080/unregister?reg_id=test_id
    """
    self._handle(self.request.GET)

  def post(self):
    """
      Stores the registration ids sent via the 'reg_id' parameter

      Sample request:
      curl -d "reg_id=test_id" http://localhost:8080/unregister
    """
    self._handle(self.request.POST)

  def _handle(self, param):

    namespace_manager.set_namespace('-global-')

    if 'reg_id' in param and len(param['reg_id']) > 0:
      key_addr = db.Key.from_path('RegisterEntry', param['reg_id'])
      db.delete(key_addr)
      self.response.set_status(202)
      return
    self.response.set_status(400)


app = webapp2.WSGIApplication([('/gcm/', Main),
                               ('/gcm/register', RegisterHandler),
                               ('/gcm/unregister', UnregisterHandler)], debug=True)
