import boto.ec2
import boto.cloudformation
conn = boto.ec2.connect_to_region('eu-west-1')
reservations = conn.get_all_reservations()
instances = reservations[0].instances
inst = instances[0]
print (instances)
print (inst.id)
print (inst.tags['Name'])
print (inst.instance_type)
print (inst.state)


conn1 = boto.cloudformation.connect_to_region('eu-west-1')
stacName = conn1.boto.cloudformation.stack()
print(stacName.tag('Name'))
