import subprocess
import argparse
import os
import signal
import glob
import sys
import shutil
import datetime
import threading
import time
import pathlib


# TODO: Gaze ile face'de aynı olan videoları iki tarafa da koy.
# usage = python3.5 process_data_fgp.py -fpgv posegazeface -in /home/ubuntu/dataset -out /home/ubuntu/dataset_out
def process_pose(in_f,out_f):
    openpose_bin_dir = "/home/ubuntu/openpose/build/examples/openpose/openpose.bin"
    dataset = in_f+'/pose/'
    dataset_out = out_f+'/pose/'
    fns_pose = os.listdir(dataset)
    for vid in fns_pose:
        folder_name = vid.replace('.mp4', '')
        folder_name = folder_name.replace('.MP4', '')
        in_path = dataset+vid
        out_path = dataset_out+folder_name+'/'
        temp_arr = glob.glob(out_path + '/*.json')
        try:
            if (len(temp_arr) <= 0):
            #if not os.path.isfile(el_avi):
                if not os.path.exists(out_path):
                    os.makedirs(out_path)
                os.chdir("/home/ubuntu/openpose/")
                cmd = openpose_bin_dir+" --video "+in_path+" --" \
                      "write_keypoint_json "+ out_path +" --display 0 " \
                     ""
                subprocess.call(cmd,shell=True)
            else:
                print('{} exists'.format(folder_name))
        except KeyboardInterrupt:
            print('SigINT caught in Pose, removing json files for: '+ folder_name)
            bad_array = glob.glob(out_path + '/*.json')
            print('length of temp_array is {}'.format(bad_array))
            for x in bad_array:
                print(str(x))
                print('hello')
                os.remove(x)
            print('Removed jsons for: ' + folder_name)
            sys.exit(0)

def process_gaze(in_f,out_f):
    dataset = in_f+'/gaze/'
    dataset_out = out_f+'/gaze/'
    fns_gaze = os.listdir(dataset)

    for vid in fns_gaze:
        folder_name = vid.replace('.mp4', '')
        folder_name = folder_name.replace('.MP4', '')

        in_path = dataset + vid
        out_path = dataset_out + folder_name + '/'
        vid_csv = out_path+folder_name+'.csv'

        try:
            if not os.path.isfile(vid_csv):
                if not os.path.exists(out_path):
                    os.makedirs(out_path)
                cmd = "/home/ubuntu/OpenFace/build/bin/FeatureExtraction -out_dir "+out_path+" -f " + in_path
                subprocess.call(cmd, shell=True)
            else:
                print('{} exists'.format(vid))
        except KeyboardInterrupt:
            print('SigINT caught in gaze, removing csv files for ' + folder_name)
            temp_rem = os.listdir(out_path)
            for f in temp_rem:
                if not os.path.isdir(out_path+f):
                    os.remove(out_path+f)
                else:
                    shutil.rmtree(out_path+f)
            print('Removing csv files for ' + folder_name)
            sys.exit(0)

def process_face(in_f,out_f):
    dataset = in_f + '/face/'
    dataset_out = out_f + '/face/'
    fns_face = os.listdir(dataset)

    for vid in fns_face:
        folder_name = vid.replace('.mp4', '')
        folder_name = folder_name.replace('.MP4', '')

        in_path = dataset + vid
        out_path = dataset_out + folder_name + '/'
        vid_csv = out_path + folder_name + '.csv'
        try:
            if not os.path.isfile(vid_csv):
                if not os.path.exists(out_path):
                    os.makedirs(out_path)
                cmd = "/home/ubuntu/OpenFace/build/bin/FeatureExtraction -out_dir " + out_path + " -f " + in_path
                subprocess.call(cmd, shell=True)
            else:
                print('{} exists'.format(vid))
        except KeyboardInterrupt:
            print('SigINT caught in face, removing csv files for ' + folder_name)
            temp_rem = os.listdir(out_path)
            for f in temp_rem:
                if not os.path.isdir(out_path+f):
                    os.remove(out_path+f)
                else:
                    shutil.rmtree(out_path+f)
            print('Removing csv files for ' + folder_name)
            sys.exit(0)

if __name__ == '__main__':

    # args.inf = /home/ubuntu/dataset
    # args.ouf = /home/ubuntu/dataset_out
    # args.fpgv = combination of facegazeposevoice

    parser = argparse.ArgumentParser(description="usage = python3.5 process_data_fgp.py -fpgv posegazeface -in /home/ubuntu/dataset -out /home/ubuntu/dataset_out")
    parser.add_argument("-fpgv", dest='fpgv', type=str, help='Specify which module you want to start', default='')
    parser.add_argument("-in", dest='inf', type=str, help='Processes the videos inside the folder',
                        default='')
    parser.add_argument("-out", dest='ouf', type=str, help='Puts labels to the videos inside the folder',
                        default='')
    args = parser.parse_args()
    signal.signal(signal.SIGINT, signal.default_int_handler)
    if('gaze' in args.fpgv):
        process_gaze(args.inf,args.ouf)
    if('face' in args.fpgv):
        process_face(args.inf,args.ouf)
    if('pose' in args.fpgv):
        process_pose(args.inf,args.ouf)
