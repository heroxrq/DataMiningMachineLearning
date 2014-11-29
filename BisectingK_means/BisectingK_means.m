clear all;% removes all variables, globals, functions and MEX links.
close all;% closes all the open figure windows.
clc;% clears the command window and homes the cursor.

% dataset
Px = [1, 1, 2, 2, 1, 1, 2, 2, 4, 4, 5, 5, 4, 4, 5, 5, 7, 7, 8, 8, 7, 7, 8, 8, 10, 10, 11, 11, 10, 10, 11, 11];% x-coordinate
Py = [1, 2, 1, 2, 7, 8, 7, 8, 1, 2, 1, 2, 7, 8, 7, 8, 1, 2, 1, 2, 7, 8, 7, 8,  1,  2,  1,  2,  7,  8,  7,  8];% y-coordinate

k = 8;% number of clusters

% initialize the cluster table
index_start = 1;
index_end = 1;
cluster_table_x{index_start} = Px;
cluster_table_y{index_start} = Py;

for m = 1 : 1e6
    % select a cluster from the table and iterate for the selected cluster
    for i = 1 : length(cluster_table_x{index_start}) / 2 
        % assign the initial centroids
        Cx(1) = cluster_table_x{index_start}(i);
        Cy(1) = cluster_table_y{index_start}(i);
        Cx(2) = cluster_table_x{index_start}(length(cluster_table_x{index_start}) - i + 1);
        Cy(2) = cluster_table_y{index_start}(length(cluster_table_y{index_start}) - i + 1);
        [cluster_x{i}, cluster_y{i}, C_x{i}, C_y{i}] = K_means( cluster_table_x{index_start}, cluster_table_y{index_start}, Cx, Cy );% basic K-means, K = 2
    end
    
    % select a pair of clusters which have the minimum total SSE
    for i = 1 : length(cluster_table_x{index_start}) / 2
        total_SSE(i) = SSE( cluster_x{i}{1}, cluster_y{i}{1}, C_x{i}(1), C_y{i}(1) ) + SSE( cluster_x{i}{2}, cluster_y{i}{2}, C_x{i}(2), C_y{i}(2) );
    end
    min = 1;
    for i = 2 : length(cluster_table_x{index_start}) / 2
        if total_SSE(i) < total_SSE(i - 1)
           min =  i;
        end
    end
    
    % add the two clusters to the cluster table
    index_end = index_end + 1;
    cluster_table_x{index_end} = cluster_x{min}{1};
    cluster_table_y{index_end} = cluster_y{min}{1};
    index_end = index_end + 1;
    cluster_table_x{index_end} = cluster_x{min}{2};
    cluster_table_y{index_end} = cluster_y{min}{2};
    
    
    % terminate the loop
    if index_end - index_start == k      
        break;
    end
    
    index_start = index_start + 1;% update the index_start of the table, which will soon be be iterated
    
    clear cluster_x cluster_y C_x C_y total_SSE;
end

for i = 1 : length(cluster_table_x)
figure;
plot(cluster_table_x{length(cluster_table_x) - i + 1}, cluster_table_y{length(cluster_table_x) - i + 1}, '+');
axis([0 12 0 9]);
saveas(gcf, strcat('cluster_table', int2str(i)), 'bmp');
end

