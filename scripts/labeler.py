import subprocess
import os
import threading
import time
import pathlib
import shutil
import argparse
import sys


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="To process the video labels to their folders")
    parser.add_argument("-txt", dest='txt', type=str, help='Reads the specified txt file',default='')
    parser.add_argument("-folder", dest='fpgv_folder', type=str, help='Puts labels to the videos inside the folder',default='')
    args = parser.parse_args()

    out_path = args.fpgv_folder
    out_dirs = os.listdir(out_path)

    label_path = args.txt
    in_label_txt = open(label_path,'r')

    for line in in_label_txt:

        split_str = line.split(',')
        video_name  = split_str[0]
        video_label = int(split_str[1][0])
        if not os.path.isdir(out_path+video_name+'/'):
            print('errorrr ' +video_name+ 'does not exists')
            # raise
        elif video_name not in out_dirs:
            print('errorrr ' + video_name + 'is not in dataset_out')
            raise
        else:
            if not os.path.isfile(out_path+video_name+'/'+video_name+'.txt'):
                print('File '+ video_name + ".txt has been written successfully.")
                out_label_txt = open(out_path+video_name+'/'+video_name+'.txt','w')
                out_label_txt.write(str(video_label))
                out_label_txt.close()
            else:
                print('File ' + video_name + ".txt exists.")
            out_dirs.remove(video_name)

if len(out_dirs) > 0:
    for unwanted in out_dirs:
        if os.path.isdir(out_path+unwanted):
            print('moving '+unwanted)
            shutil.move(out_path+unwanted,out_path+'unwanted/')
        else:
            print('Cannot move, error ')
            raise
