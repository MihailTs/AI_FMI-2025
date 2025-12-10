import numpy as np
import pandas as pd
import metrics
from neighbors import KNeighborClassifierKD, KNeighborsClassifier

def cross_validate(estimator, X, y, cv=5, k=1, random_state=None):
    X = np.asarray(X.to_numpy())
    y = np.asarray(y.to_numpy())
    
    n_samples = len(X)
    indices = np.arange(n_samples)
    rng = np.random.default_rng(random_state)
    rng.shuffle(indices)

    accuracy = []
    X_partitions = np.array_split(X, cv)
    y_partitions = np.array_split(y, cv)
    for i in range(cv):
        if estimator == 'knn_kd':
            model = KNeighborClassifierKD(k)
        elif estimator == 'knn':
            model = KNeighborsClassifier(k)
        else:
            raise ValueError('Unknown model name passed')
        X_train = np.concatenate(X_partitions[:i] + X_partitions[i+1:])
        y_train = np.concatenate(y_partitions[:i] + y_partitions[i+1:])
        model.fit(X_train=X_train, y_train=y_train)
        prediction=model.predict(X_partitions[i])
        accuracy.append(metrics.accuracy_score(y_partitions[i], prediction))
        
    for i in range(cv):
        print(f'Acuracy Fold {i + 1}: {round(accuracy[i] * 100, 2)}%')

    print(f'Average Accuracy: {round(np.mean(accuracy) * 100, 2)}%')
    print(f'Standard Deviation: {np.std(accuracy)}%')