clear all;% removes all variables, globals, functions and MEX links.
close all;% closes all the open figure windows.
clc;% clears the command window and homes the cursor.

% dataset
Px = [1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9];% x-coordinate
Py = [1, 2, 3, 1, 2, 3, 1, 2, 3, 4, 5, 6, 4, 5, 6, 4, 5, 6, 7, 8, 9, 7, 8, 9, 7, 8, 9];% y-coordinate

% plot the dataset
figure;
plot(Px, Py, '+');
axis([0 10 0 10]);
xlabel('x');
ylabel('y');

% select the initial centroids
Cx = Px(1 : 3);
Cy = Py(1 : 3);

% the previous centroids
P_Cx = Cx;
P_Cy = Cy;

% initialize the temporary variables
sumx = zeros(1, length(Cx));
sumy = zeros(1, length(Cy));
n = zeros(1, length(Cx));

for k = 1 : 1e6
    % step 1: assign the nearest centroid for the points
	for i = 1 : length(Px)% each point
        for j = 1 : length(Cx)% each centroid
            dist(j) = (Px(i) - Cx(j))^2 + (Py(i) - Cy(j))^2;% the squared distance between point i and centroid j
        end
        Pc(i) = find(dist == min(min(dist)));% assign the nearest centroid for the point i
    end
    
    %step 2: recalculate the centroids (the average value)
    for i = 1 : length(Px)
        for j = 1 : length(Cx)
            if Pc(i) == j% the point i belongs to cluster j
                sumx(j) = sumx(j) + Px(i);
                sumy(j) = sumy(j) + Py(i);
                n(j) = n(j) + 1;
                break;
            end
        end
    end
    for j = 1 : length(Cx)
        Cx(j) = sumx(j) / n(j);
        Cy(j) = sumy(j) / n(j);
    end
   
    % terminate the loop
    for j = 1 : length(Cx)
        if abs((Cx(j) - P_Cx(j)) / P_Cx(j)) > 1e-6
            break;
        end
        if abs((Cy(j) - P_Cy(j)) / P_Cy(j)) > 1e-6
            break;
        end
    end
    if j == length(Cx)
        if (abs((Cx(j) - P_Cx(j)) / P_Cx(j)) <= 1e-6) && (abs((Cy(j) - P_Cy(j)) / P_Cy(j)) <= 1e-6)
            break;% break out the loop
        end
    end
    P_Cx = Cx;
    P_Cy = Cy;
end

k% output the iterations k

% classification
n = zeros(1, length(Cx));
for i = 1 : length(Px)
    for j = 1 : length(Cx)
        if Pc(i) == j% the point i belongs to cluster j
            n(j) = n(j) + 1;
            cluster_x(j, n(j)) = Px(i);
            cluster_y(j, n(j)) = Py(i);
            break;
        end
    end
end

% plot the clusters and centroids
figure;
plot(Cx, Cy, 'o', cluster_x(1, :), cluster_y(1, :), '+', cluster_x(2, :), cluster_y(2, :), 'x', cluster_x(3, :), cluster_y(3, :), '*');
axis([0 10 0 10]);
xlabel('x');
ylabel('y');