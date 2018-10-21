from __future__ import print_function

import ephem
import datetime
import json

class SatTrack():

    def __init__(self):
        # Load all SVs from JSON
        self.data = self.load_all_satellites()

        # Set the first GPS satellite as the current SV
        l1 = self.data['GPS'][1]['id'].encode('utf-8')
        l2 = self.data['GPS'][1]['l2'].encode('utf-8')
        l3 = self.data['GPS'][1]['l3'].encode('utf-8')

        # Update ephemeris for current SV
        ctime = datetime.datetime.now()
        self.csat = ephem.readtle(l1,l2,l3) 
        self.csat.compute(ctime)

    def load_all_satellites(self):
        with open('NAV_SAT.json') as f:
            data = json.load(f)
        return data

    def print_location_all(self):

        ctime = datetime.datetime.now()
        for sys in self.data.items():
            for sat in sys[1]:
                l1 = sat['id'].encode('utf-8')
                l2 = sat['l2'].encode('utf-8')
                l3 = sat['l3'].encode('utf-8')
                sv = ephem.readtle(l1,l2,l3)
                sv.compute(ctime)

                print(l1)
                print(sv.sublat)
                print(sv.sublong)
                print(sv.elevation)
                print("\n")
                
def lambda_handler(event, context):
    #print("Received event: " + json.dumps(event, indent=2))
    print("value1 = " + event['key1'])
    print("value2 = " + event['key2'])
    print("value3 = " + event['key3'])
    
    tracker.load_all_satellites()
    tracker.print_location_all()
    
    return event['key1']  # Echo back the first key value
    #raise Exception('Something went wrong')

tracker = SatTrack()
tracker.print_location_all()