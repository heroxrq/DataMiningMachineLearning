clear all;% removes all variables, globals, functions and MEX links.
close all;% closes all the open figure windows.
clc;% clears the command window and homes the cursor.

% training dataset
x = [2104, 1600, 2400, 1416, 3000, 3200, 2400, 1700, 1000, 3900, 4700];
y = [400, 330, 369, 232, 540, 500, 400, 300, 200, 600, 700];

% plot the dataset
figure;
plot(x, y, '+');
axis([500 5000 0 1000]);
xlabel('square feet');
ylabel('price (in $1000)');
hold on;

a = [0, 0];% initial weight
b = [0, 0];% keep the previous value of the weight a
k = 1e-8;% learning rate, the selection of the k value is very important

% LMS algorithm
for m = 1 : 1e6
    for j = 1 : 2      
        temp = 0;
        for i = 1 : length(x)
            if j == 1
                z = 1;
            else
                z = x(i);
            end
            temp = temp + (y(i) - h(x(i), a(1), a(2))) * z;
        end
        b(j) = b(j) + k * temp;
    end
    
    if (abs((a(1) - b(1)) / a(1)) <= 1e-3) && (abs((a(2) - b(2)) / a(2)) <= 1e-3)
        a = b;
        break;% break out the loop
    end
    a = b;% update simultaneously
end

m% output the iterations m

% plot the curve of h(x)
i = 1;
for m = 500 : 10 : 5000
	X(i) = m;
	Y(i) = h(X(i), a(1), a(2)); 
    i = i + 1;
end
plot(X, Y);
