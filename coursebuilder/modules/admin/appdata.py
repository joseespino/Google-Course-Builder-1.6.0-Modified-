#################################################################################
## ******************************************************************************
## ************** Jose Antonio Espino Palomares (i82espaj@uco.es) ***************
## ******************************************************************************
## ****************** University: "Universidad de Cordoba" **********************
#################################################################################

import json
import logging
import time
import uuid
from google.appengine.api import memcache
from google.appengine.ext import db

class AppConfig(db.Model):
    gcm_api_key = db.StringProperty()
