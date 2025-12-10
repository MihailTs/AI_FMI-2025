import numpy as np

def train_test_split(X, y, test_size=0.2, shuffle=True, random_state=None, stratify=None):
    n_samples = len(X)
    
    train_size = 1 - test_size
    n_train = int(n_samples * train_size)
    n_test = n_samples - n_train

    if stratify != 'y':
        X_train, X_test, y_train, y_test = stratified_split(X, y, test_size=0.2, random_state=random_state, shuffle=shuffle)
    else:
        indices = np.arange(n_samples)
        if shuffle:
            rng = np.random.default_rng(random_state)
            rng.shuffle(indices)
        test_idx = indices[:n_test]
        train_idx = indices[n_test:]
        X_train, X_test = X.iloc[train_idx], X.iloc[test_idx]
        y_train, y_test = y.iloc[train_idx], y.iloc[test_idx]

    return X_train, X_test, y_train, y_test

# y is a boolean vector
def stratified_split(X, y, test_size=0.2, random_state=None, shuffle=True):
    n_samples = len(X)
    positives = sum(y)
    negatives = n_samples - sum(y)
    
    X_train = []
    X_test = []
    y_train = []
    y_test = []
    indices = np.arange(n_samples)
    if shuffle:
        rng = np.random.default_rng(random_state)
        rng.shuffle(indices)
    
    positives_count = 0
    negatives_count = 0
    for i in range(n_samples):
        if y[i] == 0:
            if positives_count < positives * test_size:
                X_test.append(X[i])
                y_test.append(y[i])
                positives_count = positives_count + 1
            else:
                X_train.append(X[i])
                y_train.append(y[i])
        else:
            if negatives_count < negatives * test_size:
                X_test.append(X[i])
                y_test.append(y[i])
                negatives_count = negatives_count + 1
            else:
                X_train.append(X[i])
                y_train.append(y[i])

    return X_train, X_test, y_train, y_test
