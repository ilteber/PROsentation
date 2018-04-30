import subprocess
import os
import threading
import time
import pathlib
import shutil
import argparse
import sys
import random

# usage python3.5 shuffleit.py -fpgv pose
if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="To process the video labels to their folders")
    parser.add_argument("-fpgv", dest='fpgv', type=str, help='Shuffles the videos',default='')
    parser.add_argument("-txt", dest='txt', type=str, help='read and check the 0s and 1s',default='')
    args = parser.parse_args()

    out_path = '/home/ubuntu/dataset_out/'+args.fpgv+'/'
    out_dirs = os.listdir(out_path)

    label_path = args.txt
    in_label_txt = open(label_path,'r')
    pos_vidz = []
    neg_vidz = []
    for line in in_label_txt:

        split_str = line.split(',')
        video_name  = split_str[0]
        video_label = int(split_str[1][0])
        if not os.path.isdir(out_path+video_name+'/'):
            print('errorrr ' +video_name+ 'does not exists')
            # raise
        elif video_name not in out_dirs:
            print('errorrr ' + video_name + 'is in the labels but not in dataset_out')
            raise
        else:
            if not os.path.isfile(out_path+video_name+'/'+video_name+'.txt'):
                print('File '+ video_name + ".txt has been written successfully.")
                out_label_txt = open(out_path+video_name+'/'+video_name+'.txt','w')
                out_label_txt.write(str(video_label))
                out_label_txt.close()
            else:
                print('File ' + video_name + ".txt exists.")
                if(video_label == 1):
                    pos_vidz.append(video_name)
                elif(video_label == 0):
                    neg_vidz.append(video_name)
            out_dirs.remove(video_name)

    if len(out_dirs) > 0:
        for unwanted in out_dirs:
            if os.path.isdir(out_path+unwanted):
                print(unwanted+' exists in dataset, but not in labels.')
                shutil.move(out_path+unwanted,out_path+'unwanted/')
            else:
                print('Cannot move, error ')
                raise
    random.shuffle(neg_vidz)
    random.shuffle(pos_vidz)
    # Change it according to the predetermined numbers
    samplednegz = random.sample(neg_vidz,35)
    sampledposz = random.sample(pos_vidz,35)
    trainfpgv = out_path+'/'+args.fpgv+'_train/'
    testfpgv = out_path +'/'+args.fpgv+'_test/'
    os.makedirs(trainfpgv)
    os.makedirs(testfpgv)
    for el in sampledposz:
        shutil.move(out_path+el,trainfpgv)
    for el in samplednegz:
        shutil.move(out_path+el,testfpgv)