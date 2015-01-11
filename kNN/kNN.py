from numpy import *
import operator
from os import listdir

def img2vector(filename):
    returnVect = zeros((1, 1024))
    fr = open(filename)
    for i in range(32):
        lineStr = fr.readline()
        for j in range(32):
            returnVect[0, 32 * i + j] = int(lineStr[j])
    return returnVect

def classify(inX, dataSet, labels, k):
    dataSetSize = dataSet.shape[0] # get the size of dataSet
    # compute the Euclidean distance between inX and each row vector in dataSet
    diffMat = tile(inX, (dataSetSize, 1)) - dataSet # create a matrix with input vector inX, and compute the difference
    sqDiffMat = diffMat ** 2 # square
    sqDistances = sqDiffMat.sum(axis = 1) # summation
    distances = sqDistances ** 0.5 # square root
    sortedDistIndicies = distances.argsort() # sort
    classCount = {} # dictionary
    # get k class labels which have the minimum distance
    for i in range(k):
        voteIlabel = labels[sortedDistIndicies[i]]
        classCount[voteIlabel] = classCount.get(voteIlabel, 0) + 1
    sortedClassCount = sorted(classCount.iteritems(), key = operator.itemgetter(1), reverse = True)
    return sortedClassCount[0][0]

def classifyTest():
    labels = []
    trainingFileList = listdir('E:/THz/program/images/trainingDigits')
    m = len(trainingFileList)
    trainingMat = zeros((m, 1024))
    for i in range(m):
        fileNameStr = trainingFileList[i]
        fileStr = fileNameStr.split('.')[0]
        classNumStr = int(fileStr.split('_')[0])
        labels.append(classNumStr)
        trainingMat[i, :] = img2vector('E:/THz/program/images/trainingDigits/%s' % fileNameStr)
    testFileStr = listdir('E:/THz/program/images/testDigits')
    errorCount = 0
    mTest = len(testFileStr)
    for i in range(mTest):
        fileNameStr = testFileStr[i]
        fileStr = fileNameStr.split('.')[0]
        classNumStr = int(fileStr.split('_')[0])
        vectorUnderTest = img2vector('E:/THz/program/images/testDigits/%s' % fileNameStr)
        classifierResult = classify(vectorUnderTest, trainingMat, labels, 3)
        print "the classifier came back with: %d, the real answer is: %d" % (classifierResult, classNumStr)
        if (classifierResult != classNumStr):
            errorCount += 1
    print "\nthe total number of errors is: %d" % errorCount
    print "\nthe total error rate is: %f" % (errorCount / float(mTest))
