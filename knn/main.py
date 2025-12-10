import dataset_split as dss
from cross_validation import cross_validate
import metrics
import pandas as pd
from neighbors import KNeighborsClassifier, KNeighborClassifierKD
import seaborn as sns
import matplotlib.pyplot as plt

def main():
    k = int(input("Enter k: "))
    df_iris = pd.read_csv("D:\\UNIVERSITY\\AI\\knn\\iris.csv")
    y = df_iris['class']
    X = df_iris.drop('class', axis=1)

    # normalisation of features
    X = (X - X.min()) / (X.max() -X .min())

    X_train, X_test, y_train, y_test = dss.train_test_split(X, y, test_size=0.2, stratify='y', shuffle=True)
    
    knn = KNeighborClassifierKD(k)
    # knn = KNeighborsClassifier(k)
    knn.fit(X_train=X_train, y_train=y_train)

    prediction = knn.predict(X_train)
    print('1. Train Set Accuracy:')
    print(f'Accuracy: {round(metrics.accuracy_score(y_train, prediction) * 100, 2)}%')

    print('2. 10-Fold Cross-Validation Results:')
    cross_validate(estimator='knn_kd', X=X, y=y, cv=10, k=10, random_state=21)

    print('3. Test Set Accuracy:')
    prediction = knn.predict(X_test)
    print(f'{round(metrics.accuracy_score(y_test, prediction) * 100, 2)}%')

    # accuracy = []
    # neighbors = []
    # for i in range(1, 102, 2):
    #     knn = KNeighborClassifierKD(i)
    #     knn.fit(X_train, y_train)
    #     predictions = knn.predict(X_test=X_test)
    #     accuracy.append(metrics.accuracy_score(y_test, predictions) * 100)
    #     neighbors.append(i)
        
    # sns.lineplot(x=neighbors, y=accuracy)
    # plt.xlabel("Neighbor Count")
    # plt.ylabel("Accuracy")
    # plt.title("Accuracy per neighbors")
    # plt.savefig("accuracy_per_neighbors")
    

if __name__ == "__main__":
    main()