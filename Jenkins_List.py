import csv
def isNotBlank(myString):
        return(myString and myString.strip())

with open('/Users/sthammana/Documents/CI_Jenkis_List_CSV.csv', newline='')as jenkins_file:
	reader = csv.reader(jenkins_file)
	for row in reader:
	   if len(row)>1:	
             print(row.split(";")[2])
             
