function [ cluster_x, cluster_y, C_x, C_y ] = K_means( Px, Py, Cx, Cy )

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
        % assign the nearest centroid for the point i
        if dist(1) < dist(2)
            Pc(i) = 1;
        else
            Pc(i) = 2;
        end
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

% classification
n = zeros(1, length(Cx));
for i = 1 : length(Px)
    for j = 1 : length(Cx)
        if Pc(i) == j% the point i belongs to cluster j
            n(j) = n(j) + 1;
            cluster_x{j}(n(j)) = Px(i);
            cluster_y{j}(n(j)) = Py(i);
            break;
        end
    end
end

C_x = Cx;
C_y = Cy;

end

