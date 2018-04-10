from python_speech_features import mfcc
from python_speech_features import delta
from python_speech_features import logfbank
from sklearn.naive_bayes import GaussianNB
from sklearn.externals import joblib
import scipy.io.wavfile as wav
from sklearn.cluster import KMeans
from sklearn import svm
import sys
import numpy
import collections
import glob
import math
import os

def zerolist(n):
    listofzeros=[0]*n
    return listofzeros
def onelist(n):
    listofones=[1]*n
    return listofones


def nlist(n, i):
    listofzeros=[i]*n
    return listofzeros

def get3secs(arr):
    new_arr = nlist(270*13, 0)
    new_arr = [new_arr]
    print(int((len(arr)-270)/90))
    for i in range(int((len(arr)-270)/90) + 1):
        arr13 = numpy.array(())
        print(i)
        for j in range(270):
            arr13 = numpy.concatenate((arr13, arr[i*90+j]), 0)

        #print(arr13)
        new_arr = numpy.append(new_arr, [arr13], 0)

    new_arr = numpy.delete(new_arr, 0, 0)
    return new_arr

numpy.set_printoptions(threshold=sys.maxsize)

cluster = 32
limit = list(range(cluster))

#hangi sırayla?

##voice_labels_txt = open('voice_test_labels.txt', 'r')
##voice_labels = voice_labels_txt.readlines()
##ac2 = list(range(len(voice_labels)))
##voice_labels = [int(str(voice_labels[i])) for i in ac2]

kmeans = joblib.load('voice_kmeans.pkl') 
clf = joblib.load('voice_gaussian.pkl') 
clf2 = joblib.load('voice_svm.pkl')

print('00afra')
print(kmeans.labels_)
print(len(kmeans.labels_))

labels_gauss = numpy.array(())
labels_svm = numpy.array(())
ordered_labels=[]
myarray = []

voice_labels_txt = open('voice_test_labels.txt', 'r')
voice_labels = voice_labels_txt.readlines()

for line in voice_labels:
        line_l = line.split(",")
        myarray.append(line_l)

#print(myarray)
length = len(myarray)

k=0
path = "test_voice/*.wav"
for fname in glob.glob(path):
   
    for i in range(length):       
        if(myarray[i][0] == os.path.basename(fname) ):
           ordered_labels.append(myarray[i][1])
    ac2 = list(range(len(ordered_labels)))
    voice_labels = [int(str(ordered_labels[i])) for i in ac2]
    #print(voice_labels)
  
    (rate,sig) = wav.read(fname)
    nfft_size = int(2**math.ceil(math.log(rate,2)))
    mfcc_feat = mfcc(sig,rate, winlen=0.25, nfft = nfft_size)

    testarr = numpy.array(mfcc_feat)
    testarr_3sec = get3secs(testarr)

    testresult = kmeans.predict(testarr_3sec)
    size = len(testarr_3sec)
    print(testresult)
    counter = collections.Counter(testresult)
    testkeys = numpy.fromiter(counter.values(), dtype=float)
    print(testkeys/size)

    testresult = numpy.append(testresult, limit, 0)
    print(testresult)
    counter = collections.Counter(testresult)
    testkeys = numpy.fromiter(counter.values(), dtype=float)
    testkeys=testkeys-1

    print(testkeys/size)

    print(clf.predict([testkeys/size]))
    print(clf2.predict([testkeys/size]))
    print(clf.predict_proba([testkeys/size]))
    print(clf2.predict_proba([testkeys/size]))

    labels_gauss = numpy.concatenate((labels_gauss, clf.predict([testkeys/size])),0)
    labels_svm = numpy.concatenate((labels_svm, clf2.predict([testkeys/size])),0)

accuracy_gauss = 1-sum(abs(labels_gauss-voice_labels))/len(labels_gauss)
accuracy_svm = 1-sum(abs(labels_svm-voice_labels))/len(labels_svm)

print("Accuracy of Gauss and SVM")
print(accuracy_gauss)
print(accuracy_svm)

