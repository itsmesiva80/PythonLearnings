import sh
sivaToken= sh.zign("token")
print ("My Token: %s" %sivaToken)
print (sh.curl("https://cloud-lobster-prod-ci.ci.zalan.do/api/request", "-H", "Authorization: Bearer %s"%sivaToken))
