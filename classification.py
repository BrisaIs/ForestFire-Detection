import os
from time import time


def save( data, file_name ):
    FILE = open(file_name, 'a')
    for img in data:
        for sub_set in img:
            for elm in sub_set:
                FILE.write(str(elm)+",")
    FILE.write("\n")
    FILE.close()

def getClassification_data(file_name):
    data = [ ]
    with open(file_name, 'r') as f:
        for elm in f:
            data.append( int(elm) )

    return data

def cross_ref(data, index):
    if( len(data) > 0 ):
        for elm in data:
            if( elm == index):
                data.remove(index)
                return '1'

    return '0'

def structure(source_file, cross_file ,out_file):
    with open(source_file, 'r') as sF:
        clsfData = getClassification_data(cross_file)
        # print(clsfData)
        if( clsfData != None ):
            with open(out_file, 'w') as oF:
                for index,line in enumerate(sF):
                    # print(line[:-1]+cross_ref(clsfData, index))
                    oF.write( line[:-1]+cross_ref(clsfData, index)+'\n' )

if __name__ == "__main__":
    startT = time()
    structure("rawDB.txt", "wfire.txt", "fire.arff")
    endT = time()
    print("Cross reference time:", endT-startT)
