f = open("sou_raw.txt", 'r')
data = f.read()
f.close()
rows = data.split('\n')
ce = {}
for row in rows:
    item = row.split()
    if(int(item[1]) in ce):
        ce[int(item[1])] = int(item[0]) + ce[int(item[1])]
    else:
        ce[int(item[1])] = int(item[0])

output = sorted(ce.items(), key=lambda x:x[0])
f = open('result.txt', 'w')
f.write('{')
for it in output:
    f.write('('+str(it[1])+','+str(it[0])+'),')
f.write('}')
f.close()