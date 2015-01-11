clear all;% removes all variables, globals, functions and MEX links.
close all;% closes all the open figure windows.
clc;% clears the command window and homes the cursor.

% load('E:\THz\program\raw_data\number.mat');
% binary_image = preprocessing(data, false);

% data = xlsread('E:\THz\program\raw_data\SIMIT.xlsx');
% binary_image = preprocessing(data, true);

data = imread('E:\THz\program\raw_data\number.jpg');
% data = rgb2gray(data);
% figure;
% imshow(data);
% saveas(gcf, strcat('E:\THz\program\images\', 'gray image'), 'jpg');

% for i = 1 : size(data, 1)
%     for j = 1 : size(data, 2)
%         data(i, j) = data(i, j) * 255;
%     end
% end
% data = imnoise(data, 'gaussian', 0, 0.02);
% figure;
% imshow(data);
% saveas(gcf, strcat('E:\THz\program\images\', 'added gaussian noise'), 'jpg');

binary_image = preprocessing(data, false);
segmentation(binary_image);