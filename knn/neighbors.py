import metrics as mtr
import numpy as np
from statistics import mode
import kd_tree

class KNeighborsClassifier:
    """ 
    metric='M' -> Manhattan distance
    metric='E' -> Euclidean distance
    """

    def __init__(self, n_neighbors, metric='E'):
        self.n_neighbors = n_neighbors
        self.metric = metric

    def fit(self, X_train, y_train):
        self.X_train = np.asarray(X_train)
        self.y_train = np.asarray(y_train)

    def predict(self, X):
        np_X = np.asarray(X)

        if self.metric == 'E':
            distance_function = mtr.euclidean_distance
        elif self.metric == 'M':
            distance_function = mtr.manhattan_distance
        else:
            raise ValueError("metric must be 'E' or 'M'")

        predictions = []
        for point in np_X:
            distances = np.array([distance_function(x, point)
                                  for x in self.X_train])
            nearest_idx = np.argsort(distances)[:self.n_neighbors]
            nearest_labels = self.y_train[nearest_idx]
            predictions.append(mode(nearest_labels))

        return np.array(predictions)


class KNeighborClassifierKD:

    def __init__(self, n_neighbors):
        self.n_neighbors = n_neighbors

    def fit(self, X_train, y_train):
        X_train = np.asarray(X_train)
        y_train = np.asarray(y_train)

        self.X_train = X_train
        self.y_train = y_train

        self.tree = kd_tree.KDTree(X_train, y_train)

    def predict(self, X_test):
        X_test = np.asarray(X_test)

        predictions = []
        for i in range(len(X_test)):
            neighbors = self.tree.get_nearest_neighbors(X_test[i], 
                                                        k=self.n_neighbors)
            example_class = mode(neighbors)
            predictions.append(example_class)

        return np.array(predictions)
