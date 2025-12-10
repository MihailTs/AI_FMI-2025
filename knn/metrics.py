import numpy as np
import math

def accuracy_score(y_true, y_pred, normalize=True):
    total = len(y_pred)
    true_predictions = sum(a == b for a, b in zip(y_true, y_pred))
    if normalize == False:
        return true_predictions
    return true_predictions / total

def euclidean_distance(point_a, point_b):
    np_point_a = np.array(point_a)
    np_point_b = np.array(point_b)
    return np.sqrt(sum(a * a for a in np_point_a - np_point_b))

def manhattan_distance(point_a, point_b):
    np_point_a = np.array(point_a)
    np_point_b = np.array(point_b)
    return sum(abs(a) for a in np_point_a - np_point_b)