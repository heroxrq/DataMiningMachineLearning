% get the neighbouring points in the radius: Eps
function [ N, num ] = get_neighbors( input_binary_image, i, j, state_matrix, Eps, x_length, y_length )

% mark if the point is visited
unvisited = 0;
visited = 1;
Eps2 = Eps^2;
num = 0; % count for neighbouring points
cnt = 1;
N{cnt} = [0, 0]; % initialize N, if at last num >= MinPts and cnt = 1(all neighboring points are visited), this is invalid

for ii = (i - Eps) : (i + Eps)
    for jj = (j - Eps) : (j + Eps)
        % beyond the edge of the image
        if (ii < 1) || (ii > x_length) || (jj < 1) || (jj > y_length)
            continue;
        end
        % add the unvisited points in the radius(Eps) to the candidate set
        if (i - ii)^2 + (j - jj)^2 <= Eps2
            if input_binary_image(ii, jj) == 0 % black point
                if state_matrix(ii, jj) == unvisited
                    N{cnt} = [ii, jj]; % add the unvisited points
                    cnt = cnt + 1;
                end
                num = num + 1; % count for the neighbouring points
            end
        end
    end
end

end
