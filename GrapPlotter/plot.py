import matplotlib.pyplot as plt
import numpy as np

filePointer = open("output.txt", "r")

Lines = filePointer.readlines()
x = []
y = []
# populates points for x axis and y axis
for line in Lines:
    tmp = line.strip().split(",")
    x.append(float(tmp[0]))
    y.append(float(tmp[1]))


plt.plot(x, y)
plt.locator_params(axis='y', nbins=10)
plt.locator_params(axis='x', nbins=10)
plt.xlabel('Number of Transactions')
plt.ylabel('Response Time')
plt.title('Performance Graph')
plt.show()
