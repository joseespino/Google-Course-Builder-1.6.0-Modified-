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

class RegisterEntry(db.Model):
	reg_id = db.StringProperty()

