f1 = open('1.txt', 'r')
f2 = open('2.txt', 'r')
n=0
while 1:
    n=n+1
    line1 = f1.readline()
    line2 = f2.readline()
    if line1 == "" and line2 == "":
        print("good")
        input()
        break
    if line1!=line2:
        print("wrong")
        for i in range(min(len(line1), len(line2))):
            if line1[i]!=line2[i]:
                print("第",n,"行，第",i,"列出现不同")
                input()
                break
        break
input()
    
