function [ cluster ] = dbscan( input_binary_image, MinPts, Eps )

% mark if the point is visited
unvisited = 0;
visited = 1;

x_length = size(input_binary_image, 1); % the number of rows
y_length = size(input_binary_image, 2); % the number of columns
state_matrix = zeros(x_length, y_length); % initialized with unvisited
cluster_count = 0; % initialize the number of clusters to 0

for i = 1 : x_length
    for j = 1 : y_length
        if input_binary_image(i, j) == 0 % black point
            if state_matrix(i, j) == unvisited
                state_matrix(i, j) = visited;
                [ N, cnt ] = get_neighbors(input_binary_image, i, j, state_matrix, Eps, x_length, y_length);
                if cnt >= MinPts
                    cluster_count = cluster_count + 1;
                    cluster{cluster_count} = {};
                    [ cluster, state_matrix ] = expand_cluster(input_binary_image, i, j, N, state_matrix, cluster, cluster_count, MinPts, Eps, x_length, y_length);
%               else
%                   input_binary_image(i, j) is a noise point, here we do
%                   not handle this
                end
            end
        end
    end
end

end
