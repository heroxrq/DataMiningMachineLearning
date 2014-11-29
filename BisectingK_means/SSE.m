function [ y ] = SSE( Px, Py, Cx, Cy )

y = 0;

for i = 1 : length(Px)
   y = y + (Px(i) - Cx).^2 + (Py(i) - Cy).^2; 
end

end

