function [ output_binary_image_data ] = preprocessing( input_image_data, flip_vertical )

max_value = max(input_image_data(:));
min_value = min(input_image_data(:));
x_length = size(input_image_data, 1);
y_length = size(input_image_data, 2);
output_binary_image_data = zeros(x_length, y_length);

% gray stretch
for i = 1 : x_length
    for j = 1 : y_length
        if flip_vertical == true
            output_binary_image_data(x_length - i + 1, j) = (input_image_data(i, j) - min_value) / (max_value - min_value);
        else
            output_binary_image_data(i, j) = (input_image_data(i, j) - min_value) / (max_value - min_value);
        end
    end
end
figure;
imshow(output_binary_image_data);
saveas(gcf, strcat('E:\THz\program\images\', 'gray stretch'), 'jpg');

% filters the data in output_image_data with the averaging filter.
output_binary_image_data = filter2(fspecial('average', 3), output_binary_image_data);
figure;
imshow(output_binary_image_data);
saveas(gcf, strcat('E:\THz\program\images\', 'average filter'), 'jpg');

% Gaussian lowpass filter.
% output_binary_image_data = filter2(fspecial('gaussian', [3 3], 0.5), output_binary_image_data);
% figure;
% imshow(output_binary_image_data);

% median filtering.
% output_binary_image_data = medfilt2(output_binary_image_data,[3 3]);
% figure;
% imshow(output_binary_image_data);

dualization_threshold = graythresh(output_binary_image_data); % computes a global threshold that can be used to convert an intensity image to a binary image with IM2BW.
output_binary_image_data = im2bw(output_binary_image_data, dualization_threshold); % Convert image to binary image by thresholding.

% dualization_threshold = 0.45;
% for i = 1 : x_length
%     for j = 1 : y_length
%         if output_binary_image_data(i, j) < dualization_threshold
%             output_binary_image_data(i, j) = 0;
%         else
%             output_binary_image_data(i, j) = 1;
%         end
%     end
% end

figure;
imshow(output_binary_image_data);
saveas(gcf, strcat('E:\THz\program\images\', 'binary image'), 'jpg');

end
