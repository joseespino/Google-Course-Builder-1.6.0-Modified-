import webapp2
import datetime

import os
import appengine_config
import jinja2
import jinja2.exceptions
import messages
from google.appengine.ext.webapp import template
from google.appengine.ext import ndb
from models import custom_modules
from appdata import *
from models import roles
from common import jinja_utils
from common import safe_dom

from modules.admin import admin

from google.appengine.api import users
import google.appengine.api.app_identity as app

DIRECT_CODE_EXECUTION_UI_ENABLED = False

appconfig = None

class ConfigureApp(webapp2.RequestHandler):

    def get(self):
        template_values = {}
        #template_values['page_title'] = self.format_title('Deployment')
        #template_values['page_description'] = messages.DEPLOYMENT_DESCRIPTION
        self.AdminHandler.render_page(template_values)
    ''' 
    def format_title(self, text):
        """Formats standard title."""
        return safe_dom.NodeList().append(
            safe_dom.Text('Course Builder ')
        ).append(
            safe_dom.Entity('&gt;')
        ).append(
            safe_dom.Text(' Admin ')
        ).append(
            safe_dom.Entity('&gt;')
        ).append(
            safe_dom.Text(' %s' % text))
    
    def render_page(self, template_values):
        """Renders a page using provided template values."""

        template_values['top_nav'] = self._get_user_nav()
        
        template_values['user_nav'] = safe_dom.NodeList().append(
            safe_dom.Text('%s | ' % users.get_current_user().email())
        ).append(
            safe_dom.Element(
                'a', href=users.create_logout_url(self.request.uri)
            ).add_text('Logout')
        )
        template_values[
            'page_footer'] = 'Created on: %s' % datetime.datetime.now()
        
        self.response.write(
            self.get_template('view.html', []).render(template_values))

    def get_template(self, template_name, dirs):
        """Sets up an environment and Gets jinja template."""
        return jinja_utils.get_template(
            template_name, dirs + [os.path.dirname(__file__)])

    def _get_user_nav(self):
        current_action = self.request.get('action')
        nav_mappings = [
            ('', 'Courses'),
            ('settings', 'Settings'),
            ('perf', 'Metrics'),
            ('deployment', 'Deployment')]
        if DIRECT_CODE_EXECUTION_UI_ENABLED:
            nav_mappings.append(('console', 'Console'))
        nav = safe_dom.NodeList()
        for action, title in nav_mappings:
            if action == current_action:
                elt = safe_dom.Element(
                    'a', href='/admin?action=%s' % action,
                    className='selected')
            else:
                elt = safe_dom.Element('a', href='/admin?action=%s' % action)
            elt.add_text(title)
            nav.append(elt).append(safe_dom.Text(' '))

        if appengine_config.gcb_appstats_enabled():
            nav.append(safe_dom.Element(
                'a', target='_blank', href='/admin/stats/'
            ).add_text('Appstats')).append(safe_dom.Text(' '))

        if appengine_config.PRODUCTION_MODE:
            app_id = app.get_application_id()
            nav.append(safe_dom.Element(
                'a', target='_blank',
                href=(
                    'https://appengine.google.com/'
                    'dashboard?app_id=s~%s' % app_id)
            ).add_text('Google App Engine'))
        else:
            nav.append(safe_dom.Element(
                'a', target='_blank', href='http://localhost:8000/'
            ).add_text('Google App Engine')).append(safe_dom.Text(' '))

        nav.append(safe_dom.Element(
            'a', target='_blank',
            href='https://code.google.com/p/course-builder/wiki/AdminPage'
        ).add_text('Help'))

        nav.append(safe_dom.Element(
            'a',
            href=(
                'https://groups.google.com/forum/'
                '?fromgroups#!forum/course-builder-announce'),
            target='_blank'
        ).add_text('News'))

        return nav

    def get(self):
        appconfig = AppConfig.get_or_insert("config")
        
        if not appconfig.gcm_api_key:
            appconfig.gcm_api_key = "<gcm key here>"
        
        appconfig.put()
        
        template_values = {
            'appconfig': appconfig,
        }
        path = os.path.join(os.path.dirname(__file__), 'config.html')
        self.response.out.write(template.render(path, template_values))
    
    def post(self):
        appconfig = AppConfig.get_or_insert("config")
        appconfig.gcm_api_key = self.request.get("gcm_api_key")
        
        
        if self.request.get("apns_test_mode") == "True":
            appconfig.apns_test_mode = True
        else:
            appconfig.apns_test_mode = False
        
        appconfig.put()
        
        template_values = {
            'appconfig': appconfig,
        }
        path = os.path.join(os.path.dirname(__file__), 'config.html')
        self.response.out.write(template.render(path, template_values))
    '''
def register_module():
    """Registers this module in the registry."""

    gcm_handlers = [('/admin/gcmprueba', ConfigureApp)]

    global custom_module
    custom_module = custom_modules.Module(
        'GCM',
        'A set of pages for managing Course Builder course.',
        [], gcm_handlers)
    return custom_module

