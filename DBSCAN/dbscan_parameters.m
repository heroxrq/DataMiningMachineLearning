function [ MinPts, Eps ] = dbscan_parameters( input_binary_image )

k = 4; % the k to determine the distance
x_length = size(input_binary_image, 1);
y_length = size(input_binary_image, 2);
k_distance = zeros(x_length, y_length);
count = 1;

% calculate the k_distance
for i = 1 : x_length
    for j = 1 : y_length
        if input_binary_image(i, j) == 0
            % for each black point, calculate the distance with each black point in the image 
            distance_matrix = zeros(x_length, y_length);
            for ii = 1 : x_length
                for jj = 1 : y_length
                    if input_binary_image(ii, jj) == 0
                       distance_matrix(ii, jj) = (i - ii)^2 + (j -jj)^2; 
                    end
                end
            end
            
            % find the k_distance
            for m = 1 : k
                index_i = 0;
                index_j = 0;
                k_distance(i, j) = inf;
                % find the minimum distance
                for ii = 1 : x_length
                    for jj = 1 : y_length
                        if distance_matrix(ii, jj) ~= 0
                            if k_distance(i, j) > distance_matrix(ii, jj)
                               k_distance(i, j) = distance_matrix(ii, jj);
                               index_i = ii;
                               index_j = jj;
                            end
                        end
                    end
                end
                distance_matrix(index_i, index_j) = 0; % set the minimum distance to 0
            end
            % k_distance is the k-th minimum distance
            k_distance(i, j) = sqrt(k_distance(i, j));
            distance_vector(count) = k_distance(i, j); % construct a verctor of distance
            count = count + 1;
        end
    end
end

% plot the k distance diagram
figure;
distance_vector = sort(distance_vector); % sort the distance
plot(distance_vector);
axis([0 length(distance_vector) 0 distance_vector(length(distance_vector))]);
saveas(gcf, strcat('E:\THz\program\images\', 'k distance diagram'), 'jpg');

% get the parameters
MinPts = k;
Eps = 2; % from the k distance diagram, we select the Eps = 2

end
