import cv2
import numpy as np

def find_edges(img):
    rows, cols = img.shape[:2]

    for i in range(rows):
        for j in range(cols):
            gray = 0.2989 * img[i, j][2] + 0.5870 * img[i, j][1] + 0.1140 * img[i, j][0]
            img[i, j] = [gray, gray, gray]
    img = cv2.GaussianBlur(img, (19, 19), 0)
    cv2.imshow('Grayscale Image (Weighted)', img)

    edges = cv2.Canny(img, 45, 90)
    return edges

"""
edges - black-white image produced with Canny edge detection
"""
def get_cube_corners(edges):
    rows, cols = edges.shape[:2]
    max_x_point = (-1, 0)
    max_y_point = (0 , -1)
    min_x_point = (cols + 1, 0)
    min_y_point = (0 , rows + 1)
    for i in range(rows):
        for j in range(cols):
            if (edges[i, j] == 0):            
                continue
            else:
                if i <= min_y_point[1]:
                    min_y_point = (j, i)
                if i >= max_y_point[1]:
                    max_y_point = (j, i)
                if j <= min_x_point[0]:
                    min_x_point = (j, i)
                if j >= max_x_point[0]:
                    max_x_point = (j, i)

    vertical_treshold = (max_x_point[0] + min_x_point[0]) / 2
    
    if min_y_point[0] < vertical_treshold:
        return min_y_point, max_x_point, max_y_point, min_x_point    
    else:
        return min_x_point, min_y_point, max_x_point, max_y_point


def main():
    original = cv2.imread('cube_test_1.png')
    original = cv2.resize(original, (780, 540), interpolation=cv2.INTER_LINEAR)
    img = original.copy()

    edges = find_edges(img)
    cv2.imshow("Edges", edges)

    get_cube_corners(edges=edges)
    u_l, u_r, l_r, l_l = get_cube_corners(edges)

    original = cv2.line(original, u_l, u_r, (0, 255, 0), 3)
    original = cv2.line(original, u_r, l_r, (0, 255, 0), 3)
    original = cv2.line(original, l_r, l_l, (0, 255, 0), 3)
    original = cv2.line(original, l_l, u_l, (0, 255, 0), 3)
    cv2.imshow("Contoured", original)

    cv2.waitKey(0)
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
