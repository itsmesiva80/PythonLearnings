import requests
import sh
sivaToken= (sh.zign("token")).strip()
print(sivaToken)
url='https://cloud-lobster-prod-ci.ci.zalan.do/api/request'
headers={'Authorization': 'Bearer %s'%sivaToken}
r = requests.get(url,headers=headers )
responseTxt=r.text
responseTxt = responseTxt.split("},")
i=0
for i in range(len(responseTxt)):
    print(responseTxt[i])
    print(rfind(responseTxt[i],"request_id",","))
    print("__________________________________________________________________")
