import cv2
import math


def find_edges(img):
    rows, cols = img.shape[:2]

    for i in range(rows):
        for j in range(cols):
            gray = 0.2989 * img[i, j][2] + 0.5870 * img[
                i, j][1] + 0.1140 * img[i, j][0]
            img[i, j] = [gray, gray, gray]
    img = cv2.GaussianBlur(img, (15, 15), 0)
    # cv2.imshow("Blurred", img)

    edges = cv2.Canny(img, 45, 100)
    return edges


"""
edges - black-white image produced with Canny edge detection
"""


def get_cube_corners(edges):
    rows, cols = edges.shape[:2]
    max_x_point = (-1, 0)
    max_y_point = (0, -1)
    min_x_point = (cols + 1, 0)
    min_y_point = (0, rows + 1)
    for i in range(rows):
        for j in range(cols):
            if (edges[i, j] == 0):
                continue
            else:
                if i <= min_y_point[1]:
                    min_y_point = (j, i)
                if i > max_y_point[1]:
                    max_y_point = (j, i)
                if j < min_x_point[0]:
                    min_x_point = (j, i)
                if j >= max_x_point[0]:
                    max_x_point = (j, i)

    pi = math.pi

    # theorem of cosines
    side_1 = math.dist(min_x_point, min_y_point)
    side_2 = math.dist(min_y_point, max_x_point)
    side_3 = math.dist(max_x_point, max_y_point)
    side_4 = math.dist(max_y_point, min_x_point)
    diag_1 = math.dist(min_x_point, max_x_point)
    diag_2 = math.dist(min_y_point, max_y_point)
    alpha_1 = math.acos(
        (side_1**2 + side_2**2 - diag_1**2) / (2 * side_1 * side_2))
    alpha_2 = math.acos(
        (side_2**2 + side_3**2 - diag_2**2) / (2 * side_2 * side_3))
    alpha_3 = math.acos(
        (side_3**2 + side_4**2 - diag_1**2) / (2 * side_3 * side_4))
    alpha_4 = math.acos(
        (side_4**2 + side_1**2 - diag_2**2) / (2 * side_4 * side_1))
    area = (side_1 * side_2 * math.sin(alpha_1) / 2) + (side_3 * side_4 *
                                                        math.sin(alpha_3) / 2)
    diagonals_intersection_angle = math.asin(2 * area / (diag_1 * diag_2))

    angle_treshold = 20 * pi / 180
    if (abs(alpha_1 - pi / 2)
            > angle_treshold) or (abs(alpha_2 - pi / 2) > angle_treshold) or (
                abs(alpha_3 - pi / 2) > angle_treshold) or (
                    abs(alpha_4 - pi / 2)
                    > angle_treshold) or (abs(diagonals_intersection_angle -
                                              pi / 2) > angle_treshold):
        return (min_x_point[0], min_y_point[1]), (
            max_x_point[0], min_y_point[1]), (max_x_point[0],
                                              max_y_point[1]), (min_x_point[0],
                                                                max_y_point[1])
    else:
        vertical_treshold = (max_x_point[0] + min_x_point[0]) / 2

        if min_y_point[0] < vertical_treshold:
            return min_y_point, max_x_point, max_y_point, min_x_point
        else:
            return min_x_point, min_y_point, max_x_point, max_y_point


def main():
    for i in range(1, 13):
        img_name = f'test_images/cube_test_{i}.png'

        original = cv2.imread(img_name)
        original = cv2.resize(original, (540, 540),
                              interpolation=cv2.INTER_LINEAR)
        img = original.copy()

        edges = find_edges(img)
        cv2.imshow(f"Edges{i}", edges)

        get_cube_corners(edges=edges)
        u_l, u_r, l_r, l_l = get_cube_corners(edges)

        original = cv2.line(original, u_l, u_r, (0, 255, 0), 3)
        original = cv2.line(original, u_r, l_r, (0, 255, 0), 3)
        original = cv2.line(original, l_r, l_l, (0, 255, 0), 3)
        original = cv2.line(original, l_l, u_l, (0, 255, 0), 3)
        cv2.imshow(f"Contoured{i}", original)

    cv2.waitKey(0)
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
