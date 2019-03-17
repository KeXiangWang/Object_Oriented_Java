import subprocess
import random
import time
import re

ins = ['(ER,#3,6)\n', '(ER,#2,5)\n', '(FR,5,DOWN)\n', '(FR,2,DOWN)\n', '(FR,5,DOWN)\n', '(FR,6,DOWN)\n', '(FR,5,UP)\n', '(ER,#2,2)\n', '(ER,#3,4)\n', '(FR,2,DOWN)\n', '(FR,5,UP)\n', '(FR,6,DOWN)\n']
t = [3.8, 4.0, 5.0, 2.4, 3.2, 6.1, 6.6, 7.7, 2.7, 3.9, 4.5, 0.1]

p = subprocess.Popen("java main.Main", shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE)
time.sleep(0.5)
print("java Class is performing")
for i in range(len(ins)):
    p.stdin.write(ins[i].encode())
    p.stdin.flush()
    time.sleep(t[i])
p.stdin.write("END\n".encode())
p.stdin.flush()
out = str(p.stdout.read())[2:-1]
out = re.split(r"\\r\\n",out)
for i in range(len(out)):
	print(out[i])
p.wait()
print("java Class has end")
p.kill()

'''
file = open("input.txt","r")
print(file.read()[0:-1])
file.close()
'''

file = open("resultR.txt","r")
print(file.read()[0:-1])
file.close()