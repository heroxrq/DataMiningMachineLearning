function [  ] = segmentation( input_binary_image )

% [MinPts, Eps] = dbscan_parameters(input_binary_image);
MinPts = 4;
Eps = 2;
[ cluster ] = dbscan(input_binary_image, MinPts, Eps);
cluster_num = length(cluster); % number of clusters
x_length = size(input_binary_image, 1); % the number of rows
y_length = size(input_binary_image, 2); % the number of columns
my_cluster = ones(x_length, y_length);
x_minimum = 1;
x_maximum = 2;
y_minimum = 3;
y_maximum = 4;
for n = 1 : cluster_num
    cluster_edge{n}(x_minimum) = inf;
    cluster_edge{n}(x_maximum) = 0;
    cluster_edge{n}(y_minimum) = inf;
    cluster_edge{n}(y_maximum) = 0;
end

for i = 1 : length(cluster)
    for j = 1 : length(cluster{i})
        ii = cluster{i}{j}(1);
        jj = cluster{i}{j}(2);
        my_cluster(ii, jj) = (i - 1) / cluster_num;
        
        if cluster_edge{i}(x_minimum) > ii
            cluster_edge{i}(x_minimum) = ii;
        end
        if cluster_edge{i}(x_maximum) < ii
            cluster_edge{i}(x_maximum) = ii;
        end
        if cluster_edge{i}(y_minimum) > jj
            cluster_edge{i}(y_minimum) = jj;
        end
        if cluster_edge{i}(y_maximum) < jj
            cluster_edge{i}(y_maximum) = jj;
        end
    end
end

for n = 1 : cluster_num
    single_cluster{n} = zeros(cluster_edge{n}(x_maximum) - cluster_edge{n}(x_minimum) + 1, cluster_edge{n}(y_maximum) - cluster_edge{n}(y_minimum) + 1);
    for i = cluster_edge{n}(x_minimum) : cluster_edge{n}(x_maximum)
        for j = cluster_edge{n}(y_minimum) : cluster_edge{n}(y_maximum)
            single_cluster{n}(i - cluster_edge{n}(x_minimum) + 1, j - cluster_edge{n}(y_minimum) + 1) = input_binary_image(i, j);
        end
    end
end

for n = 1 : cluster_num
    save_image( single_cluster{n}, n );
end

end
