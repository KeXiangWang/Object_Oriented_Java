import random


def PolyOut():
    k = random.randint(-1,0)
    st = ""
    if(k==0):
        k=1
        st += '+'
    else:
        st += '-'
    for i in range(1, numPoly):
        st += '{'
        numItem = random.randint(1, maxItem)
        numItem = 50
        print(numItem)
        for j in range(1, numItem):
            coe = random.randint(-Max, Max)
            exp = random.randint(0, Max)
            raw.write(str(coe*k)+' '+str(exp)+'\n')
            st = st+'('+str(coe)+','+str(exp)+'),'
        coe = random.randint(-Max, Max)
        exp = random.randint(0, Max)
        raw.write(str(coe*k)+' '+str(exp)+'\n')
        st = st+'('+str(coe)+','+str(exp)+')}' 
        k = random.randint(-1,0)
        if(k==0):
            k=1
            st += '+'
        else:
            st += '-'
    st += '{'
    numItem = random.randint(1, maxItem)
    numItem = 50
    print(numItem)
    for j in range(1, numItem):
        coe = random.randint(-Max, Max)
        exp = random.randint(0, Max)
        raw.write(str(coe*k)+' '+str(exp)+'\n')
        st = st+'('+str(coe)+','+str(exp)+'),'
    coe = random.randint(-Max, Max)
    exp = random.randint(0, Max)
    raw.write(str(coe*k)+' '+str(exp)+'\n')
    st = st+'('+str(coe)+','+str(exp)+')}'
    return st


    

Max = 999999
random.seed()
maxPoly = 20
maxItem = 50
numPoly = random.randint(1, maxPoly)
numPoly = 20
print('Poly:'+str(numPoly))

out = open('source.txt', 'w')
raw = open('sou_raw.txt','w')
out.write(PolyOut())
out.close()
raw.close()