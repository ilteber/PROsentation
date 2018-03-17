import boto3
#import thread
import time
import os
import threading
import subprocess
from boto3.dynamodb.conditions import Key, Attr

dynamodb = boto3.resource('dynamodb')
def insertItem(Id, type, length, name, status, username):

    table = dynamodb.Table('videos')
    #print(table.creation_date_time)
    table.put_item(
        Item={
            'Id': Id,
            'type': type,
            'length': length,
            'name': name,
            'status': status,
            'username': username,
        }
    )

def deleteItem(Id, type):

    table = dynamodb.Table('videos')
    table.delete_item(
        Key={
            'Id': Id,
            'type': type
        }
    )


def getUnprocessedItems():
    #threads = []
    threadID = 1

    table = dynamodb.Table('videos')

    response = table.scan()
    items = response['Items']

    temp = 0

    if (items == []):
        print ("None")
    else:
        for item in items:
            if (item['type'] == 0):
                thread = myThread(threadID, item)
                thread.start()
                #threads.append(thread)
                threadID += 1
                # startProcess(item)
                temp = temp + 1
                deleteItem(item['Id'], item['type'])
                # startProcess(item)
        if (temp == 0):
            print ("None")


        time.sleep(10)

    #for t in threads:
    #    t.join()
    print ("Exiting Main Thread")

class myThread (threading.Thread):
   def __init__(self, threadID, item):
      threading.Thread.__init__(self)
      self.threadID = threadID
      self.item = item
   def run(self):
       self.threadID = str(self.threadID)
       filename = self.item['name']
       filename = str(filename)
       print (filename)
       print ("Starting: " + self.threadID)
      #process_data(self.name)
      #print "Exiting " + self.name
       process_data(self.item)

def process_data(item):
    print ("process started")
    pwd ='123'
    videopath = "/home/ilteber/3.mp4"
    os.chdir("/home/ilteber/openpose/")
    cmd = "/home/ilteber/OpenFace/build/bin/FeatureExtraction -verbose -out_dir /home/ilteber/selam2 -f "+videopath
    subprocess.call('echo {} | sudo -S {}'.format(pwd, cmd), shell=True)
    cmd = "./build/examples/openpose/openpose.bin --video "+videopath+" --write_keypoint_json /home/ilteber/selam2 --write_video /home/ilteber/selam2/result.avi --display 0"
    subprocess.call('echo {} | sudo -S {}'.format(pwd, cmd), shell=True)


#checkDBInEveryNSeconds()

# while True:
# 	getUnprocessedItems()
process_data("3.mp4")

#insertItem(4, 1, 150, "heyyo.mp4", 25, "ilteber")
