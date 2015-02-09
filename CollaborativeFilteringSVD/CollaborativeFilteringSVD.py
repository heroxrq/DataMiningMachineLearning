from numpy import *

utilityMatrix = matrix([[4, 4, 0, 2, 2],
                        [4, 0, 0, 3, 3],
                        [4, 0, 0, 1, 1],
                        [1, 1, 1, 2, 0],
                        [2, 2, 2, 0, 0],
                        [1, 1, 1, 0, 0],
                        [5, 5, 5, 0, 0]])

utilityMatrixSVD = matrix([[0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 5],
                           [0, 0, 0, 3, 0, 4, 0, 0, 0, 0, 3],
                           [0, 0, 0, 0, 4, 0, 0, 1, 0, 4, 0],
                           [3, 3, 4, 0, 0, 0, 0, 2, 2, 0, 0],
                           [5, 4, 5, 0, 0, 0, 0, 5, 5, 0, 0],
                           [0, 0, 0, 0, 5, 0, 1, 0, 0, 5, 0],
                           [4, 3, 4, 0, 0, 0, 0, 5, 5, 0, 1],
                           [0, 0, 0, 4, 0, 4, 0, 0, 0, 0, 4],
                           [0, 0, 0, 2, 0, 2, 5, 0, 0, 1, 2],
                           [0, 0, 0, 0, 5, 0, 0, 0, 0, 4, 0],
                           [1, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0]])

#Euclidean distance
def euclidSim(inA, inB):
    return 1.0/(1.0 + linalg.norm(inA - inB))

#Pearson similarity
def pearsSim(inA, inB):
    if len(inA) < 3: return 1.0
    return 0.5 + 0.5 * corrcoef(inA, inB, rowvar = 0)[0][1]

#cosine similarity
def cosSim(inA, inB):
    num = float(inA.T * inB)
    denom = linalg.norm(inA) * linalg.norm(inB)
    return 0.5 + 0.5 * (num / denom)

#get the estimation of the (user, item)
def standEst(dataMat, user, simMeas, item):
    n = shape(dataMat)[1]
    simTotal = 0.0
    ratSimTotal = 0.0
    for j in range(n):
        userRating = dataMat[user, j]
        if userRating == 0: continue
        #find two items (in column: 'item' and 'j') which have been both evaluated by users
        overLap = nonzero(logical_and(dataMat[:, item].A > 0, dataMat[:, j].A > 0))[0]
        if len(overLap) == 0: similarity = 0
        else: similarity = simMeas(dataMat[overLap, item], dataMat[overLap, j])
        print 'the %d and %d similarity is: %f' %(item, j, similarity)
        simTotal += similarity
        ratSimTotal += similarity * userRating
    if simTotal == 0: return 0
    else: return ratSimTotal / simTotal

#get the estimation of the (user, item) by using the method of SVD
def svdEst(dataMat, user, simMeas, item):
    n = shape(dataMat)[1]
    simTotal = 0.0
    ratSimTotal = 0.0
    U, Sigma, VT = linalg.svd(dataMat)
    Sig4 = mat(eye(4) * Sigma[0:4])
    transformedItems = dataMat.T * U[:, 0:4] * Sig4.I
    for j in range(n):
        userRating = dataMat[user, j]
        if userRating == 0 or j == item: continue
        similarity = simMeas(transformedItems[item, :].T, transformedItems[j, :].T)
        print 'the %d and %d similarity is: %f' %(item, j, similarity)
        simTotal += similarity
        ratSimTotal += similarity * userRating
    if simTotal == 0: return 0
    else: return ratSimTotal / simTotal

#recommend items to the user
def recommend(dataMat, user, N=3, simMeas=cosSim, estMethod=standEst):
    #find the items which have not been evaluated by the user
    unratedItems = nonzero(dataMat[user, :].A == 0)[1]
    if len(unratedItems) == 0: return 'you rated everything'
    itemScore = []
    for item in unratedItems:
        estimatedScore = estMethod(dataMat, user, simMeas, item)
        itemScore.append((item, estimatedScore))
    #recommend N items which are most similar to the evaluated items to the user
    return sorted(itemScore, key = lambda jj: jj[1], reverse = True)[:N]  
