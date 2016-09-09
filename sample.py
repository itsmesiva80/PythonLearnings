import datetime
val4 = "Aug 24 2016 2:11 PM"
do = datetime.datetime.strptime(val4, "%b %d %Y %I:%M %p")
print(do.strftime("%Y-%m-%d %H:%M").date())
