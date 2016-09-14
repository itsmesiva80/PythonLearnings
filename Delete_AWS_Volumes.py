import boto.ec2
import boto.cloudformation
conn = boto.ec2.connect_to_region('eu-west-1')

curr_vol = conn.get_all_volumes()
tags = conn.get_all_tags()
print(len(curr_vol))
i=0
volNames=["trux","whip","zaster","zeus","goodbuy"]
for volName in volNames:
    print(volName)
    for i in range(len(curr_vol)):
        print(curr_vol[i].id)
        print(curr_vol[i].status)
        if curr_vol[i].status == "available":
            print((curr_vol[i].tags).get("Team"))
            if (curr_vol[i].tags).get("Team") == volName :
                print ("Pass")
                curr_vol[i].delete(dry_run=False)
