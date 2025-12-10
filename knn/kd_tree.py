import pandas as pd
import numpy as np
import heapq

class TreeNode:

    def __init__(self, value, data_class, plane_split=None, left=None, right=None):
        self.value = value
        self.data_class = data_class
        self.plane_split = plane_split
        self.left = left
        self.right = right

class KDTree:

    # k equals the number of features of X
    def __init__(self, X, y, split_feature=0):
        if len(X) == 1:
            self.root = TreeNode(X.iloc[0], y.iloc[0])
            return
        
        self.split_feature = split_feature

        X_df = pd.DataFrame(X)
        y_df = pd.DataFrame(y)
        k = X_df.shape[1]

        split_feature_name = X_df.columns[split_feature]

        X_df = X_df.sort_values(split_feature_name)
        y_df = y_df.loc[X_df.index] 
        split_index = int((len(X_df) + 1) / 2)
        if split_index == 0:
            left = None
        else:
            left = KDTree(X_df[:split_index], y_df[:split_index], split_feature=(split_feature + 1) % k).root
        if len(X_df) == split_index + 1:
            right = None
        else:
            right = KDTree(X_df[split_index + 1:], y_df[split_index + 1:], split_feature=(split_feature + 1) % k).root
        self.root = TreeNode(X_df.iloc[split_index], y_df.iloc[split_index], X_df.iloc[split_index, split_feature], left, right)

    def get_nearest_neighbors(self, X, k=1):
        heap = []
        self._nearest_neighbors_helper(X, heap, k, self.root, 0)
        result = []
        for _, class_value in heap:
            if hasattr(class_value, 'values'):
                result.append(class_value.values[0])
            else:
                result.append(class_value)
        return result

    def _nearest_neighbors_helper(self, X, heap, k, node, depth):
        if node is None:
            return
        
        k_features = len(X)
        split_feature = depth % k_features
        
        node_np = node.value.to_numpy()
        distance = float(np.sum((X - node_np) ** 2))
        
        class_label = node.data_class.values[0] if hasattr(node.data_class, 'values') else node.data_class

        if len(heap) < k:
            heapq.heappush(heap, (-distance, class_label))
        elif distance < -heap[0][0]:
            heapq.heappop(heap)
            heapq.heappush(heap, (-distance, class_label))
        
        axis_distance = float(X[split_feature] - node_np[split_feature])
        
        near_child = node.left if axis_distance < 0 else node.right
        far_child = node.right if axis_distance < 0 else node.left
        
        self._nearest_neighbors_helper(X, heap, k, near_child, depth + 1)
        
        if len(heap) < k or abs(axis_distance) ** 2 < -heap[0][0]:
            self._nearest_neighbors_helper(X, heap, k, far_child, depth + 1)