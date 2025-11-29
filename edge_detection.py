import cv2
import numpy as np

img = cv2.imread('cube_test_3.png')
img = cv2.resize(img, (780, 540), interpolation=cv2.INTER_LINEAR)

original = img
rows, cols = img.shape[:2]

for i in range(rows):
    for j in range(cols):
        gray = 0.2989 * img[i, j][2] + 0.5870 * img[i, j][1] + 0.1140 * img[i, j][0]
        img[i, j] = [gray, gray, gray]
img = cv2.GaussianBlur(img, (15, 15), 0)
cv2.imshow('Grayscale Image (Weighted)', img)

Ix = cv2.Sobel(img, cv2.CV_64F, 1, 0)
Iy = cv2.Sobel(img, cv2.CV_64F, 0, 1)

cv2.imshow('Derivative x', Ix)
cv2.imshow('Derivative y', Iy)

Ixx = Ix**2
Iyy = Iy**2
Ixy = Ix*Iy

Sxx = cv2.GaussianBlur(Ixx, (21,21), sigmaX=1)
Syy = cv2.GaussianBlur(Iyy, (21,21), sigmaX=1)
Sxy = cv2.GaussianBlur(Ixy, (21,21), sigmaX=1)

# Harris response
k = 0.05
R = (Sxx * Syy - Sxy**2) - k*(Sxx + Syy)**2

# Threshold and mark corners
threshold = 0.01 * R.max()
corners = np.zeros_like(img)
corners[R > threshold] = 255

cv2.imshow('Corners', corners)

cv2.waitKey(0)
cv2.destroyAllWindows()
