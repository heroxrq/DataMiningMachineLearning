function [  ] = save_image( input_image_data, n )

normalized_data = imresize(input_image_data, [32 32]);
dualization_threshold = graythresh(normalized_data); % computes a global threshold that can be used to convert an intensity image to a binary image with IM2BW.
normalized_data = im2bw(normalized_data, dualization_threshold); % Convert image to binary image by thresholding.

fid = fopen(strcat('E:\THz\program\images\testDigits\', num2str(n)),'wt');
x_length = size(normalized_data, 1);
y_length = size(normalized_data, 2);
for i = 1 : x_length
    for j = 1 : y_length
        if normalized_data(i, j) == 1
            fprintf(fid, '%g', 0);
        else 
            fprintf(fid, '%g', 1);
        end
    end
    fprintf(fid,'\n');
end
fclose(fid);

end
