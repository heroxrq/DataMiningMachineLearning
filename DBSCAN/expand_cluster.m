function [ cluster, state_matrix ] = expand_cluster( input_binary_image, i, j, N, state_matrix, cluster, cluster_count, MinPts, Eps, x_length, y_length )

% mark if the point is visited
unvisited = 0;
visited = 1;
cluster_point_count = 1;
cluster{cluster_count}{cluster_point_count} = [i, j];
cluster_point_count = cluster_point_count + 1;
len_N = length(N);
m = 0;

while 1
    m = m + 1;
    if m <= len_N
        ii = N{m}(1);
        jj = N{m}(2);
        if state_matrix(ii, jj) == unvisited
            state_matrix(ii, jj) = visited;
            [ NN, num ] = get_neighbors(input_binary_image, ii, jj, state_matrix, Eps, x_length, y_length);
            len_NN = length(NN);
            if num >= MinPts
                for t = 1 : len_NN
                    iii = NN{t}(1);
                    jjj = NN{t}(2);
                    if (iii ~= 0) && (jjj ~= 0)
                        N{len_N + t} = NN{t};
                    end
                end
                len_N = length(N);
            end 

            is_in_cluster = false;
            for p = 1 : length(cluster)
                for q = 1 : length(cluster{p})
                    if cluster{p}{q} == [ii, jj]
                        is_in_cluster = true; % (ii, jj) is in a cluster
                    end
                end
            end
            if is_in_cluster == false
                cluster{cluster_count}{cluster_point_count} = [ii, jj];
                cluster_point_count = cluster_point_count + 1;
            end
        end
    else
        break;
    end
end

end
