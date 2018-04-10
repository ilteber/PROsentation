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


cluster = 32
limit = list(range(cluster))

X = zerolist(cluster)
X = [X]

numpy.set_printoptions(threshold=sys.maxsize)

A = zerolist(270*13)
A = [A]

afra = 1

sizes = numpy.array([0])

iyi_size = 0
kotu_size = 0

path = "iyi_ses/*.wav"
for fname in glob.glob(path):
    print(fname)
    iyi_size = iyi_size + 1
    (rate,sig) = wav.read(fname)
    nfft_size = int(2**math.ceil(math.log(rate,2)))

    mfcc_feat = mfcc(sig,rate, winlen=0.025, nfft = nfft_size)
    a = numpy.array(mfcc_feat)

    voice3sec = get3secs(a)

    A = numpy.append(A, voice3sec, 0)

    size = len(voice3sec)
    sizes = numpy.append(sizes, [size], 0)
    print(afra)
    afra = afra + 1

path = "kotu_ses/*.wav"
for fname in glob.glob(path):
    print(fname)
    kotu_size = kotu_size + 1
    (rate,sig) = wav.read(fname)
    nfft_size = int(2**math.ceil(math.log(rate,2)))

    mfcc_feat = mfcc(sig,rate, winlen=0.025, nfft = nfft_size)
    a = numpy.array(mfcc_feat)

    voice3sec = get3secs(a)

    A = numpy.append(A, voice3sec, 0)

    size = len(voice3sec)
    sizes = numpy.append(sizes, [size], 0)
    print(afra)
    afra = afra + 1

    
sizes = numpy.delete(sizes, 0, 0)
A = numpy.delete(A, 0, 0)

kmeans = KMeans(n_clusters=cluster).fit(A)
print('labels')
print(kmeans.labels_)
print(len(kmeans.labels_))
#counter=collections.Counter(kmeans.labels_)

left = 0
for size in sizes:
    B = A[left : left+size]
    label = kmeans.labels_[left : left+size]
    
    print(len(label))
    label = numpy.append(label, limit, 0)
    
    counter = collections.Counter(label)
    keys = numpy.fromiter(counter.values(), dtype=float)
    print(counter.keys())
    keys = keys-1
    print(keys)
    X = numpy.append(X, [keys/size], 0)
    left = left + size
    
print(X)
print(len(X))

ones = onelist(iyi_size)
zeros = zerolist(kotu_size)
Y = numpy.concatenate((ones,zeros),axis=0)

X = numpy.delete(X, 0, 0)

clf = GaussianNB()
clf.fit(X, Y)
GaussianNB(priors=None)

clf2 = svm.SVC(probability=True)
clf2.fit(X, Y)

joblib.dump(kmeans, 'voice_kmeans.pkl') 
joblib.dump(clf, 'voice_gaussian.pkl')
joblib.dump(clf2, 'voice_svm.pkl') 
