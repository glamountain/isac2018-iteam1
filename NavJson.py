import numpy as np
import pylab as plt
import ephem
import datetime
import json

with open('NAV_SAT.json') as f:
    data = json.load(f)

ctime = datetime.datetime.now()
for sys in data.items():
    for sat in sys[1]:
        l1 = sat['id'].encode('utf-8')
        l2 = sat['l2'].encode('utf-8')
        l3 = sat['l3'].encode('utf-8')
        sv = ephem.readtle(l1,l2,l3)
        sv.compute(ctime)
        print sv.ra, sv.dec
