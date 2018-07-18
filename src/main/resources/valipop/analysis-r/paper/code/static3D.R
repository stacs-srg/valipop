static3D <- function(x,y,z,data,title) {
  
  library(lattice)
  #aspect controls the shape of the cuboid containing the plot
  wireframe(z ~  x * y, data, aspect = c(1, 1),
            main = title,
            
            scales = list(arrows = FALSE),
            screen = list(x = 5, z = -25, x = -80), # this line lets you rotate the plot (i.e. change POV)
            
            panel.3d.wireframe =
              function(x, y, z,
                       xlim, ylim, zlim,
                       xlim.scaled, ylim.scaled, zlim.scaled,
                       
                       ...) {
                panel.3dwire(x = x, y = y, z = z,
                             xlim = xlim,
                             ylim = ylim,
                             zlim = zlim,
                             xlim.scaled = xlim.scaled,
                             ylim.scaled = ylim.scaled,
                             zlim.scaled = zlim.scaled,
                             col = "blue",
                             ...)
                
              })
  
  
}

