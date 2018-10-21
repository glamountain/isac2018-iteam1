import numpy as np
import pylab as plt
import ephem
import datetime
import json

# def_sat = 
def_loc = ('42.37', '-71.03', '0')

def load_all_satellites():
    with open('NAV_SAT.json') as f:
        data = json.load(f)
    return data

def print_location_lle(data):

    ctime = datetime.datetime.now()
    for sys in data.items():
        for sat in sys[1]:
            l1 = sat['id'].encode('utf-8')
            l2 = sat['l2'].encode('utf-8')
            l3 = sat['l3'].encode('utf-8')
            sv = ephem.readtle(l1,l2,l3)
            sv.compute(ctime)
            print sv.sublat, sv.sublong, sv.elevation

data = load_all_satellites()
print_location_lle(data)

